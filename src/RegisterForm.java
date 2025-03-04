import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterForm extends JFrame {
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JPasswordField txtConfirmPassword;
    private final JTextField txtFullname;
    private final JTextField txtEmail;
    private final JTextField txtPhone;

    public RegisterForm() {
        setTitle("Drama Club Membership - Register");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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
        JButton btnCancel = new JButton("Cancel");

        // Create panel and set layout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Add components to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblUsername, gbc);

        gbc.gridx = 1;
        panel.add(txtUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblPassword, gbc);

        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblConfirmPassword, gbc);

        gbc.gridx = 1;
        panel.add(txtConfirmPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(lblFullname, gbc);

        gbc.gridx = 1;
        panel.add(txtFullname, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(lblEmail, gbc);

        gbc.gridx = 1;
        panel.add(txtEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(lblPhone, gbc);

        gbc.gridx = 1;
        panel.add(txtPhone, gbc);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnCancel);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Add panel to frame
        add(panel);

        // Add action listeners
        btnRegister.addActionListener(_ -> register());
        btnCancel.addActionListener(_ -> cancel());
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
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cancel() {
        LoginForm loginForm = new LoginForm();
        loginForm.setVisible(true);
        dispose();
    }
}