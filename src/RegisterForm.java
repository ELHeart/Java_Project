import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 * Registration form for the Java Ambassadors Programming Club Membership Application.
 * Handles new user registration with validation and duplicate checking.
 */
public class RegisterForm extends JFrame {
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JPasswordField txtConfirmPassword;
    private final JTextField txtFullname;
    private final JTextField txtEmail;
    private final JTextField txtPhone;

    /**
     * Constructs the registration form with all required input fields.
     * Initializes the UI components and sets up event handlers.
     */
    public RegisterForm() {
        setTitle("Java Ambassadors Programming Club - Register");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set frame icon
        ImageUtils.setFrameIcon(this);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Add logo at the top
        JLabel logoLabel = new JLabel(ImageUtils.getLogo(100, 100));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(logoLabel, BorderLayout.NORTH);

        // Create registration panel
        JPanel registerPanel = new JPanel(new GridBagLayout());
        registerPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create components
        JLabel lblUsername = new JLabel("Username:");
        JLabel lblPassword = new JLabel("Password:");
        JLabel lblConfirmPassword = new JLabel("Confirm Password:");
        JLabel lblFullname = new JLabel("Full Name:");
        JLabel lblEmail = new JLabel("Email:");
        JLabel lblPhone = new JLabel("Phone:");

        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);
        txtConfirmPassword = new JPasswordField(20);
        txtFullname = new JTextField(20);
        txtEmail = new JTextField(20);
        txtPhone = new JTextField(20);
        JButton btnRegister = new JButton("Register");
        JButton btnBack = new JButton("Back to Login");

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0;
        registerPanel.add(lblUsername, gbc);
        gbc.gridx = 1;
        registerPanel.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        registerPanel.add(lblPassword, gbc);
        gbc.gridx = 1;
        registerPanel.add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        registerPanel.add(lblConfirmPassword, gbc);
        gbc.gridx = 1;
        registerPanel.add(txtConfirmPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        registerPanel.add(lblFullname, gbc);
        gbc.gridx = 1;
        registerPanel.add(txtFullname, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        registerPanel.add(lblEmail, gbc);
        gbc.gridx = 1;
        registerPanel.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        registerPanel.add(lblPhone, gbc);
        gbc.gridx = 1;
        registerPanel.add(txtPhone, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnBack);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        registerPanel.add(buttonPanel, gbc);

        mainPanel.add(registerPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Add action listeners
        btnRegister.addActionListener(_ -> register());
        btnBack.addActionListener(_ -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
            dispose();
        });
    }

    /**
     * Handles the registration process with input validation and database operations.
     * Validates required fields, password matching, and checks for duplicate usernames/fullnames.
     * If successful, creates a new user account and returns to login screen.
     */
    private void register() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        String fullname = txtFullname.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();

        // Validate input
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || fullname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Checks if the Student's name entered already exists.
        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM students WHERE fullname = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, fullname);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new DuplicateNameException("A student with the name '" + fullname + "' already exists.");
                    }
                }
            }

            // Check if username already exists
            checkSql = "SELECT COUNT(*) FROM students WHERE username = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            // Inserts new student data into the table
            String sql = "INSERT INTO students (username, password, fullname, email, phone) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, fullname);
                stmt.setString(4, email);
                stmt.setString(5, phone);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    LoginForm loginForm = new LoginForm();
                    loginForm.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (DuplicateNameException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Duplicate Name Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            System.err.println("Database error during registration: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}