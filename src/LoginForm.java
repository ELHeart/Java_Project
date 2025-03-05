import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginForm extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(LoginForm.class);
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;

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
        btnLogin.addActionListener(e -> login());
        btnRegister.addActionListener(e -> {
            RegisterForm registerForm = new RegisterForm();
            registerForm.setVisible(true);
            dispose();
        });
    }

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
            logger.error("Database error during login", ex);
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            logger.error("Unexpected error during login", ex);
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}