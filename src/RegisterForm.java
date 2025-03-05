import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterForm extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(RegisterForm.class);
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JPasswordField txtConfirmPassword;
    private final JTextField txtFullname;
    private final JTextField txtEmail;
    private final JTextField txtPhone;

    public RegisterForm() {
        setTitle("Drama Club Membership - Register");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set frame icon
        ImageUtils.setFrameIcon(this);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(new Color(240, 240, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add logo at the top
        JLabel logoLabel = new JLabel(ImageUtils.getLogo(120, 120));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(logoLabel, BorderLayout.NORTH);

        // Create registration panel
        JPanel registerPanel = new JPanel(new GridBagLayout());
        registerPanel.setBackground(new Color(240, 240, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create components with enhanced styling
        JLabel lblUsername = new JLabel("Username:");
        JLabel lblPassword = new JLabel("Password:");
        JLabel lblConfirmPassword = new JLabel("Confirm Password:");
        JLabel lblFullname = new JLabel("Full Name:");
        JLabel lblEmail = new JLabel("Email:");
        JLabel lblPhone = new JLabel("Phone:");

        // Set font for labels
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        lblUsername.setFont(labelFont);
        lblPassword.setFont(labelFont);
        lblConfirmPassword.setFont(labelFont);
        lblFullname.setFont(labelFont);
        lblEmail.setFont(labelFont);
        lblPhone.setFont(labelFont);

        // Create and style text fields
        txtUsername = createStyledTextField();
        txtPassword = createStyledPasswordField();
        txtConfirmPassword = createStyledPasswordField();
        txtFullname = createStyledTextField();
        txtEmail = createStyledTextField();
        txtPhone = createStyledTextField();

        // Style buttons
        JButton btnRegister = new JButton("Register");
        styleButton(btnRegister, new Color(60, 179, 113));
        
        JButton btnBack = new JButton("Back to Login");
        styleButton(btnBack, new Color(70, 130, 180));

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
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(240, 240, 250));
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnBack);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        registerPanel.add(buttonPanel, gbc);

        mainPanel.add(registerPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Add action listeners
        btnRegister.addActionListener(e -> register());
        btnBack.addActionListener(e -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
            dispose();
        });
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setPreferredSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setPreferredSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return field;
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setPreferredSize(new Dimension(120, 35));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
    }

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

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if name already exists
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

            // Insert new student
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
            logger.error("Database error during registration", ex);
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}