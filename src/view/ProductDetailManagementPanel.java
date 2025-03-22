package view;

import controller.ProductController;
import controller.ProductDetailController;
import controller.SupplierController;
import model.Product;
import model.ProductDetail;
import model.Supplier;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProductDetailManagementPanel extends JPanel {
    private ProductDetailController detailController;
    private ProductController productController;
    private SupplierController supplierController;

    private JTable detailTable;
    private DefaultTableModel tableModel;

    private JTextField idField;
    private JComboBox<Product> productComboBox;
    private JTextField quantityField;
    private JSpinner importDateSpinner;
    private JSpinner expiryDateSpinner;
    private JTextField batchNumberField;
    private JComboBox<Supplier> supplierComboBox;

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

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public ProductDetailManagementPanel(ProductDetailController detailController,
            ProductController productController,
            SupplierController supplierController) {
        this.detailController = detailController;
        this.productController = productController;
        this.supplierController = supplierController;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(lightColor);

        // Create title panel
        createTitlePanel();

        // Create components
        createTable();
        createForm();

        // Refresh data
        refreshDetailData();
    }

    private void createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(lightColor);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("\uD83D\uDDD3 Quản lý chi tiết sản phẩm");
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
        refreshButton.addActionListener(e -> refreshDetailData());

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

        tableModel.addColumn("Mã chi tiết");
        tableModel.addColumn("Sản phẩm");
        tableModel.addColumn("Số lượng");
        tableModel.addColumn("Ngày nhập");
        tableModel.addColumn("Hạn sử dụng");
        tableModel.addColumn("Số lô");
        tableModel.addColumn("Nhà cung cấp");

        // Create table and add to scroll pane
        detailTable = new JTable(tableModel);
        detailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        detailTable.getTableHeader().setReorderingAllowed(false);
        detailTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        detailTable.setRowHeight(30);
        detailTable.setFont(new Font("Arial", Font.PLAIN, 14));
        detailTable.setGridColor(new Color(230, 230, 230));
        detailTable.setShowGrid(true);

        // Add selection listener
        detailTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && detailTable.getSelectedRow() != -1) {
                int selectedRow = detailTable.getSelectedRow();
                String id = detailTable.getValueAt(selectedRow, 0).toString();
                ProductDetail detail = detailController.getProductDetailById(id);
                if (detail != null) {
                    displayDetailData(detail);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(detailTable);
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
                        "Thông tin chi tiết sản phẩm"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Form fields panel
        JPanel fieldsPanel = new JPanel(new GridLayout(7, 2, 15, 15));
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Initialize form fields
        idField = createTextField();

        // Product combobox
        productComboBox = new JComboBox<>();
        productComboBox.setFont(new Font("Arial", Font.PLAIN, 14));

        quantityField = createTextField();

        // Date spinners
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        SpinnerDateModel importDateModel = new SpinnerDateModel(now, null, null, Calendar.DAY_OF_MONTH);
        importDateSpinner = new JSpinner(importDateModel);
        JSpinner.DateEditor importDateEditor = new JSpinner.DateEditor(importDateSpinner, "dd/MM/yyyy");
        importDateSpinner.setEditor(importDateEditor);
        importDateSpinner.setFont(new Font("Arial", Font.PLAIN, 14));

        calendar.add(Calendar.YEAR, 1); // Default expiry date is 1 year from now
        Date nextYear = calendar.getTime();
        SpinnerDateModel expiryDateModel = new SpinnerDateModel(nextYear, null, null, Calendar.DAY_OF_MONTH);
        expiryDateSpinner = new JSpinner(expiryDateModel);
        JSpinner.DateEditor expiryDateEditor = new JSpinner.DateEditor(expiryDateSpinner, "dd/MM/yyyy");
        expiryDateSpinner.setEditor(expiryDateEditor);
        expiryDateSpinner.setFont(new Font("Arial", Font.PLAIN, 14));

        batchNumberField = createTextField();

        // Supplier combobox
        supplierComboBox = new JComboBox<>();
        supplierComboBox.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add fields to panel
        fieldsPanel.add(createLabelWithIcon("Mã chi tiết:", "\uD83C\uDD94"));
        fieldsPanel.add(idField);
        fieldsPanel.add(createLabelWithIcon("Sản phẩm:", "\uD83C\uDF7A"));
        fieldsPanel.add(productComboBox);
        fieldsPanel.add(createLabelWithIcon("Số lượng:", "\uD83D\uDCCA"));
        fieldsPanel.add(quantityField);
        fieldsPanel.add(createLabelWithIcon("Ngày nhập:", "\uD83D\uDCC5"));
        fieldsPanel.add(importDateSpinner);
        fieldsPanel.add(createLabelWithIcon("Hạn sử dụng:", "\uD83D\uDCC6"));
        fieldsPanel.add(expiryDateSpinner);
        fieldsPanel.add(createLabelWithIcon("Số lô:", "\uD83D\uDCBB"));
        fieldsPanel.add(batchNumberField);
        fieldsPanel.add(createLabelWithIcon("Nhà cung cấp:", "\uD83C\uDFEC"));
        fieldsPanel.add(supplierComboBox);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setOpaque(false);

        // Initialize buttons
        addButton = createButton("Thêm mới", successColor, "➕");
        addButton.addActionListener(e -> addDetail());

        updateButton = createButton("Cập nhật", warningColor, "✏️");
        updateButton.addActionListener(e -> updateDetail());

        deleteButton = createButton("Xóa", dangerColor, "❌");
        deleteButton.addActionListener(e -> deleteDetail());

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

        // Load products and suppliers to comboboxes
        loadProducts();
        loadSuppliers();
    }

    private void loadProducts() {
        productComboBox.removeAllItems();
        List<Product> products = productController.getAllProducts();
        for (Product product : products) {
            productComboBox.addItem(product);
        }
    }

    private void loadSuppliers() {
        supplierComboBox.removeAllItems();
        List<Supplier> suppliers = supplierController.getAllSuppliers();
        for (Supplier supplier : suppliers) {
            supplierComboBox.addItem(supplier);
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

    private void refreshDetailData() {
        // Clear table
        tableModel.setRowCount(0);

        // Get all product details
        List<ProductDetail> details = detailController.getAllProductDetails();

        // Add details to table
        for (ProductDetail detail : details) {
            tableModel.addRow(new Object[] {
                    detail.getDetailId(),
                    detail.getProductName(),
                    detail.getQuantity(),
                    formatDate(detail.getImportDate()),
                    formatDate(detail.getExpiryDate()),
                    detail.getBatchNumber(),
                    detail.getSupplierName()
            });
        }

        // Refresh products and suppliers
        loadProducts();
        loadSuppliers();

        // Clear form
        clearForm();
    }

    private String formatDate(Date date) {
        if (date == null)
            return "";
        return dateFormat.format(date);
    }

    private void displayDetailData(ProductDetail detail) {
        // Display detail data in form
        idField.setText(detail.getDetailId());
        quantityField.setText(String.valueOf(detail.getQuantity()));
        batchNumberField.setText(detail.getBatchNumber());

        // Set dates
        if (detail.getImportDate() != null) {
            importDateSpinner.setValue(detail.getImportDate());
        }
        if (detail.getExpiryDate() != null) {
            expiryDateSpinner.setValue(detail.getExpiryDate());
        }

        // Select the right product in combo box
        for (int i = 0; i < productComboBox.getItemCount(); i++) {
            Product product = productComboBox.getItemAt(i);
            if (product.getProductId().equals(detail.getProductId())) {
                productComboBox.setSelectedIndex(i);
                break;
            }
        }

        // Select the right supplier in combo box
        for (int i = 0; i < supplierComboBox.getItemCount(); i++) {
            Supplier supplier = supplierComboBox.getItemAt(i);
            if (supplier.getSupplierId().equals(detail.getSupplierId())) {
                supplierComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    private void clearForm() {
        // Generate new ID
        String newId = detailController.generateNewDetailId();

        // Set fields
        idField.setText(newId);
        quantityField.setText("");
        batchNumberField.setText("");

        // Set dates
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        importDateSpinner.setValue(now);

        calendar.add(Calendar.YEAR, 1); // Default expiry date is 1 year from now
        Date nextYear = calendar.getTime();
        expiryDateSpinner.setValue(nextYear);

        // Select first product and supplier if available
        if (productComboBox.getItemCount() > 0) {
            productComboBox.setSelectedIndex(0);
        }
        if (supplierComboBox.getItemCount() > 0) {
            supplierComboBox.setSelectedIndex(0);
        }

        // Set focus to quantity field
        quantityField.requestFocus();

        // Deselect any row in the table
        detailTable.clearSelection();
    }

    private void addDetail() {
        if (!validateInput()) {
            return;
        }

        // Get detail from form
        ProductDetail detail = getDetailFromForm();

        // Save to database
        boolean success = detailController.addProductDetail(detail);

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Thêm chi tiết sản phẩm thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            // Refresh data
            refreshDetailData();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Thêm chi tiết sản phẩm thất bại! Vui lòng thử lại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDetail() {
        int selectedRow = detailTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn chi tiết sản phẩm cần cập nhật!",
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
                "Bạn có chắc chắn muốn cập nhật chi tiết sản phẩm này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Get detail from form
        ProductDetail detail = getDetailFromForm();

        // Update in database
        boolean success = detailController.updateProductDetail(detail);

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cập nhật chi tiết sản phẩm thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            // Refresh data
            refreshDetailData();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Cập nhật chi tiết sản phẩm thất bại! Vui lòng thử lại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDetail() {
        int selectedRow = detailTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn chi tiết sản phẩm cần xóa!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get detail ID
        String detailId = detailTable.getValueAt(selectedRow, 0).toString();

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa chi tiết sản phẩm này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Delete from database
        boolean success = detailController.deleteProductDetail(detailId);

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Xóa chi tiết sản phẩm thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            // Refresh data
            refreshDetailData();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Xóa chi tiết sản phẩm thất bại! Chi tiết sản phẩm này có thể đang được sử dụng bởi hóa đơn.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private ProductDetail getDetailFromForm() {
        ProductDetail detail = new ProductDetail();

        // Get basic info
        detail.setDetailId(idField.getText().trim());
        detail.setBatchNumber(batchNumberField.getText().trim());

        // Get quantity
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            detail.setQuantity(quantity);
        } catch (NumberFormatException e) {
            detail.setQuantity(0);
        }

        // Get dates
        detail.setImportDate((Date) importDateSpinner.getValue());
        detail.setExpiryDate((Date) expiryDateSpinner.getValue());

        // Get product
        Product selectedProduct = (Product) productComboBox.getSelectedItem();
        if (selectedProduct != null) {
            detail.setProductId(selectedProduct.getProductId());
        }

        // Get supplier
        Supplier selectedSupplier = (Supplier) supplierComboBox.getSelectedItem();
        if (selectedSupplier != null) {
            detail.setSupplierId(selectedSupplier.getSupplierId());
        }

        return detail;
    }

    private boolean validateInput() {
        // Validate product
        if (productComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn sản phẩm!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            productComboBox.requestFocus();
            return false;
        }

        // Validate supplier
        if (supplierComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn nhà cung cấp!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            supplierComboBox.requestFocus();
            return false;
        }

        // Validate quantity
        try {
            String quantityText = quantityField.getText().trim();
            if (quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng nhập số lượng!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                quantityField.requestFocus();
                return false;
            }

            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Số lượng phải lớn hơn 0!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                quantityField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Số lượng không hợp lệ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            quantityField.requestFocus();
            return false;
        }

        // Validate dates
        Date importDate = (Date) importDateSpinner.getValue();
        Date expiryDate = (Date) expiryDateSpinner.getValue();

        if (importDate == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn ngày nhập!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            importDateSpinner.requestFocus();
            return false;
        }

        if (expiryDate != null && importDate.after(expiryDate)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ngày hết hạn phải sau ngày nhập!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            expiryDateSpinner.requestFocus();
            return false;
        }

        return true;
    }
}