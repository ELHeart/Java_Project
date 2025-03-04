import javax.swing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClubMembershipApp {
    private static final Logger logger = LoggerFactory.getLogger(ClubMembershipApp.class);

    public static void main(String[] args) {
        try {
            // Set the look and feel to the system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.error("Failed to set system look and feel", e);
        }
        // Launch the login form
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}