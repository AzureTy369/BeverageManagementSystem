// package GUI;

// import BUS.ProductBUS;
// import BUS.SupplierBUS;
// import BUS.EmployeeBUS;
// import DTO.ProductDTO;
// import DTO.SupplierDTO;
// import DTO.EmployeeDTO;
// import DTO.SupplierProductDTO;
// import BUS.SupplierProductBUS;

// import javax.swing.*;
// import javax.swing.border.EmptyBorder;
// import javax.swing.table.DefaultTableModel;
// import java.awt.*;
// import java.awt.event.*;
// import java.util.List;

// public class ImportGoodsGUI extends JPanel {
// private ProductBUS productController = new ProductBUS();
// private SupplierBUS supplierController = new SupplierBUS();
// private EmployeeBUS employeeController = new EmployeeBUS();
// private SupplierProductBUS supplierProductBUS = new SupplierProductBUS();

// private JTable productTable;
// private DefaultTableModel productTableModel;
// private JTable selectedTable;
// private DefaultTableModel selectedTableModel;

// private JTextField searchField;
// private JComboBox<SupplierDTO> supplierComboBox;
// private JTextField receiptIdField;
// private JTextField createdByField;
// private JTextField quantityField;
// private JTextField totalAmountField;

// private final Color primaryColor = new Color(0, 123, 255);
// private final Color successColor = new Color(40, 167, 69);
// private final Color dangerColor = new Color(220, 53, 69);
// private final Color lightColor = new Color(248, 249, 250);

// private List<SupplierProductDTO> currentSupplierProducts;

// public ImportGoodsGUI() {
// setLayout(new BorderLayout());
// setOpaque(false);
// setBorder(new EmptyBorder(10, 10, 10, 10));
// setBackground(lightColor);

// createGUI();
// }

// private void createGUI() {
// // Panel tiêu đề ở trên cùng
// JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
// titlePanel.setBackground(lightColor);

// JLabel titleLabel = new JLabel("Nhập Hàng");
// titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
// titleLabel.setForeground(primaryColor);
// titlePanel.add(titleLabel);

// // Panel chính chứa bảng sản phẩm và phiếu nhập
// JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
// mainPanel.setOpaque(false);

// // Panel bên trái: Danh sách sản phẩm
// JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
// leftPanel.setBackground(lightColor);
// leftPanel.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm"));

// // Panel tìm kiếm
// JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
// searchPanel.setBackground(lightColor);
// searchPanel.add(new JLabel("Tìm kiếm:"));
// searchField = new JTextField(15);
// searchField.setPreferredSize(new Dimension(180, 25));
// searchField.setFont(new Font("Arial", Font.PLAIN, 12));
// searchField.addKeyListener(new KeyAdapter() {
// @Override
// public void keyPressed(KeyEvent e) {
// if (e.getKeyCode() == KeyEvent.VK_ENTER) {
// searchProducts();
// }
// }
// });
// searchPanel.add(searchField);
// JButton refreshButton = createOutlineButton("Làm mới", new Color(23, 162,
// 184));
// refreshButton.addActionListener(e -> refreshProductData());
// searchPanel.add(refreshButton);
// leftPanel.add(searchPanel, BorderLayout.NORTH);

// // Tạo bảng sản phẩm
// createProductTable();
// JScrollPane productScroll = new JScrollPane(productTable);
// leftPanel.add(productScroll, BorderLayout.CENTER);

// // Panel nhập số lượng
// JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
// quantityPanel.setBackground(lightColor);
// quantityPanel.add(new JLabel("Số lượng:"));
// quantityField = new JTextField("1", 4);
// quantityPanel.add(quantityField);
// JButton addToReceiptButton = createOutlineButton("Thêm", successColor);
// addToReceiptButton.addActionListener(e -> addToReceipt());
// quantityPanel.add(addToReceiptButton);
// leftPanel.add(quantityPanel, BorderLayout.SOUTH);

// // Panel bên phải: Thông tin phiếu nhập
// JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
// rightPanel.setBackground(lightColor);
// rightPanel.setBorder(BorderFactory.createTitledBorder("Phiếu nhập"));

// // Panel thông tin phiếu nhập
// JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
// infoPanel.setBackground(lightColor);
// infoPanel.add(new JLabel("Mã phiếu nhập:"));
// receiptIdField = new JTextField();
// infoPanel.add(receiptIdField);
// infoPanel.add(new JLabel("Nhà cung cấp:"));
// supplierComboBox = new JComboBox<>();
// loadSuppliers();
// infoPanel.add(supplierComboBox);
// infoPanel.add(new JLabel("Người tạo phiếu:"));
// createdByField = new JTextField();
// createdByField.setEditable(false);
// infoPanel.add(createdByField);
// rightPanel.add(infoPanel, BorderLayout.NORTH);

// // Tạo bảng sản phẩm đã chọn
// createSelectedTable();
// JScrollPane selectedScroll = new JScrollPane(selectedTable);
// rightPanel.add(selectedScroll, BorderLayout.CENTER);

// // Panel nút chức năng và tổng tiền
// JPanel bottomRightPanel = new JPanel(new BorderLayout());
// bottomRightPanel.setBackground(lightColor);

// // Panel nút chức năng
// JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
// buttonPanel.setBackground(lightColor);

// // Nút Excel
// JButton importExcelButton = createOutlineButton("Nhập Excel", successColor);
// importExcelButton.addActionListener(e -> importFromExcel());

// // Nút chức năng khác
// JButton editQuantityButton = createOutlineButton("Sửa số lượng",
// successColor);
// editQuantityButton.addActionListener(e -> editQuantity());
// JButton deleteProductButton = createOutlineButton("Xóa sản phẩm",
// dangerColor);
// deleteProductButton.addActionListener(e -> deleteProduct());

// buttonPanel.add(importExcelButton);
// buttonPanel.add(editQuantityButton);
// buttonPanel.add(deleteProductButton);
// bottomRightPanel.add(buttonPanel, BorderLayout.NORTH);

// // Panel tổng tiền
// JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
// totalPanel.setBackground(lightColor);
// totalPanel.add(new JLabel("Tổng tiền:"));
// totalAmountField = new JTextField("0", 10);
// totalAmountField.setEditable(false);
// totalPanel.add(totalAmountField);
// JButton confirmImportButton = createOutlineButton("Nhập hàng", successColor);
// confirmImportButton.addActionListener(e -> confirmImport());
// totalPanel.add(confirmImportButton);
// bottomRightPanel.add(totalPanel, BorderLayout.SOUTH);

// rightPanel.add(bottomRightPanel, BorderLayout.SOUTH);

// mainPanel.add(leftPanel);
// mainPanel.add(rightPanel);

// // Thêm vào layout chính
// add(titlePanel, BorderLayout.NORTH);
// add(mainPanel, BorderLayout.CENTER);

// supplierComboBox.addActionListener(e -> {
// String supplierId = getSelectedSupplierId();
// if (supplierId != null) {
// currentSupplierProducts =
// supplierProductBUS.getProductsBySupplier(supplierId);
// loadProductsForSupplier(currentSupplierProducts);
// }
// });
// }

// private void createProductTable() {
// String[] columnNames = { "Mã SP", "Tên SP", "Danh mục", "Đơn vị", "Giá bán",
// "Tồn kho" };
// productTableModel = new DefaultTableModel(columnNames, 0) {
// @Override
// public boolean isCellEditable(int row, int column) {
// return false;
// }
// };

// productTable = new JTable(productTableModel);
// productTable.setFont(new Font("Arial", Font.PLAIN, 14));
// productTable.setRowHeight(25);
// productTable.setGridColor(new Color(230, 230, 230));
// productTable.setBackground(Color.WHITE);
// productTable.setSelectionBackground(new Color(173, 216, 230));
// productTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
// productTable.getTableHeader().setBackground(Color.WHITE);
// productTable.getTableHeader().setForeground(Color.BLACK);
// productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
// }

// private void createSelectedTable() {
// String[] columnNames = { "STT", "Mã SP", "Tên SP", "Số lượng", "Đơn giá",
// "Thành tiền" };
// selectedTableModel = new DefaultTableModel(columnNames, 0) {
// @Override
// public boolean isCellEditable(int row, int column) {
// return false;
// }
// };

// selectedTable = new JTable(selectedTableModel);
// selectedTable.setFont(new Font("Arial", Font.PLAIN, 14));
// selectedTable.setRowHeight(25);
// selectedTable.setGridColor(new Color(230, 230, 230));
// selectedTable.setBackground(Color.WHITE);
// selectedTable.setSelectionBackground(new Color(173, 216, 230));
// selectedTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
// selectedTable.getTableHeader().setBackground(Color.WHITE);
// selectedTable.getTableHeader().setForeground(Color.BLACK);
// selectedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
// }

// private JButton createOutlineButton(String text, Color color) {
// JButton button = new JButton(text);
// button.setFont(new Font("Arial", Font.PLAIN, 14));
// button.setBackground(new Color(255, 255, 255));
// button.setForeground(color);
// button.setFocusPainted(false);
// button.setBorder(BorderFactory.createLineBorder(color, 1));
// button.setToolTipText(text);
// button.setCursor(new Cursor(Cursor.HAND_CURSOR));
// button.setPreferredSize(new Dimension(100, 35));

// // Hiệu ứng hover
// button.addMouseListener(new MouseAdapter() {
// @Override
// public void mouseEntered(MouseEvent e) {
// button.setBackground(color);
// button.setForeground(Color.WHITE);
// }

// @Override
// public void mouseExited(MouseEvent e) {
// button.setBackground(new Color(255, 255, 255));
// button.setForeground(color);
// }
// });

// return button;
// }

// private void loadSuppliers() {
// List<SupplierDTO> suppliers = supplierController.getAllSuppliers();
// supplierComboBox.removeAllItems();
// for (SupplierDTO supplier : suppliers) {
// supplierComboBox.addItem(supplier);
// }
// }

// private void searchProducts() {
// String keyword = searchField.getText().trim();
// if (keyword.isEmpty()) {
// refreshProductData();
// return;
// }
// // TODO: Implement product search
// }

// private void refreshProductData() {
// // TODO: Implement refresh product data
// }

// private void addToReceipt() {
// // TODO: Implement add to receipt
// }

// private void editQuantity() {
// // TODO: Implement edit quantity
// }

// private void deleteProduct() {
// // TODO: Implement delete product
// }

// private void confirmImport() {
// // TODO: Implement confirm import
// }

// private void importFromExcel() {
// // TODO: Implement import from Excel
// }

// private void exportToExcel() {
// // TODO: Implement export to Excel
// }

// private void loadProductsForSupplier(List<SupplierProductDTO> products) {
// productTableModel.setRowCount(0);
// for (SupplierProductDTO sp : products) {
// productTableModel.addRow(new Object[] { sp.getId(), sp.getProductName(),
// sp.getUnit(), sp.getImportPrice(),
// sp.getDescription(), sp.getProductCode() });
// }
// }

// private String getSelectedSupplierId() {
// SupplierDTO selectedSupplier = (SupplierDTO)
// supplierComboBox.getSelectedItem();
// return selectedSupplier != null ? selectedSupplier.getSupplierId() : null;
// }
// }