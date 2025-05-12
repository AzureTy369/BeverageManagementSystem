package GUI;

import BUS.ProductBUS;
import BUS.ProductDetailBUS;
import BUS.ImportReceiptBUS;
import BUS.ImportReceiptDetailBUS;
import DTO.ImportReceipt;
import DTO.ImportReceiptDetail;
import DTO.ProductDTO;
import DTO.ProductCategoryDTO;
import GUI.utils.DatePicker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Vector;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.HashMap;

public class ProductStatisticsGUI extends JPanel {
    private ProductBUS productController;
    private ImportReceiptBUS importReceiptController;

    private JTabbedPane tabbedPane;
    private JTable importReceiptTable;
    private JTable inventoryTable;
    private JTable revenueProductTable;
    private JTable revenueCategoryTable;

    private DefaultTableModel importReceiptTableModel;
    private DefaultTableModel inventoryTableModel;
    private DefaultTableModel revenueProductTableModel;
    private DefaultTableModel revenueCategoryTableModel;

    private JTextField searchField;
    private DatePicker fromDatePicker;
    private DatePicker toDatePicker;
    private JTextField minAmountField;
    private JTextField maxAmountField;

    // Add these class variables after the existing DatePicker declarations (around
    // line 48)
    private DatePicker productFromDatePicker;
    private DatePicker productToDatePicker;

    // Colors
    private Color primaryColor = new Color(0, 123, 255);
    private Color lightColor = new Color(248, 249, 250);

    private DecimalFormat currencyFormatter = new DecimalFormat("#,##0 VNĐ");

    public ProductStatisticsGUI(ProductBUS productController, ProductDetailBUS productDetailController) {
        this.productController = productController;
        this.importReceiptController = new ImportReceiptBUS();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(lightColor);

        // Create title panel
        createTitlePanel();

        // Create tabbed pane
        createTabbedPane();

        // Load data
        refreshData();

        // Thêm listeners cho các bảng để lưu dữ liệu khi chỉnh sửa
        setupTableEditListeners();
    }

    private void createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(lightColor);

        JLabel titleLabel = new JLabel("Quản Lý Thống Kê Sản Phẩm");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(primaryColor);

        titlePanel.add(titleLabel);

        add(titlePanel, BorderLayout.NORTH);
    }

    private void createTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        // Create tabs
        JPanel importReceiptPanel = createImportReceiptPanel();
        JPanel productPanel = createProductPanel();
        JPanel revenuePanel = createRevenuePanel();

        // Add tabs to tabbed pane
        tabbedPane.addTab("Phiếu nhập", importReceiptPanel);
        tabbedPane.addTab("Sản phẩm", productPanel);
        tabbedPane.addTab("Doanh thu", revenuePanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createImportReceiptPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top control panel with two function groups
        JPanel topControlPanel = new JPanel(new GridBagLayout());
        topControlPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // LEFT GROUP: View details button
        JPanel leftFunctionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftFunctionPanel.setBackground(new Color(240, 240, 240));
        leftFunctionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JButton viewDetailsButton = new JButton("Xem chi tiết");
        viewDetailsButton.setFont(new Font("Arial", Font.BOLD, 14));
        viewDetailsButton.setBackground(primaryColor);
        viewDetailsButton.setForeground(Color.WHITE);
        viewDetailsButton.setFocusPainted(false);
        viewDetailsButton.setOpaque(true);
        viewDetailsButton.setBorderPainted(false);
        viewDetailsButton.addActionListener(e -> viewImportReceiptDetails());

        // Add hover effect
        viewDetailsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                viewDetailsButton.setBackground(new Color(0, 105, 217)); // Darker shade of primary color
                viewDetailsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                viewDetailsButton.setBackground(primaryColor);
                viewDetailsButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        leftFunctionPanel.add(viewDetailsButton);

        // RIGHT GROUP: Search field and refresh button
        JPanel rightFunctionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightFunctionPanel.setBackground(new Color(240, 240, 240));
        rightFunctionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(150, 30));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchImportReceipts();
                }
            }
        });

        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 14));
        searchButton.addActionListener(e -> searchImportReceipts());

        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshImportReceipts());

        rightFunctionPanel.add(searchLabel);
        rightFunctionPanel.add(searchField);
        rightFunctionPanel.add(searchButton);
        rightFunctionPanel.add(refreshButton);

        // Add function panels to top control panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        topControlPanel.add(leftFunctionPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        topControlPanel.add(rightFunctionPanel, gbc);

        // Bottom filter panel with two filter groups
        JPanel bottomFilterPanel = new JPanel(new GridBagLayout());
        bottomFilterPanel.setBackground(Color.WHITE);
        bottomFilterPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // LEFT FILTER: Date range
        JPanel dateFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dateFilterPanel.setBackground(new Color(240, 240, 240));
        dateFilterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JLabel fromDateLabel = new JLabel("Từ ngày:");
        fromDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        fromDatePicker = new DatePicker(getDefaultStartDate());
        fromDatePicker.setPreferredSize(new Dimension(120, 30));

        JLabel toDateLabel = new JLabel("Đến ngày:");
        toDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        toDatePicker = new DatePicker(new java.util.Date());
        toDatePicker.setPreferredSize(new Dimension(120, 30));

        JButton applyDateButton = new JButton("Lọc");
        applyDateButton.setFont(new Font("Arial", Font.PLAIN, 14));
        applyDateButton.addActionListener(e -> filterByDate());

        dateFilterPanel.add(fromDateLabel);
        dateFilterPanel.add(fromDatePicker);
        dateFilterPanel.add(toDateLabel);
        dateFilterPanel.add(toDatePicker);
        dateFilterPanel.add(applyDateButton);

        // RIGHT FILTER: Amount range
        JPanel amountFilterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        amountFilterPanel.setBackground(new Color(240, 240, 240));
        amountFilterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JLabel amountLabel = new JLabel("Tổng tiền:");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel fromAmountLabel = new JLabel("Từ:");
        fromAmountLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        minAmountField = new JTextField(8);
        minAmountField.setPreferredSize(new Dimension(80, 30));

        JLabel toAmountLabel = new JLabel("Đến:");
        toAmountLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        maxAmountField = new JTextField(8);
        maxAmountField.setPreferredSize(new Dimension(80, 30));

        JButton applyAmountButton = new JButton("Lọc");
        applyAmountButton.setFont(new Font("Arial", Font.PLAIN, 14));
        applyAmountButton.addActionListener(e -> filterByAmount());

        amountFilterPanel.add(amountLabel);
        amountFilterPanel.add(fromAmountLabel);
        amountFilterPanel.add(minAmountField);
        amountFilterPanel.add(toAmountLabel);
        amountFilterPanel.add(maxAmountField);
        amountFilterPanel.add(applyAmountButton);

        // Add filter panels to bottom filter panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        bottomFilterPanel.add(dateFilterPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        bottomFilterPanel.add(amountFilterPanel, gbc);

        // Table
        importReceiptTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Cho phép chỉnh sửa cột mã phiếu (0), nhà cung cấp (1), người tạo (2)
                return column == 0 || column == 1 || column == 2;
            }
        };

        importReceiptTableModel.addColumn("Mã phiếu");
        importReceiptTableModel.addColumn("Nhà cung cấp");
        importReceiptTableModel.addColumn("Người tạo");
        importReceiptTableModel.addColumn("Ngày nhập");
        importReceiptTableModel.addColumn("Tổng tiền");
        importReceiptTableModel.addColumn("Trạng thái");

        importReceiptTable = new JTable(importReceiptTableModel);
        importReceiptTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        importReceiptTable.getTableHeader().setReorderingAllowed(false);
        importReceiptTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        importReceiptTable.setRowHeight(30);
        importReceiptTable.setFont(new Font("Arial", Font.PLAIN, 14));
        importReceiptTable.setGridColor(new Color(230, 230, 230));
        importReceiptTable.setShowGrid(true);

        JScrollPane scrollPane = new JScrollPane(importReceiptTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));

        // Add components to main panel
        JPanel controlContainer = new JPanel(new BorderLayout());
        controlContainer.setBackground(Color.WHITE);
        controlContainer.add(topControlPanel, BorderLayout.NORTH);
        controlContainer.add(bottomFilterPanel, BorderLayout.CENTER);

        panel.add(controlContainer, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(lightColor);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top control panel with two function groups
        JPanel topControlPanel = new JPanel(new GridBagLayout());
        topControlPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // LEFT GROUP: Search functionality
        JPanel leftFunctionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftFunctionPanel.setBackground(new Color(240, 240, 240));
        leftFunctionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JTextField productSearchField = new JTextField(15);
        productSearchField.setPreferredSize(new Dimension(150, 30));
        productSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchProducts(productSearchField.getText());
                }
            }
        });

        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 14));
        searchButton.addActionListener(e -> searchProducts(productSearchField.getText()));

        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> loadProductData());

        leftFunctionPanel.add(searchLabel);
        leftFunctionPanel.add(productSearchField);
        leftFunctionPanel.add(searchButton);
        leftFunctionPanel.add(refreshButton);

        // RIGHT GROUP: Date filter
        JPanel rightFunctionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rightFunctionPanel.setBackground(new Color(240, 240, 240));
        rightFunctionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JLabel fromDateLabel = new JLabel("Từ ngày:");
        fromDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        productFromDatePicker = new DatePicker(getDefaultStartDate());
        productFromDatePicker.setPreferredSize(new Dimension(120, 30));

        JLabel toDateLabel = new JLabel("Đến ngày:");
        toDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        productToDatePicker = new DatePicker(new java.util.Date());
        productToDatePicker.setPreferredSize(new Dimension(120, 30));

        JButton applyDateButton = new JButton("Lọc");
        applyDateButton.setFont(new Font("Arial", Font.PLAIN, 14));
        applyDateButton.addActionListener(e -> filterProductsByDate());

        rightFunctionPanel.add(fromDateLabel);
        rightFunctionPanel.add(productFromDatePicker);
        rightFunctionPanel.add(toDateLabel);
        rightFunctionPanel.add(productToDatePicker);
        rightFunctionPanel.add(applyDateButton);

        // Add function panels to top control panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        topControlPanel.add(leftFunctionPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        topControlPanel.add(rightFunctionPanel, gbc);

        // Table
        inventoryTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Cho phép chỉnh sửa cột mã sản phẩm (0)
                return column == 0;
            }
        };

        inventoryTableModel.addColumn("Mã SP");
        inventoryTableModel.addColumn("Tên sản phẩm");
        inventoryTableModel.addColumn("Số lượng nhập");
        inventoryTableModel.addColumn("Danh mục");
        inventoryTableModel.addColumn("Thời gian nhập");

        inventoryTable = new JTable(inventoryTableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.getTableHeader().setReorderingAllowed(false);
        inventoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        inventoryTable.setRowHeight(30);
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 14));
        inventoryTable.setGridColor(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));

        // Add components to main panel
        JPanel controlContainer = new JPanel(new BorderLayout());
        controlContainer.setBackground(Color.WHITE);
        controlContainer.add(topControlPanel, BorderLayout.NORTH);

        panel.add(controlContainer, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void searchProducts(String keyword) {
        // Clear table
        inventoryTableModel.setRowCount(0);

        if (keyword == null || keyword.trim().isEmpty()) {
            loadProductData();
            return;
        }

        // Get all import receipts that are completed
        List<ImportReceipt> allReceipts = importReceiptController.getAllImportReceipts();
        List<ImportReceipt> completedReceipts = new ArrayList<>();

        // Filter only completed receipts
        for (ImportReceipt receipt : allReceipts) {
            if ("Đã hoàn thành".equals(receipt.getStatus())) {
                completedReceipts.add(receipt);
            }
        }

        // If no completed receipts, show empty message
        if (completedReceipts.isEmpty()) {
            inventoryTableModel.addRow(new Object[] { "", "Không có dữ liệu", "", "", "" });
            return;
        }

        // Process each completed receipt to get product details
        boolean foundMatches = false;

        for (ImportReceipt receipt : completedReceipts) {
            // Here you would normally get the receipt details from a controller/service and
            // filter by keyword
            // For this example with placeholder data, I'll simulate filtering

            String importDate = receipt.getImportDate();

            // Simulate searching through products in completed receipts
            if (receipt.getImportId().equals("PN001")) {
                // Check if any product matches the keyword
                if ("SP101".contains(keyword) || "1".contains(keyword)) {
                    inventoryTableModel.addRow(new Object[] { "SP101", "1", "1", "Danh mục 1", importDate });
                    foundMatches = true;
                }
                if ("SP102".contains(keyword) || "2".contains(keyword)) {
                    inventoryTableModel.addRow(new Object[] { "SP102", "2", "1", "Danh mục 1", importDate });
                    foundMatches = true;
                }
                if ("SP103".contains(keyword) || "3".contains(keyword)) {
                    inventoryTableModel.addRow(new Object[] { "SP103", "3", "1", "Danh mục 1", importDate });
                    foundMatches = true;
                }
            }

            // In the actual implementation, you would do something like:
            /*
             * for (ImportReceiptDetail detail : details) {
             * String productId = detail.getProductId();
             * ProductDTO product = productController.getProductById(productId);
             * String productName = product != null ? product.getProductName() : "";
             * int quantity = detail.getQuantity();
             * 
             * // Check if the product matches the search keyword
             * if (productId.contains(keyword) || productName.contains(keyword)) {
             * inventoryTableModel.addRow(new Object[] {
             * productId,
             * productName,
             * String.valueOf(quantity),
             * receipt.getImportDate()
             * });
             * foundMatches = true;
             * }
             * }
             */
        }

        // If no matches found, show empty message
        if (!foundMatches) {
            inventoryTableModel.addRow(new Object[] { "", "Không có kết quả phù hợp", "", "", "" });
        }
    }

    private void filterProductsByDate() {
        try {
            java.util.Date fromDate = productFromDatePicker.getDate();
            java.util.Date toDate = productToDatePicker.getDate();

            if (fromDate == null || toDate == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (fromDate.after(toDate)) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu không thể sau ngày kết thúc!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Format dates for display
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fromDateStr = sdf.format(fromDate);
            String toDateStr = sdf.format(toDate);

            // Set the end of day for the toDate to include the entire day
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(toDate);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            toDate = calendar.getTime();

            // Get all receipts
            List<ImportReceipt> receipts = importReceiptController.getAllImportReceipts();

            // Create a map to store total quantity for each product
            Map<String, Integer> productQuantities = new HashMap<>();
            Map<String, Double> productTotals = new HashMap<>();

            // Track products added during filtered period
            for (ImportReceipt receipt : receipts) {
                try {
                    // Parse the receipt date
                    java.util.Date receiptDate = null;
                    Object dateObj = receipt.getImportDate();

                    if (dateObj instanceof java.util.Date) {
                        receiptDate = (java.util.Date) dateObj;
                    } else if (dateObj instanceof String) {
                        receiptDate = parseDate((String) dateObj);
                    }

                    // Skip receipts outside date range
                    if (receiptDate == null || receiptDate.before(fromDate) || receiptDate.after(toDate)) {
                        continue;
                    }

                    // Get receipt details
                    ImportReceiptDetailBUS detailController = new ImportReceiptDetailBUS();
                    List<ImportReceiptDetail> details = detailController
                            .getImportReceiptDetailsByReceiptId(receipt.getImportId());

                    for (ImportReceiptDetail detail : details) {
                        String productId = detail.getProductId();
                        int quantity = detail.getQuantity();
                        double total = detail.getPrice() * quantity;

                        // Update product maps
                        if (productQuantities.containsKey(productId)) {
                            productQuantities.put(productId, productQuantities.get(productId) + quantity);
                            productTotals.put(productId, productTotals.get(productId) + total);
                        } else {
                            productQuantities.put(productId, quantity);
                            productTotals.put(productId, total);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error processing receipt " + receipt.getImportId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Clear table
            inventoryTableModel.setRowCount(0);

            // Check if we have any products in the date range
            if (productQuantities.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Không có sản phẩm nhập trong khoảng thời gian từ " + fromDateStr + " đến " + toDateStr,
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                inventoryTableModel
                        .addRow(new Object[] { "", "Không có dữ liệu trong khoảng thời gian này", "", "", "" });
                return;
            }

            // Add each product to the table
            for (String productId : productQuantities.keySet()) {
                try {
                    // Get product details
                    ProductDTO product = productController.getProductById(productId);

                    if (product != null) {
                        int quantity = productQuantities.get(productId);
                        double total = productTotals.get(productId);

                        inventoryTableModel.addRow(new Object[] {
                                product.getProductId(),
                                product.getProductName(),
                                quantity,
                                product.getCategoryName(),
                                fromDateStr + " - " + toDateStr
                        });
                    }
                } catch (Exception e) {
                    System.out.println("Error adding product " + productId + " to table: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lọc dữ liệu theo ngày: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();

            // Show error in table
            inventoryTableModel.setRowCount(0);
            inventoryTableModel.addRow(new Object[] { "", "Lỗi khi lọc dữ liệu", "", "", "" });
        }
    }

    private void loadProductData() {
        // Clear table
        inventoryTableModel.setRowCount(0);

        // Lấy danh sách phiếu nhập đã hoàn thành
        List<ImportReceipt> allReceipts = importReceiptController.getAllImportReceipts();
        List<ImportReceipt> completedReceipts = new ArrayList<>();

        // Lọc các phiếu nhập đã hoàn thành
        for (ImportReceipt receipt : allReceipts) {
            if ("Đã hoàn thành".equals(receipt.getStatus())) {
                completedReceipts.add(receipt);
            }
        }

        // Nếu không có phiếu nhập nào, hiển thị thông báo
        if (completedReceipts.isEmpty()) {
            inventoryTableModel.addRow(new Object[] { "", "Không có dữ liệu", "", "", "" });
            return;
        }

        // Duyệt qua từng phiếu nhập
        for (ImportReceipt receipt : completedReceipts) {
            String importDate = receipt.getImportDate();

            // Lấy chi tiết của phiếu nhập từ controller
            List<ImportReceiptDetail> details = new ImportReceiptDetailBUS()
                    .getImportReceiptDetailsByReceiptId(receipt.getImportId());

            if (details != null && !details.isEmpty()) {
                for (ImportReceiptDetail detail : details) {
                    String productId = detail.getProductId();
                    ProductDTO product = productController.getProductById(productId);
                    String productName = product != null ? product.getProductName() : productId;
                    String categoryName = product != null ? product.getCategoryName() : "Chưa phân loại";
                    int quantity = detail.getQuantity();

                    inventoryTableModel.addRow(new Object[] {
                            productId,
                            productName,
                            String.valueOf(quantity),
                            categoryName,
                            importDate
                    });
                }
            }
        }
    }

    private void loadInventoryStatistics() {
        // This method will be replaced by loadProductData
        loadProductData();
    }

    private void loadRevenueStatistics() {
        // Load with default dates
        loadRevenueStatistics(getDefaultStartDate(), new java.util.Date());
    }

    private void loadRevenueStatistics(java.util.Date startDate, java.util.Date endDate) {
        try {
            // Clear tables
            revenueProductTableModel.setRowCount(0);
            revenueCategoryTableModel.setRowCount(0);

            // Validate dates
            if (startDate == null || endDate == null) {
                JOptionPane.showMessageDialog(this, "Ngày không hợp lệ. Vui lòng chọn lại ngày.", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);

                // Show empty message
                revenueProductTableModel.addRow(new Object[] { "", "Ngày không hợp lệ", "", "" });
                revenueCategoryTableModel.addRow(new Object[] { "", "Ngày không hợp lệ", "", "", "" });
                return;
            }

            if (startDate.after(endDate)) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu không thể sau ngày kết thúc!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);

                // Show empty message
                revenueProductTableModel.addRow(new Object[] { "", "Ngày không hợp lệ", "", "" });
                revenueCategoryTableModel.addRow(new Object[] { "", "Ngày không hợp lệ", "", "", "" });
                return;
            }

            // Get revenue by product
            List<Object[]> productRevenue = productController.getRevenueByProduct(startDate, endDate);

            // Check if product revenue data exists
            if (productRevenue == null || productRevenue.isEmpty()) {
                revenueProductTableModel
                        .addRow(new Object[] { "", "Không có dữ liệu trong khoảng thời gian này", "", "" });
            } else {
                // Add data to product table
                for (Object[] row : productRevenue) {
                    String productId = (String) row[0];
                    String productName = (String) row[1];
                    Double revenue = ((Number) row[2]).doubleValue();

                    revenueProductTableModel.addRow(new Object[] {
                            productId,
                            productName,
                            "1", // Số lượng mặc định là 1, hoặc có thể tính từ dữ liệu thực tế
                            currencyFormatter.format(revenue)
                    });
                }
            }

            // Get revenue by category
            List<Object[]> categoryRevenue = productController.getRevenueByCategory(startDate, endDate);

            // Check if category revenue data exists
            if (categoryRevenue == null || categoryRevenue.isEmpty()) {
                revenueCategoryTableModel
                        .addRow(new Object[] { "", "Không có dữ liệu trong khoảng thời gian này", "", "", "" });
            } else {
                // Add data to category table
                for (Object[] row : categoryRevenue) {
                    String categoryId = (String) row[0];
                    String categoryName = (String) row[1];
                    Double revenue = ((Number) row[2]).doubleValue();

                    revenueCategoryTableModel.addRow(new Object[] {
                            categoryId,
                            categoryName,
                            "1", // Số lượng sản phẩm mặc định (có thể thay đổi nếu có dữ liệu thực tế)
                            "1", // Số lượng bán mặc định (có thể thay đổi nếu có dữ liệu thực tế)
                            currencyFormatter.format(revenue)
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu thống kê: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();

            // Show error in tables
            revenueProductTableModel.addRow(new Object[] { "", "Lỗi khi tải dữ liệu", "", "" });
            revenueCategoryTableModel.addRow(new Object[] { "", "Lỗi khi tải dữ liệu", "", "", "" });
        }
    }

    private java.util.Date getDefaultStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1); // 1 month ago
        return calendar.getTime();
    }

    private void refreshData() {
        loadImportReceipts();
        loadInventoryStatistics();
        loadRevenueStatistics();
    }

    private void loadImportReceipts() {
        // Clear table
        importReceiptTableModel.setRowCount(0);

        // Get import receipts
        List<ImportReceipt> receipts = importReceiptController.getAllImportReceipts();

        // Check if data exists
        if (receipts.isEmpty()) {
            importReceiptTableModel.addRow(new Object[] { "", "Không có dữ liệu", "", "", "", "" });
            return;
        }

        // Add receipts to table
        for (ImportReceipt receipt : receipts) {
            importReceiptTableModel.addRow(new Object[] {
                    receipt.getImportId(),
                    receipt.getSupplierId(),
                    receipt.getEmployeeId(),
                    formatImportDate(receipt.getImportDate()),
                    formatAmount(receipt.getTotalAmount()),
                    receipt.getStatus()
            });
        }
    }

    private void viewImportReceiptDetails() {
        int selectedRow = importReceiptTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu nhập để xem chi tiết!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String importId = importReceiptTable.getValueAt(selectedRow, 0).toString();
        // Open import receipt detail window/dialog
        // This would typically launch a new window showing the details of the selected
        // receipt
        JOptionPane.showMessageDialog(this, "Xem chi tiết phiếu nhập: " + importId, "Chi tiết phiếu nhập",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void searchImportReceipts() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshImportReceipts();
            return;
        }

        // Clear table
        importReceiptTableModel.setRowCount(0);

        // Search import receipts
        List<ImportReceipt> receipts = importReceiptController.search(keyword);

        // Check if data exists
        if (receipts.isEmpty()) {
            importReceiptTableModel.addRow(new Object[] { "", "Không có dữ liệu", "", "", "", "" });
            return;
        }

        // Add receipts to table
        for (ImportReceipt receipt : receipts) {
            importReceiptTableModel.addRow(new Object[] {
                    receipt.getImportId(),
                    receipt.getSupplierId(),
                    receipt.getEmployeeId(),
                    formatImportDate(receipt.getImportDate()),
                    formatAmount(receipt.getTotalAmount()),
                    receipt.getStatus()
            });
        }
    }

    private void refreshImportReceipts() {
        // Clear filters
        searchField.setText("");
        fromDatePicker.setDate(getDefaultStartDate());
        toDatePicker.setDate(new java.util.Date());
        minAmountField.setText("");
        maxAmountField.setText("");

        // Load all receipts
        loadImportReceipts();
    }

    private void filterByDate() {
        try {
            java.util.Date fromDate = fromDatePicker.getDate();
            java.util.Date toDate = toDatePicker.getDate();

            if (fromDate == null || toDate == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (fromDate.after(toDate)) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu không thể sau ngày kết thúc!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Format dates for display
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fromDateStr = sdf.format(fromDate);
            String toDateStr = sdf.format(toDate);

            // Set the end of day for the toDate to include the entire day
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(toDate);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            toDate = calendar.getTime();

            // Get import receipts within date range
            List<ImportReceipt> receipts = importReceiptController.getAllImportReceipts();
            List<ImportReceipt> filteredReceipts = new ArrayList<>();

            for (ImportReceipt receipt : receipts) {
                try {
                    java.util.Date importDate = null;
                    Object dateObj = receipt.getImportDate();

                    if (dateObj instanceof java.util.Date) {
                        importDate = (java.util.Date) dateObj;
                    } else if (dateObj instanceof String) {
                        importDate = parseDate((String) dateObj);
                    }

                    if (importDate != null && !importDate.before(fromDate) && !importDate.after(toDate)) {
                        filteredReceipts.add(receipt);
                    }
                } catch (Exception e) {
                    System.out
                            .println("Error parsing date for receipt " + receipt.getImportId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Clear table
            importReceiptTableModel.setRowCount(0);

            // Check if we have any receipts
            if (filteredReceipts.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Không có phiếu nhập trong khoảng thời gian từ " + fromDateStr + " đến " + toDateStr,
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                importReceiptTableModel
                        .addRow(new Object[] { "", "Không có dữ liệu trong khoảng thời gian này", "", "", "", "" });
                return;
            }

            // Populate table with filtered receipts
            for (ImportReceipt receipt : filteredReceipts) {
                importReceiptTableModel.addRow(new Object[] {
                        receipt.getImportId(),
                        receipt.getSupplierId(),
                        receipt.getEmployeeId(),
                        formatImportDate(receipt.getImportDate()),
                        formatAmount(receipt.getTotalAmount()),
                        receipt.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lọc dữ liệu: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();

            // Show error in table
            importReceiptTableModel.setRowCount(0);
            importReceiptTableModel.addRow(new Object[] { "", "Lỗi khi lọc dữ liệu", "", "", "", "" });
        }
    }

    private String formatImportDate(Object dateObj) {
        if (dateObj == null) {
            return "";
        }

        if (dateObj instanceof java.util.Date) {
            return new SimpleDateFormat("dd/MM/yyyy").format((java.util.Date) dateObj);
        } else if (dateObj instanceof String) {
            try {
                java.util.Date date = parseDate((String) dateObj);
                if (date != null) {
                    return new SimpleDateFormat("dd/MM/yyyy").format(date);
                }
            } catch (ParseException e) {
                // If parsing fails, return original string
            }
            return (String) dateObj;
        }

        // For any other type, return string representation
        return dateObj.toString();
    }

    private String formatAmount(String amountStr) {
        try {
            if (amountStr == null || amountStr.trim().isEmpty()) {
                return "0 VNĐ";
            }
            double amount = Double.parseDouble(amountStr);
            return currencyFormatter.format(amount);
        } catch (NumberFormatException e) {
            System.out.println("Error parsing amount: " + amountStr + ": " + e.getMessage());
            return amountStr; // Return original string if it can't be parsed
        }
    }

    private void filterByAmount() {
        String minStr = minAmountField.getText().trim();
        String maxStr = maxAmountField.getText().trim();

        double min = 0;
        double max = Double.MAX_VALUE;

        try {
            if (!minStr.isEmpty()) {
                min = Double.parseDouble(minStr);
            }

            if (!maxStr.isEmpty()) {
                max = Double.parseDouble(maxStr);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho giá trị tổng tiền!", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Clear table
        importReceiptTableModel.setRowCount(0);

        // Get all receipts for filtering
        List<ImportReceipt> allReceipts = importReceiptController.getAllImportReceipts();
        List<ImportReceipt> filteredReceipts = new ArrayList<>();

        // Filter by amount range
        for (ImportReceipt receipt : allReceipts) {
            try {
                double amount = Double.parseDouble(receipt.getTotalAmount());
                if (amount >= min && amount <= max) {
                    filteredReceipts.add(receipt);
                }
            } catch (NumberFormatException e) {
                // Skip receipts with invalid amount format
                System.out.println("Error parsing amount for receipt " + receipt.getImportId() + ": " + e.getMessage());
            }
        }

        // Check if data exists
        if (filteredReceipts.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không có phiếu nhập trong khoảng tiền từ " + currencyFormatter.format(min) + " đến "
                            + currencyFormatter.format(max),
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            importReceiptTableModel
                    .addRow(new Object[] { "", "Không có dữ liệu trong khoảng giá trị này", "", "", "", "" });
            return;
        }

        // Add filtered receipts to table
        for (ImportReceipt receipt : filteredReceipts) {
            importReceiptTableModel.addRow(new Object[] {
                    receipt.getImportId(),
                    receipt.getSupplierId(),
                    receipt.getEmployeeId(),
                    formatImportDate(receipt.getImportDate()),
                    formatAmount(receipt.getTotalAmount()),
                    receipt.getStatus()
            });
        }
    }

    private JPanel createRevenuePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(lightColor);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(lightColor);
        filterPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel fromLabel = new JLabel("Từ ngày:");
        fromLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        DatePicker revenueDateFrom = new DatePicker(getDefaultStartDate());
        revenueDateFrom.setPreferredSize(new Dimension(120, 30));
        revenueDateFrom.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel toLabel = new JLabel("Đến ngày:");
        toLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        DatePicker revenueDateTo = new DatePicker(new java.util.Date());
        revenueDateTo.setPreferredSize(new Dimension(120, 30));
        revenueDateTo.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton applyButton = new JButton("Áp dụng");
        applyButton.setFont(new Font("Arial", Font.PLAIN, 14));
        applyButton.setBackground(primaryColor);
        applyButton.setForeground(Color.WHITE);
        applyButton.setFocusPainted(false);
        applyButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        applyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        applyButton.addActionListener(e -> {
            try {
                java.util.Date fromDate = revenueDateFrom.getDate();
                java.util.Date toDate = revenueDateTo.getDate();

                if (fromDate == null || toDate == null) {
                    JOptionPane.showMessageDialog(panel, "Vui lòng chọn ngày hợp lệ", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (fromDate.after(toDate)) {
                    JOptionPane.showMessageDialog(panel, "Ngày bắt đầu không thể sau ngày kết thúc!", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                loadRevenueStatistics(fromDate, toDate);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Lỗi khi lọc dữ liệu: " + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        JButton resetButton = new JButton("Làm mới");
        resetButton.setFont(new Font("Arial", Font.PLAIN, 14));
        resetButton.setBackground(new Color(108, 117, 125));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(e -> {
            revenueDateFrom.setDate(getDefaultStartDate());
            revenueDateTo.setDate(new java.util.Date());
            loadRevenueStatistics(getDefaultStartDate(), new java.util.Date());
        });

        filterPanel.add(fromLabel);
        filterPanel.add(revenueDateFrom);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(toLabel);
        filterPanel.add(revenueDateTo);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(applyButton);
        filterPanel.add(resetButton);

        // Create tabbed pane for revenue tables
        JTabbedPane revenueTabbedPane = new JTabbedPane();
        revenueTabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        // Product revenue table
        revenueProductTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Cho phép chỉnh sửa cột mã sản phẩm (0)
                return column == 0;
            }
        };

        revenueProductTableModel.addColumn("Mã sản phẩm");
        revenueProductTableModel.addColumn("Tên sản phẩm");
        revenueProductTableModel.addColumn("Số lượng bán");
        revenueProductTableModel.addColumn("Doanh thu");

        revenueProductTable = new JTable(revenueProductTableModel);
        revenueProductTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        revenueProductTable.getTableHeader().setReorderingAllowed(false);
        revenueProductTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        revenueProductTable.setRowHeight(30);
        revenueProductTable.setFont(new Font("Arial", Font.PLAIN, 14));
        revenueProductTable.setGridColor(new Color(230, 230, 230));

        JScrollPane productScrollPane = new JScrollPane(revenueProductTable);
        productScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Category revenue table
        revenueCategoryTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Cho phép chỉnh sửa cột mã loại (0)
                return column == 0;
            }
        };

        revenueCategoryTableModel.addColumn("Mã loại");
        revenueCategoryTableModel.addColumn("Tên loại sản phẩm");
        revenueCategoryTableModel.addColumn("Số lượng sản phẩm");
        revenueCategoryTableModel.addColumn("Số lượng bán");
        revenueCategoryTableModel.addColumn("Doanh thu");

        revenueCategoryTable = new JTable(revenueCategoryTableModel);
        revenueCategoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        revenueCategoryTable.getTableHeader().setReorderingAllowed(false);
        revenueCategoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        revenueCategoryTable.setRowHeight(30);
        revenueCategoryTable.setFont(new Font("Arial", Font.PLAIN, 14));
        revenueCategoryTable.setGridColor(new Color(230, 230, 230));

        JScrollPane categoryScrollPane = new JScrollPane(revenueCategoryTable);
        categoryScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Add tabs to revenue tabbed pane
        revenueTabbedPane.addTab("Theo sản phẩm", productScrollPane);
        revenueTabbedPane.addTab("Theo loại sản phẩm", categoryScrollPane);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(revenueTabbedPane, BorderLayout.CENTER);

        return panel;
    }

    private void setupTableEditListeners() {
        // Thêm các cell editor để có thể chỉnh sửa dữ liệu
        setupTableEditors();

        // Listener cho bảng phiếu nhập
        importReceiptTable.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (row >= 0 && column >= 0) {
                    saveImportReceiptChanges(row, column);
                }
            }
        });

        // Listener cho bảng tồn kho
        inventoryTable.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (row >= 0 && column >= 0) {
                    saveInventoryChanges(row, column);
                }
            }
        });

        // Listener cho bảng doanh thu sản phẩm
        revenueProductTable.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (row >= 0 && column >= 0) {
                    saveRevenueProductChanges(row, column);
                }
            }
        });

        // Listener cho bảng doanh thu danh mục
        revenueCategoryTable.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (row >= 0 && column >= 0) {
                    saveRevenueCategoryChanges(row, column);
                }
            }
        });
    }

    private void setupTableEditors() {
        // Thiết lập lại các DefaultTableModel để cho phép chỉnh sửa

        // Bảng phiếu nhập
        DefaultTableModel importReceiptModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 1 || column == 2;
            }
        };
        // Sao chép dữ liệu hiện tại vào mô hình mới
        for (int i = 0; i < importReceiptTableModel.getColumnCount(); i++) {
            importReceiptModel.addColumn(importReceiptTableModel.getColumnName(i));
        }
        for (int i = 0; i < importReceiptTableModel.getRowCount(); i++) {
            Vector<Object> rowData = new Vector<>();
            for (int j = 0; j < importReceiptTableModel.getColumnCount(); j++) {
                rowData.add(importReceiptTableModel.getValueAt(i, j));
            }
            importReceiptModel.addRow(rowData);
        }
        importReceiptTable.setModel(importReceiptModel);
        importReceiptTableModel = importReceiptModel;

        // Bảng tồn kho
        DefaultTableModel inventoryModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        // Sao chép dữ liệu hiện tại vào mô hình mới
        for (int i = 0; i < inventoryTableModel.getColumnCount(); i++) {
            inventoryModel.addColumn(inventoryTableModel.getColumnName(i));
        }
        for (int i = 0; i < inventoryTableModel.getRowCount(); i++) {
            Vector<Object> rowData = new Vector<>();
            for (int j = 0; j < inventoryTableModel.getColumnCount(); j++) {
                rowData.add(inventoryTableModel.getValueAt(i, j));
            }
            inventoryModel.addRow(rowData);
        }
        inventoryTable.setModel(inventoryModel);
        inventoryTableModel = inventoryModel;

        // Bảng doanh thu sản phẩm
        DefaultTableModel revenueProductModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        // Sao chép dữ liệu hiện tại vào mô hình mới
        for (int i = 0; i < revenueProductTableModel.getColumnCount(); i++) {
            revenueProductModel.addColumn(revenueProductTableModel.getColumnName(i));
        }
        for (int i = 0; i < revenueProductTableModel.getRowCount(); i++) {
            Vector<Object> rowData = new Vector<>();
            for (int j = 0; j < revenueProductTableModel.getColumnCount(); j++) {
                rowData.add(revenueProductTableModel.getValueAt(i, j));
            }
            revenueProductModel.addRow(rowData);
        }
        revenueProductTable.setModel(revenueProductModel);
        revenueProductTableModel = revenueProductModel;

        // Bảng doanh thu danh mục
        DefaultTableModel revenueCategoryModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        // Sao chép dữ liệu hiện tại vào mô hình mới
        for (int i = 0; i < revenueCategoryTableModel.getColumnCount(); i++) {
            revenueCategoryModel.addColumn(revenueCategoryTableModel.getColumnName(i));
        }
        for (int i = 0; i < revenueCategoryTableModel.getRowCount(); i++) {
            Vector<Object> rowData = new Vector<>();
            for (int j = 0; j < revenueCategoryTableModel.getColumnCount(); j++) {
                rowData.add(revenueCategoryTableModel.getValueAt(i, j));
            }
            revenueCategoryModel.addRow(rowData);
        }
        revenueCategoryTable.setModel(revenueCategoryModel);
        revenueCategoryTableModel = revenueCategoryModel;

        // Đảm bảo bảng cho phép chỉnh sửa
        importReceiptTable.setEnabled(true);
        inventoryTable.setEnabled(true);
        revenueProductTable.setEnabled(true);
        revenueCategoryTable.setEnabled(true);
    }

    private void saveImportReceiptChanges(int row, int column) {
        try {
            String oldId = null;
            String newValue = importReceiptTable.getValueAt(row, column).toString();

            if (column == 0) { // Mã phiếu
                oldId = importReceiptTable.getValueAt(row, 0).toString();
                // Thực hiện cập nhật mã phiếu
                ImportReceipt receipt = importReceiptController.getImportReceiptById(oldId);
                if (receipt != null) {
                    receipt.setImportId(newValue);
                    importReceiptController.updateImportReceipt(receipt);
                    JOptionPane.showMessageDialog(this, "Đã cập nhật mã phiếu: " + oldId + " thành " + newValue);
                }
            } else if (column == 1) { // Nhà cung cấp
                oldId = importReceiptTable.getValueAt(row, 0).toString();
                // Thực hiện cập nhật mã nhà cung cấp
                ImportReceipt receipt = importReceiptController.getImportReceiptById(oldId);
                if (receipt != null) {
                    receipt.setSupplierId(newValue);
                    importReceiptController.updateImportReceipt(receipt);
                    JOptionPane.showMessageDialog(this,
                            "Đã cập nhật mã nhà cung cấp của phiếu " + oldId + " thành " + newValue);
                }
            } else if (column == 2) { // Người tạo
                oldId = importReceiptTable.getValueAt(row, 0).toString();
                // Thực hiện cập nhật mã người tạo
                ImportReceipt receipt = importReceiptController.getImportReceiptById(oldId);
                if (receipt != null) {
                    receipt.setEmployeeId(newValue);
                    importReceiptController.updateImportReceipt(receipt);
                    JOptionPane.showMessageDialog(this,
                            "Đã cập nhật mã người tạo của phiếu " + oldId + " thành " + newValue);
                }
            }

            // Refresh lại dữ liệu sau khi cập nhật
            refreshImportReceipts();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveInventoryChanges(int row, int column) {
        try {
            if (column == 0) { // Mã sản phẩm
                String oldId = inventoryTable.getValueAt(row, 0).toString();
                String newValue = inventoryTable.getValueAt(row, column).toString();
                // Thực hiện cập nhật mã sản phẩm
                ProductDTO product = productController.getProductById(oldId);
                if (product != null) {
                    product.setProductId(newValue);
                    productController.updateProduct(product);
                    JOptionPane.showMessageDialog(this, "Đã cập nhật mã sản phẩm: " + oldId + " thành " + newValue);
                }

                // Refresh lại dữ liệu sau khi cập nhật
                loadProductData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveRevenueProductChanges(int row, int column) {
        try {
            if (column == 0) { // Mã sản phẩm
                String oldId = revenueProductTable.getValueAt(row, 0).toString();
                String newValue = revenueProductTable.getValueAt(row, column).toString();
                // Thực hiện cập nhật mã sản phẩm
                ProductDTO product = productController.getProductById(oldId);
                if (product != null) {
                    product.setProductId(newValue);
                    productController.updateProduct(product);
                    JOptionPane.showMessageDialog(this, "Đã cập nhật mã sản phẩm: " + oldId + " thành " + newValue);
                }

                // Refresh lại dữ liệu sau khi cập nhật
                loadRevenueStatistics();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveRevenueCategoryChanges(int row, int column) {
        try {
            if (column == 0) { // Mã loại sản phẩm
                String oldId = revenueCategoryTable.getValueAt(row, 0).toString();
                String newValue = revenueCategoryTable.getValueAt(row, column).toString();
                // Thực hiện cập nhật mã loại sản phẩm
                ProductCategoryDTO category = productController.getCategoryById(oldId);
                if (category != null) {
                    category.setCategoryId(newValue);
                    productController.addCategory(newValue, category.getCategoryName(), category.getDescription());
                    JOptionPane.showMessageDialog(this,
                            "Đã cập nhật mã loại sản phẩm: " + oldId + " thành " + newValue);
                }

                // Refresh lại dữ liệu sau khi cập nhật
                loadRevenueStatistics();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Helper method to parse dates in multiple formats
     * 
     * @param dateString The date string to parse
     * @return A Date object or null if parsing fails
     */
    private java.util.Date parseDate(String dateStr) throws ParseException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        // Try different date formats
        String[] dateFormats = {
                "yyyy-MM-dd", // SQL date format
                "yyyy-MM-dd HH:mm:ss",
                "dd/MM/yyyy"
        };

        for (String format : dateFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                return sdf.parse(dateStr);
            } catch (ParseException e) {
                // Try next format
            }
        }

        // If all formats fail, throw exception
        throw new ParseException("Unable to parse date: " + dateStr, 0);
    }
}