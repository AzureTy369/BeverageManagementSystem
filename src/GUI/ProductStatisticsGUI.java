package GUI;

import BUS.ProductBUS;
import BUS.ProductDetailBUS;
import BUS.ImportReceiptBUS;
import DTO.ImportReceipt;
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
                return false;
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

        DatePicker productFromDatePicker = new DatePicker(getDefaultStartDate());
        productFromDatePicker.setPreferredSize(new Dimension(120, 30));

        JLabel toDateLabel = new JLabel("Đến ngày:");
        toDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        DatePicker productToDatePicker = new DatePicker(new java.util.Date());
        productToDatePicker.setPreferredSize(new Dimension(120, 30));

        JButton applyDateButton = new JButton("Lọc");
        applyDateButton.setFont(new Font("Arial", Font.PLAIN, 14));
        applyDateButton.addActionListener(e -> filterProductsByDate(
                productFromDatePicker.getDate(),
                productToDatePicker.getDate()));

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
                return false;
            }
        };

        inventoryTableModel.addColumn("Mã SP");
        inventoryTableModel.addColumn("Tên sản phẩm");
        inventoryTableModel.addColumn("Số lượng nhập");
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
            inventoryTableModel.addRow(new Object[] { "", "Không có dữ liệu", "", "" });
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
                    inventoryTableModel.addRow(new Object[] { "SP101", "1", "1", importDate });
                    foundMatches = true;
                }
                if ("SP102".contains(keyword) || "2".contains(keyword)) {
                    inventoryTableModel.addRow(new Object[] { "SP102", "2", "1", importDate });
                    foundMatches = true;
                }
                if ("SP103".contains(keyword) || "3".contains(keyword)) {
                    inventoryTableModel.addRow(new Object[] { "SP103", "3", "1", importDate });
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
            inventoryTableModel.addRow(new Object[] { "", "Không có kết quả phù hợp", "", "" });
        }
    }

    private void filterProductsByDate(java.util.Date fromDate, java.util.Date toDate) {
        // Clear table
        inventoryTableModel.setRowCount(0);

        if (fromDate.after(toDate)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không thể sau ngày kết thúc!", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get all import receipts that are completed
        List<ImportReceipt> allReceipts = importReceiptController.getAllImportReceipts();
        List<ImportReceipt> filteredReceipts = new ArrayList<>();

        // Filter completed receipts within the date range
        for (ImportReceipt receipt : allReceipts) {
            if ("Đã hoàn thành".equals(receipt.getStatus())) {
                try {
                    java.util.Date receiptDate = java.sql.Date.valueOf(receipt.getImportDate());
                    if (!receiptDate.before(fromDate) && !receiptDate.after(toDate)) {
                        filteredReceipts.add(receipt);
                    }
                } catch (Exception e) {
                    // Handle date parsing errors
                }
            }
        }

        // If no filtered receipts, show empty message
        if (filteredReceipts.isEmpty()) {
            inventoryTableModel.addRow(new Object[] { "", "Không có dữ liệu trong khoảng thời gian này", "", "" });
            return;
        }

        // Process each filtered receipt to get product details
        for (ImportReceipt receipt : filteredReceipts) {
            // Placeholder code similar to loadProductData
            String importDate = receipt.getImportDate();

            if (receipt.getImportId().equals("PN001")) {
                inventoryTableModel.addRow(new Object[] { "SP101", "1", "1", importDate });
                inventoryTableModel.addRow(new Object[] { "SP102", "2", "1", importDate });
                inventoryTableModel.addRow(new Object[] { "SP103", "3", "1", importDate });
            } else if (receipt.getImportId().equals("PN002")) {
                inventoryTableModel.addRow(new Object[] { "SP102", "2", "2", importDate });
            } else if (receipt.getImportId().equals("PN003")) {
                inventoryTableModel.addRow(new Object[] { "SP103", "3", "2", importDate });
            }

            // Actual implementation would iterate through receipt details
        }
    }

    private void loadProductData() {
        // Clear table
        inventoryTableModel.setRowCount(0);

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
            inventoryTableModel.addRow(new Object[] { "", "Không có dữ liệu", "", "" });
            return;
        }

        // Process each completed receipt to get product details
        for (ImportReceipt receipt : completedReceipts) {
            // Here you would normally get the receipt details from a controller/service
            // For each product in the receipt details, add it to the table
            // Since we don't have the actual implementation, I'll show placeholder code

            // Example: get import receipt details
            // List<ImportReceiptDetail> details =
            // importReceiptDetailController.getImportReceiptDetailsByReceiptId(receipt.getImportId());

            // For this example, I'll use dummy data based on the screenshot
            // In your real implementation, you would iterate through the actual receipt
            // details

            String importDate = receipt.getImportDate();

            if (receipt.getImportId().equals("PN001")) {
                inventoryTableModel.addRow(new Object[] { "SP101", "1", "1", importDate });
                inventoryTableModel.addRow(new Object[] { "SP102", "2", "1", importDate });
                inventoryTableModel.addRow(new Object[] { "SP103", "3", "1", importDate });
            } else if (receipt.getImportId().equals("PN002")) {
                inventoryTableModel.addRow(new Object[] { "SP102", "2", "2", importDate });
            } else if (receipt.getImportId().equals("PN003")) {
                inventoryTableModel.addRow(new Object[] { "SP103", "3", "2", importDate });
            }

            // In the actual implementation, you would do something like:
            /*
             * for (ImportReceiptDetail detail : details) {
             * String productId = detail.getProductId();
             * ProductDTO product = productController.getProductById(productId);
             * String productName = product != null ? product.getProductName() : "";
             * int quantity = detail.getQuantity();
             * 
             * inventoryTableModel.addRow(new Object[] {
             * productId,
             * productName,
             * String.valueOf(quantity),
             * receipt.getImportDate()
             * });
             * }
             */
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
        // Clear tables
        revenueProductTableModel.setRowCount(0);
        revenueCategoryTableModel.setRowCount(0);

        // Get revenue by product
        List<Object[]> productRevenue = productController.getRevenueByProduct(startDate, endDate);

        // Check if product revenue data exists
        if (productRevenue.isEmpty()) {
            revenueProductTableModel.addRow(new Object[] { "", "Không có dữ liệu", "", "" });
        } else {
            // Add data to product table
            for (Object[] row : productRevenue) {
                String productId = (String) row[0];
                String productName = (String) row[1];
                Double revenue = ((Number) row[2]).doubleValue();

                revenueProductTableModel.addRow(new Object[] {
                        productId,
                        productName,
                        "N/A", // Placeholder for quantity since not returned by the DAO method
                        currencyFormatter.format(revenue)
                });
            }
        }

        // Get revenue by category
        List<Object[]> categoryRevenue = productController.getRevenueByCategory(startDate, endDate);

        // Check if category revenue data exists
        if (categoryRevenue.isEmpty()) {
            revenueCategoryTableModel.addRow(new Object[] { "", "Không có dữ liệu", "", "", "" });
        } else {
            // Add data to category table
            for (Object[] row : categoryRevenue) {
                String categoryId = (String) row[0];
                String categoryName = (String) row[1];
                Double revenue = ((Number) row[2]).doubleValue();

                revenueCategoryTableModel.addRow(new Object[] {
                        categoryId,
                        categoryName,
                        "N/A", // Placeholder for product count since not returned by the DAO method
                        "N/A", // Placeholder for sold quantity since not returned by the DAO method
                        currencyFormatter.format(revenue)
                });
            }
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
                    receipt.getImportDate(),
                    currencyFormatter.format(Double.parseDouble(receipt.getTotalAmount())),
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
                    receipt.getImportDate(),
                    currencyFormatter.format(Double.parseDouble(receipt.getTotalAmount())),
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
        java.util.Date fromDate = fromDatePicker.getDate();
        java.util.Date toDate = toDatePicker.getDate();

        if (fromDate.after(toDate)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không thể sau ngày kết thúc!", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Clear table
        importReceiptTableModel.setRowCount(0);

        // Get all receipts for filtering
        List<ImportReceipt> allReceipts = importReceiptController.getAllImportReceipts();
        List<ImportReceipt> filteredReceipts = new ArrayList<>();

        // Filter by date range
        for (ImportReceipt receipt : allReceipts) {
            try {
                java.util.Date receiptDate = java.sql.Date.valueOf(receipt.getImportDate());
                if (!receiptDate.before(fromDate) && !receiptDate.after(toDate)) {
                    filteredReceipts.add(receipt);
                }
            } catch (Exception e) {
                // Handle date parsing errors
            }
        }

        // Check if data exists
        if (filteredReceipts.isEmpty()) {
            importReceiptTableModel.addRow(new Object[] { "", "Không có dữ liệu", "", "", "", "" });
            return;
        }

        // Add filtered receipts to table
        for (ImportReceipt receipt : filteredReceipts) {
            importReceiptTableModel.addRow(new Object[] {
                    receipt.getImportId(),
                    receipt.getSupplierId(),
                    receipt.getEmployeeId(),
                    receipt.getImportDate(),
                    currencyFormatter.format(Double.parseDouble(receipt.getTotalAmount())),
                    receipt.getStatus()
            });
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
            } catch (Exception e) {
                // Handle parsing errors
            }
        }

        // Check if data exists
        if (filteredReceipts.isEmpty()) {
            importReceiptTableModel.addRow(new Object[] { "", "Không có dữ liệu", "", "", "", "" });
            return;
        }

        // Add filtered receipts to table
        for (ImportReceipt receipt : filteredReceipts) {
            importReceiptTableModel.addRow(new Object[] {
                    receipt.getImportId(),
                    receipt.getSupplierId(),
                    receipt.getEmployeeId(),
                    receipt.getImportDate(),
                    currencyFormatter.format(Double.parseDouble(receipt.getTotalAmount())),
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

        DatePicker fromDatePicker = new DatePicker(getDefaultStartDate());
        fromDatePicker.setPreferredSize(new Dimension(120, 30));
        fromDatePicker.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel toLabel = new JLabel("Đến ngày:");
        toLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        DatePicker toDatePicker = new DatePicker(new java.util.Date());
        toDatePicker.setPreferredSize(new Dimension(120, 30));
        toDatePicker.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton applyButton = new JButton("Áp dụng");
        applyButton.setFont(new Font("Arial", Font.PLAIN, 14));
        applyButton.setBackground(primaryColor);
        applyButton.setForeground(Color.WHITE);
        applyButton.setFocusPainted(false);
        applyButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        applyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        applyButton.addActionListener(e -> loadRevenueStatistics(fromDatePicker.getDate(), toDatePicker.getDate()));

        filterPanel.add(fromLabel);
        filterPanel.add(fromDatePicker);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(toLabel);
        filterPanel.add(toDatePicker);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(applyButton);

        // Create tabbed pane for revenue tables
        JTabbedPane revenueTabbedPane = new JTabbedPane();
        revenueTabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        // Product revenue table
        revenueProductTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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
                return false;
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
}