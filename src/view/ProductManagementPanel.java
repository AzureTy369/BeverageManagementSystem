package view;

import controller.ProductController;
import controller.ProductCategoryController;
import model.Product;
import model.ProductCategory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ProductManagementPanel extends JPanel {
    private ProductController productController;
    private ProductCategoryController categoryController;

    private JTable productTable;
    private DefaultTableModel tableModel;

    private JTextField idField;
    private JTextField nameField;
    private JComboBox<ProductCategory> categoryComboBox;
    private JTextField descriptionField;
    private JTextField imageField;
    private JButton browseButton;
    private JTextField priceField;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    // Colors
    private Color primaryColor = new Color(0, 123, 255);
    private Color successColor = new Color(40, 167, 69);
    private Color warningColor = new Color(255, 193, 7);
    private Color dangerColor = new Color(220, 53, 69);
    private Color lightColor = new Color(248, 249, 250);
    private Color textColor = Color.BLACK;

    public ProductManagementPanel(ProductController productController, ProductCategoryController categoryController) {
        this.productController = productController;
        this.categoryController = categoryController;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(lightColor);

        // Create title panel
        createTitlePanel();

        // Create components
        createTable();
        createForm();

        // Refresh data
        refreshProductData();
    }

    private void createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(lightColor);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("\uD83C\uDF7A Quản lý sản phẩm");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(textColor);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);

        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.setBackground(primaryColor);
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> refreshProductData());

        actionPanel.add(refreshButton);

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(actionPanel, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);
    }

    private void createTable() {
        // Create table model with columns
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableModel.addColumn("Mã SP");
        tableModel.addColumn("Tên sản phẩm");
        tableModel.addColumn("Loại sản phẩm");
        tableModel.addColumn("Giá bán");
        tableModel.addColumn("Mô tả");

        // Create table and add to scroll pane
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getTableHeader().setReorderingAllowed(false);
        productTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        productTable.setRowHeight(30);
        productTable.setFont(new Font("Arial", Font.PLAIN, 14));
        productTable.setGridColor(new Color(230, 230, 230));
        productTable.setShowGrid(true);

        // Add selection listener
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && productTable.getSelectedRow() != -1) {
                int selectedRow = productTable.getSelectedRow();
                String id = productTable.getValueAt(selectedRow, 0).toString();
                Product product = productController.getProductById(id);
                if (product != null) {
                    displayProductData(product);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createForm() {
        // Form panel
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(222, 226, 230)),
                        "Thông tin sản phẩm"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Form fields panel
        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 15, 15));
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Initialize form fields
        idField = createTextField();
        nameField = createTextField();

        // Category combobox
        categoryComboBox = new JComboBox<>();
        categoryComboBox.setFont(new Font("Arial", Font.PLAIN, 14));

        descriptionField = createTextField();

        // Image field with browse button
        imageField = createTextField();
        browseButton = new JButton("Chọn file");
        browseButton.setFont(new Font("Arial", Font.PLAIN, 14));
        browseButton.setBackground(primaryColor);
        browseButton.setForeground(Color.WHITE);
        browseButton.addActionListener(e -> browseImage());

        JPanel imagePanel = new JPanel(new BorderLayout(5, 0));
        imagePanel.setOpaque(false);
        imagePanel.add(imageField, BorderLayout.CENTER);
        imagePanel.add(browseButton, BorderLayout.EAST);

        priceField = createTextField();

        // Add fields to panel
        fieldsPanel.add(createLabelWithIcon("Mã sản phẩm:", "\uD83C\uDD94"));
        fieldsPanel.add(idField);
        fieldsPanel.add(createLabelWithIcon("Tên sản phẩm:", "\uD83C\uDF7A"));
        fieldsPanel.add(nameField);
        fieldsPanel.add(createLabelWithIcon("Loại sản phẩm:", "\uD83D\uDCCA"));
        fieldsPanel.add(categoryComboBox);
        fieldsPanel.add(createLabelWithIcon("Mô tả:", "\uD83D\uDCDD"));
        fieldsPanel.add(descriptionField);
        fieldsPanel.add(createLabelWithIcon("Hình ảnh:", "\uD83D\uDCF7"));
        fieldsPanel.add(imagePanel);
        fieldsPanel.add(createLabelWithIcon("Giá bán:", "\uD83D\uDCB0"));
        fieldsPanel.add(priceField);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setOpaque(false);

        // Initialize buttons
        addButton = createButton("Thêm mới", successColor, "➕");
        addButton.addActionListener(e -> addProduct());

        updateButton = createButton("Cập nhật", warningColor, "✏️");
        updateButton.addActionListener(e -> updateProduct());

        deleteButton = createButton("Xóa", dangerColor, "❌");
        deleteButton.addActionListener(e -> deleteProduct());

        clearButton = createButton("Làm mới form", primaryColor, "🔄");
        clearButton.addActionListener(e -> clearForm());

        // Add buttons to panel
        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(clearButton);

        // Add panels to form
        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Add form to main panel
        add(formPanel, BorderLayout.SOUTH);

        // Load categories to combobox
        loadCategories();
    }

    private void loadCategories() {
        categoryComboBox.removeAllItems();
        List<ProductCategory> categories = categoryController.getAllCategories();
        for (ProductCategory category : categories) {
            categoryComboBox.addItem(category);
        }
    }

    private void browseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn hình ảnh sản phẩm");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imageField.setText(selectedFile.getAbsolutePath());
        }
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return textField;
    }

    private JLabel createLabelWithIcon(String text, String icon) {
        JLabel label = new JLabel(icon + " " + text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(textColor);
        return label;
    }

    private JButton createButton(String text, Color backgroundColor, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(darker(backgroundColor, 0.8f));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    private Color darker(Color color, float factor) {
        return new Color(
                Math.max((int) (color.getRed() * factor), 0),
                Math.max((int) (color.getGreen() * factor), 0),
                Math.max((int) (color.getBlue() * factor), 0),
                color.getAlpha());
    }

    private void refreshProductData() {
        // Clear table
        tableModel.setRowCount(0);

        // Get all products
        List<Product> products = productController.getAllProducts();

        // Add products to table
        for (Product product : products) {
            tableModel.addRow(new Object[] {
                    product.getProductId(),
                    product.getProductName(),
                    product.getCategoryName(),
                    String.format("%,.0f VNĐ", product.getPrice()),
                    product.getDescription()
            });
        }

        // Refresh categories
        loadCategories();

        // Clear form
        clearForm();
    }

    private void displayProductData(Product product) {
        // Display product data in form
        idField.setText(product.getProductId());
        nameField.setText(product.getProductName());
        descriptionField.setText(product.getDescription());
        imageField.setText(product.getImage());
        priceField.setText(String.format("%.0f", product.getPrice()));

        // Select the right category in combo box
        for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
            ProductCategory category = categoryComboBox.getItemAt(i);
            if (category.getCategoryId().equals(product.getCategoryId())) {
                categoryComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    private void clearForm() {
        // Generate new ID
        String newId = productController.generateNewProductId();

        // Set fields
        idField.setText(newId);
        nameField.setText("");
        descriptionField.setText("");
        imageField.setText("");
        priceField.setText("");

        // Select first category if available
        if (categoryComboBox.getItemCount() > 0) {
            categoryComboBox.setSelectedIndex(0);
        }

        // Set focus to name field
        nameField.requestFocus();

        // Deselect any row in the table
        productTable.clearSelection();
    }

    private void addProduct() {
        if (!validateInput()) {
            return;
        }

        // Get product from form
        Product product = getProductFromForm();

        // Save to database
        boolean success = productController.addProduct(product);

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Thêm sản phẩm thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            // Refresh data
            refreshProductData();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Thêm sản phẩm thất bại! Vui lòng thử lại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProduct() {
        int selectedRow = productTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn sản phẩm cần cập nhật!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateInput()) {
            return;
        }

        // Confirm update
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn cập nhật sản phẩm này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Get product from form
        Product product = getProductFromForm();

        // Update in database
        boolean success = productController.updateProduct(product);

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cập nhật sản phẩm thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            // Refresh data
            refreshProductData();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Cập nhật sản phẩm thất bại! Vui lòng thử lại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn sản phẩm cần xóa!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get product ID
        String productId = productTable.getValueAt(selectedRow, 0).toString();

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa sản phẩm này?\nTất cả chi tiết sản phẩm liên quan cũng sẽ bị xóa!",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Delete from database
        boolean success = productController.deleteProduct(productId);

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Xóa sản phẩm thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            // Refresh data
            refreshProductData();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Xóa sản phẩm thất bại! Sản phẩm này có thể đang được sử dụng bởi chi tiết sản phẩm hoặc hóa đơn.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Product getProductFromForm() {
        Product product = new Product();

        // Get basic info
        product.setProductId(idField.getText().trim());
        product.setProductName(nameField.getText().trim());
        product.setDescription(descriptionField.getText().trim());
        product.setImage(imageField.getText().trim());

        // Get price
        try {
            double price = Double.parseDouble(priceField.getText().trim().replace(",", ""));
            product.setPrice(price);
        } catch (NumberFormatException e) {
            product.setPrice(0);
        }

        // Get category
        ProductCategory selectedCategory = (ProductCategory) categoryComboBox.getSelectedItem();
        if (selectedCategory != null) {
            product.setCategoryId(selectedCategory.getCategoryId());
        }

        return product;
    }

    private boolean validateInput() {
        // Validate name
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập tên sản phẩm!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        // Validate category
        if (categoryComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn loại sản phẩm!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            categoryComboBox.requestFocus();
            return false;
        }

        // Validate price
        try {
            String priceText = priceField.getText().trim().replace(",", "");
            if (priceText.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng nhập giá sản phẩm!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                priceField.requestFocus();
                return false;
            }

            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Giá sản phẩm phải lớn hơn 0!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                priceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Giá sản phẩm không hợp lệ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
            return false;
        }

        return true;
    }
}