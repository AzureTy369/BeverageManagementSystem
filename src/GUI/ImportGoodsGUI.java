package GUI;

import BUS.EmployeeBUS;
import BUS.ImportReceiptBUS;
import BUS.ImportReceiptDetailBUS;
import BUS.InventoryBUS;
import BUS.ProductBUS;
import BUS.SupplierBUS;
import BUS.Tool;
import DTO.ProductDTO;
import DTO.SupplierDTO;
import DTO.EmployeeDTO;
import DTO.SupplierProductDTO;
import DTO.ImportReceipt;
import DTO.ImportReceiptDetail;
import BUS.Tool;
import DAO.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImportGoodsGUI extends JPanel {
    private ProductBUS productController = new ProductBUS();
    private SupplierBUS supplierController = new SupplierBUS();
    private EmployeeBUS employeeController = new EmployeeBUS();
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

    // Format tiền tệ
    private java.text.DecimalFormat dfVND = new java.text.DecimalFormat("#,###");

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

        // Create Sample Data Button
        JButton createSampleButton = new JButton("Tạo dữ liệu mẫu");
        createSampleButton.setFont(new Font("Arial", Font.BOLD, 14));
        createSampleButton.setBackground(new Color(108, 117, 125));
        createSampleButton.setForeground(Color.WHITE);
        createSampleButton.setFocusPainted(false);
        createSampleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createSampleButton.addActionListener(e -> createSampleData());

        // Add a Reset Data Button
        JButton resetDataButton = new JButton("Xóa & tạo lại dữ liệu");
        resetDataButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetDataButton.setBackground(new Color(220, 53, 69));
        resetDataButton.setForeground(Color.WHITE);
        resetDataButton.setFocusPainted(false);
        resetDataButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetDataButton.addActionListener(e -> resetAndCreateData());

        // Add buttons to panel
        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topButtonPanel.add(resetDataButton);
        topButtonPanel.add(createSampleButton);

        topPanel.add(topButtonPanel, BorderLayout.EAST);

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
        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setBackground(primaryColor);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        createdByField = new JTextField("NV001"); // TODO: Get current user ID
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
        JButton searchButton = new JButton("Tìm kiếm");
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

        JButton importButton = new JButton("Nhập hàng");
        importButton.setFont(new Font("Arial", Font.BOLD, 14));
        importButton.setBackground(successColor);
        importButton.setForeground(Color.WHITE);
        importButton.setFocusPainted(false);
        importButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        String[] columnNames = { "STT", "Mã SP", "Tên SP", "Đơn vị", "Số lượng", "Đơn giá", "Thành tiền" };
        selectedTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        selectedTable = new JTable(selectedTableModel);
        selectedTable.setFont(new Font("Arial", Font.PLAIN, 14));
        selectedTable.setRowHeight(25);
        selectedTable.setGridColor(new Color(230, 230, 230));
        selectedTable.setBackground(Color.WHITE);
        selectedTable.setSelectionBackground(new Color(173, 216, 230));
        selectedTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        selectedTable.getTableHeader().setBackground(Color.WHITE);
        selectedTable.getTableHeader().setForeground(Color.BLACK);
        selectedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private JButton createOutlineButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(255, 255, 255));
        button.setForeground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(color, 1));
        button.setToolTipText(text);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 35));

        // Hiệu ứng hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color);
                button.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(255, 255, 255));
                button.setForeground(color);
            }
        });

        return button;
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
            String productName = productTable.getValueAt(selectedRow, 1).toString();
            String productId = productTable.getValueAt(selectedRow, 0).toString();
            String unit = "";
            try {
                unit = productTable.getValueAt(selectedRow, 3).toString();
            } catch (Exception e) {
                // Nếu không lấy được unit từ bảng, sẽ dùng giá trị mặc định
                unit = "Cái";
            }

            // Kiểm tra sản phẩm đã có trong phiếu chưa
            for (int i = 0; i < selectedTableModel.getRowCount(); i++) {
                if (selectedTableModel.getValueAt(i, 1).equals(productId)) {
                    JOptionPane.showMessageDialog(this, "Sản phẩm đã có trong phiếu nhập");
                    return;
                }
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

            if (supplierProduct == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin sản phẩm của nhà cung cấp");
                return;
            }

            // Sử dụng unit từ supplierProduct nếu có
            if (supplierProduct.getUnit() != null && !supplierProduct.getUnit().isEmpty()) {
                unit = supplierProduct.getUnit();
            }

            double price = supplierProduct.getPrice();
            double total = price * quantity;

            // Xác định mã sản phẩm gốc nếu đang dùng mã sản phẩm của nhà cung cấp
            String baseProductId = productId;

            // Nếu mã sản phẩm là của nhà cung cấp (SPNCC), cần lấy mã sản phẩm gốc
            if (productId.startsWith("SPNCC")) {
                // Mã sản phẩm gốc là 3 số cuối của mã SPNCC
                // Ví dụ: SPNCC00101 -> SP001
                String numberPart = productId.substring(productId.length() - 3);
                baseProductId = "SP" + numberPart;

                // Kiểm tra xem sản phẩm gốc có tồn tại không
                ProductDTO baseProduct = productController.getProductById(baseProductId);
                if (baseProduct == null) {
                    System.out.println("Cảnh báo: Không tìm thấy sản phẩm gốc có mã " + baseProductId);
                } else {
                    System.out.println("Đã tìm thấy sản phẩm gốc: " + baseProduct.getProductName());
                }
            }

            // Thêm vào bảng sản phẩm đã chọn
            Object[] rowData = {
                    selectedTableModel.getRowCount() + 1, // STT
                    baseProductId, // Mã sản phẩm
                    productName, // Tên sản phẩm
                    unit, // Đơn vị tính
                    quantity, // Số lượng
                    dfVND.format(price), // Đơn giá
                    dfVND.format(total) // Thành tiền
            };

            System.out.println("Thêm sản phẩm vào phiếu nhập: " +
                    "Mã SP=" + baseProductId +
                    ", Tên=" + productName +
                    ", Đơn vị=" + unit +
                    ", SL=" + quantity +
                    ", Giá=" + price +
                    ", Tổng=" + total);

            selectedTableModel.addRow(rowData);

            // Cập nhật tổng tiền
            updateTotalAmount();

            // Reset số lượng về 1 sau khi thêm
            quantityField.setText("1");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTotalAmount() {
        double total = 0;
        for (int i = 0; i < selectedTableModel.getRowCount(); i++) {
            total += Double.parseDouble(selectedTableModel.getValueAt(i, 5).toString());
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

        String input = JOptionPane.showInputDialog(this, "Nhập số lượng mới:",
                selectedTable.getValueAt(selectedRow, 3));
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

        double price = Double.parseDouble(selectedTable.getValueAt(selectedRow, 4).toString());

        selectedTableModel.setValueAt(quantity, selectedRow, 3);
        selectedTableModel.setValueAt(price * quantity, selectedRow, 5);

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
        if (selectedTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một sản phẩm vào phiếu nhập");
            return;
        }

        String supplierId = getSelectedSupplierId();
        if (supplierId == null || supplierId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp");
            return;
        }

        // Đảm bảo tất cả sản phẩm tồn tại trong CSDL
        try {
            ensureProductsExistInDatabase();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }

        // Lấy thông tin phiếu nhập
        String importId = receiptIdField.getText();
        String employeeId = createdByField.getText();
        String totalAmount = totalAmountField.getText().replace(",", "").replace("VNĐ", "").trim();

        // Lấy ngày hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String importDate = sdf.format(new Date());

        // Tạo đối tượng phiếu nhập với trạng thái mặc định "Đang xử lý"
        ImportReceipt receipt = new ImportReceipt(
                importId, supplierId, employeeId, importDate, totalAmount, "Đang xử lý");
        receipt.setStatus("Đang xử lý"); // Đảm bảo cả trường status cũng được thiết lập

        // Lưu phiếu nhập vào cơ sở dữ liệu
        boolean success = importReceiptController.insertImportReceipt(receipt);

        if (!success) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tạo phiếu nhập");
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
        for (int i = 0; i < selectedTableModel.getRowCount(); i++) {
            // STT ở cột 0
            // Mã SP ở cột 1
            // Tên SP ở cột 2
            // Đơn vị ở cột 3
            // Số lượng ở cột 4
            // Đơn giá ở cột 5
            // Thành tiền ở cột 6

            String productId = selectedTableModel.getValueAt(i, 1).toString();
            String productName = selectedTableModel.getValueAt(i, 2).toString();
            int quantity = Integer.parseInt(selectedTableModel.getValueAt(i, 4).toString());

            // Xử lý chuỗi giá tiền
            String priceStr = selectedTableModel.getValueAt(i, 5).toString()
                    .replace(",", "").replace("VNĐ", "").trim();
            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                System.out.println("Lỗi chuyển đổi giá: " + priceStr);
                price = 0;
            }

            double total = price * quantity;

            System.out.println("Chi tiết phiếu nhập: Mã SP=" + productId +
                    ", Tên=" + productName +
                    ", SL=" + quantity +
                    ", Giá=" + price +
                    ", Tổng=" + total);

            ImportReceiptDetail detail = new ImportReceiptDetail(importId, productId, quantity, price, total);
            details.add(detail);
        }

        // Lưu chi tiết phiếu nhập vào cơ sở dữ liệu
        boolean detailsSuccess = importReceiptDetailBUS.insertMultipleImportReceiptDetails(details);

        if (!detailsSuccess) {
            JOptionPane.showMessageDialog(this,
                    "Phiếu nhập đã được tạo nhưng có lỗi khi thêm chi tiết phiếu.\n" +
                            "Vui lòng kiểm tra lại trong danh sách phiếu nhập.");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Tạo phiếu nhập thành công.\nPhiếu nhập đang ở trạng thái 'Đang xử lý'. Sản phẩm sẽ được cập nhật vào tồn kho khi phiếu nhập được chuyển sang trạng thái 'Đã hoàn thành'.");

            // Reset form
            resetForm();
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
        // TODO: Implement import from Excel
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

    private void createSampleData() {
        try {
            // Tạo mẫu nhà cung cấp
            SupplierBUS supplierBUS = new SupplierBUS();
            String supplierId = "NCC002";
            if (supplierBUS.getSupplierById(supplierId) == null) {
                supplierBUS.addSupplier(supplierId, "Công ty Pepsi", "HCM", "0987654321", "contact@pepsi.com");
                System.out.println("Đã tạo nhà cung cấp mẫu: " + supplierId);
            }

            // Thêm các sản phẩm vào bảng sanpham (nếu chưa có)
            createSampleProducts();

            // Tạo mẫu sản phẩm của nhà cung cấp
            if (supplierController.getSupplierProducts(supplierId).isEmpty()) {
                SupplierProductDTO product1 = new SupplierProductDTO(
                        "SP00201", supplierId, "Pepsi Lon", "Lon", "Nước ngọt Pepsi", 11000.0);
                SupplierProductDTO product2 = new SupplierProductDTO(
                        "SP00202", supplierId, "Mountain Dew", "Chai", "Nước ngọt Mountain Dew", 12000.0);
                SupplierProductDTO product3 = new SupplierProductDTO(
                        "SP00203", supplierId, "7Up", "Chai", "Nước ngọt 7Up", 10000.0);

                supplierController.addSupplierProduct(product1);
                supplierController.addSupplierProduct(product2);
                supplierController.addSupplierProduct(product3);

                System.out.println("Đã tạo sản phẩm nhà cung cấp mẫu");
            }

            // Cập nhật giao diện
            loadSuppliers();
            JOptionPane.showMessageDialog(this, "Đã tạo dữ liệu mẫu thành công", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            System.out.println("Lỗi khi tạo dữ liệu mẫu: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tạo dữ liệu mẫu: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createSampleProducts() {
        try {
            ProductBUS productBUS = new ProductBUS();

            // Kiểm tra và tạo loại sản phẩm mẫu nếu chưa có
            if (productBUS.getCategoryById("LSP001") == null) {
                productBUS.addCategory("LSP001", "Nước giải khát", "Các loại đồ uống");
                System.out.println("Đã tạo loại sản phẩm mẫu: LSP001 - Nước giải khát");
            }

            // Kiểm tra và tạo các sản phẩm mẫu nếu chưa có
            String[][] products = {
                    { "SP001", "Nước suối Aquafina", "Chai", "8000", "LSP001" },
                    { "SP002", "Coca Cola", "Lon", "12000", "LSP001" },
                    { "SP003", "Sting đỏ", "Chai", "10000", "LSP001" },
                    { "SP004", "Pepsi", "Lon", "11000", "LSP001" },
                    { "SP005", "Trà xanh 0 độ", "Chai", "13000", "LSP001" }
            };

            // Xóa tất cả sản phẩm hiện có (tùy chọn)
            if (JOptionPane.showConfirmDialog(this,
                    "Bạn có muốn xóa tất cả sản phẩm hiện có và tạo mới không?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                System.out.println("Xóa tất cả sản phẩm hiện có...");
                Connection conn = DBConnection.getConnection();
                if (conn != null) {
                    Statement stmt = conn.createStatement();
                    // Tắt kiểm tra khóa ngoại tạm thời
                    stmt.execute("SET FOREIGN_KEY_CHECKS=0");

                    // Xóa sản phẩm
                    int deleted = stmt.executeUpdate("DELETE FROM sanpham");
                    System.out.println("Đã xóa " + deleted + " sản phẩm");

                    // Bật lại kiểm tra khóa ngoại
                    stmt.execute("SET FOREIGN_KEY_CHECKS=1");
                    stmt.close();
                }
            }

            int addedCount = 0;
            for (String[] product : products) {
                // Luôn tạo mới các sản phẩm sau khi xóa
                ProductDTO newProduct = new ProductDTO();
                newProduct.setProductId(product[0]);
                newProduct.setProductName(product[1]);
                newProduct.setUnit(product[2]);
                newProduct.setPrice(Double.parseDouble(product[3]));
                newProduct.setCategoryId(product[4]);

                if (productBUS.getProductById(product[0]) == null) {
                    productBUS.addProduct(newProduct);
                } else {
                    productBUS.updateProduct(newProduct);
                }

                addedCount++;
                System.out.println("Đã tạo/cập nhật sản phẩm mẫu: " + product[0] + " - " + product[1]);
            }

            System.out.println("Đã thêm/cập nhật " + addedCount + " sản phẩm mẫu vào CSDL");

            // Kiểm tra xem sản phẩm đã được thêm thành công chưa
            System.out.println("Danh sách sản phẩm sau khi thêm:");
            List<ProductDTO> allProducts = productBUS.getAllProducts();
            for (ProductDTO p : allProducts) {
                System.out.println(" - " + p.getProductId() + ": " + p.getProductName());
            }

            if (allProducts.isEmpty()) {
                System.out.println("CẢNH BÁO: Vẫn không có sản phẩm nào trong CSDL sau khi thêm!");
                JOptionPane.showMessageDialog(this,
                        "Không thể thêm sản phẩm vào CSDL. Vui lòng kiểm tra lại kết nối và quyền truy cập.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            System.out.println("Lỗi khi tạo sản phẩm mẫu: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tạo sản phẩm mẫu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xóa dữ liệu nhà cung cấp và phiếu nhập
     */
    private void clearAllData() {
        try {
            // Xác nhận trước khi xóa
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa TẤT CẢ dữ liệu phiếu nhập, chi tiết phiếu nhập,\n" +
                            "sản phẩm nhà cung cấp và nhà cung cấp không?\n\n" +
                            "LƯU Ý: Hành động này không thể hoàn tác!",
                    "Xác nhận xóa dữ liệu", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Thực hiện SQL trực tiếp để xóa dữ liệu
            try {
                // Sử dụng Statement để xóa dữ liệu
                Connection conn = null;
                try {
                    // Lấy connection
                    conn = DBConnection.getConnection();

                    if (conn != null) {
                        Statement stmt = conn.createStatement();

                        // Tắt kiểm tra khóa ngoại tạm thời
                        stmt.execute("SET FOREIGN_KEY_CHECKS=0");

                        // Xóa dữ liệu từ các bảng theo thứ tự
                        System.out.println("Xóa dữ liệu từ bảng ChiTietPhieuNhap...");
                        int detailsDeleted = stmt.executeUpdate("DELETE FROM chitietphieunhap");
                        System.out.println("Đã xóa " + detailsDeleted + " chi tiết phiếu nhập");

                        System.out.println("Xóa dữ liệu từ bảng PhieuNhap...");
                        int receiptsDeleted = stmt.executeUpdate("DELETE FROM phieunhap");
                        System.out.println("Đã xóa " + receiptsDeleted + " phiếu nhập");

                        System.out.println("Xóa dữ liệu từ bảng SanPhamNCC...");
                        int supplierProductsDeleted = stmt.executeUpdate("DELETE FROM SanPhamNCC");
                        System.out.println("Đã xóa " + supplierProductsDeleted + " sản phẩm nhà cung cấp");

                        System.out.println("Xóa dữ liệu từ bảng NhaCungCap...");
                        int suppliersDeleted = stmt.executeUpdate("DELETE FROM NhaCungCap");
                        System.out.println("Đã xóa " + suppliersDeleted + " nhà cung cấp");

                        // Bật lại kiểm tra khóa ngoại
                        stmt.execute("SET FOREIGN_KEY_CHECKS=1");

                        JOptionPane.showMessageDialog(this,
                                "Đã xóa thành công:\n" +
                                        "- " + detailsDeleted + " chi tiết phiếu nhập\n" +
                                        "- " + receiptsDeleted + " phiếu nhập\n" +
                                        "- " + supplierProductsDeleted + " sản phẩm nhà cung cấp\n" +
                                        "- " + suppliersDeleted + " nhà cung cấp",
                                "Xóa dữ liệu thành công", JOptionPane.INFORMATION_MESSAGE);

                        // Cập nhật lại giao diện
                        loadSuppliers();
                        refreshProductData();
                        resetForm();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Lỗi khi xóa dữ liệu: " + e.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                } finally {
                    // Đóng tài nguyên
                    try {
                        if (conn != null)
                            conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Lỗi không xác định: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi không xác định: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetAndCreateData() {
        clearAllData();
        createSampleData();
    }
}