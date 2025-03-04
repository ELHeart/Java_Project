import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MainForm extends JFrame {
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

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setMnemonic(KeyEvent.VK_O);
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openMenuItem.addActionListener(_ -> loadStudents());

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic(KeyEvent.VK_X);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        exitMenuItem.addActionListener(_ -> System.exit(0));

        fileMenu.add(openMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);
        aboutMenuItem.addActionListener(_ -> showAboutWindow());

        helpMenu.add(aboutMenuItem);

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        // Set menu bar
        setJMenuBar(menuBar);

        // Create table model
        tableModel = new DefaultTableModel(
                new String[] {"ID", "Username", "Full Name", "Email", "Phone", "Date Registered"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create table
        tblStudents = new JTable(tableModel);
        tblStudents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tblStudents);

        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(_ -> searchStudent());
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(_ -> deleteStudent());
        buttonPanel.add(btnDelete);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Load students
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
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
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
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error searching students: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
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
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting student: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
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
        closeButton.addActionListener(_ -> aboutWindow.dispose());
        panel.add(closeButton, gbc);

        aboutWindow.setContentPane(panel);
        aboutWindow.pack();
        aboutWindow.setLocationRelativeTo(this);
        aboutWindow.setVisible(true);

        // Auto-close after 10 seconds
        Timer timer = new Timer(10000, _ -> aboutWindow.dispose());
        timer.setRepeats(false);
        timer.start();
    }
}