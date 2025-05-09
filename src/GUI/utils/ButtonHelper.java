package GUI.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Lớp tiện ích giúp tạo các button có giao diện và màu sắc nhất quán cho toàn
 * bộ ứng dụng
 */
public class ButtonHelper {
    private static final Color PRIMARY_COLOR = new Color(0, 123, 255);
    private static final Color SUCCESS_COLOR = new Color(0x4CAF50);
    private static final Color WARNING_COLOR = new Color(0xFFCA28);
    private static final Color DANGER_COLOR = new Color(0xF44336);
    private static final Color INFO_COLOR = new Color(0x26A69A);
    private static final Color EXCEL_COLOR = new Color(0x455A64);

    /**
     * Tạo button với giao diện và màu sắc tùy chỉnh
     * 
     * @param text  Text hiển thị trên button
     * @param color Màu nền của button
     * @return JButton đã được cấu hình
     */
    public static JButton createButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                // Xác định màu nền dựa theo loại nút
                Color bgColor;
                if (text.toLowerCase().contains("thêm")) {
                    bgColor = SUCCESS_COLOR; // Xanh lá
                } else if (text.toLowerCase().contains("xóa")) {
                    bgColor = DANGER_COLOR; // Đỏ
                } else if (text.toLowerCase().contains("sửa") || text.toLowerCase().contains("chỉnh sửa")) {
                    bgColor = WARNING_COLOR; // Vàng
                } else if (text.toLowerCase().contains("tìm kiếm")) {
                    bgColor = PRIMARY_COLOR; // Màu menu của đồ án
                } else if (text.toLowerCase().contains("excel")) {
                    bgColor = EXCEL_COLOR; // Xám đậm
                } else if (text.toLowerCase().contains("làm mới") || text.toLowerCase().contains("refresh")) {
                    bgColor = INFO_COLOR; // Xanh ngọc
                } else {
                    // Các nút khác
                    bgColor = color;
                }

                // Vẽ background
                g.setColor(bgColor);
                g.fillRect(0, 0, getWidth(), getHeight());

                // Vẽ text
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();

                Color textColor;
                if (text.toLowerCase().contains("sửa") || text.toLowerCase().contains("chỉnh sửa")) {
                    textColor = new Color(0x000000); // Đen
                } else {
                    textColor = new Color(0xFFFFFF); // Trắng
                }

                g.setColor(textColor);
                g.drawString(getText(), (getWidth() - textWidth) / 2,
                        (getHeight() - textHeight) / 2 + fm.getAscent());
            }
        };

        // Thiết lập font in đậm cho text
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(color, 1));
        button.setToolTipText(text);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 35));

        return button;
    }

    /**
     * Tạo button với màu primary (xanh chính)
     */
    public static JButton createPrimaryButton(String text) {
        return createButton(text, PRIMARY_COLOR);
    }

    /**
     * Tạo button với màu success (xanh lá)
     */
    public static JButton createSuccessButton(String text) {
        return createButton(text, SUCCESS_COLOR);
    }

    /**
     * Tạo button với màu warning (vàng)
     */
    public static JButton createWarningButton(String text) {
        return createButton(text, WARNING_COLOR);
    }

    /**
     * Tạo button với màu danger (đỏ)
     */
    public static JButton createDangerButton(String text) {
        return createButton(text, DANGER_COLOR);
    }

    /**
     * Tạo button với màu info (xanh ngọc)
     */
    public static JButton createInfoButton(String text) {
        return createButton(text, INFO_COLOR);
    }

    /**
     * Tạo button với màu excel (xám đậm)
     */
    public static JButton createExcelButton(String text) {
        return createButton(text, EXCEL_COLOR);
    }
}