// package GUI;

// import BUS.ImportReceiptBUS;
// import DTO.ImportReceipt;

// import javax.swing.*;
// import javax.swing.border.EmptyBorder;
// import javax.swing.table.DefaultTableModel;
// import java.awt.*;
// import java.awt.event.*;
// import java.util.List;
// import java.util.ArrayList;
// import GUI.utils.ExcelUtils;

// public class ImportReceiptsGUI extends JPanel {
//     private ImportReceiptBUS importReceiptController;
//     private JTable receiptTable;
//     private DefaultTableModel tableModel;
//     private JTextField receiptSearchField;
//     private JPanel advancedSearchPanel;
//     private final Color primaryColor = new Color(0, 123, 255);
//     private final Color successColor = new Color(40, 167, 69);
//     private final Color warningColor = new Color(255, 193, 7);
//     private final Color dangerColor = new Color(220, 53, 69);
//     private final Color lightColor = new Color(248, 249, 250);

//     public ImportReceiptsGUI() {
//         this.importReceiptController = new ImportReceiptBUS();
//         setLayout(new BorderLayout());
//         setOpaque(false);
//         setBorder(new EmptyBorder(10, 10, 10, 10));
//         setBackground(lightColor);

//         createAdvancedSearchPanel();
//         createGUI();
//         refreshReceiptData();
//     }

//     private void createGUI() {
//         setLayout(new BorderLayout());

//         // Panel tiêu đề ở trên cùng
//         JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//         titlePanel.setBackground(lightColor);

//         JLabel titleLabel = new JLabel("Quản Lý Phiếu Nhập");
//         titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
//         titleLabel.setForeground(primaryColor);

//         titlePanel.add(titleLabel);

//         // Panel chức năng ở dưới tiêu đề
//         JPanel topPanel = new JPanel(new BorderLayout());
//         topPanel.setOpaque(false);
//         topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

//         // NHÓM 1: Panel chức năng bên trái - căn trái các phần tử
//         JPanel leftFunctionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
//         leftFunctionPanel.setBackground(new Color(240, 240, 240));
//         leftFunctionPanel.setBorder(BorderFactory.createCompoundBorder(
//                 BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
//                 BorderFactory.createEmptyBorder(5, 5, 5, 5)));

//         // Nút xem chi tiết phiếu nhập
//         JButton viewDetailsButton = new JButton("Xem chi tiết phiếu nhập");
//         viewDetailsButton.setFont(new Font("Arial", Font.PLAIN, 14));
//         viewDetailsButton.setBackground(primaryColor);
//         viewDetailsButton.setForeground(Color.BLACK);
//         viewDetailsButton.setFocusPainted(false);
//         viewDetailsButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
//         viewDetailsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
//         viewDetailsButton.addActionListener(e -> showReceiptDetailsDialog());

//         // Nút xóa - giảm kích thước
//         JButton deleteBtn = createOutlineButton("Xóa", dangerColor);
//         deleteBtn.setMargin(new Insets(3, 8, 3, 8));
//         deleteBtn.setFont(new Font("Arial", Font.PLAIN, 12));
//         deleteBtn.addActionListener(e -> deleteReceipt());

//         // Nút chỉnh sửa - giảm kích thước
//         JButton editBtn = createOutlineButton("Chỉnh sửa", warningColor);
//         editBtn.setMargin(new Insets(3, 8, 3, 8));
//         editBtn.setFont(new Font("Arial", Font.PLAIN, 12));
//         editBtn.addActionListener(e -> showEditReceiptDialog());

//         // Separator
//         JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
//         separator.setPreferredSize(new Dimension(1, 20));
//         separator.setForeground(new Color(200, 200, 200));

//         // Nút xuất Excel
//         JButton exportExcelButton = createOutlineButton("Xuất Excel", primaryColor);
//         exportExcelButton.setMargin(new Insets(3, 8, 3, 8));
//         exportExcelButton.setFont(new Font("Arial", Font.PLAIN, 12));
//         exportExcelButton.addActionListener(e -> exportToExcel());

//         // Thêm các nút vào panel bên trái
//         leftFunctionPanel.add(viewDetailsButton);
//         leftFunctionPanel.add(deleteBtn);
//         leftFunctionPanel.add(editBtn);
//         leftFunctionPanel.add(separator);
//         leftFunctionPanel.add(exportExcelButton);

//         // NHÓM 2: Panel tìm kiếm bên phải - căn giữa các phần tử
//         JPanel rightSearchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
//         rightSearchPanel.setBackground(new Color(240, 240, 240));
//         rightSearchPanel.setBorder(BorderFactory.createCompoundBorder(
//                 BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
//                 BorderFactory.createEmptyBorder(5, 5, 5, 5)));

//         JLabel searchLabel = new JLabel("Tìm kiếm:");
//         searchLabel.setForeground(Color.BLACK);
//         searchLabel.setFont(new Font("Arial", Font.PLAIN, 12));

//         // TextField tìm kiếm
//         receiptSearchField = new JTextField(15);
//         receiptSearchField.setPreferredSize(new Dimension(180, 25));
//         receiptSearchField.setFont(new Font("Arial", Font.PLAIN, 12));
//         receiptSearchField.addKeyListener(new KeyAdapter() {
//             @Override
//             public void keyPressed(KeyEvent e) {
//                 if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                     searchReceipts();
//                 }
//             }
//         });

//         // Nút tìm kiếm
//         JButton searchButton = createOutlineButton("Tìm kiếm", primaryColor);
//         searchButton.setMargin(new Insets(3, 8, 3, 8));
//         searchButton.setFont(new Font("Arial", Font.PLAIN, 12));
//         searchButton.addActionListener(e -> searchReceipts());

//         // Nút làm mới
//         JButton refreshButton = createOutlineButton("Làm mới", new Color(23, 162, 184));
//         refreshButton.setMargin(new Insets(3, 8, 3, 8));
//         refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
//         refreshButton.addActionListener(e -> refreshReceiptData());

//         // Thêm các thành phần vào panel tìm kiếm
//         rightSearchPanel.add(searchLabel);
//         rightSearchPanel.add(receiptSearchField);
//         rightSearchPanel.add(searchButton);
//         rightSearchPanel.add(refreshButton);

//         // Thêm hai panel vào panel chính ở trên cùng
//         JPanel topFunctionContainer = new JPanel(new GridBagLayout());
//         topFunctionContainer.setOpaque(false);

//         GridBagConstraints gbc = new GridBagConstraints();
//         gbc.fill = GridBagConstraints.HORIZONTAL;
//         gbc.gridx = 0;
//         gbc.gridy = 0;
//         gbc.weightx = 0.5;
//         gbc.insets = new Insets(0, 0, 0, 5);
//         topFunctionContainer.add(leftFunctionPanel, gbc);

//         gbc.gridx = 1;
//         gbc.insets = new Insets(0, 5, 0, 0);
//         topFunctionContainer.add(rightSearchPanel, gbc);

//         topPanel.add(topFunctionContainer, BorderLayout.CENTER);

//         // Thêm panel tìm kiếm nâng cao
//         JPanel advancedSearchContainer = new JPanel(new BorderLayout());
//         advancedSearchContainer.setOpaque(false);
//         advancedSearchContainer.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));

//         JPanel advancedSearchBordered = new JPanel(new BorderLayout());
//         advancedSearchBordered.setBorder(BorderFactory.createCompoundBorder(
//                 BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
//                 BorderFactory.createEmptyBorder(5, 5, 5, 5)));
//         advancedSearchBordered.setBackground(new Color(240, 243, 247));

//         advancedSearchBordered.add(advancedSearchPanel, BorderLayout.CENTER);
//         advancedSearchContainer.add(advancedSearchBordered, BorderLayout.CENTER);

//         advancedSearchPanel.setVisible(true);

//         // Tạo bảng phiếu nhập
//         createReceiptTable();
//         JScrollPane tableScrollPane = new JScrollPane(receiptTable);
//         tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
//         tableScrollPane.getViewport().setBackground(lightColor);

//         // Panel chứa bảng có viền
//         JPanel tableContainer = new JPanel(new BorderLayout());
//         tableContainer.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

//         JPanel borderedTablePanel = new JPanel(new BorderLayout());
//         borderedTablePanel.setBorder(BorderFactory.createCompoundBorder(
//                 BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
//                 BorderFactory.createEmptyBorder(5, 5, 5, 5)));
//         borderedTablePanel.add(tableScrollPane, BorderLayout.CENTER);

//         tableContainer.add(borderedTablePanel, BorderLayout.CENTER);

//         // Panel trạng thái hiển thị thông tin tổng quan
//         JPanel statusPanel = createStatusPanel();

//         // Panel chứa tổng thể (ngoại trừ topPanel)
//         JPanel contentPanel = new JPanel(new BorderLayout());
//         contentPanel.setOpaque(false);
//         contentPanel.add(advancedSearchContainer, BorderLayout.NORTH);
//         contentPanel.add(tableContainer, BorderLayout.CENTER);
//         contentPanel.add(statusPanel, BorderLayout.SOUTH);

//         // Tạo container chứa tất cả panel phía trên
//         JPanel headerPanel = new JPanel(new BorderLayout());
//         headerPanel.setOpaque(false);
//         headerPanel.add(titlePanel, BorderLayout.NORTH);
//         headerPanel.add(topPanel, BorderLayout.CENTER);

//         // Thêm vào layout chính
//         add(headerPanel, BorderLayout.NORTH);
//         add(contentPanel, BorderLayout.CENTER);
//     }

//     private void createAdvancedSearchPanel() {
//         advancedSearchPanel = new JPanel();
//         advancedSearchPanel.setLayout(new BoxLayout(advancedSearchPanel, BoxLayout.Y_AXIS));
//         advancedSearchPanel.setBackground(new Color(240, 243, 247));

//         // Tiêu đề và giới thiệu
//         JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//         headerPanel.setOpaque(false);
//         JLabel headerLabel = new JLabel("Tìm kiếm nâng cao - Nhập các điều kiện tìm kiếm");
//         headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
//         headerPanel.add(headerLabel);
//         advancedSearchPanel.add(headerPanel);

//         // Panel chứa các điều kiện tìm kiếm
//         JPanel conditionsPanel = new JPanel(new GridBagLayout());
//         conditionsPanel.setOpaque(false);
//         GridBagConstraints gbc = new GridBagConstraints();
//         gbc.insets = new Insets(5, 5, 5, 5);
//         gbc.fill = GridBagConstraints.HORIZONTAL;

//         // Mã phiếu nhập
//         gbc.gridx = 0;
//         gbc.gridy = 0;
//         JLabel receiptIdLabel = new JLabel("Mã phiếu nhập:");
//         receiptIdLabel.setFont(new Font("Arial", Font.PLAIN, 14));
//         conditionsPanel.add(receiptIdLabel, gbc);

//         gbc.gridx = 1;
//         JTextField receiptIdSearchField = new JTextField(15);
//         receiptIdSearchField.setFont(new Font("Arial", Font.PLAIN, 14));
//         conditionsPanel.add(receiptIdSearchField, gbc);

//         // Nhà cung cấp
//         gbc.gridx = 2;
//         JLabel supplierLabel = new JLabel("Nhà cung cấp:");
//         supplierLabel.setFont(new Font("Arial", Font.PLAIN, 14));
//         conditionsPanel.add(supplierLabel, gbc);

//         gbc.gridx = 3;
//         JTextField supplierSearchField = new JTextField(15);
//         supplierSearchField.setFont(new Font("Arial", Font.PLAIN, 14));
//         conditionsPanel.add(supplierSearchField, gbc);

//         // Người tạo
//         gbc.gridx = 0;
//         gbc.gridy = 1;
//         JLabel creatorLabel = new JLabel("Người tạo:");
//         creatorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
//         conditionsPanel.add(creatorLabel, gbc);

//         gbc.gridx = 1;
//         JTextField creatorSearchField = new JTextField(15);
//         creatorSearchField.setFont(new Font("Arial", Font.PLAIN, 14));
//         conditionsPanel.add(creatorSearchField, gbc);

//         // Trạng thái
//         gbc.gridx = 2;
//         JLabel statusLabel = new JLabel("Trạng thái:");
//         statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
//         conditionsPanel.add(statusLabel, gbc);

//         gbc.gridx = 3;
//         JComboBox<String> statusComboBox = new JComboBox<>(
//                 new String[] { "Tất cả", "Đã hoàn thành", "Đang xử lý", "Đã hủy" });
//         statusComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
//         conditionsPanel.add(statusComboBox, gbc);

//         advancedSearchPanel.add(conditionsPanel);

//         // Panel chứa nút tìm kiếm và điều kiện kết hợp
//         JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//         controlPanel.setOpaque(false);

//         JRadioButton andRadio = new JRadioButton("Tất cả điều kiện");
//         andRadio.setFont(new Font("Arial", Font.PLAIN, 14));
//         andRadio.setOpaque(false);
//         andRadio.setSelected(true);

//         JRadioButton orRadio = new JRadioButton("Bất kỳ điều kiện nào");
//         orRadio.setFont(new Font("Arial", Font.PLAIN, 14));
//         orRadio.setOpaque(false);

//         ButtonGroup group = new ButtonGroup();
//         group.add(andRadio);
//         group.add(orRadio);

//         JButton searchButton = createOutlineButton("Tìm kiếm", primaryColor);
//         searchButton.setForeground(Color.BLACK);
//         searchButton.addActionListener(e -> {
//             // Lấy thông tin từ các ô tìm kiếm
//             String receiptId = receiptIdSearchField.getText().trim();
//             String supplier = supplierSearchField.getText().trim();
//             String creator = creatorSearchField.getText().trim();
//             String status = statusComboBox.getSelectedItem().toString();
//             boolean isAnd = andRadio.isSelected();

//             // Gọi phương thức tìm kiếm nâng cao
//             advancedSearchReceipts(receiptId, supplier, creator, status, isAnd);
//         });

//         JButton clearButton = createOutlineButton("Xóa tìm kiếm", new Color(108, 117, 125));
//         clearButton.setForeground(Color.BLACK);
//         clearButton.addActionListener(e -> {
//             receiptIdSearchField.setText("");
//             supplierSearchField.setText("");
//             creatorSearchField.setText("");
//             statusComboBox.setSelectedIndex(0);
//             refreshReceiptData();
//         });

//         controlPanel.add(andRadio);
//         controlPanel.add(orRadio);
//         controlPanel.add(Box.createHorizontalStrut(20));
//         controlPanel.add(searchButton);
//         controlPanel.add(clearButton);

//         advancedSearchPanel.add(controlPanel);
//     }

//     private void createReceiptTable() {
//         String[] columnNames = { "Mã phiếu nhập", "Nhà cung cấp", "Người tạo", "Ngày nhập", "Tổng tiền", "Trạng thái",
//                 "Chi tiết" };
//         tableModel = new DefaultTableModel(columnNames, 0) {
//             @Override
//             public boolean isCellEditable(int row, int column) {
//                 return false;
//             }
//         };

//         receiptTable = new JTable(tableModel);
//         receiptTable.setFont(new Font("Arial", Font.PLAIN, 14));
//         receiptTable.setRowHeight(25);
//         receiptTable.setGridColor(new Color(230, 230, 230));
//         receiptTable.setSelectionBackground(new Color(173, 216, 230));
//         receiptTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
//         receiptTable.getTableHeader().setBackground(primaryColor);
//         receiptTable.getTableHeader().setForeground(Color.BLACK);
//         receiptTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

//         // Thêm sự kiện double click để xem chi tiết
//         receiptTable.addMouseListener(new MouseAdapter() {
//             @Override
//             public void mouseClicked(MouseEvent e) {
//                 if (e.getClickCount() == 2) {
//                     int column = receiptTable.columnAtPoint(e.getPoint());
//                     if (column == 6) { // Cột "Chi tiết"
//                         showReceiptDetails();
//                     }
//                 }
//             }
//         });

//         // Set column width
//         receiptTable.getColumnModel().getColumn(0).setPreferredWidth(100);
//         receiptTable.getColumnModel().getColumn(1).setPreferredWidth(150);
//         receiptTable.getColumnModel().getColumn(2).setPreferredWidth(120);
//         receiptTable.getColumnModel().getColumn(3).setPreferredWidth(120);
//         receiptTable.getColumnModel().getColumn(4).setPreferredWidth(120);
//         receiptTable.getColumnModel().getColumn(5).setPreferredWidth(100);
//         receiptTable.getColumnModel().getColumn(6).setPreferredWidth(80);
//     }

//     private JPanel createStatusPanel() {
//         JPanel statusPanel = new JPanel(new BorderLayout());
//         statusPanel.setBorder(BorderFactory.createCompoundBorder(
//                 BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(222, 226, 230)),
//                 BorderFactory.createEmptyBorder(10, 0, 0, 0)));
//         statusPanel.setOpaque(false);

//         JLabel statusLabel = new JLabel("Tổng số phiếu nhập: 0");
//         statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));

//         statusPanel.add(statusLabel, BorderLayout.WEST);

//         // Cập nhật số lượng phiếu nhập khi dữ liệu thay đổi
//         statusLabel.setText("Tổng số phiếu nhập: " + importReceiptController.getAllImportReceipts().size());

//         return statusPanel;
//     }

//     private JButton createOutlineButton(String text, Color color) {
//         JButton button = new JButton(text);
//         button.setFont(new Font("Arial", Font.PLAIN, 14));
//         button.setBackground(new Color(255, 255, 255));
//         button.setForeground(color);
//         button.setFocusPainted(false);
//         button.setBorder(BorderFactory.createLineBorder(color, 1));
//         button.setToolTipText(text);
//         button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//         button.setPreferredSize(new Dimension(100, 35));

//         // Hiệu ứng hover
//         button.addMouseListener(new MouseAdapter() {
//             @Override
//             public void mouseEntered(MouseEvent e) {
//                 button.setBackground(color);
//                 button.setForeground(Color.WHITE);
//             }

//             @Override
//             public void mouseExited(MouseEvent e) {
//                 button.setBackground(new Color(255, 255, 255));
//                 button.setForeground(color);
//             }
//         });

//         return button;
//     }

//     private void searchReceipts() {
//         String keyword = receiptSearchField.getText().trim();
//         if (keyword.isEmpty()) {
//             refreshReceiptData();
//             return;
//         }

//         List<ImportReceipt> results = importReceiptController.search(keyword);
//         displayReceipts(results);
//     }

//     private void advancedSearchReceipts(String receiptId, String supplier, String creator, String status,
//             boolean isAnd) {
//         List<ImportReceipt> allReceipts = importReceiptController.getAllImportReceipts();
//         List<ImportReceipt> results = new ArrayList<>();

//         // Nếu tất cả điều kiện trống, trả về danh sách đầy đủ
//         if (receiptId.isEmpty() && supplier.isEmpty() && creator.isEmpty() && status.equals("Tất cả")) {
//             displayReceipts(allReceipts);
//             return;
//         }

//         // Tìm kiếm theo các điều kiện
//         for (ImportReceipt receipt : allReceipts) {
//             boolean receiptIdMatch = receiptId.isEmpty() ||
//                     (receipt.getImportId() != null &&
//                             receipt.getImportId().toLowerCase().contains(receiptId.toLowerCase()));

//             boolean supplierMatch = supplier.isEmpty() ||
//                     (receipt.getSupplierId() != null &&
//                             receipt.getSupplierId().toLowerCase().contains(supplier.toLowerCase()));

//             boolean creatorMatch = creator.isEmpty() ||
//                     (receipt.getEmployeeId() != null &&
//                             receipt.getEmployeeId().toLowerCase().contains(creator.toLowerCase()));

//             boolean statusMatch = status.equals("Tất cả") ||
//                     (receipt.getStatus() != null &&
//                             receipt.getStatus().equals(status));

//             if (isAnd) {
//                 // Phép AND - tất cả điều kiện phải đúng
//                 if (receiptIdMatch && supplierMatch && creatorMatch && statusMatch) {
//                     results.add(receipt);
//                 }
//             } else {
//                 // Phép OR - chỉ cần một điều kiện đúng
//                 if (receiptIdMatch || supplierMatch || creatorMatch || statusMatch) {
//                     results.add(receipt);
//                 }
//             }
//         }

//         // Hiển thị kết quả
//         displayReceipts(results);
//     }

//     private void displayReceipts(List<ImportReceipt> receipts) {
//         // Xóa dữ liệu cũ
//         tableModel.setRowCount(0);

//         // Thêm dữ liệu mới
//         for (ImportReceipt receipt : receipts) {
//             Object[] row = {
//                     receipt.getImportId(),
//                     receipt.getSupplierId(),
//                     receipt.getEmployeeId(),
//                     receipt.getImportDate(),
//                     receipt.getTotalAmount(),
//                     receipt.getStatus(),
//                     "Chi tiết"
//             };
//             tableModel.addRow(row);
//         }
//     }

//     private void refreshReceiptData() {
//         List<ImportReceipt> receipts = importReceiptController.getAllImportReceipts();
//         displayReceipts(receipts);
//     }

//     private void exportToExcel() {
//         // Lấy dữ liệu từ bảng
//         List<Object[]> data = new ArrayList<>();
//         for (int i = 0; i < tableModel.getRowCount(); i++) {
//             Object[] rowData = new Object[tableModel.getColumnCount()];
//             for (int j = 0; j < tableModel.getColumnCount(); j++) {
//                 rowData[j] = tableModel.getValueAt(i, j);
//             }
//             data.add(rowData);
//         }

//         // Tạo tiêu đề các cột
//         String[] headers = new String[tableModel.getColumnCount()];
//         for (int i = 0; i < tableModel.getColumnCount(); i++) {
//             headers[i] = tableModel.getColumnName(i);
//         }

//         // Xuất ra file Excel
//         ExcelUtils.exportToExcel(headers, data, "Phiếu nhập", "DANH SÁCH PHIẾU NHẬP");
//     }

//     private void importFromExcel() {
//         try {
//             // Tạo tiêu đề các cột
//             String[] headers = new String[tableModel.getColumnCount()];
//             for (int i = 0; i < tableModel.getColumnCount(); i++) {
//                 headers[i] = tableModel.getColumnName(i);
//             }

//             // Nhập dữ liệu từ file Excel
//             List<Object[]> data = ExcelUtils.importFromExcel(headers);
//             if (data == null || data.isEmpty()) {
//                 JOptionPane.showMessageDialog(this,
//                         "Không có dữ liệu nào được nhập từ file Excel.",
//                         "Thông báo",
//                         JOptionPane.WARNING_MESSAGE);
//                 return;
//             }

//             // Xác nhận nhập dữ liệu
//             int result = JOptionPane.showConfirmDialog(this,
//                     "Bạn có chắc chắn muốn nhập " + data.size() + " phiếu nhập từ file Excel?",
//                     "Xác nhận nhập dữ liệu",
//                     JOptionPane.YES_NO_OPTION);

//             if (result != JOptionPane.YES_OPTION) {
//                 return;
//             }

//             // Xử lý dữ liệu nhập vào
//             int successCount = 0;
//             int failCount = 0;
//             StringBuilder errorMessages = new StringBuilder();

//             for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
//                 Object[] rowData = data.get(rowIndex);
//                 try {
//                     // Kiểm tra dữ liệu bắt buộc
//                     if (rowData.length < 6 || rowData[0] == null || rowData[1] == null ||
//                             rowData[2] == null || rowData[3] == null || rowData[4] == null ||
//                             rowData[5] == null) {
//                         errorMessages.append("Dòng ").append(rowIndex + 1)
//                                 .append(": Thiếu thông tin bắt buộc\n");
//                         failCount++;
//                         continue;
//                     }

//                     // Tạo đối tượng ImportReceipt từ dữ liệu nhập vào
//                     ImportReceipt receipt = new ImportReceipt();
//                     receipt.setImportId(rowData[0].toString());
//                     receipt.setSupplierId(rowData[1].toString());
//                     receipt.setEmployeeId(rowData[2].toString());
//                     receipt.setImportDate(rowData[3].toString());
//                     receipt.setTotalAmount(rowData[4].toString());
//                     receipt.setStatus(rowData[5].toString());

//                     // Thêm phiếu nhập vào cơ sở dữ liệu
//                     if (importReceiptController.insertImportReceipt(receipt)) {
//                         successCount++;
//                     } else {
//                         errorMessages.append("Dòng ").append(rowIndex + 1)
//                                 .append(": Không thể thêm phiếu nhập vào cơ sở dữ liệu\n");
//                         failCount++;
//                     }
//                 } catch (Exception e) {
//                     errorMessages.append("Dòng ").append(rowIndex + 1).append(": Lỗi xử lý dữ liệu - ")
//                             .append(e.getMessage()).append("\n");
//                     failCount++;
//                 }
//             }

//             // Hiển thị kết quả
//             String message = "Nhập dữ liệu hoàn tất!\n" +
//                     "Số phiếu nhập nhập thành công: " + successCount + "\n" +
//                     "Số phiếu nhập nhập thất bại: " + failCount;

//             if (failCount > 0 && errorMessages.length() > 0) {
//                 message += "\n\nChi tiết lỗi:\n" + errorMessages.toString();
//             }

//             JOptionPane.showMessageDialog(this,
//                     message,
//                     "Kết quả nhập dữ liệu",
//                     failCount > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);

//             // Cập nhật lại dữ liệu hiển thị
//             refreshReceiptData();
//         } catch (Exception e) {
//             JOptionPane.showMessageDialog(this,
//                     "Lỗi khi nhập dữ liệu từ Excel: " + e.getMessage(),
//                     "Lỗi",
//                     JOptionPane.ERROR_MESSAGE);
//             e.printStackTrace();
//         }
//     }

//     private void showAddReceiptDialog() {
//         // TODO: Implement add receipt dialog
//         JOptionPane.showMessageDialog(this,
//                 "Chức năng thêm phiếu nhập sẽ được triển khai sau",
//                 "Thông báo",
//                 JOptionPane.INFORMATION_MESSAGE);
//     }

//     private void showEditReceiptDialog() {
//         int selectedRow = receiptTable.getSelectedRow();
//         if (selectedRow == -1) {
//             JOptionPane.showMessageDialog(this,
//                     "Vui lòng chọn phiếu nhập cần sửa",
//                     "Thông báo",
//                     JOptionPane.INFORMATION_MESSAGE);
//             return;
//         }

//         String receiptId = receiptTable.getValueAt(selectedRow, 0).toString();
//         // TODO: Implement edit receipt dialog
//         JOptionPane.showMessageDialog(this,
//                 "Chức năng sửa phiếu nhập sẽ được triển khai sau",
//                 "Thông báo",
//                 JOptionPane.INFORMATION_MESSAGE);
//     }

//     private void deleteReceipt() {
//         int selectedRow = receiptTable.getSelectedRow();
//         if (selectedRow == -1) {
//             JOptionPane.showMessageDialog(this,
//                     "Vui lòng chọn phiếu nhập cần xóa",
//                     "Thông báo",
//                     JOptionPane.INFORMATION_MESSAGE);
//             return;
//         }

//         String receiptId = receiptTable.getValueAt(selectedRow, 0).toString();
//         String supplierName = receiptTable.getValueAt(selectedRow, 1).toString();

//         int confirm = JOptionPane.showConfirmDialog(this,
//                 "Bạn có chắc chắn muốn xóa phiếu nhập " + receiptId + " của nhà cung cấp " + supplierName + "?",
//                 "Xác nhận xóa",
//                 JOptionPane.YES_NO_OPTION);

//         if (confirm == JOptionPane.YES_OPTION) {
//             boolean result = importReceiptController.deleteImportReceipt(receiptId);
//             if (result) {
//                 JOptionPane.showMessageDialog(this,
//                         "Xóa phiếu nhập thành công",
//                         "Thông báo",
//                         JOptionPane.INFORMATION_MESSAGE);
//                 refreshReceiptData();
//             } else {
//                 JOptionPane.showMessageDialog(this,
//                         "Xóa phiếu nhập thất bại",
//                         "Lỗi",
//                         JOptionPane.ERROR_MESSAGE);
//             }
//         }
//     }

//     private void showReceiptDetails() {
//         int selectedRow = receiptTable.getSelectedRow();
//         if (selectedRow == -1) {
//             JOptionPane.showMessageDialog(this,
//                     "Vui lòng chọn phiếu nhập cần xem chi tiết",
//                     "Thông báo",
//                     JOptionPane.INFORMATION_MESSAGE);
//             return;
//         }

//         String receiptId = receiptTable.getValueAt(selectedRow, 0).toString();
//         // TODO: Implement receipt details dialog
//         JOptionPane.showMessageDialog(this,
//                 "Chức năng xem chi tiết phiếu nhập sẽ được triển khai sau",
//                 "Thông báo",
//                 JOptionPane.INFORMATION_MESSAGE);
//     }

//     private void showReceiptDetailsDialog() {
//         int selectedRow = receiptTable.getSelectedRow();
//         if (selectedRow == -1) {
//             JOptionPane.showMessageDialog(this,
//                     "Vui lòng chọn một phiếu nhập để xem chi tiết",
//                     "Thông báo",
//                     JOptionPane.INFORMATION_MESSAGE);
//             return;
//         }

//         String receiptId = receiptTable.getValueAt(selectedRow, 0).toString();

//         // Create a new dialog to show receipt details
//         JDialog detailsDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết phiếu nhập",
//                 true);
//         detailsDialog.setLayout(new BorderLayout());
//         detailsDialog.setSize(900, 600);
//         detailsDialog.setLocationRelativeTo(this);

//         // Create a new instance of ImportReceiptDetailGUI for this specific receipt
//         ImportReceiptDetailGUI receiptDetailPanel = new ImportReceiptDetailGUI();
//         receiptDetailPanel.showDetailsForReceipt(receiptId);

//         detailsDialog.add(receiptDetailPanel, BorderLayout.CENTER);

//         // Add close button at the bottom
//         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//         buttonPanel.setBackground(lightColor);

//         JButton closeButton = new JButton("Đóng");
//         closeButton.setFont(new Font("Arial", Font.PLAIN, 14));
//         closeButton.setBackground(new Color(108, 117, 125)); // gray
//         closeButton.setForeground(Color.BLACK);
//         closeButton.setFocusPainted(false);
//         closeButton.addActionListener(e -> detailsDialog.dispose());

//         buttonPanel.add(closeButton);
//         detailsDialog.add(buttonPanel, BorderLayout.SOUTH);

//         // Show the dialog
//         detailsDialog.setVisible(true);
//     }
// }