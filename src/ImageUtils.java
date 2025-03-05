import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

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
    
    public static JPanel createBlurredBackgroundPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imageIcon = new ImageIcon(LOGO_PATH);
                Image image = imageIcon.getImage();
                
                // Create a buffered image for blurring
                BufferedImage buffered = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = buffered.createGraphics();
                
                // Draw the image scaled to fill the panel
                g2d.drawImage(image, 0, 0, getWidth(), getHeight(), null);
                g2d.dispose();
                
                // Apply blur effect
                float[] matrix = {
                    1/9f, 1/9f, 1/9f,
                    1/9f, 1/9f, 1/9f,
                    1/9f, 1/9f, 1/9f
                };
                ConvolveOp op = new ConvolveOp(new Kernel(3, 3, matrix), ConvolveOp.EDGE_NO_OP, null);
                buffered = op.filter(buffered, null);
                
                // Set alpha composite for transparency
                Graphics2D g2 = (Graphics2D) g;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g2.drawImage(buffered, 0, 0, null);
            }
        };
    }
} 