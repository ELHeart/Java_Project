import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 * Login form for the Drama Club Membership Application.
 * Handles user authentication and navigation to the main application.
 */
public class LoginForm extends JFrame {
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;

    /**
     * Constructs the login form with username and password fields.
     * Initializes the UI components and sets up event handlers.
     */
    public LoginForm() {
        setTitle("Drama Club Membership - Login");
        setSize(400, 300);
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

        // Create login panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add components
        JLabel lblUsername = new JLabel("Username:");
        JLabel lblPassword = new JLabel("Password:");
        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);
        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Register");

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0;
        loginPanel.add(lblUsername, gbc);
        gbc.gridx = 1;
        loginPanel.add(txtUsername, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        loginPanel.add(lblPassword, gbc);
        gbc.gridx = 1;
        loginPanel.add(txtPassword, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginPanel.add(buttonPanel, gbc);

        mainPanel.add(loginPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Add action listeners
        btnLogin.addActionListener(_ -> login());
        btnRegister.addActionListener(_ -> {
            RegisterForm registerForm = new RegisterForm();
            registerForm.setVisible(true);
            dispose();
        });
    }

    /**
     * Handles the login process by validating credentials against the database.
     * If successful, opens the main form; otherwise, shows an error message.
     */
    private void login() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM students WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        MainForm mainForm = new MainForm(rs.getInt("id"));
                        mainForm.setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Database error during login: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}