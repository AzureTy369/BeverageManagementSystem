package GUI;

import BUS.ImportReceiptBUS;
import BUS.SupplierBUS;
import BUS.EmployeeBUS;
import DTO.ImportReceipt;
import DTO.ImportReceiptDetail;
import BUS.ImportReceiptDetailBUS;
import BUS.ProductBUS;
import DTO.ProductDTO;
import DTO.SupplierProductDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import GUI.utils.ExcelUtils;
import GUI.utils.ButtonHelper;

public class ImportReceiptDetailGUI extends JPanel {
    private ImportReceiptBUS importReceiptController;
    private SupplierBUS supplierController;
    private EmployeeBUS employeeController;
    private ProductBUS productController;
    private ImportReceiptDetailBUS importReceiptDetailBUS;

    private ImportReceipt receipt;
    private String receiptId;
    private boolean isEditable;

    private JLabel titleLabel;
    private JLabel receiptIdValueLabel;
    private JLabel supplierValueLabel;
    private JLabel creatorValueLabel;
    private JLabel dateValueLabel;

    private JTable detailTable;
    private DefaultTableModel tableModel;

    private JLabel totalAmountLabel;
    private JPanel buttonPanel;

    // UI Components for editing
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JTextField quantityField;
    private List<SupplierProductDTO> supplierProducts;

    private final Color primaryColor = new Color(0, 123, 255);
    private final Color successColor = new Color(40, 167, 69);
    private final Color dangerColor = new Color(220, 53, 69);
    private final Color lightColor = new Color(248, 249, 250);

    public ImportReceiptDetailGUI(String receiptId) {
        this(receiptId, false);
    }

    public ImportReceiptDetailGUI(String receiptId, boolean isEditable) {
        this.receiptId = receiptId;
        this.isEditable = isEditable;
        this.importReceiptController = new ImportReceiptBUS();
        this.supplierController = new SupplierBUS();
        this.employeeController = new EmployeeBUS();
        this.productController = new ProductBUS();
        this.importReceiptDetailBUS = new ImportReceiptDetailBUS();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE); // Đổi màu nền thành trắng
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Load receipt data
        loadReceiptData();

        // Create GUI components
        createGUI();
    }

    private void loadReceiptData() {
        receipt = importReceiptController.getImportReceiptById(receiptId);
        if (receipt == null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy phiếu nhập với mã " + receiptId,
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createGUI() {
        // Top panel with title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);

        titleLabel = new JLabel("CHI TIẾT PHIẾU NHẬP", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(primaryColor); // Đổi màu chữ thành màu chính

        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Information panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 4, 20, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Left column
        JLabel receiptIdLabel = new JLabel("Mã phiếu:");
        receiptIdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(receiptIdLabel);

        receiptIdValueLabel = new JLabel(receipt != null ? receipt.getImportId() : "N/A");
        receiptIdValueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(receiptIdValueLabel);

        JLabel supplierLabel = new JLabel("Nhà cung cấp:");
        supplierLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(supplierLabel);

        String supplierName = "N/A";
        if (receipt != null && supplierController != null) {
            supplierName = receipt.getSupplierId();
            // In a real application, you would fetch the supplier name from the database
        }
        supplierValueLabel = new JLabel(supplierName);
        supplierValueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(supplierValueLabel);

        // Right column
        JLabel creatorLabel = new JLabel("Người tạo:");
        creatorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(creatorLabel);

        String creatorName = "N/A";
        if (receipt != null && employeeController != null) {
            creatorName = receipt.getEmployeeId();
            // In a real application, you would fetch the employee name from the database
        }
        creatorValueLabel = new JLabel(creatorName);
        creatorValueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(creatorValueLabel);

        JLabel dateLabel = new JLabel("Thời gian tạo:");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(dateLabel);

        dateValueLabel = new JLabel(receipt != null ? receipt.getImportDate() : "N/A");
        dateValueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(dateValueLabel);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        if (isEditable) {
            // Tạo giao diện chỉnh sửa
            createEditableInterface(tablePanel);
        } else {
            // Tạo giao diện hiển thị
            createDetailTable();
            JScrollPane scrollPane = new JScrollPane(detailTable);
            scrollPane.setBackground(Color.WHITE);
            scrollPane.getViewport().setBackground(Color.WHITE);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255), 1));
            tablePanel.add(scrollPane, BorderLayout.CENTER);
        }

        // Bottom panel with total
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(Color.WHITE);

        JLabel totalLabel = new JLabel("TỔNG TIỀN:");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalPanel.add(totalLabel);

        String formattedAmount = "0đ";
        if (receipt != null) {
            try {
                double amount = Double.parseDouble(receipt.getTotalAmount().replace(",", ""));
                formattedAmount = String.format("%,.0fđ", amount);
            } catch (NumberFormatException e) {
                formattedAmount = receipt.getTotalAmount();
            }
        }
        totalAmountLabel = new JLabel(formattedAmount);
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalAmountLabel.setForeground(primaryColor); // Đổi màu chữ thành màu chính
        totalPanel.add(totalAmountLabel);

        bottomPanel.add(totalPanel, BorderLayout.EAST);

        // Button panel
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        if (!isEditable) {
            JButton exportPdfButton = ButtonHelper.createButton("Xuất PDF", primaryColor);
            exportPdfButton.setFont(new Font("Arial", Font.BOLD, 16));
            exportPdfButton.setPreferredSize(new Dimension(120, 35));
            exportPdfButton.setForeground(Color.WHITE);
            exportPdfButton.addActionListener(e -> exportToPdf());
            buttonPanel.add(exportPdfButton);
        }

        // Add all components to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load receipt detail data from database
        loadReceiptDetailData();
    }

    private void createEditableInterface(JPanel mainPanel) {
        // Tạo layout chia đôi màn hình
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(0.5);
        splitPane.setResizeWeight(0.5);
        splitPane.setBackground(Color.WHITE);

        // Panel bên trái: Danh sách sản phẩm của nhà cung cấp
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Sản phẩm của nhà cung cấp"));
        leftPanel.setBackground(Color.WHITE);

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(Color.WHITE);
        JTextField searchField = new JTextField(15);
        JButton searchButton = ButtonHelper.createButton("Tìm kiếm", primaryColor);
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));
        searchButton.setForeground(Color.WHITE);

        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                displayAllSupplierProducts();
            } else {
                searchSupplierProducts(keyword);
            }
        });

        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        // Bảng sản phẩm
        createProductTable();
        JScrollPane productScrollPane = new JScrollPane(productTable);
        productScrollPane.setBackground(Color.WHITE);
        productScrollPane.getViewport().setBackground(Color.WHITE);
        leftPanel.add(productScrollPane, BorderLayout.CENTER);

        // Panel thêm sản phẩm
        JPanel addPanel = new JPanel();
        addPanel.setBackground(Color.WHITE);
        addPanel.add(new JLabel("Số lượng:"));
        quantityField = new JTextField("1", 5);
        JButton addButton = ButtonHelper.createButton("Thêm vào phiếu", successColor);
        addButton.setFont(new Font("Arial", Font.BOLD, 16));
        addButton.setPreferredSize(new Dimension(150, 35));
        addButton.setForeground(Color.WHITE);

        addButton.addActionListener(e -> addProductToReceipt());

        addPanel.add(quantityField);
        addPanel.add(addButton);
        leftPanel.add(addPanel, BorderLayout.SOUTH);

        // Panel bên phải: Chi tiết phiếu nhập
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết phiếu nhập"));
        rightPanel.setBackground(Color.WHITE);

        // Bảng chi tiết
        createDetailTable();
        JScrollPane detailScrollPane = new JScrollPane(detailTable);
        detailScrollPane.setBackground(Color.WHITE);
        detailScrollPane.getViewport().setBackground(Color.WHITE);
        rightPanel.add(detailScrollPane, BorderLayout.CENTER);

        // Panel chức năng
        JPanel functionPanel = new JPanel();
        functionPanel.setBackground(Color.WHITE);

        JButton editButton = ButtonHelper.createButton("Sửa số lượng", successColor);
        editButton.setFont(new Font("Arial", Font.BOLD, 16));
        editButton.setPreferredSize(new Dimension(150, 35));
        editButton.setForeground(Color.WHITE);

        JButton deleteButton = ButtonHelper.createButton("Xóa sản phẩm", dangerColor);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 16));
        deleteButton.setPreferredSize(new Dimension(150, 35));
        deleteButton.setForeground(Color.WHITE);

        editButton.addActionListener(e -> editProductQuantity());
        deleteButton.addActionListener(e -> removeProductFromReceipt());

        functionPanel.add(editButton);
        functionPanel.add(deleteButton);
        rightPanel.add(functionPanel, BorderLayout.SOUTH);

        // Thêm vào split pane
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        // Thêm vào panel chính
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Tải sản phẩm của nhà cung cấp
        if (receipt != null) {
            loadSupplierProducts(receipt.getSupplierId());
        }
    }

    private void createProductTable() {
        // Tạo model cho bảng sản phẩm
        productTableModel = new DefaultTableModel(
                new String[] { "Mã sản phẩm", "Tên sản phẩm", "Đơn vị tính", "Giá", "Danh mục" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(productTableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setRowHeight(25);
    }

    private void createDetailTable() {
        String[] columns = { "STT", "Mã SP", "Tên SP", "Số lượng", "Đơn giá", "Thành tiền" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return isEditable && column == 3; // Chỉ cho phép sửa cột số lượng khi ở chế độ chỉnh sửa
            }
        };

        detailTable = new JTable(tableModel);
        detailTable.setFont(new Font("Arial", Font.PLAIN, 14));
        detailTable.setRowHeight(25);
        detailTable.setGridColor(new Color(230, 230, 230));
        detailTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        detailTable.getTableHeader().setReorderingAllowed(false);
        detailTable.setBackground(Color.WHITE);
        detailTable.getTableHeader().setBackground(Color.WHITE);

        if (isEditable) {
            // Thêm cell editor cho cột số lượng
            detailTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JTextField()) {
                @Override
                public boolean stopCellEditing() {
                    try {
                        int quantity = Integer.parseInt(getCellEditorValue().toString());
                        if (quantity <= 0) {
                            JOptionPane.showMessageDialog(detailTable, "Số lượng phải lớn hơn 0", "Lỗi",
                                    JOptionPane.ERROR_MESSAGE);
                            return false;
                        }

                        // Cập nhật thành tiền
                        int row = detailTable.getSelectedRow();
                        double price = Double.parseDouble(tableModel.getValueAt(row, 4).toString());
                        double total = price * quantity;
                        tableModel.setValueAt(total, row, 5);

                        // Cập nhật tổng tiền
                        updateTotalAmount();

                        return super.stopCellEditing();
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(detailTable, "Số lượng phải là số nguyên dương", "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            });
        }
    }

    /**
     * Tải dữ liệu chi tiết phiếu nhập từ cơ sở dữ liệu
     */
    private void loadReceiptDetailData() {
        // Làm trống bảng
        tableModel.setRowCount(0);

        System.out.println("DEBUG: Loading receipt details for receipt ID: " + receiptId);

        // Lấy danh sách chi tiết phiếu nhập
        List<ImportReceiptDetail> details = importReceiptDetailBUS.getImportReceiptDetailsByReceiptId(receiptId);

        System.out.println("DEBUG: Loaded " + details.size() + " receipt details for receipt ID: " + receiptId);

        if (details.isEmpty()) {
            // Nếu không có chi tiết phiếu nhập
            if (isEditable) {
                JOptionPane.showMessageDialog(this,
                        "Phiếu nhập chưa có chi tiết sản phẩm. Vui lòng thêm sản phẩm vào phiếu.",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Phiếu nhập này chưa có chi tiết sản phẩm.",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
            totalAmountLabel.setText("0 VNĐ");
            return;
        }

        // Thêm dữ liệu vào bảng
        double totalAmount = 0;
        for (ImportReceiptDetail detail : details) {
            String productId = detail.getProductId();

            // Lấy thông tin sản phẩm
            ProductDTO product = productController.getProductById(productId);
            String productName = "Không xác định";
            if (product != null) {
                productName = product.getProductName();
                System.out.println("DEBUG: Adding product to table: " + productName + ", ID: " + productId);
            } else {
                System.out.println("DEBUG: Product not found in database: " + productId);
            }

            // Tính lại thành tiền (đề phòng lỗi)
            double total = detail.getQuantity() * detail.getPrice();
            if (Math.abs(total - detail.getTotal()) > 0.01) {
                System.out.println("WARNING: Total price mismatch for product " + productId + ": " +
                        detail.getTotal() + " vs calculated: " + total);
                detail.setTotal(total);
            }

            // Thêm vào bảng
            tableModel.addRow(new Object[] {
                    tableModel.getRowCount() + 1,
                    productId,
                    productName,
                    detail.getQuantity(),
                    String.format("%,.0f", detail.getPrice()),
                    String.format("%,.0f", detail.getTotal())
            });

            totalAmount += detail.getTotal();
        }

        // Cập nhật tổng tiền
        totalAmountLabel.setText(String.format("%,.0f VNĐ", totalAmount));
    }

    private void loadSupplierProducts(String supplierId) {
        // Lấy danh sách sản phẩm của nhà cung cấp
        supplierProducts = supplierController.getSupplierProducts(supplierId);

        // Hiển thị danh sách sản phẩm
        displayAllSupplierProducts();
    }

    private void displayAllSupplierProducts() {
        // Xóa dữ liệu cũ
        productTableModel.setRowCount(0);

        // Hiển thị dữ liệu mới
        for (SupplierProductDTO product : supplierProducts) {
            // Lấy thông tin danh mục từ đối tượng SupplierProductDTO
            String category = product.getCategoryName();
            if (category == null || category.isEmpty()) {
                category = "Chưa phân loại";
            }

            Object[] row = {
                    product.getProductId(),
                    product.getProductName(),
                    product.getUnit(),
                    product.getPrice(),
                    category
            };

            productTableModel.addRow(row);
        }
    }

    private void searchSupplierProducts(String keyword) {
        // Xóa dữ liệu cũ
        productTableModel.setRowCount(0);

        // Tìm kiếm và hiển thị sản phẩm phù hợp
        keyword = keyword.toLowerCase();
        for (SupplierProductDTO product : supplierProducts) {
            if (product.getProductId().toLowerCase().contains(keyword) ||
                    product.getProductName().toLowerCase().contains(keyword)) {

                // Lấy thông tin danh mục từ đối tượng SupplierProductDTO
                String category = product.getCategoryName();
                if (category == null || category.isEmpty()) {
                    category = "Chưa phân loại";
                }

                Object[] row = {
                        product.getProductId(),
                        product.getProductName(),
                        product.getUnit(),
                        product.getPrice(),
                        category
                };

                productTableModel.addRow(row);
            }
        }
    }

    private void addProductToReceipt() {
        if (!isEditable)
            return;

        // Kiểm tra sản phẩm đã chọn
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để thêm", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            // Lấy số lượng
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Lấy thông tin sản phẩm
            String productId = productTable.getValueAt(selectedRow, 0).toString();
            String productName = productTable.getValueAt(selectedRow, 1).toString();
            double price = Double.parseDouble(productTable.getValueAt(selectedRow, 3).toString());
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

            // Kiểm tra sản phẩm đã có trong danh sách chưa
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 1).equals(baseProductId)) {
                    // Cập nhật số lượng
                    int oldQuantity = Integer.parseInt(tableModel.getValueAt(i, 3).toString());
                    int newQuantity = oldQuantity + quantity;
                    tableModel.setValueAt(newQuantity, i, 3);
                    tableModel.setValueAt(price * newQuantity, i, 5);

                    // Cập nhật tổng tiền
                    updateTotalAmount();
                    return;
                }
            }

            // Thêm sản phẩm mới
            int stt = tableModel.getRowCount() + 1;
            Object[] row = {
                    stt,
                    baseProductId, // Sử dụng mã sản phẩm gốc
                    productName,
                    quantity,
                    price,
                    total
            };

            tableModel.addRow(row);

            // Cập nhật tổng tiền
            updateTotalAmount();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên dương", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editProductQuantity() {
        if (!isEditable)
            return;

        // Kiểm tra sản phẩm đã chọn
        int selectedRow = detailTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để sửa", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Hộp thoại nhập số lượng mới
        String input = JOptionPane.showInputDialog(this, "Nhập số lượng mới:", tableModel.getValueAt(selectedRow, 3));
        if (input == null || input.trim().isEmpty())
            return;

        try {
            // Cập nhật số lượng
            int quantity = Integer.parseInt(input.trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double price = Double.parseDouble(tableModel.getValueAt(selectedRow, 4).toString());
            tableModel.setValueAt(quantity, selectedRow, 3);
            tableModel.setValueAt(price * quantity, selectedRow, 5);

            // Cập nhật tổng tiền
            updateTotalAmount();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên dương", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeProductFromReceipt() {
        if (!isEditable)
            return;

        // Kiểm tra sản phẩm đã chọn
        int selectedRow = detailTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xóa", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Xác nhận xóa
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sản phẩm này khỏi phiếu nhập?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        // Xóa sản phẩm
        tableModel.removeRow(selectedRow);

        // Cập nhật STT
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(i + 1, i, 0);
        }

        // Cập nhật tổng tiền
        updateTotalAmount();
    }

    private void updateTotalAmount() {
        if (receipt == null)
            return;

        // Tính tổng tiền
        double total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += Double.parseDouble(tableModel.getValueAt(i, 5).toString());
        }

        // Hiển thị tổng tiền
        String formattedAmount = String.format("%,.0fđ", total);
        totalAmountLabel.setText(formattedAmount);
    }

    public boolean saveChanges() {
        if (!isEditable || receipt == null)
            return false;

        try {
            // Kiểm tra danh sách chi tiết
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Phiếu nhập phải có ít nhất một sản phẩm", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Tính tổng tiền
            double total = 0;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                total += Double.parseDouble(tableModel.getValueAt(i, 5).toString());
            }

            // Cập nhật tổng tiền cho phiếu nhập
            receipt.setTotalAmount(String.format("%.0f", total));
            boolean success = importReceiptController.updateImportReceipt(receipt);

            if (success) {
                // Xóa chi tiết cũ
                importReceiptDetailBUS.deleteAllImportReceiptDetails(receiptId);

                // Thêm chi tiết mới
                List<ImportReceiptDetail> details = new ArrayList<>();
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String productId = tableModel.getValueAt(i, 1).toString();
                    int quantity = Integer.parseInt(tableModel.getValueAt(i, 3).toString());
                    double price = Double.parseDouble(tableModel.getValueAt(i, 4).toString());
                    double productTotal = Double.parseDouble(tableModel.getValueAt(i, 5).toString());

                    ImportReceiptDetail detail = new ImportReceiptDetail(receiptId, productId, quantity, price,
                            productTotal);
                    details.add(detail);
                }

                boolean detailsSuccess = importReceiptDetailBUS.insertMultipleImportReceiptDetails(details);

                if (detailsSuccess) {
                    JOptionPane.showMessageDialog(this, "Cập nhật phiếu nhập thành công", "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    return true;
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Cập nhật phiếu nhập thành công nhưng không thể lưu chi tiết phiếu nhập", "Cảnh báo",
                            JOptionPane.WARNING_MESSAGE);
                    return true;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật phiếu nhập thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void exportToPdf() {
        // TODO: Implement PDF export functionality
        JOptionPane.showMessageDialog(this, "Chức năng xuất PDF đang được phát triển", "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }
}