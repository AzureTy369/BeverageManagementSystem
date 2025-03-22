package view;

import controller.ProductController;
import model.Product;
import view.utils.DatePicker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Calendar;

public class ProductStatisticsPanel extends JPanel {
    private ProductController productController;

    private JTabbedPane tabbedPane;
    private JTable bestSellingTable;
    private JTable inventoryTable;
    private JTable revenueProductTable;
    private JTable revenueCategoryTable;

    private DefaultTableModel bestSellingTableModel;
    private DefaultTableModel inventoryTableModel;
    private DefaultTableModel revenueProductTableModel;
    private DefaultTableModel revenueCategoryTableModel;

    private JComboBox<String> inventoryFilterComboBox;
    private JSpinner limitSpinner;

    // Colors
    private Color primaryColor = new Color(0, 123, 255);
    private Color lightColor = new Color(248, 249, 250);
    private Color textColor = Color.BLACK;

    private DecimalFormat currencyFormatter = new DecimalFormat("#,##0 VNĐ");
    private DecimalFormat numberFormatter = new DecimalFormat("#,##0");

    public ProductStatisticsPanel(ProductController productController) {
        this.productController = productController;

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
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(lightColor);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("\uD83D\uDCCA Thống kê sản phẩm");
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
        refreshButton.addActionListener(e -> refreshData());

        actionPanel.add(refreshButton);

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(actionPanel, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);
    }

    private void createTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        // Create tabs
        JPanel bestSellingPanel = createBestSellingPanel();
        JPanel inventoryPanel = createInventoryPanel();
        JPanel revenuePanel = createRevenuePanel();

        // Add tabs to tabbed pane
        tabbedPane.addTab("Sản phẩm bán chạy", bestSellingPanel);
        tabbedPane.addTab("Tồn kho", inventoryPanel);
        tabbedPane.addTab("Doanh thu", revenuePanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createBestSellingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(Color.WHITE);

        JLabel limitLabel = new JLabel("Số lượng hiển thị:");
        limitLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(10, 1, 100, 1);
        limitSpinner = new JSpinner(spinnerModel);
        limitSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        limitSpinner.setPreferredSize(new Dimension(80, 30));

        JButton applyButton = new JButton("Áp dụng");
        applyButton.setFont(new Font("Arial", Font.PLAIN, 14));
        applyButton.setBackground(primaryColor);
        applyButton.setForeground(Color.WHITE);
        applyButton.setFocusPainted(false);
        applyButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        applyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        applyButton.addActionListener(e -> loadBestSellingProducts());

        controlPanel.add(limitLabel);
        controlPanel.add(limitSpinner);
        controlPanel.add(applyButton);

        // Table
        bestSellingTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bestSellingTableModel.addColumn("Mã SP");
        bestSellingTableModel.addColumn("Tên sản phẩm");
        bestSellingTableModel.addColumn("Loại sản phẩm");
        bestSellingTableModel.addColumn("Giá bán");
        bestSellingTableModel.addColumn("Tổng bán");

        bestSellingTable = new JTable(bestSellingTableModel);
        bestSellingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bestSellingTable.getTableHeader().setReorderingAllowed(false);
        bestSellingTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        bestSellingTable.setRowHeight(30);
        bestSellingTable.setFont(new Font("Arial", Font.PLAIN, 14));
        bestSellingTable.setGridColor(new Color(230, 230, 230));
        bestSellingTable.setShowGrid(true);

        JScrollPane scrollPane = new JScrollPane(bestSellingTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(lightColor);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(lightColor);
        filterPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        inventoryFilterComboBox = new JComboBox<>(new String[] { "Tồn kho nhiều nhất", "Tồn kho ít nhất" });
        inventoryFilterComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        inventoryFilterComboBox.addActionListener(e -> loadInventoryStatistics());

        JLabel limitLabel = new JLabel("Số lượng hiển thị:");
        limitLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(20, 5, 100, 5);
        JSpinner inventoryLimitSpinner = new JSpinner(spinnerModel);
        inventoryLimitSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        inventoryLimitSpinner.addChangeListener(e -> loadInventoryStatistics());

        filterPanel.add(inventoryFilterComboBox);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(limitLabel);
        filterPanel.add(Box.createHorizontalStrut(5));
        filterPanel.add(inventoryLimitSpinner);

        // Table
        inventoryTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        inventoryTableModel.addColumn("Mã sản phẩm");
        inventoryTableModel.addColumn("Tên sản phẩm");
        inventoryTableModel.addColumn("Loại sản phẩm");
        inventoryTableModel.addColumn("Số lượng tồn kho");

        inventoryTable = new JTable(inventoryTableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.getTableHeader().setReorderingAllowed(false);
        inventoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        inventoryTable.setRowHeight(30);
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 14));
        inventoryTable.setGridColor(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
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

    private java.util.Date getDefaultStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1); // 1 month ago
        return calendar.getTime();
    }

    private void refreshData() {
        loadBestSellingProducts();
        loadInventoryStatistics();
        loadRevenueStatistics();
    }

    private void loadBestSellingProducts() {
        // Clear table
        bestSellingTableModel.setRowCount(0);

        // Get limit value
        int limit = (Integer) limitSpinner.getValue();

        // Get best selling products
        List<Product> bestSellingProducts = productController.getBestSellingProducts(limit);

        // Check if data exists
        if (bestSellingProducts.isEmpty()) {
            bestSellingTableModel.addRow(new Object[] { "", "Không có dữ liệu", "", "", "" });
            return;
        }

        // Add products to table
        for (Product product : bestSellingProducts) {
            bestSellingTableModel.addRow(new Object[] {
                    product.getProductId(),
                    product.getProductName(),
                    product.getCategoryName(),
                    currencyFormatter.format(product.getPrice()),
                    numberFormatter.format(product.getSoldQuantity())
            });
        }
    }

    private void loadInventoryStatistics() {
        // Clear table
        inventoryTableModel.setRowCount(0);

        // Get filter value (true = low stock, false = high stock)
        boolean lowStock = inventoryFilterComboBox.getSelectedIndex() == 1;

        // Get inventory statistics
        List<Object[]> inventoryStats = productController.getInventoryStatistics(lowStock);

        // Check if data exists
        if (inventoryStats.isEmpty()) {
            inventoryTableModel.addRow(new Object[] { "", "Không có dữ liệu", "", "" });
            return;
        }

        // Add data to table
        for (Object[] row : inventoryStats) {
            String productId = (String) row[0];
            String productName = (String) row[1];
            Integer quantity = ((Number) row[2]).intValue(); // Column 2 is TonKho (Integer)
            String categoryName = (String) row[3]; // Column 3 is TenLoaiSP (String)

            inventoryTableModel.addRow(new Object[] {
                    productId,
                    productName,
                    categoryName,
                    numberFormatter.format(quantity)
            });
        }
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
                Integer quantity = ((Number) row[2]).intValue();
                Double revenue = ((Number) row[3]).doubleValue();

                revenueProductTableModel.addRow(new Object[] {
                        productId,
                        productName,
                        numberFormatter.format(quantity),
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
                Integer productCount = ((Number) row[2]).intValue();
                Integer quantity = ((Number) row[3]).intValue();
                Double revenue = ((Number) row[4]).doubleValue();

                revenueCategoryTableModel.addRow(new Object[] {
                        categoryId,
                        categoryName,
                        numberFormatter.format(productCount),
                        numberFormatter.format(quantity),
                        currencyFormatter.format(revenue)
                });
            }
        }
    }
}