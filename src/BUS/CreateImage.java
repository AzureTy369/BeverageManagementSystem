
package BUS;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author TaiNhat
 */
public class CreateImage {

    private String imageName;

    public CreateImage() {
    }

    public CreateImage(String imageName) {
        this.imageName = imageName;
    }

    public String getImage() {
        return this.imageName;
    }

    public void setImage(String imageName) {
        this.imageName = imageName;
    }

    //    tạo ảnh cho panel
    public void setBackgroundForPanel(JPanel panel, String imagePath) {
        CreateImage cre = new CreateImage();
        // Tạo hình nền từ đường dẫn hình ảnh
        ImageIcon imageIcon = new ImageIcon(cre.createPathImage(imagePath));
        Image backgroundImage = imageIcon.getImage();

        // Tạo một JPanel tạm thời để vẽ hình nền
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Vẽ hình nền
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        // Thiết lập kích thước và vị trí của backgroundPanel
        backgroundPanel.setBounds(0, 0, panel.getWidth(), panel.getHeight());

        // Thêm panel hiện tại vào backgroundPanel
        panel.add(backgroundPanel);

        for (Component component : panel.getComponents()) {
            if (component != backgroundPanel) {
                if (component instanceof JComponent) {
                    ((JComponent) component).setOpaque(false); // Đặt thành phần trong suốt
                    ((JComponent) component).setBackground(new Color(0, 0, 0, 0));
                }
            }
        }
        // Đặt các thành phần con vào vị trí đúng trên hình nền
        for (Component component : panel.getComponents()) {
            if (component != backgroundPanel) {
                panel.setComponentZOrder(component, 0); // Đặt thành phần lên trên backgroundPanel
            }
        }
        // Cập nhật UI
        panel.revalidate();
        panel.repaint();
    }

//  thay đổi màu sắc khi hover vào button    
    public void changeColorButton(JButton button) {
        button.setBorder(BorderFactory.createBevelBorder(0));
        button.addMouseListener(new MouseAdapter() {
            Color defaultBackground = button.getBackground();
            Color defaultForeground = button.getForeground();

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 201, 222, 255));
                button.setForeground(Color.RED);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(defaultBackground);
                button.setForeground(defaultForeground);
            }
        });
    }

    public String getImagePathRelative(String imageName) {
        String imagePath = File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + "CarPhotos" + File.separator + imageName;
        return imagePath;
    }

    public String getImagePathAbsolute(String imageName) {
        CreateImage cre = new CreateImage();
        File currentDirectory = new File(System.getProperty("user.dir"));
        String absolutePath = new File(currentDirectory, cre.getImagePathRelative(imageName)).getAbsolutePath();
        return absolutePath;
    }

    public String createPathImage(String oldPath) {
        CreateImage cre = new CreateImage();
        oldPath = cre.getImagePathAbsolute(oldPath);
        StringBuilder newPath = new StringBuilder();
        for (int i = 0; i < oldPath.length(); i++) {
            char currentChar = oldPath.charAt(i);
            newPath.append(currentChar);
            if (currentChar == '\\') {
                newPath.append('\\'); // Thêm một dấu \ phía sau dấu \
            }
        }
        return newPath.toString();
    }
//    Tạo đường dẫn ảnh của thư mục Icon

    public String getIconPathRelative(String imageName) {
        String imagePath = File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + "Icon" + File.separator + imageName;
        return imagePath;
    }

    public String getIconPathAbsolute(String imageName) {
        CreateImage cre = new CreateImage();
        File currentDirectory = new File(System.getProperty("user.dir"));
        String absolutePath = new File(currentDirectory, cre.getIconPathRelative(imageName)).getAbsolutePath();
        return absolutePath;
    }

    public String createPathIcon(String oldPath) {
        CreateImage cre = new CreateImage();
        oldPath = cre.getIconPathAbsolute(oldPath);
        StringBuilder newPath = new StringBuilder();
        for (int i = 0; i < oldPath.length(); i++) {
            char currentChar = oldPath.charAt(i);
            newPath.append(currentChar);
            if (currentChar == '\\') {
                newPath.append('\\'); // Thêm một dấu \ phía sau dấu \
            }
        }
        return newPath.toString();
    }

    //    tạo ảnh trên label qua Icon
    public ImageIcon setIconForLabel(JLabel label, String fileName) {
        fileName = createPathIcon(fileName);
        try {
            BufferedImage image = ImageIO.read(new File(fileName));
            ImageIcon icon = new ImageIcon(image.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH));
            label.setIcon(icon);
            label.setHorizontalTextPosition(SwingConstants.CENTER);
            label.setVerticalTextPosition(SwingConstants.CENTER);
            return icon;
        } catch (IOException e) {
            // Xử lý lỗi bằng cách hiển thị thông báo hoặc trả về một biểu tượng mặc định
            JOptionPane.showMessageDialog(null, "Không thể tìm thấy hình ảnh: " + fileName, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    //    tạo ảnh trên label qua Carphotos
    public ImageIcon setImageForLabel(JLabel label, String fileName) {
        fileName = createPathImage(fileName);
        try {
            BufferedImage image = ImageIO.read(new File(fileName));
            ImageIcon icon = new ImageIcon(image.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH));
            label.setIcon(icon);
            label.setHorizontalTextPosition(SwingConstants.CENTER);
            label.setVerticalTextPosition(SwingConstants.CENTER);
            return icon;
        } catch (IOException e) {
            // Xử lý lỗi bằng cách hiển thị thông báo hoặc trả về một biểu tượng mặc định
            JOptionPane.showMessageDialog(null, "Không thể tìm thấy hình ảnh: " + fileName, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
//    tạo icon trên button
    public ImageIcon setIconForButton(JButton button, String fileName) {
        fileName = createPathIcon(fileName);
        try {
            BufferedImage image = ImageIO.read(new File(fileName));
//            set size icon
            ImageIcon icon = new ImageIcon(image.getScaledInstance(46, 40, Image.SCALE_SMOOTH));
            button.setIcon(icon);
            button.setHorizontalTextPosition(SwingConstants.RIGHT); // Đặt văn bản ở bên phải của hình ảnh
            button.setVerticalTextPosition(SwingConstants.CENTER);
            return icon;
        } catch (IOException e) {
            // Xử lý lỗi bằng cách hiển thị thông báo hoặc trả về một biểu tượng mặc định
            JOptionPane.showMessageDialog(null, "Không thể tìm thấy hình ảnh: " + fileName, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

//    public static void main(String[] args) {
//        CreateImage createImage = new CreateImage();
//        System.out.println(createImage.getImagePathAbsolute("AudiE-Tron"));
//        System.out.println(createImage.createPathImage("AudiE-Tron"));
//    }
}
