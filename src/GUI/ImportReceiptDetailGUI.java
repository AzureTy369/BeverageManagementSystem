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
import java.util.List;
import java.util.ArrayList;
import GUI.utils.ButtonHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.awt.Desktop;

// Bỏ comment các import iText
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javax.swing.filechooser.FileNameExtensionFilter;

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
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        titleLabel.setForeground(primaryColor); // Đổi màu chữ thành màu chính

        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Information panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 4, 20, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Left column
        JLabel receiptIdLabel = new JLabel("Mã phiếu:");
        receiptIdLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        infoPanel.add(receiptIdLabel);

        receiptIdValueLabel = new JLabel(receipt != null ? receipt.getImportId() : "N/A");
        receiptIdValueLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        infoPanel.add(receiptIdValueLabel);

        JLabel supplierLabel = new JLabel("Nhà cung cấp:");
        supplierLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        infoPanel.add(supplierLabel);

        String supplierName = "N/A";
        if (receipt != null && supplierController != null) {
            supplierName = receipt.getSupplierId();
            // In a real application, you would fetch the supplier name from the database
        }
        supplierValueLabel = new JLabel(supplierName);
        supplierValueLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        infoPanel.add(supplierValueLabel);

        // Right column
        JLabel creatorLabel = new JLabel("Người tạo:");
        creatorLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        infoPanel.add(creatorLabel);

        String creatorName = "N/A";
        if (receipt != null && employeeController != null) {
            creatorName = receipt.getEmployeeId();
            // In a real application, you would fetch the employee name from the database
        }
        creatorValueLabel = new JLabel(creatorName);
        creatorValueLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        infoPanel.add(creatorValueLabel);

        JLabel dateLabel = new JLabel("Thời gian tạo:");
        dateLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        infoPanel.add(dateLabel);

        dateValueLabel = new JLabel(receipt != null ? receipt.getImportDate() : "N/A");
        dateValueLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
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
        totalLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
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
        totalAmountLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        totalAmountLabel.setForeground(primaryColor); // Đổi màu chữ thành màu chính
        totalPanel.add(totalAmountLabel);

        bottomPanel.add(totalPanel, BorderLayout.EAST);

        // Button panel
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        if (!isEditable) {
            JButton exportPdfButton = ButtonHelper.createButton("Xuất PDF", primaryColor);
            exportPdfButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
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
        searchButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
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
        addButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
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
        editButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        editButton.setPreferredSize(new Dimension(150, 35));
        editButton.setForeground(Color.WHITE);

        JButton deleteButton = ButtonHelper.createButton("Xóa sản phẩm", dangerColor);
        deleteButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
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
        // Tạo model cho bảng chi tiết
        tableModel = new DefaultTableModel(
                new Object[] { "STT", "Mã sản phẩm", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền", "Mã loại",
                        "Tên loại" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return isEditable && column == 3; // Chỉ cho phép sửa cột số lượng nếu đang trong chế độ chỉnh sửa
            }
        };

        detailTable = new JTable(tableModel);
        detailTable.setRowHeight(30);
        detailTable.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        detailTable.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        detailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Đặt chiều rộng cột
        detailTable.getColumnModel().getColumn(0).setPreferredWidth(50); // STT
        detailTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Mã sản phẩm
        detailTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Tên sản phẩm
        detailTable.getColumnModel().getColumn(3).setPreferredWidth(80); // Số lượng
        detailTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Đơn giá
        detailTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Thành tiền

        // Ẩn cột mã loại và tên loại (chỉ để lưu thông tin)
        detailTable.getColumnModel().getColumn(6).setMinWidth(0);
        detailTable.getColumnModel().getColumn(6).setMaxWidth(0);
        detailTable.getColumnModel().getColumn(6).setWidth(0);

        detailTable.getColumnModel().getColumn(7).setMinWidth(0);
        detailTable.getColumnModel().getColumn(7).setMaxWidth(0);
        detailTable.getColumnModel().getColumn(7).setWidth(0);

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
                    String.format("%,.0f", detail.getTotal()),
                    detail.getCategoryId(),
                    detail.getCategoryName()
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

            // Lấy thông tin sản phẩm - giữ nguyên mã sản phẩm gốc
            String productId = productTable.getValueAt(selectedRow, 0).toString();
            String productName = productTable.getValueAt(selectedRow, 1).toString();
            double price = Double.parseDouble(productTable.getValueAt(selectedRow, 3).toString());
            double total = price * quantity;

            // Lấy thông tin danh mục
            String categoryId = "";
            String categoryName = "";

            // Cố gắng lấy thông tin danh mục từ bảng
            if (productTable.getColumnCount() > 4) {
                try {
                    categoryName = productTable.getValueAt(selectedRow, 4) != null
                            ? productTable.getValueAt(selectedRow, 4).toString()
                            : "Chưa phân loại";
                } catch (Exception e) {
                    System.out.println("Không thể lấy tên danh mục từ bảng: " + e.getMessage());
                }
            }

            // Lấy thông tin danh mục từ supplier product nếu có
            for (SupplierProductDTO product : supplierProducts) {
                if (product.getProductId().equals(productId)) {
                    if (product.getCategoryId() != null && !product.getCategoryId().isEmpty()) {
                        categoryId = product.getCategoryId();
                        categoryName = product.getCategoryName() != null ? product.getCategoryName() : categoryName;
                        System.out.println("Lấy danh mục từ supplier product: " + categoryId + " - " + categoryName);
                        break;
                    }
                }
            }

            // Nếu không tìm thấy từ supplier product, lấy từ product
            if (categoryId.isEmpty()) {
                ProductDTO product = productController.getProductById(productId);
                if (product != null) {
                    categoryId = product.getCategoryId();
                    categoryName = product.getCategoryName();
                    System.out.println("Lấy danh mục từ product: " + categoryId + " - " + categoryName);
                }
            }

            // Nếu vẫn không có, sử dụng giá trị mặc định
            if (categoryId == null || categoryId.isEmpty()) {
                categoryId = "LSP001";
                if (categoryName == null || categoryName.isEmpty() || "Chưa phân loại".equals(categoryName)) {
                    categoryName = "Chưa phân loại";
                }
                System.out.println("Sử dụng danh mục mặc định: " + categoryId + " - " + categoryName);
            }

            System.out.println("Thêm sản phẩm: " + productId + ", danh mục: " + categoryId + " - " + categoryName);

            // Kiểm tra sản phẩm đã có trong danh sách chưa - sử dụng mã gốc không chuyển
            // đổi
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 1).equals(productId)) {
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

            // Thêm sản phẩm mới - sử dụng mã gốc không chuyển đổi
            int stt = tableModel.getRowCount() + 1;
            Object[] row = {
                    stt,
                    productId, // Sử dụng mã sản phẩm gốc không chuyển đổi
                    productName,
                    quantity,
                    String.format("%,.0f", price),
                    String.format("%,.0f", total),
                    categoryId,
                    categoryName
            };
            tableModel.addRow(row);

            // Cập nhật tổng tiền
            updateTotalAmount();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm sản phẩm: " + e.getMessage());
            e.printStackTrace();
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
                // Đảm bảo lấy giá trị thành tiền đúng cách
                Object value = tableModel.getValueAt(i, 5);
                if (value != null) {
                    // Xử lý cả trường hợp là String có định dạng và là số Double
                    if (value instanceof String) {
                        String valueStr = ((String) value).replaceAll("[^\\d.]", ""); // Loại bỏ các ký tự không phải số
                        if (!valueStr.isEmpty()) {
                            try {
                                total += Double.parseDouble(valueStr);
                            } catch (NumberFormatException e) {
                                System.out.println("Lỗi chuyển đổi giá: " + valueStr);
                            }
                        }
                    } else if (value instanceof Number) {
                        total += ((Number) value).doubleValue();
                    }
                }
            }

            // Kiểm tra nếu tổng tiền vượt quá giới hạn
            if (total > 999999.99) {
                // Giới hạn tổng tiền và hiển thị cảnh báo
                total = 999999.99;
                JOptionPane.showMessageDialog(this,
                        "Tổng tiền vượt quá giới hạn cho phép và đã được điều chỉnh xuống 999,999.99.\n" +
                                "Bạn nên chia nhỏ phiếu nhập để tránh lỗi dữ liệu.",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }

            // Định dạng tổng tiền phù hợp với cột trong cơ sở dữ liệu (DECIMAL(10,2))
            String formattedTotal = String.format(java.util.Locale.US, "%.2f", total);

            // Hiển thị log để kiểm tra giá trị
            System.out.println("Tổng tiền trước khi lưu: " + formattedTotal);

            // Cập nhật tổng tiền cho phiếu nhập
            receipt.setTotalAmount(formattedTotal);
            boolean success = importReceiptController.updateImportReceipt(receipt);

            if (!success) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi cập nhật phiếu nhập.\n" +
                                "Có thể phiếu nhập đã bị xóa hoặc đang ở trạng thái không cho phép sửa.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Xóa chi tiết cũ
            boolean deleteSuccess = importReceiptDetailBUS.deleteAllImportReceiptDetails(receiptId);
            if (!deleteSuccess) {
                System.out.println("Cảnh báo: Không thể xóa chi tiết phiếu nhập cũ - mã phiếu: " + receiptId);
                JOptionPane.showMessageDialog(this,
                        "Không thể xóa chi tiết phiếu nhập cũ. Vui lòng kiểm tra lại dữ liệu.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Thêm chi tiết mới
            List<ImportReceiptDetail> details = new ArrayList<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String productId = tableModel.getValueAt(i, 1).toString();

                // Chuyển đổi số lượng
                int quantity = 0;
                Object quantityObj = tableModel.getValueAt(i, 3);
                if (quantityObj instanceof String) {
                    quantity = Integer.parseInt(((String) quantityObj).replaceAll("[^\\d]", ""));
                } else {
                    quantity = ((Number) quantityObj).intValue();
                }

                // Chuyển đổi đơn giá
                double price = 0;
                Object priceObj = tableModel.getValueAt(i, 4);
                if (priceObj instanceof String) {
                    String priceStr = ((String) priceObj).replaceAll("[^\\d.]", "");
                    if (!priceStr.isEmpty()) {
                        try {
                            price = Double.parseDouble(priceStr);
                        } catch (NumberFormatException e) {
                            System.out.println("Lỗi chuyển đổi giá: " + priceObj);
                        }
                    }
                } else if (priceObj instanceof Number) {
                    price = ((Number) priceObj).doubleValue();
                }

                // Giới hạn giá đơn vị nếu cần
                if (price > 999999.99) {
                    price = 999999.99;
                    System.out.println("Đã giới hạn giá sản phẩm " + productId + " xuống 999,999.99");
                }

                // Chuyển đổi thành tiền
                double productTotal = price * quantity;

                // Giới hạn thành tiền sản phẩm nếu cần
                if (productTotal > 999999.99) {
                    productTotal = 999999.99;
                    System.out.println("Đã giới hạn thành tiền sản phẩm " + productId + " xuống 999,999.99");
                }

                // Kiểm tra hợp lệ
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(this, "Số lượng sản phẩm " + productId + " phải lớn hơn 0", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                if (price <= 0) {
                    JOptionPane.showMessageDialog(this, "Giá sản phẩm " + productId + " phải lớn hơn 0", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                // Lấy thông tin danh mục
                String categoryId = "";
                String categoryName = "";

                // Thử lấy từ bảng nếu có cột danh mục ẩn
                try {
                    if (tableModel.getColumnCount() > 6) {
                        categoryId = tableModel.getValueAt(i, 6) != null ? tableModel.getValueAt(i, 6).toString() : "";
                        categoryName = tableModel.getValueAt(i, 7) != null ? tableModel.getValueAt(i, 7).toString()
                                : "";
                    }
                } catch (Exception e) {
                    System.out.println("Không thể đọc thông tin danh mục từ bảng: " + e.getMessage());
                }

                // Nếu không có từ bảng, lấy từ cơ sở dữ liệu
                if (categoryId == null || categoryId.isEmpty()) {
                    ProductDTO product = productController.getProductById(productId);
                    if (product != null) {
                        categoryId = product.getCategoryId();
                        categoryName = product.getCategoryName();
                    }
                }

                // Nếu vẫn không có, sử dụng giá trị mặc định
                if (categoryId == null || categoryId.isEmpty()) {
                    categoryId = "LSP001";
                    categoryName = "Chưa phân loại";
                }

                ImportReceiptDetail detail = new ImportReceiptDetail(receiptId, productId, quantity, price,
                        productTotal, categoryId, categoryName);
                details.add(detail);

                System.out.println("Chi tiết phiếu nhập: SP=" + productId + ", SL=" + quantity +
                        ", Giá=" + price + ", Tổng=" + productTotal +
                        ", Mã loại=" + categoryId + ", Tên loại=" + categoryName);
            }

            // Lưu chi tiết phiếu nhập vào cơ sở dữ liệu
            boolean detailsSuccess = importReceiptDetailBUS.insertMultipleImportReceiptDetails(details);

            if (!detailsSuccess) {
                JOptionPane.showMessageDialog(this,
                        "Cập nhật phiếu nhập thành công nhưng không thể lưu chi tiết phiếu nhập.\n" +
                                "Vui lòng kiểm tra lại phiếu nhập trong danh sách.",
                        "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                // Trả về true vì phiếu nhập đã được cập nhật thành công
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật phiếu nhập thành công", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu phiếu nhập: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    private void exportToPdf() {
        FileOutputStream fos = null;
        try {
            // Create file chooser for saving the PDF
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu file PDF");
            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));

            // Add default file name
            String defaultFileName = "phieunhap_" + receiptId + ".pdf";
            fileChooser.setSelectedFile(new File(defaultFileName));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection != JFileChooser.APPROVE_OPTION) {
                return; // User cancelled
            }

            File fileToSave = fileChooser.getSelectedFile();
            // Add .pdf extension if not already present
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }

            // Create PDF document
            Document document = new Document(PageSize.A4);
            fos = new FileOutputStream(fileToSave);
            PdfWriter writer = PdfWriter.getInstance(document, fos);
            document.open();

            // Initialize fonts for Vietnamese support
            BaseFont baseFont;
            try {
                // Try to load Arial Unicode MS from Windows system (good for Vietnamese)
                baseFont = BaseFont.createFont("C:\\Windows\\Fonts\\arialuni.ttf", BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED);
            } catch (Exception e) {
                try {
                    // Try Times New Roman which usually has better Vietnamese support
                    baseFont = BaseFont.createFont("C:\\Windows\\Fonts\\times.ttf", BaseFont.IDENTITY_H,
                            BaseFont.EMBEDDED);
                } catch (Exception e2) {
                    // Fallback to built-in font
                    baseFont = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                }
            }

            // Create fonts
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(baseFont, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(baseFont, 12, com.itextpdf.text.Font.NORMAL);
            com.itextpdf.text.Font boldFont = new com.itextpdf.text.Font(baseFont, 12, com.itextpdf.text.Font.BOLD);

            // Add title
            Paragraph title = new Paragraph("CHI TIẾT PHIẾU NHẬP", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Empty line

            // Create 2 column table for receipt info
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);

            // Add receipt info
            addInfoRow(infoTable, "Mã phiếu:", receiptIdValueLabel.getText(), boldFont, normalFont);
            addInfoRow(infoTable, "Nhà cung cấp:", supplierValueLabel.getText(), boldFont, normalFont);
            addInfoRow(infoTable, "Người tạo:", creatorValueLabel.getText(), boldFont, normalFont);
            addInfoRow(infoTable, "Thời gian tạo:", dateValueLabel.getText(), boldFont, normalFont);

            document.add(infoTable);
            document.add(new Paragraph(" ")); // Empty line

            // Create table for receipt details
            PdfPTable detailsTable = new PdfPTable(5);
            detailsTable.setWidthPercentage(100);
            try {
                float[] columnWidths = { 0.5f, 2f, 2f, 1f, 1.5f };
                detailsTable.setWidths(columnWidths);
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            // Add table headers
            String[] headers = { "STT", "Mã SP", "Tên SP", "Số lượng", "Thành tiền" };
            for (String header : headers) {
                PdfPCell headerCell = new PdfPCell(new Phrase(header, boldFont));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                headerCell.setPadding(5);
                detailsTable.addCell(headerCell);
            }

            // Add table data
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                // STT column
                addCell(detailsTable, String.valueOf(i + 1), normalFont, Element.ALIGN_CENTER);

                // Mã SP column
                addCell(detailsTable, tableModel.getValueAt(i, 1).toString(), normalFont, Element.ALIGN_LEFT);

                // Tên SP column
                addCell(detailsTable, tableModel.getValueAt(i, 2).toString(), normalFont, Element.ALIGN_LEFT);

                // Số lượng column
                addCell(detailsTable, tableModel.getValueAt(i, 3).toString(), normalFont, Element.ALIGN_CENTER);

                // Thành tiền column
                addCell(detailsTable, tableModel.getValueAt(i, 5).toString(), normalFont, Element.ALIGN_RIGHT);
            }

            document.add(detailsTable);
            document.add(new Paragraph(" ")); // Empty line

            // Add total amount
            Paragraph totalParagraph = new Paragraph("Tổng tiền: " + totalAmountLabel.getText(), boldFont);
            totalParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalParagraph);

            // Add footer
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            Paragraph dateParagraph = new Paragraph(
                    "Ngày ......... tháng ......... năm .........", normalFont);
            dateParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(dateParagraph);

            Paragraph signatureParagraph = new Paragraph(
                    "Người lập phiếu", boldFont);
            signatureParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(signatureParagraph);

            Paragraph signatureNameParagraph = new Paragraph(
                    "(Ký, ghi rõ họ tên)", normalFont);
            signatureNameParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(signatureNameParagraph);

            // Ensure all data is written
            writer.flush();

            // Close the document and writer
            if (document != null && document.isOpen()) {
                document.close();
            }
            if (writer != null) {
                writer.close();
            }

            // Show success message
            JOptionPane.showMessageDialog(this,
                    "Xuất PDF thành công:\n" + fileToSave.getAbsolutePath(),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            // Open the PDF file with the default PDF viewer
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(fileToSave);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất PDF: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            // Đảm bảo đóng FileOutputStream
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Helper method to add info row to the table
    private void addInfoRow(PdfPTable table, String label, String value, com.itextpdf.text.Font labelFont,
            com.itextpdf.text.Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }

    // Helper method to add a cell to the table
    private void addCell(PdfPTable table, String text, com.itextpdf.text.Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }
}