package GUI;

import BUS.ProductBUS;
import BUS.ProductDetailBUS;
import DTO.ProductDTO;
import BUS.InventoryBUS;
import BUS.ImportReceiptDetailBUS;
import DTO.ImportReceiptDetail;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import GUI.utils.ExcelUtils;
import GUI.utils.ButtonHelper;

public class InventoryGUI extends JPanel {
    private ProductBUS productController;

    private JTable inventoryTable;
    private DefaultTableModel tableModel;

    private JTextField inventorySearchField;
    private JPanel advancedSearchPanel;

    private final Color primaryColor = new Color(0, 123, 255);
    private final Color lightColor = new Color(248, 249, 250);

    public InventoryGUI(ProductBUS productController, ProductDetailBUS productDetailController) {
        this.productController = productController;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(lightColor);

        createAdvancedSearchPanel();
        createGUI();
        refreshInventoryData();
    }

    private void createGUI() {
        setLayout(new BorderLayout());

        // Panel tiêu đề ở trên cùng
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(lightColor);

        JLabel titleLabel = new JLabel("Quản Lý Tồn Kho");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(primaryColor);

        titlePanel.add(titleLabel);

        // Panel chức năng ở dưới tiêu đề
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        // NHÓM 1: Panel chức năng bên trái
        JPanel leftFunctionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
        leftFunctionPanel.setBackground(new Color(240, 240, 240));
        leftFunctionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Nút xuất Excel
        JButton exportExcelButton = createOutlineButton("Xuất Excel", primaryColor);
        exportExcelButton.setMargin(new Insets(3, 8, 3, 8));
        exportExcelButton.setFont(new Font("Arial", Font.BOLD, 16));
        exportExcelButton.addActionListener(e -> exportToExcel());

        // Thêm các nút vào panel bên trái
        leftFunctionPanel.add(exportExcelButton);

        // NHÓM 2: Panel tìm kiếm bên phải
        JPanel rightSearchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
        rightSearchPanel.setBackground(new Color(240, 240, 240));
        rightSearchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setForeground(Color.BLACK);
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // TextField tìm kiếm
        inventorySearchField = new JTextField(15);
        inventorySearchField.setPreferredSize(new Dimension(180, 25));
        inventorySearchField.setFont(new Font("Arial", Font.PLAIN, 12));
        inventorySearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchInventory();
                }
            }
        });

        // Nút tìm kiếm
        JButton searchButton = createOutlineButton("Tìm kiếm", primaryColor);
        searchButton.setMargin(new Insets(3, 8, 3, 8));
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));
        searchButton.addActionListener(e -> searchInventory());

        // Nút làm mới
        JButton refreshButton = createOutlineButton("Làm mới", new Color(23, 162, 184));
        refreshButton.setMargin(new Insets(3, 8, 3, 8));
        refreshButton.setFont(new Font("Arial", Font.BOLD, 16));
        refreshButton.addActionListener(e -> refreshInventoryData());

        // Thêm các thành phần vào panel tìm kiếm
        rightSearchPanel.add(searchLabel);
        rightSearchPanel.add(inventorySearchField);
        rightSearchPanel.add(searchButton);
        rightSearchPanel.add(refreshButton);

        // Thêm hai panel vào panel chính ở trên cùng
        JPanel topFunctionContainer = new JPanel(new GridBagLayout());
        topFunctionContainer.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 0, 0, 5);
        topFunctionContainer.add(leftFunctionPanel, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 5, 0, 0);
        topFunctionContainer.add(rightSearchPanel, gbc);

        topPanel.add(topFunctionContainer, BorderLayout.CENTER);

        // Thêm panel tìm kiếm nâng cao
        JPanel advancedSearchContainer = new JPanel(new BorderLayout());
        advancedSearchContainer.setOpaque(false);
        advancedSearchContainer.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));

        JPanel advancedSearchBordered = new JPanel(new BorderLayout());
        advancedSearchBordered.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        advancedSearchBordered.setBackground(new Color(240, 243, 247));

        advancedSearchBordered.add(advancedSearchPanel, BorderLayout.CENTER);
        advancedSearchContainer.add(advancedSearchBordered, BorderLayout.CENTER);

        advancedSearchPanel.setVisible(true);

        // Tạo bảng tồn kho
        createInventoryTable();
        JScrollPane tableScrollPane = new JScrollPane(inventoryTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.getViewport().setBackground(lightColor);

        // Panel chứa bảng có viền
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        JPanel borderedTablePanel = new JPanel(new BorderLayout());
        borderedTablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        borderedTablePanel.add(tableScrollPane, BorderLayout.CENTER);

        tableContainer.add(borderedTablePanel, BorderLayout.CENTER);

        // Panel trạng thái hiển thị thông tin tổng quan
        JPanel statusPanel = createStatusPanel();

        // Panel chứa tổng thể (ngoại trừ topPanel)
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(advancedSearchContainer, BorderLayout.NORTH);
        contentPanel.add(tableContainer, BorderLayout.CENTER);
        contentPanel.add(statusPanel, BorderLayout.SOUTH);

        // Tạo container chứa tất cả panel phía trên
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(topPanel, BorderLayout.CENTER);

        // Thêm vào layout chính
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void createAdvancedSearchPanel() {
        advancedSearchPanel = new JPanel();
        advancedSearchPanel.setLayout(new BoxLayout(advancedSearchPanel, BoxLayout.Y_AXIS));
        advancedSearchPanel.setBackground(new Color(240, 243, 247));

        // Tiêu đề và giới thiệu
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel("Tìm kiếm nâng cao - Nhập các điều kiện tìm kiếm");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(headerLabel);
        advancedSearchPanel.add(headerPanel);

        // Panel chứa các điều kiện tìm kiếm
        JPanel conditionsPanel = new JPanel(new GridBagLayout());
        conditionsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mã sản phẩm
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel productIdLabel = new JLabel("Mã sản phẩm:");
        productIdLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(productIdLabel, gbc);

        gbc.gridx = 1;
        JTextField productIdField = new JTextField(15);
        productIdField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(productIdField, gbc);

        // Tên sản phẩm
        gbc.gridx = 2;
        JLabel productNameLabel = new JLabel("Tên sản phẩm:");
        productNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(productNameLabel, gbc);

        gbc.gridx = 3;
        JTextField productNameField = new JTextField(15);
        productNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(productNameField, gbc);

        // Danh mục
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel categoryLabel = new JLabel("Danh mục:");
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(categoryLabel, gbc);

        gbc.gridx = 1;
        JTextField categoryField = new JTextField(15);
        categoryField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(categoryField, gbc);

        // Số lượng tồn
        gbc.gridx = 2;
        JLabel quantityLabel = new JLabel("Số lượng tồn:");
        quantityLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(quantityLabel, gbc);

        gbc.gridx = 3;
        JTextField quantityField = new JTextField(15);
        quantityField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(quantityField, gbc);

        advancedSearchPanel.add(conditionsPanel);

        // Panel chứa nút tìm kiếm và điều kiện kết hợp
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.setOpaque(false);

        JRadioButton andRadio = new JRadioButton("Tất cả điều kiện");
        andRadio.setFont(new Font("Arial", Font.PLAIN, 14));
        andRadio.setOpaque(false);
        andRadio.setSelected(true);

        JRadioButton orRadio = new JRadioButton("Bất kỳ điều kiện nào");
        orRadio.setFont(new Font("Arial", Font.PLAIN, 14));
        orRadio.setOpaque(false);

        ButtonGroup group = new ButtonGroup();
        group.add(andRadio);
        group.add(orRadio);

        JButton searchButton = createOutlineButton("Tìm kiếm", primaryColor);
        searchButton.setForeground(Color.BLACK);
        searchButton.addActionListener(e -> {
            // Lấy thông tin từ các ô tìm kiếm
            String productId = productIdField.getText().trim();
            String productName = productNameField.getText().trim();
            String category = categoryField.getText().trim();
            String quantity = quantityField.getText().trim();
            boolean isAnd = andRadio.isSelected();

            // Gọi phương thức tìm kiếm nâng cao
            advancedSearchInventory(productId, productName, category, quantity, isAnd);
        });

        JButton clearButton = createOutlineButton("Xóa tìm kiếm", new Color(108, 117, 125));
        clearButton.setForeground(Color.BLACK);
        clearButton.addActionListener(e -> {
            productIdField.setText("");
            productNameField.setText("");
            categoryField.setText("");
            quantityField.setText("");
            refreshInventoryData();
        });

        controlPanel.add(andRadio);
        controlPanel.add(orRadio);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(searchButton);
        controlPanel.add(clearButton);

        advancedSearchPanel.add(controlPanel);
    }

    private void createInventoryTable() {
        String[] columnNames = { "Mã SP", "Tên SP", "Danh mục", "Đơn vị", "Giá nhập", "Tồn kho", "Trạng thái" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        inventoryTable = new JTable(tableModel);
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 14));
        inventoryTable.setRowHeight(25);
        inventoryTable.setGridColor(new Color(230, 230, 230));
        inventoryTable.setSelectionBackground(new Color(173, 216, 230));
        inventoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        inventoryTable.getTableHeader().setBackground(primaryColor);
        inventoryTable.getTableHeader().setForeground(Color.BLACK);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column width
        inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        inventoryTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        inventoryTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        inventoryTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        inventoryTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        inventoryTable.getColumnModel().getColumn(6).setPreferredWidth(120);
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(222, 226, 230)),
                BorderFactory.createEmptyBorder(10, 0, 0, 0)));
        statusPanel.setOpaque(false);

        JLabel statusLabel = new JLabel("Tổng số sản phẩm: 0");
        statusLabel.setName("statusLabel");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        statusPanel.add(statusLabel, BorderLayout.WEST);

        updateStatusPanel();

        return statusPanel;
    }

    /**
     * Cập nhật hiển thị tổng số sản phẩm trong kho
     */
    private void updateStatusPanel() {
        int productCount = productController.getAllProducts().size();

        for (Component comp : this.getComponents()) {
            if (comp instanceof JPanel) {
                searchStatusLabelInPanel((JPanel) comp, productCount);
            }
        }
    }

    private void searchStatusLabelInPanel(JPanel panel, int productCount) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JLabel && "statusLabel".equals(comp.getName())) {
                ((JLabel) comp).setText("Tổng số sản phẩm: " + productCount);
                return;
            } else if (comp instanceof JPanel) {
                searchStatusLabelInPanel((JPanel) comp, productCount);
            }
        }
    }

    private JButton createOutlineButton(String text, Color color) {
        return ButtonHelper.createButton(text, color);
    }

    private void searchInventory() {
        String keyword = inventorySearchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshInventoryData();
            return;
        }

        // Lấy danh sách sản phẩm từ tìm kiếm
        List<ProductDTO> results = productController.searchProducts(keyword);

        // Khởi tạo InventoryBUS để lấy thông tin tồn kho
        InventoryBUS inventoryBUS = new InventoryBUS();
        ImportReceiptDetailBUS importReceiptDetailBUS = new ImportReceiptDetailBUS();

        // Xóa dữ liệu cũ
        tableModel.setRowCount(0);

        // Thêm dữ liệu mới
        for (ProductDTO product : results) {
            // Lấy số lượng tồn kho từ bảng sanphamtonkho
            int quantity = inventoryBUS.getCurrentQuantity(product.getProductId());
            String status = (quantity > 0) ? "Còn hàng" : "Hết hàng";

            // Lấy giá nhập từ phiếu nhập chi tiết
            double importPrice = getImportPrice(product.getProductId(), importReceiptDetailBUS);
            String formattedPrice = String.format("%,.0f", importPrice);

            Object[] row = {
                    product.getProductId(),
                    product.getProductName(),
                    product.getCategoryName(),
                    product.getUnit(),
                    formattedPrice,
                    quantity,
                    status
            };
            tableModel.addRow(row);
        }
    }

    private void advancedSearchInventory(String productId, String productName, String category, String quantity,
            boolean isAnd) {
        // Lấy danh sách sản phẩm
        List<ProductDTO> allProducts = productController.getAllProducts();

        // Khởi tạo InventoryBUS để lấy thông tin tồn kho
        InventoryBUS inventoryBUS = new InventoryBUS();
        ImportReceiptDetailBUS importReceiptDetailBUS = new ImportReceiptDetailBUS();

        // Danh sách kết quả
        List<ProductDTO> results = new ArrayList<>();

        // Nếu tất cả điều kiện trống, trả về danh sách đầy đủ
        if ((productId.isEmpty()) && (productName.isEmpty()) && (category.isEmpty()) && (quantity.isEmpty())) {
            refreshInventoryData();
            return;
        }

        // Tìm kiếm theo các điều kiện
        for (ProductDTO product : allProducts) {
            boolean productIdMatch = productId.isEmpty() ||
                    (product.getProductId() != null &&
                            product.getProductId().toLowerCase().contains(productId.toLowerCase()));

            boolean productNameMatch = productName.isEmpty() ||
                    (product.getProductName() != null &&
                            product.getProductName().toLowerCase().contains(productName.toLowerCase()));

            boolean categoryMatch = category.isEmpty() ||
                    (product.getCategoryName() != null &&
                            product.getCategoryName().toLowerCase().contains(category.toLowerCase()));

            // Lấy số lượng tồn kho của sản phẩm từ bảng sanphamtonkho
            int stockQuantity = inventoryBUS.getCurrentQuantity(product.getProductId());

            boolean quantityMatch = quantity.isEmpty();
            try {
                if (!quantity.isEmpty()) {
                    int queryQuantity = Integer.parseInt(quantity);
                    quantityMatch = (stockQuantity == queryQuantity);
                }
            } catch (NumberFormatException e) {
                // Nếu không phải số, bỏ qua điều kiện này
                quantityMatch = true;
            }

            if (isAnd) {
                // Phép AND - tất cả điều kiện phải đúng
                if (productIdMatch && productNameMatch && categoryMatch && quantityMatch) {
                    results.add(product);
                }
            } else {
                // Phép OR - chỉ cần một điều kiện đúng
                if (productIdMatch || productNameMatch || categoryMatch || quantityMatch) {
                    results.add(product);
                }
            }
        }

        // Hiển thị kết quả
        tableModel.setRowCount(0);

        for (ProductDTO product : results) {
            // Lấy số lượng tồn kho từ bảng sanphamtonkho
            int stockQuantity = inventoryBUS.getCurrentQuantity(product.getProductId());
            String status = (stockQuantity > 0) ? "Còn hàng" : "Hết hàng";

            // Lấy giá nhập từ phiếu nhập chi tiết
            double importPrice = getImportPrice(product.getProductId(), importReceiptDetailBUS);
            String formattedPrice = String.format("%,.0f", importPrice);

            Object[] row = {
                    product.getProductId(),
                    product.getProductName(),
                    product.getCategoryName(),
                    product.getUnit(),
                    formattedPrice,
                    stockQuantity,
                    status
            };
            tableModel.addRow(row);
        }
    }

    private void refreshInventoryData() {
        // Clear table
        tableModel.setRowCount(0);

        // Get all products
        List<ProductDTO> products = productController.getAllProducts();

        // Add products to table
        displayInventory(products);

        // Update status panel to show current count
        updateStatusPanel();
    }

    /**
     * Lấy giá nhập hàng từ phiếu nhập chi tiết
     * 
     * @param productId              Mã sản phẩm cần lấy giá
     * @param importReceiptDetailBUS ĐỐi tượng ImportReceiptDetailBUS
     * @return Giá nhập, nếu không có thì trả về 0
     */
    private double getImportPrice(String productId, ImportReceiptDetailBUS importReceiptDetailBUS) {
        // Lấy tất cả chi tiết phiếu nhập
        List<ImportReceiptDetail> allDetails = importReceiptDetailBUS.getAllImportReceiptDetails();

        // Lọc các chi tiết phiếu nhập của sản phẩm này
        List<ImportReceiptDetail> productDetails = new ArrayList<>();
        for (ImportReceiptDetail detail : allDetails) {
            if (detail.getProductId().equals(productId)) {
                productDetails.add(detail);
            }
        }

        // Nếu không có chi tiết phiếu nhập nào, trả về 0
        if (productDetails.isEmpty()) {
            return 0;
        }

        // Tính tổng thành tiền và tổng số lượng để tính giá nhập trung bình
        double totalAmount = 0;
        int totalQuantity = 0;

        for (ImportReceiptDetail detail : productDetails) {
            totalAmount += detail.getTotal();
            totalQuantity += detail.getQuantity();
        }

        // Tính giá nhập trung bình
        return totalQuantity > 0 ? (totalAmount / totalQuantity) : 0;
    }

    private void exportToExcel() {
        try {
            // Lấy dữ liệu từ bảng
            List<Object[]> data = new ArrayList<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Object[] rowData = new Object[tableModel.getColumnCount()];
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    rowData[j] = tableModel.getValueAt(i, j);
                }
                data.add(rowData);
            }

            // Tạo tiêu đề các cột
            String[] headers = new String[tableModel.getColumnCount()];
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                headers[i] = tableModel.getColumnName(i);
            }

            // Xuất ra file Excel
            boolean result = ExcelUtils.exportToExcel(headers, data, "Tồn kho", "DANH SÁCH TỒN KHO");

            if (result) {
                JOptionPane.showMessageDialog(this,
                        "Xuất file Excel thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Xuất file Excel thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất file Excel: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Display inventory data in the table
     * 
     * @param products List of products to display
     */
    private void displayInventory(List<ProductDTO> products) {
        // Clear table
        tableModel.setRowCount(0);

        // Khởi tạo InventoryBUS để lấy thông tin tồn kho
        InventoryBUS inventoryBUS = new InventoryBUS();
        ImportReceiptDetailBUS importReceiptDetailBUS = new ImportReceiptDetailBUS();

        // Thêm dữ liệu mới
        for (ProductDTO product : products) {
            // Lấy số lượng tồn kho từ bảng sanphamtonkho
            int quantity = inventoryBUS.getCurrentQuantity(product.getProductId());
            String status = (quantity > 0) ? "Còn hàng" : "Hết hàng";

            // Lấy giá nhập từ phiếu nhập chi tiết
            double importPrice = getImportPrice(product.getProductId(), importReceiptDetailBUS);
            String formattedPrice = String.format("%,.0f", importPrice);

            // Đảm bảo thông tin danh mục được hiển thị đúng
            String categoryName = product.getCategoryName();
            if (categoryName == null || categoryName.isEmpty()) {
                categoryName = "Chưa phân loại";
            }

            Object[] row = {
                    product.getProductId(),
                    product.getProductName(),
                    categoryName,
                    product.getUnit(),
                    formattedPrice,
                    quantity,
                    status
            };
            tableModel.addRow(row);
        }
    }
}