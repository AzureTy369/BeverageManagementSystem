package GUI;

import BUS.ImportReceiptBUS;
import BUS.ImportReceiptDetailBUS;
import BUS.ProductBUS;
import BUS.SupplierBUS;
import BUS.Tool;
import DTO.ProductDTO;
import DTO.SupplierDTO;
import DTO.SupplierProductDTO;
import DTO.ImportReceipt;
import DTO.ImportReceiptDetail;
import GUI.utils.ButtonHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class ImportGoodsGUI extends JPanel {
    private ProductBUS productController = new ProductBUS();
    private SupplierBUS supplierController = new SupplierBUS();
    private ImportReceiptBUS importReceiptController = new ImportReceiptBUS();
    private ImportReceiptDetailBUS importReceiptDetailBUS = new ImportReceiptDetailBUS();

    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JTable selectedTable;
    private DefaultTableModel selectedTableModel;

    private JTextField searchField;
    private JComboBox<SupplierDTO> supplierComboBox;
    private JTextField receiptIdField;
    private JTextField createdByField;
    private JTextField quantityField;
    private JTextField totalAmountField;

    // Colors
    private final Color primaryColor = new Color(0, 123, 255);
    private final Color successColor = new Color(40, 167, 69);
    private final Color dangerColor = new Color(220, 53, 69);
    private final Color lightColor = new Color(248, 249, 250);

    private List<SupplierProductDTO> currentSupplierProducts;

    public ImportGoodsGUI() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(lightColor);

        currentSupplierProducts = new ArrayList<>();
        createGUI();
    }

    private void createGUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ========== TOP PANEL ==========
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Title
        JLabel titleLabel = new JLabel("Nhập hàng từ nhà cung cấp");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(primaryColor);
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Xóa các nút tạo dữ liệu mẫu và reset dữ liệu
        topPanel.add(new JPanel(), BorderLayout.EAST); // Panel trống để thay thế

        add(topPanel, BorderLayout.NORTH);

        // ========== CENTER PANEL ==========
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(1, 3, 20, 0));

        // Supplier Selection
        JPanel supplierPanel = new JPanel(new BorderLayout());
        supplierPanel.setBorder(BorderFactory.createTitledBorder("Nhà cung cấp"));

        // Thêm panel để chứa combobox và nút làm mới
        JPanel supplierControlPanel = new JPanel(new BorderLayout(5, 0));

        supplierComboBox = new JComboBox<>();
        supplierComboBox.addActionListener(e -> {
            if (supplierComboBox.getSelectedItem() != null) {
                String supplierId = ((SupplierDTO) supplierComboBox.getSelectedItem()).getSupplierId();
                loadProductsForSupplier(supplierId);
            }
        });

        // Thêm nút làm mới
        JButton refreshButton = ButtonHelper.createButton("Làm mới", new Color(0x26A69A));
        refreshButton.addActionListener(e -> refreshSupplierAndProducts());

        supplierControlPanel.add(supplierComboBox, BorderLayout.CENTER);
        supplierControlPanel.add(refreshButton, BorderLayout.EAST);

        supplierPanel.add(supplierControlPanel, BorderLayout.CENTER);
        formPanel.add(supplierPanel);

        // Receipt ID
        JPanel receiptIdPanel = new JPanel(new BorderLayout());
        receiptIdPanel.setBorder(BorderFactory.createTitledBorder("Mã phiếu nhập"));
        receiptIdField = new JTextField(generateImportId());
        receiptIdField.setEditable(false);
        receiptIdPanel.add(receiptIdField, BorderLayout.CENTER);
        formPanel.add(receiptIdPanel);

        // Created By
        JPanel createdByPanel = new JPanel(new BorderLayout());
        createdByPanel.setBorder(BorderFactory.createTitledBorder("Người tạo"));
        createdByField = new JTextField("NV001");
        createdByField.setEditable(false);
        createdByPanel.add(createdByField, BorderLayout.CENTER);
        formPanel.add(createdByPanel);

        centerPanel.add(formPanel, BorderLayout.NORTH);

        // ========== TABLES PANEL ==========
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 20, 0));

        // Products Panel
        JPanel productsPanel = new JPanel(new BorderLayout());
        productsPanel.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm"));

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        searchField = new JTextField();
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchProducts();
                }
            }
        });
        JButton searchButton = ButtonHelper.createButton("Tìm kiếm", primaryColor);
        searchButton.addActionListener(e -> searchProducts());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        productsPanel.add(searchPanel, BorderLayout.NORTH);

        // Product Table
        createProductTable();
        JScrollPane productScrollPane = new JScrollPane(productTable);
        productsPanel.add(productScrollPane, BorderLayout.CENTER);

        // Quantity Panel
        JPanel quantityPanel = new JPanel(new BorderLayout());
        quantityPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        quantityField = new JTextField("1");
        JButton addButton = createOutlineButton("Thêm vào phiếu", successColor);
        // Tăng chiều rộng nút Thêm vào phiếu
        addButton.setPreferredSize(new Dimension(150, 35));
        addButton.addActionListener(e -> addToReceipt());
        quantityPanel.add(new JLabel("Số lượng: "), BorderLayout.WEST);
        quantityPanel.add(quantityField, BorderLayout.CENTER);
        quantityPanel.add(addButton, BorderLayout.EAST);
        productsPanel.add(quantityPanel, BorderLayout.SOUTH);

        tablesPanel.add(productsPanel);

        // Selected Products Panel
        JPanel selectedPanel = new JPanel(new BorderLayout());
        selectedPanel.setBorder(BorderFactory.createTitledBorder("Sản phẩm đã chọn"));

        // Selected Products Table
        createSelectedTable();
        JScrollPane selectedScrollPane = new JScrollPane(selectedTable);
        selectedPanel.add(selectedScrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton editButton = createOutlineButton("Sửa số lượng", primaryColor);
        editButton.addActionListener(e -> editQuantity());
        buttonPanel.add(editButton);

        JButton deleteButton = createOutlineButton("Xóa", dangerColor);
        deleteButton.addActionListener(e -> deleteProduct());
        buttonPanel.add(deleteButton);

        selectedPanel.add(buttonPanel, BorderLayout.SOUTH);

        tablesPanel.add(selectedPanel);

        centerPanel.add(tablesPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // ========== BOTTOM PANEL ==========
        JPanel bottomPanel = new JPanel(new BorderLayout(20, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Total Amount Panel
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBorder(BorderFactory.createTitledBorder("Tổng tiền"));
        totalAmountField = new JTextField("0");
        totalAmountField.setEditable(false);
        totalAmountField.setFont(new Font("Arial", Font.BOLD, 16));
        totalAmountField.setHorizontalAlignment(JTextField.RIGHT);
        totalPanel.add(totalAmountField, BorderLayout.CENTER);
        bottomPanel.add(totalPanel, BorderLayout.CENTER);

        // Action Buttons Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton importFromExcelButton = createOutlineButton("Nhập từ Excel", primaryColor);
        importFromExcelButton.addActionListener(e -> importFromExcel());
        actionPanel.add(importFromExcelButton);

        // Sử dụng createOutlineButton để tạo nút Nhập hàng có cùng kích thước
        JButton importButton = createOutlineButton("Nhập hàng", successColor);
        importButton.setFont(new Font("Arial", Font.BOLD, 14));
        importButton.addActionListener(e -> confirmImport());
        actionPanel.add(importButton);

        bottomPanel.add(actionPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // Load data
        loadSuppliers();
    }

    private void createProductTable() {
        String[] columnNames = { "Mã SP", "Tên SP", "Danh mục", "Đơn vị", "Giá nhập", "Tồn kho" };
        productTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(productTableModel);
        productTable.setFont(new Font("Arial", Font.PLAIN, 14));
        productTable.setRowHeight(25);
        productTable.setGridColor(new Color(230, 230, 230));
        productTable.setBackground(Color.WHITE);
        productTable.setSelectionBackground(new Color(173, 216, 230));
        productTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        productTable.getTableHeader().setBackground(Color.WHITE);
        productTable.getTableHeader().setForeground(Color.BLACK);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void createSelectedTable() {
        // Model cho bảng sản phẩm đã chọn
        selectedTableModel = new DefaultTableModel(
                new Object[] { "STT", "Mã SP", "Tên sản phẩm", "Đơn vị tính", "Số lượng", "Đơn giá", "Thành tiền",
                        "Mã loại", "Tên loại" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Chỉ cho phép chỉnh sửa cột số lượng
            }
        };

        selectedTable = new JTable(selectedTableModel);
        selectedTable.getColumnModel().getColumn(0).setPreferredWidth(50); // STT
        selectedTable.getColumnModel().getColumn(1).setPreferredWidth(80); // Mã SP
        selectedTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Tên SP
        selectedTable.getColumnModel().getColumn(3).setPreferredWidth(80); // Đơn vị tính
        selectedTable.getColumnModel().getColumn(4).setPreferredWidth(80); // Số lượng
        selectedTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Đơn giá
        selectedTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Thành tiền
        selectedTable.getColumnModel().getColumn(7).setPreferredWidth(80); // Mã loại
        selectedTable.getColumnModel().getColumn(8).setPreferredWidth(120); // Tên loại

        // Ẩn cột mã loại và tên loại (chỉ để lưu thông tin)
        selectedTable.getColumnModel().getColumn(7).setMinWidth(0);
        selectedTable.getColumnModel().getColumn(7).setMaxWidth(0);
        selectedTable.getColumnModel().getColumn(7).setWidth(0);

        selectedTable.getColumnModel().getColumn(8).setMinWidth(0);
        selectedTable.getColumnModel().getColumn(8).setMaxWidth(0);
        selectedTable.getColumnModel().getColumn(8).setWidth(0);

        selectedTable.setRowHeight(25);
    }

    private JButton createOutlineButton(String text, Color color) {
        return ButtonHelper.createButton(text, color);
    }

    private void loadSuppliers() {
        List<SupplierDTO> suppliers = supplierController.getAllSuppliers();
        supplierComboBox.removeAllItems();
        for (SupplierDTO supplier : suppliers) {
            supplierComboBox.addItem(supplier);
        }

        // Nếu có nhà cung cấp, tải sản phẩm của nhà cung cấp đầu tiên
        if (supplierComboBox.getItemCount() > 0) {
            SupplierDTO firstSupplier = (SupplierDTO) supplierComboBox.getItemAt(0);
            loadProductsForSupplier(firstSupplier.getSupplierId());
        }
    }

    /**
     * Làm mới danh sách nhà cung cấp và sản phẩm
     */
    private void refreshSupplierAndProducts() {
        // Lưu lại nhà cung cấp đang chọn trước khi làm mới
        String selectedSupplierId = null;
        if (supplierComboBox.getSelectedItem() != null) {
            selectedSupplierId = ((SupplierDTO) supplierComboBox.getSelectedItem()).getSupplierId();
        }

        // Làm mới danh sách nhà cung cấp
        loadSuppliers();

        // Nếu trước đó có nhà cung cấp được chọn, cố gắng chọn lại
        if (selectedSupplierId != null) {
            for (int i = 0; i < supplierComboBox.getItemCount(); i++) {
                SupplierDTO supplier = (SupplierDTO) supplierComboBox.getItemAt(i);
                if (supplier.getSupplierId().equals(selectedSupplierId)) {
                    supplierComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Làm mới danh sách sản phẩm
        if (supplierComboBox.getSelectedItem() != null) {
            String supplierId = ((SupplierDTO) supplierComboBox.getSelectedItem()).getSupplierId();
            loadProductsForSupplier(supplierId);
        }

        JOptionPane.showMessageDialog(this,
                "Đã làm mới danh sách nhà cung cấp và sản phẩm.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void searchProducts() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshProductData();
            return;
        }

        // Nếu đã chọn nhà cung cấp, chỉ tìm kiếm trong sản phẩm của nhà cung cấp đó
        if (supplierComboBox.getSelectedItem() != null) {
            String supplierId = getSelectedSupplierId();
            List<SupplierProductDTO> supplierProducts = supplierController.getSupplierProducts(supplierId);
            List<SupplierProductDTO> filteredProducts = new ArrayList<>();

            for (SupplierProductDTO product : supplierProducts) {
                if (product.getProductName().toLowerCase().contains(keyword.toLowerCase()) ||
                        product.getProductId().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredProducts.add(product);
                }
            }

            displaySupplierProducts(filteredProducts);
        } else {
            // Nếu chưa chọn nhà cung cấp, tìm kiếm trong tất cả sản phẩm
            List<ProductDTO> results = productController.searchProducts(keyword);
            displayProducts(results);
        }
    }

    private void refreshProductData() {
        // Nếu có nhà cung cấp được chọn, hiển thị sản phẩm của nhà cung cấp đó
        if (supplierComboBox.getSelectedItem() != null) {
            String supplierId = getSelectedSupplierId();
            if (supplierId != null) {
                loadProductsForSupplier(supplierId);
                return;
            }
        }

        // Nếu không có nhà cung cấp nào được chọn, hiển thị thông báo
        productTableModel.setRowCount(0);
        JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp để xem sản phẩm", "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void displayProducts(List<ProductDTO> products) {
        productTableModel.setRowCount(0);
        for (ProductDTO product : products) {
            productTableModel.addRow(new Object[] {
                    product.getProductId(),
                    product.getProductName(),
                    product.getCategoryName(),
                    product.getUnit(),
                    product.getPrice(),
                    "N/A" // Tồn kho - cần bổ sung sau
            });
        }
    }

    private void displaySupplierProducts(List<SupplierProductDTO> products) {
        productTableModel.setRowCount(0);

        // Lưu lại danh sách sản phẩm của nhà cung cấp hiện tại
        currentSupplierProducts = products;

        for (SupplierProductDTO product : products) {
            // Lấy thông tin danh mục của sản phẩm từ SupplierProductDTO
            String categoryName = product.getCategoryName();
            if (categoryName == null || categoryName.isEmpty()) {
                categoryName = "Chưa phân loại";
            }

            productTableModel.addRow(new Object[] {
                    product.getProductId(),
                    product.getProductName(),
                    categoryName,
                    product.getUnit(),
                    product.getPrice(),
                    "N/A" // Tồn kho - cần bổ sung sau
            });
        }
    }

    /**
     * Thêm sản phẩm vào phiếu nhập
     */
    private void addToReceipt() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để thêm vào phiếu nhập");
            return;
        }

        try {
            // Lấy thông tin sản phẩm từ bảng
            String productId = productTable.getValueAt(selectedRow, 0).toString();
            String productName = productTable.getValueAt(selectedRow, 1).toString();
            String unit = "";
            try {
                unit = productTable.getValueAt(selectedRow, 3).toString();
            } catch (Exception e) {
                // Nếu không lấy được unit từ bảng, sẽ dùng giá trị mặc định
                unit = "Cái";
            }

            // Lấy số lượng từ trường quantityField
            int quantity;
            try {
                quantity = Integer.parseInt(quantityField.getText());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ");
                return;
            }

            // Lấy giá sản phẩm từ nhà cung cấp
            SupplierProductDTO supplierProduct = null;

            // Tìm sản phẩm trong danh sách sản phẩm của nhà cung cấp
            if (currentSupplierProducts != null) {
                for (SupplierProductDTO product : currentSupplierProducts) {
                    if (product.getProductId().equals(productId)) {
                        supplierProduct = product;
                        break;
                    }
                }
            }

            // Kiểm tra đã chọn sản phẩm chưa
            if (supplierProduct == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin giá của sản phẩm này");
                return;
            }

            // Lấy giá từ supplierProduct
            double price = supplierProduct.getPrice();
            double totalPrice = price * quantity;

            // Lấy thông tin danh mục
            String categoryId = supplierProduct.getCategoryId();
            String categoryName = supplierProduct.getCategoryName();

            System.out.println("Danh mục ban đầu từ supplierProduct: " + categoryId + " - " + categoryName);

            // Nếu danh mục chưa được thiết lập trong supplier product, lấy từ product
            if (categoryId == null || categoryId.isEmpty()) {
                ProductDTO product = productController.getProductById(productId);
                if (product != null) {
                    categoryId = product.getCategoryId();
                    categoryName = product.getCategoryName();
                    System.out.println("Danh mục từ product: " + categoryId + " - " + categoryName);
                }
            }

            // Nếu vẫn không có danh mục, sử dụng giá trị mặc định
            if (categoryId == null || categoryId.isEmpty()) {
                categoryId = "LSP001";
                categoryName = "Chưa phân loại";
                System.out.println("Sử dụng danh mục mặc định: " + categoryId + " - " + categoryName);
            }

            System.out
                    .println("Thêm sản phẩm với mã: " + productId + ", danh mục: " + categoryId + " - " + categoryName);

            // Kiểm tra sản phẩm đã tồn tại trong bảng chưa
            for (int i = 0; i < selectedTableModel.getRowCount(); i++) {
                if (selectedTableModel.getValueAt(i, 1).equals(productId)) {
                    // Cập nhật số lượng
                    int currentQuantity = Integer.parseInt(selectedTableModel.getValueAt(i, 4).toString());
                    int newQuantity = currentQuantity + quantity;

                    // Cập nhật số lượng và tổng tiền
                    selectedTableModel.setValueAt(newQuantity, i, 4);
                    selectedTableModel.setValueAt(price * newQuantity, i, 6);

                    // Cập nhật tổng tiền phiếu
                    updateTotalAmount();
                    return;
                }
            }

            // Thêm dòng mới vào bảng
            selectedTableModel.addRow(new Object[] {
                    selectedTableModel.getRowCount() + 1, // STT
                    productId,
                    productName,
                    unit,
                    quantity,
                    price,
                    totalPrice,
                    categoryId,
                    categoryName
            });

            // Cập nhật tổng tiền phiếu nhập
            updateTotalAmount();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTotalAmount() {
        double total = 0;
        // Cột 6 là cột thành tiền, và giá trị được lưu trực tiếp không qua định dạng
        for (int i = 0; i < selectedTableModel.getRowCount(); i++) {
            total += Double.parseDouble(selectedTableModel.getValueAt(i, 6).toString());
        }
        totalAmountField.setText(String.format("%,.0f", total));
    }

    private void editQuantity() {
        int selectedRow = selectedTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để sửa số lượng", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy giá trị số lượng hiện tại - cột 4 là số lượng
        String currentValue = selectedTable.getValueAt(selectedRow, 4).toString();

        String input = JOptionPane.showInputDialog(this, "Nhập số lượng mới:", currentValue);
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        if (!Tool.isInt(input)) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên dương", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantity = Integer.parseInt(input);
        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lấy đơn giá từ cột 5
        double price = Double.parseDouble(selectedTable.getValueAt(selectedRow, 5).toString());
        double total = price * quantity;

        // Cập nhật số lượng và thành tiền
        selectedTableModel.setValueAt(quantity, selectedRow, 4);
        selectedTableModel.setValueAt(total, selectedRow, 6);

        updateTotalAmount();
    }

    private void deleteProduct() {
        int selectedRow = selectedTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xóa", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        selectedTableModel.removeRow(selectedRow);

        // Cập nhật lại STT
        for (int i = 0; i < selectedTableModel.getRowCount(); i++) {
            selectedTableModel.setValueAt(i + 1, i, 0);
        }

        updateTotalAmount();
    }

    /**
     * Xác nhận nhập hàng và lưu thông tin phiếu nhập
     */
    private void confirmImport() {
        // Kiểm tra đã chọn nhà cung cấp chưa
        SupplierDTO selectedSupplier = (SupplierDTO) supplierComboBox.getSelectedItem();
        if (selectedSupplier == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp");
            return;
        }

        // Kiểm tra sản phẩm đã chọn
        if (selectedTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một sản phẩm để nhập");
            return;
        }

        try {
            // Đảm bảo tất cả sản phẩm tồn tại trong database
            ensureProductsExistInDatabase();

            // Tạo mã phiếu nhập
            String importId = receiptIdField.getText();

            // Lấy thông tin người tạo
            String createdBy = createdByField.getText();

            // Lấy thông tin nhà cung cấp
            String supplierId = selectedSupplier.getSupplierId();

            // Tính tổng tiền
            double totalAmount = 0;
            for (int i = 0; i < selectedTableModel.getRowCount(); i++) {
                // Lấy thành tiền từ bảng, đồng thời chuyển đổi thành số
                Object val = selectedTableModel.getValueAt(i, 6);
                if (val instanceof String) {
                    String valueStr = ((String) val).replaceAll("[^\\d.]", "");
                    totalAmount += Double.parseDouble(valueStr);
                } else if (val instanceof Number) {
                    totalAmount += ((Number) val).doubleValue();
                }
            }

            // Kiểm tra nếu tổng tiền vượt quá giới hạn
            if (totalAmount > 999999.99) {
                totalAmount = 999999.99; // Giới hạn giá trị tối đa
                JOptionPane.showMessageDialog(this,
                        "Tổng tiền vượt quá giới hạn cho phép và đã được điều chỉnh xuống 999,999.99.\n" +
                                "Bạn nên chia nhỏ phiếu nhập để tránh lỗi dữ liệu.",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }

            // Định dạng số với dấu phân cách phần thập phân là dấu chấm (.)
            String formattedTotal = String.format(java.util.Locale.US, "%.2f", totalAmount);

            // Tạo phiếu nhập
            ImportReceipt receipt = new ImportReceipt(
                    importId,
                    supplierId,
                    createdBy,
                    new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()),
                    formattedTotal,
                    "");

            receipt.setStatus("Đang xử lý"); // Đảm bảo cả trường status cũng được thiết lập

            // Lưu phiếu nhập vào cơ sở dữ liệu
            boolean success = importReceiptController.insertImportReceipt(receipt);

            if (!success) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi tạo phiếu nhập.\n" +
                                "Vui lòng kiểm tra dữ liệu nhập và đảm bảo tổng tiền không vượt quá giới hạn.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tạo danh sách chi tiết phiếu nhập
            List<ImportReceiptDetail> details = new ArrayList<>();

            System.out.println("Đang tạo chi tiết phiếu nhập với " + selectedTableModel.getRowCount() + " sản phẩm");

            // In ra tên cột để debug
            System.out.println("Cấu trúc bảng sản phẩm đã chọn:");
            for (int i = 0; i < selectedTableModel.getColumnCount(); i++) {
                System.out.println("Cột " + i + ": " + selectedTable.getColumnName(i));
            }

            // Thêm chi tiết phiếu nhập
            boolean hasInvalidPrice = false;
            for (int i = 0; i < selectedTableModel.getRowCount(); i++) {
                String productId = selectedTableModel.getValueAt(i, 1).toString();
                String productName = selectedTableModel.getValueAt(i, 2).toString();

                // Chuyển đổi số lượng và giá thành số
                int quantity = 0;
                double price = 0;
                double productTotal = 0;

                Object quantityObj = selectedTableModel.getValueAt(i, 4);
                if (quantityObj != null) {
                    if (quantityObj instanceof String) {
                        quantity = Integer.parseInt(((String) quantityObj).replaceAll("[^\\d]", ""));
                    } else {
                        quantity = ((Number) quantityObj).intValue();
                    }
                }

                Object priceObj = selectedTableModel.getValueAt(i, 5);
                if (priceObj != null) {
                    if (priceObj instanceof String) {
                        String priceStr = ((String) priceObj).replaceAll("[^\\d.]", "");
                        if (!priceStr.isEmpty()) {
                            price = Double.parseDouble(priceStr);
                            // Kiểm tra và giới hạn giá nếu cần
                            if (price > 999999.99) {
                                price = 999999.99;
                                hasInvalidPrice = true;
                            }
                        }
                    } else {
                        price = ((Number) priceObj).doubleValue();
                        // Kiểm tra và giới hạn giá nếu cần
                        if (price > 999999.99) {
                            price = 999999.99;
                            hasInvalidPrice = true;
                        }
                    }
                }

                // Tính toán lại tổng tiền của sản phẩm
                productTotal = price * quantity;
                // Giới hạn thành tiền của sản phẩm nếu cần
                if (productTotal > 999999.99) {
                    productTotal = 999999.99;
                    hasInvalidPrice = true;
                }

                // Lấy thông tin danh mục
                String categoryId = "";
                String categoryName = "";
                try {
                    categoryId = selectedTableModel.getValueAt(i, 7) != null
                            ? selectedTableModel.getValueAt(i, 7).toString()
                            : "";
                    categoryName = selectedTableModel.getValueAt(i, 8) != null
                            ? selectedTableModel.getValueAt(i, 8).toString()
                            : "";
                } catch (Exception e) {
                    System.out.println("Không thể đọc thông tin danh mục từ bảng. Lỗi: " + e.getMessage());
                }

                // Nếu không có danh mục từ bảng, lấy từ cơ sở dữ liệu
                if (categoryId == null || categoryId.isEmpty()) {
                    ProductDTO product = productController.getProductById(productId);
                    if (product != null) {
                        categoryId = product.getCategoryId();
                        categoryName = product.getCategoryName();
                    }
                }

                // Nếu vẫn không có danh mục, sử dụng giá trị mặc định
                if (categoryId == null || categoryId.isEmpty()) {
                    categoryId = "LSP001";
                    categoryName = "Chưa phân loại";
                }

                System.out.println("Chi tiết phiếu nhập: Mã SP=" + productId +
                        ", Tên=" + productName +
                        ", SL=" + quantity +
                        ", Giá=" + price +
                        ", Tổng=" + productTotal +
                        ", Mã loại=" + categoryId +
                        ", Tên loại=" + categoryName);

                ImportReceiptDetail detail = new ImportReceiptDetail(importId, productId, quantity, price,
                        productTotal, categoryId, categoryName);
                details.add(detail);
            }

            if (hasInvalidPrice) {
                JOptionPane.showMessageDialog(this,
                        "Cảnh báo: Một số giá trị giá hoặc thành tiền vượt quá giới hạn và đã được điều chỉnh xuống.\n"
                                +
                                "Phiếu nhập đã được tạo nhưng các giá trị có thể không chính xác như mong đợi.",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }

            // Lưu chi tiết phiếu nhập vào cơ sở dữ liệu
            boolean detailsSuccess = importReceiptDetailBUS.insertMultipleImportReceiptDetails(details);

            if (!detailsSuccess) {
                JOptionPane.showMessageDialog(this,
                        "Phiếu nhập đã được tạo nhưng có lỗi khi thêm chi tiết phiếu.\n" +
                                "Vui lòng kiểm tra lại trong danh sách phiếu nhập.",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Tạo phiếu nhập thành công.\nPhiếu nhập đang ở trạng thái 'Đang xử lý'. Sản phẩm sẽ được cập nhật vào tồn kho khi phiếu nhập được chuyển sang trạng thái 'Đã hoàn thành'.",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

                // Reset form
                resetForm();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xử lý phiếu nhập: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Đảm bảo tất cả sản phẩm tồn tại trong CSDL trước khi thêm vào phiếu nhập
     */
    private void ensureProductsExistInDatabase() {
        for (int i = 0; i < selectedTableModel.getRowCount(); i++) {
            String productId = selectedTableModel.getValueAt(i, 1).toString();
            String productName = selectedTableModel.getValueAt(i, 2).toString();

            // Lấy đơn vị từ bảng (nếu có)
            String unit = "Cái"; // Đơn vị mặc định
            try {
                unit = selectedTableModel.getValueAt(i, 3).toString();
            } catch (Exception e) {
                System.out.println("Không tìm thấy cột đơn vị, sử dụng mặc định: " + unit);
            }

            // Kiểm tra sản phẩm có tồn tại trong CSDL không
            ProductDTO existingProduct = productController.getProductById(productId);

            if (existingProduct == null) {
                System.out.println("Sản phẩm " + productId + " chưa tồn tại trong CSDL, đang tạo mới...");

                // Tạo sản phẩm mới với thông tin cơ bản
                ProductDTO newProduct = new ProductDTO();
                newProduct.setProductId(productId);
                newProduct.setProductName(productName);
                newProduct.setUnit(unit);
                newProduct.setCategoryId("LSP001"); // Sử dụng danh mục mặc định

                // Thêm sản phẩm vào CSDL
                boolean success = productController.addProduct(newProduct);

                if (success) {
                    System.out.println("Đã tạo thành công sản phẩm " + productId);
                } else {
                    System.out.println("Lỗi khi tạo sản phẩm " + productId);

                    // Hiển thị dialog xác nhận tạo sản phẩm
                    int choice = JOptionPane.showConfirmDialog(
                            this,
                            "Sản phẩm " + productId + " không tồn tại trong CSDL và không thể tự động tạo.\n" +
                                    "Bạn có muốn tiếp tục nhập hàng không?",
                            "Xác nhận",
                            JOptionPane.YES_NO_OPTION);

                    if (choice != JOptionPane.YES_OPTION) {
                        throw new RuntimeException("Đã hủy nhập hàng do sản phẩm không tồn tại.");
                    }
                }
            }
        }
    }

    private void resetForm() {
        // Tạo mã phiếu nhập mới
        receiptIdField.setText(generateImportId());

        // Xóa danh sách sản phẩm đã chọn
        selectedTableModel.setRowCount(0);

        // Reset tổng tiền
        totalAmountField.setText("0");

        // Reset số lượng
        quantityField.setText("1");

        // Nếu có nhà cung cấp được chọn, tải lại danh sách sản phẩm
        if (supplierComboBox.getSelectedItem() != null) {
            String supplierId = getSelectedSupplierId();
            loadProductsForSupplier(supplierId);
        }
    }

    private void importFromExcel() {

        JOptionPane.showMessageDialog(this, "Chức năng nhập từ Excel đang được phát triển", "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadProductsForSupplier(String supplierId) {
        if (supplierId == null || supplierId.isEmpty()) {
            return;
        }

        System.out.println("Đang tải sản phẩm của nhà cung cấp: " + supplierId);

        // Lấy danh sách sản phẩm của nhà cung cấp
        List<SupplierProductDTO> products = supplierController.getSupplierProducts(supplierId);

        // Lưu danh sách sản phẩm hiện tại
        currentSupplierProducts = products;

        // Hiển thị sản phẩm trong bảng
        displaySupplierProducts(products);

        if (products.isEmpty()) {
            System.out.println("Không có sản phẩm nào của nhà cung cấp này");
        } else {
            System.out.println("Đã tìm thấy " + products.size() + " sản phẩm");
        }
    }

    private String getSelectedSupplierId() {
        SupplierDTO selectedSupplier = (SupplierDTO) supplierComboBox.getSelectedItem();
        return selectedSupplier != null ? selectedSupplier.getSupplierId() : null;
    }

    private String generateImportId() {
        // Lấy danh sách phiếu nhập hiện tại
        List<ImportReceipt> importReceipts = importReceiptController.getAllImportReceipts();

        // Tìm số lớn nhất hiện tại
        int maxId = 0;
        for (ImportReceipt receipt : importReceipts) {
            String id = receipt.getImportId();
            if (id.startsWith("PN")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > maxId) {
                        maxId = num;
                    }
                } catch (NumberFormatException e) {
                    // Bỏ qua nếu không phải định dạng hợp lệ
                }
            }
        }

        // Tạo mã mới
        return String.format("PN%03d", maxId + 1);
    }
}