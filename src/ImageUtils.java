import javax.swing.*;
import java.awt.*;

public class ImageUtils {
    public static final String LOGO_PATH = "images/atu_logo.png";
    
    public static ImageIcon getLogo(int width, int height) {
        ImageIcon imageIcon = new ImageIcon(LOGO_PATH);
        Image image = imageIcon.getImage();
        Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }
    
    public static void setFrameIcon(JFrame frame) {
        ImageIcon icon = new ImageIcon(LOGO_PATH);
        frame.setIconImage(icon.getImage());
    }
} 