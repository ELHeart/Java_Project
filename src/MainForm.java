import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainForm extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainForm.class);
    private final JTable tblStudents;
    private final DefaultTableModel tableModel;
    private final JTextField txtSearch;
    private final int currentUserId;

    public MainForm(int userId) {
        this.currentUserId = userId;

        setTitle("Drama Club Membership - Main");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set frame icon
        ImageUtils.setFrameIcon(this);

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem refreshItem = new JMenuItem("Refresh");
        JMenuItem logoutItem = new JMenuItem("Logout");
        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(logoutItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Create header panel with logo
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        // Add logo to the right side of header
        JLabel logoLabel = new JLabel(ImageUtils.getLogo(80, 80));
        headerPanel.add(logoLabel, BorderLayout.EAST);
        
        // Add search panel to the left side of header
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        headerPanel.add(searchPanel, BorderLayout.WEST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"ID", "Username", "Full Name", "Email", "Phone", "Date Registered"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblStudents = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblStudents);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        JButton btnRefresh = new JButton("Refresh");
        JButton btnDelete = new JButton("Delete");
        JButton btnLogout = new JButton("Logout");
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnLogout);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the main panel to the frame
        add(mainPanel);

        // Add action listeners
        btnRefresh.addActionListener(e -> loadStudents());
        btnDelete.addActionListener(e -> deleteStudent());
        btnLogout.addActionListener(e -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
            dispose();
        });
        btnSearch.addActionListener(e -> searchStudent());

        // Add menu item action listeners
        refreshItem.addActionListener(e -> loadStudents());
        logoutItem.addActionListener(e -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
            dispose();
        });
        aboutItem.addActionListener(e -> showAboutWindow());

        // Initial table load
        loadStudents();
    }

    private void loadStudents() {
        tableModel.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM students ORDER BY fullname";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    Object[] row = new Object[6];
                    row[0] = rs.getInt("id");
                    row[1] = rs.getString("username");
                    row[2] = rs.getString("fullname");
                    row[3] = rs.getString("email");
                    row[4] = rs.getString("phone");
                    row[5] = rs.getString("date_registered");
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException ex) {
            logger.error("Database error during member list refresh", ex);
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchStudent() {
        String searchTerm = txtSearch.getText().trim();

        if (searchTerm.isEmpty()) {
            loadStudents();
            return;
        }

        tableModel.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM students WHERE fullname LIKE ? OR username LIKE ? ORDER BY fullname";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String pattern = "%" + searchTerm + "%";
                stmt.setString(1, pattern);
                stmt.setString(2, pattern);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Object[] row = new Object[6];
                        row[0] = rs.getInt("id");
                        row[1] = rs.getString("username");
                        row[2] = rs.getString("fullname");
                        row[3] = rs.getString("email");
                        row[4] = rs.getString("phone");
                        row[5] = rs.getString("date_registered");
                        tableModel.addRow(row);
                    }
                }
            }
        } catch (SQLException ex) {
            logger.error("Database error during member search", ex);
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        int selectedRow = tblStudents.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int studentId = (int) tableModel.getValueAt(selectedRow, 0);

        // Prevent user from deleting themselves
        if (studentId == currentUserId) {
            JOptionPane.showMessageDialog(this, "You cannot delete your own account",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this student?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM students WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, studentId);

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Student deleted successfully",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadStudents();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete student",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                logger.error("Database error during member deletion", ex);
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAboutWindow() {
        JWindow aboutWindow = new JWindow(this);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel titleLabel = new JLabel("Drama Club Membership Application");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel copyrightLabel = new JLabel("Copyright © 2025");

        JLabel membersLabel = new JLabel("Group Members:");

        JLabel member1Label = new JLabel("Davidson Kwaipe Boye");
        JLabel member1IdLabel = new JLabel("01212270D");

        JLabel courseLabel = new JLabel("Course: Object Oriented Software Development with Java II");
        JLabel yearLabel = new JLabel("Academic Year: 2024/2025");

        panel.add(titleLabel, gbc);
        panel.add(new JLabel(" "), gbc);
        panel.add(copyrightLabel, gbc);
        panel.add(new JLabel(" "), gbc);
        panel.add(membersLabel, gbc);
        panel.add(member1Label, gbc);
        panel.add(member1IdLabel, gbc);
        panel.add(new JLabel(" "), gbc);
        panel.add(courseLabel, gbc);
        panel.add(yearLabel, gbc);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> aboutWindow.dispose());
        panel.add(closeButton, gbc);

        aboutWindow.setContentPane(panel);
        aboutWindow.pack();
        aboutWindow.setLocationRelativeTo(this);
        aboutWindow.setVisible(true);

        // Auto-close after 10 seconds
        Timer timer = new Timer(10000, e -> aboutWindow.dispose());
        timer.setRepeats(false);
        timer.start();
    }
}