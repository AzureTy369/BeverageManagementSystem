package GUI;

import BUS.ProductBUS;
import BUS.ProductDetailBUS;
import BUS.ProductCategoryBUS;
import BUS.SupplierBUS;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProductManagementGUI extends JPanel {
    private ProductGUI productPanel;

    private ProductBUS productController;
    private ProductDetailBUS detailController;
    private ProductCategoryBUS categoryController;

    private final Color lightColor = new Color(248, 249, 250);
    private final Color primaryColor = new Color(0, 123, 255);

    public ProductManagementGUI(ProductBUS productController,
            ProductDetailBUS detailController,
            ProductCategoryBUS categoryController,
            SupplierBUS supplierController) {
        this.productController = productController;
        this.detailController = detailController;
        this.categoryController = categoryController;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(lightColor);

        createGUI();
    }

    private void createGUI() {
        // Create panels
        productPanel = new ProductGUI(productController, categoryController);

        // Add view details button to product panel
        addViewDetailsButtonToProductPanel();

        // Add category management button to product panel
        addCategoryManagementButtonToProductPanel();

        // Add only the product panel to the main panel
        add(productPanel, BorderLayout.CENTER);
    }

    private void addViewDetailsButtonToProductPanel() {
        JButton viewDetailsButton = new JButton("Xem chi tiết sản phẩm");
        viewDetailsButton.setFont(new Font("Arial", Font.PLAIN, 14));
        viewDetailsButton.setBackground(primaryColor);
        viewDetailsButton.setForeground(Color.BLACK);
        viewDetailsButton.setFocusPainted(false);
        viewDetailsButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        viewDetailsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        viewDetailsButton.addActionListener(e -> {
            int selectedRow = productPanel.getSelectedProductRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn một sản phẩm để xem chi tiết",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String productId = productPanel.getSelectedProductId();
            if (productId != null) {
                showProductDetailsDialog(productId);
            }
        });

        // Add the button to the product panel
        productPanel.addViewDetailsButton(viewDetailsButton);
    }

    private void addCategoryManagementButtonToProductPanel() {
        JButton categoryButton = new JButton("Xem chi tiết danh mục sản phẩm");
        categoryButton.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryButton.setBackground(primaryColor);
        categoryButton.setForeground(Color.BLACK);
        categoryButton.setFocusPainted(false);
        categoryButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        categoryButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        categoryButton.addActionListener(e -> showCategoryManagementDialog());

        // Add the button to the product panel
        productPanel.addExtraActionButton(categoryButton);
    }

    private void showProductDetailsDialog(String productId) {
        // Create a new dialog to show product details
        JDialog detailsDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết sản phẩm", true);
        detailsDialog.setLayout(new BorderLayout());
        detailsDialog.setSize(900, 600);
        detailsDialog.setLocationRelativeTo(this);

        // Create a new instance of ProductDetailGUI for this specific product
        ProductDetailGUI productDetailPanel = new ProductDetailGUI(detailController, productController);
        productDetailPanel.showDetailsForProduct(productId);

        detailsDialog.add(productDetailPanel, BorderLayout.CENTER);

        // Add close button at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(lightColor);

        JButton closeButton = new JButton("Đóng");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        closeButton.setBackground(new Color(108, 117, 125)); // gray
        closeButton.setForeground(Color.BLACK);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> detailsDialog.dispose());

        buttonPanel.add(closeButton);
        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Show the dialog
        detailsDialog.setVisible(true);
    }

    private void showCategoryManagementDialog() {
        try {
            // Kiểm tra categoryController
            if (categoryController == null) {
                JOptionPane.showMessageDialog(this,
                        "Không thể hiển thị danh mục sản phẩm: Bộ điều khiển danh mục không được khởi tạo",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create a new dialog to show category management
            JDialog categoryDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    "Quản lý danh mục sản phẩm", true);
            categoryDialog.setLayout(new BorderLayout());
            categoryDialog.setSize(900, 600);
            categoryDialog.setLocationRelativeTo(this);

            // Create a new instance of ProductCategoryGUI with dialog mode enabled
            ProductCategoryGUI categoryPanel = new ProductCategoryGUI(categoryController, true);

            categoryDialog.add(categoryPanel, BorderLayout.CENTER);

            // Add close button at the bottom
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(lightColor);

            JButton closeButton = new JButton("Đóng");
            closeButton.setFont(new Font("Arial", Font.PLAIN, 14));
            closeButton.setBackground(new Color(108, 117, 125)); // gray
            closeButton.setForeground(Color.BLACK);
            closeButton.setFocusPainted(false);
            closeButton.addActionListener(e -> categoryDialog.dispose());

            buttonPanel.add(closeButton);
            categoryDialog.add(buttonPanel, BorderLayout.SOUTH);

            // Show the dialog
            categoryDialog.setVisible(true);
        } catch (NullPointerException e) {
            System.err.println("NullPointerException in showCategoryManagementDialog: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi hiển thị danh mục sản phẩm: Có thành phần null",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("Exception in showCategoryManagementDialog: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi hiển thị danh mục sản phẩm: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}