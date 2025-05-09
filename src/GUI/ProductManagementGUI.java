package GUI;

import BUS.ProductBUS;
import BUS.ProductDetailBUS;
import BUS.ProductCategoryBUS;
import BUS.SupplierBUS;
import GUI.utils.ButtonHelper;

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
        setBorder(new EmptyBorder(10, 10, 10, 10));
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
        JButton viewDetailsButton = ButtonHelper.createButton("Xem chi tiết sản phẩm", primaryColor);
        viewDetailsButton.setPreferredSize(new Dimension(200, 35));

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
        JButton categoryButton = ButtonHelper.createButton("Xem chi tiết danh mục sản phẩm", primaryColor);
        categoryButton.setPreferredSize(new Dimension(230, 35));

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

        JButton closeButton = ButtonHelper.createButton("Đóng", new Color(220, 53, 69)); // Sử dụng màu đỏ cho nút đóng

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

            JButton closeButton = ButtonHelper.createButton("Đóng", new Color(220, 53, 69)); // Sử dụng màu đỏ cho nút
                                                                                             // đóng

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