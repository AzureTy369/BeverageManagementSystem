package GUI;

import BUS.EmployeeBUS;
import BUS.ImportReceiptBUS;
import BUS.SupplierBUS;
import DTO.EmployeeDTO;
import DTO.ImportReceipt;
import DTO.SupplierDTO;
import BUS.ImportReceiptDetailBUS;
import BUS.InventoryBUS;
import DTO.ImportReceiptDetail;
import DTO.ProductDTO;
import BUS.ProductBUS;
import DTO.SupplierProductDTO;
import DAO.ImportReceiptDetailDAO;
import BUS.Tool;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import GUI.utils.ExcelUtils;
import java.util.Map;
import java.util.HashMap;

public class ImportReceiptsGUI extends JPanel {
    private ImportReceiptBUS importReceiptController;
    private JTable receiptTable;
    private DefaultTableModel tableModel;
    private JTextField receiptSearchField;
    private JPanel advancedSearchPanel;
    private final Color primaryColor = new Color(0, 123, 255);
    private final Color successColor = new Color(40, 167, 69);
    private final Color warningColor = new Color(255, 193, 7);
    private final Color dangerColor = new Color(220, 53, 69);
    private final Color lightColor = new Color(248, 249, 250);

    public ImportReceiptsGUI() {
        this.importReceiptController = new ImportReceiptBUS();
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(lightColor);

        createAdvancedSearchPanel();
        createGUI();
        refreshReceiptData();
    }

    private void createGUI() {
        setLayout(new BorderLayout());

        // Panel tiêu đề ở trên cùng
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(lightColor);

        JLabel titleLabel = new JLabel("Quản Lý Phiếu Nhập");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(primaryColor);

        titlePanel.add(titleLabel);

        // Panel chức năng ở dưới tiêu đề
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        // NHÓM 1: Panel chức năng bên trái - căn trái các phần tử
        JPanel leftFunctionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
        leftFunctionPanel.setBackground(new Color(240, 240, 240));
        leftFunctionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Nút xem chi tiết phiếu nhập
        JButton viewDetailsButton = new JButton("Xem chi tiết phiếu nhập");
        viewDetailsButton.setFont(new Font("Arial", Font.PLAIN, 14));
        viewDetailsButton.setBackground(primaryColor);
        viewDetailsButton.setForeground(Color.BLACK);
        viewDetailsButton.setFocusPainted(false);
        viewDetailsButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        viewDetailsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewDetailsButton.addActionListener(e -> showReceiptDetails());

        // Nút xóa - giảm kích thước
        JButton deleteBtn = createOutlineButton("Xóa", dangerColor);
        deleteBtn.setMargin(new Insets(3, 8, 3, 8));
        deleteBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        deleteBtn.addActionListener(e -> deleteReceipt());

        // Nút chỉnh sửa - giảm kích thước
        JButton editBtn = createOutlineButton("Chỉnh sửa", warningColor);
        editBtn.setMargin(new Insets(3, 8, 3, 8));
        editBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        editBtn.addActionListener(e -> showEditReceiptDialog());

        // Separator
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(1, 20));
        separator.setForeground(new Color(200, 200, 200));

        // Nút xuất Excel
        JButton exportExcelButton = createOutlineButton("Xuất Excel", primaryColor);
        exportExcelButton.setMargin(new Insets(3, 8, 3, 8));
        exportExcelButton.setFont(new Font("Arial", Font.PLAIN, 12));
        exportExcelButton.addActionListener(e -> exportToExcel());

        // Nút cập nhật trạng thái
        JButton updateStatusButton = createOutlineButton("Cập nhật trạng thái", new Color(0, 200, 83));
        updateStatusButton.setMargin(new Insets(3, 8, 3, 8));
        updateStatusButton.setFont(new Font("Arial", Font.PLAIN, 12));
        updateStatusButton.addActionListener(e -> updateReceiptNote());

        // Thêm các nút vào panel bên trái
        leftFunctionPanel.add(viewDetailsButton);
        leftFunctionPanel.add(deleteBtn);
        leftFunctionPanel.add(editBtn);
        leftFunctionPanel.add(separator);
        leftFunctionPanel.add(exportExcelButton);
        leftFunctionPanel.add(updateStatusButton);

        // NHÓM 2: Panel tìm kiếm bên phải - căn giữa các phần tử
        JPanel rightSearchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
        rightSearchPanel.setBackground(new Color(240, 240, 240));
        rightSearchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setForeground(Color.BLACK);
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // TextField tìm kiếm
        receiptSearchField = new JTextField(15);
        receiptSearchField.setPreferredSize(new Dimension(180, 25));
        receiptSearchField.setFont(new Font("Arial", Font.PLAIN, 12));
        receiptSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchReceipts();
                }
            }
        });

        // Nút tìm kiếm
        JButton searchButton = createOutlineButton("Tìm kiếm", primaryColor);
        searchButton.setMargin(new Insets(3, 8, 3, 8));
        searchButton.setFont(new Font("Arial", Font.PLAIN, 12));
        searchButton.addActionListener(e -> searchReceipts());

        // Nút làm mới
        JButton refreshButton = createOutlineButton("Làm mới", new Color(23, 162, 184));
        refreshButton.setMargin(new Insets(3, 8, 3, 8));
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshButton.addActionListener(e -> refreshReceiptData());

        // Thêm các thành phần vào panel tìm kiếm
        rightSearchPanel.add(searchLabel);
        rightSearchPanel.add(receiptSearchField);
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

        // Tạo bảng phiếu nhập
        createReceiptTable();
        JScrollPane tableScrollPane = new JScrollPane(receiptTable);
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

        // Mã phiếu nhập
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel receiptIdLabel = new JLabel("Mã phiếu nhập:");
        receiptIdLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(receiptIdLabel, gbc);

        gbc.gridx = 1;
        JTextField receiptIdSearchField = new JTextField(15);
        receiptIdSearchField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(receiptIdSearchField, gbc);

        // Nhà cung cấp
        gbc.gridx = 2;
        JLabel supplierLabel = new JLabel("Nhà cung cấp:");
        supplierLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(supplierLabel, gbc);

        gbc.gridx = 3;
        JTextField supplierSearchField = new JTextField(15);
        supplierSearchField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(supplierSearchField, gbc);

        // Người tạo
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel creatorLabel = new JLabel("Người tạo:");
        creatorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(creatorLabel, gbc);

        gbc.gridx = 1;
        JTextField creatorSearchField = new JTextField(15);
        creatorSearchField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(creatorSearchField, gbc);

        // Ghi chú
        gbc.gridx = 2;
        JLabel noteLabel = new JLabel("Ghi chú:");
        noteLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(noteLabel, gbc);

        gbc.gridx = 3;
        JTextField noteSearchField = new JTextField(15);
        noteSearchField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(noteSearchField, gbc);

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
            String receiptId = receiptIdSearchField.getText().trim();
            String supplier = supplierSearchField.getText().trim();
            String creator = creatorSearchField.getText().trim();
            String note = noteSearchField.getText().trim();
            boolean isAnd = andRadio.isSelected();

            // Gọi phương thức tìm kiếm nâng cao
            advancedSearchReceipts(receiptId, supplier, creator, note, isAnd);
        });

        JButton clearButton = createOutlineButton("Xóa tìm kiếm", new Color(108, 117, 125));
        clearButton.setForeground(Color.BLACK);
        clearButton.addActionListener(e -> {
            receiptIdSearchField.setText("");
            supplierSearchField.setText("");
            creatorSearchField.setText("");
            noteSearchField.setText("");
            refreshReceiptData();
        });

        controlPanel.add(andRadio);
        controlPanel.add(orRadio);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(searchButton);
        controlPanel.add(clearButton);

        advancedSearchPanel.add(controlPanel);
    }

    private void createReceiptTable() {
        String[] columnNames = { "Mã phiếu", "Nhà cung cấp", "Người tạo", "Ngày nhập", "Tổng tiền", "Ghi chú",
                "Trạng thái" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        receiptTable = new JTable(tableModel);
        receiptTable.setFont(new Font("Arial", Font.PLAIN, 14));
        receiptTable.setRowHeight(25);
        receiptTable.setGridColor(new Color(230, 230, 230));
        receiptTable.setBackground(Color.WHITE);
        receiptTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        receiptTable.getTableHeader().setBackground(Color.WHITE);
        receiptTable.getTableHeader().setForeground(Color.BLACK);

        // Thêm sự kiện khi click vào bảng
        receiptTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double click để xem chi tiết
                    showReceiptDetails();
                }
            }
        });

        // Thêm renderer để hiển thị trạng thái với màu sắc
        receiptTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String status = (String) value;
                if ("Đã hoàn thành".equals(status)) {
                    c.setForeground(successColor);
                } else if ("Đã hủy".equals(status)) {
                    c.setForeground(dangerColor);
                } else {
                    c.setForeground(warningColor); // Đang xử lý
                }

                return c;
            }
        });
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setBackground(lightColor);

        JButton refreshButton = createOutlineButton("Làm mới", primaryColor);
        refreshButton.addActionListener(e -> refreshReceiptData());
        panel.add(refreshButton);

        JButton changeStatusButton = createOutlineButton("Thay đổi trạng thái", warningColor);
        changeStatusButton.addActionListener(e -> changeReceiptStatus());
        panel.add(changeStatusButton);

        JButton viewButton = createOutlineButton("Xem chi tiết", primaryColor);
        viewButton.addActionListener(e -> showReceiptDetails());
        panel.add(viewButton);

        JButton editButton = createOutlineButton("Sửa phiếu nhập", warningColor);
        editButton.addActionListener(e -> showEditReceiptDialog());
        panel.add(editButton);

        JButton deleteButton = createOutlineButton("Xóa phiếu nhập", dangerColor);
        deleteButton.addActionListener(e -> deleteReceipt());
        panel.add(deleteButton);

        return panel;
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

    private void searchReceipts() {
        String keyword = receiptSearchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshReceiptData();
            return;
        }

        List<ImportReceipt> results = importReceiptController.search(keyword);
        displayReceipts(results);
    }

    private void advancedSearchReceipts(String receiptId, String supplier, String creator, String note,
            boolean isAnd) {
        List<ImportReceipt> results = new ArrayList<>();
        List<ImportReceipt> allReceipts = importReceiptController.getAllImportReceipts();

        for (ImportReceipt receipt : allReceipts) {
            boolean matchReceiptId = receiptId.isEmpty()
                    || receipt.getImportId().toLowerCase().contains(receiptId.toLowerCase());
            boolean matchSupplier = supplier.isEmpty()
                    || receipt.getSupplierId().toLowerCase().contains(supplier.toLowerCase());
            boolean matchCreator = creator.isEmpty()
                    || receipt.getEmployeeId().toLowerCase().contains(creator.toLowerCase());
            boolean matchNote = note.isEmpty()
                    || receipt.getNote() != null && receipt.getNote().toLowerCase().contains(note.toLowerCase());

            if (isAnd) {
                if (matchReceiptId && matchSupplier && matchCreator && matchNote) {
                    results.add(receipt);
                }
            } else {
                if (matchReceiptId || matchSupplier || matchCreator || matchNote) {
                    results.add(receipt);
                }
            }
        }

        displayReceipts(results);
    }

    private void displayReceipts(List<ImportReceipt> receipts) {
        tableModel.setRowCount(0);

        SupplierBUS supplierBUS = new SupplierBUS();
        EmployeeBUS employeeBUS = new EmployeeBUS();

        for (ImportReceipt receipt : receipts) {
            String supplierId = receipt.getSupplierId();
            String supplierName = supplierId;
            SupplierDTO supplier = supplierBUS.getSupplierById(supplierId);
            if (supplier != null) {
                supplierName = supplier.getSupplierName();
            }

            String employeeId = receipt.getEmployeeId();
            String employeeName = employeeId;
            EmployeeDTO employee = employeeBUS.getEmployeeById(employeeId);
            if (employee != null) {
                employeeName = employee.getFirstName() + " " + employee.getLastName();
            }

            Object[] rowData = {
                    receipt.getImportId(),
                    supplierName,
                    employeeName,
                    receipt.getImportDate(),
                    receipt.getTotalAmount(),
                    receipt.getNote(),
                    receipt.getStatus() != null ? receipt.getStatus() : "Đang xử lý"
            };
            tableModel.addRow(rowData);
        }
    }

    private void refreshReceiptData() {
        List<ImportReceipt> receipts = importReceiptController.getAllImportReceipts();
        displayReceipts(receipts);
    }

    private void exportToExcel() {
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
        ExcelUtils.exportToExcel(headers, data, "Phiếu nhập", "DANH SÁCH PHIẾU NHẬP");
    }

    private void importFromExcel() {
        try {
            // Tạo tiêu đề các cột
            String[] headers = new String[tableModel.getColumnCount()];
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                headers[i] = tableModel.getColumnName(i);
            }

            // Nhập dữ liệu từ file Excel
            List<Object[]> data = ExcelUtils.importFromExcel(headers);
            if (data == null || data.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Không có dữ liệu nào được nhập từ file Excel.",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Xác nhận nhập dữ liệu
            int result = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn nhập " + data.size() + " phiếu nhập từ file Excel?",
                    "Xác nhận nhập dữ liệu",
                    JOptionPane.YES_NO_OPTION);

            if (result != JOptionPane.YES_OPTION) {
                return;
            }

            // Xử lý dữ liệu nhập vào
            int successCount = 0;
            int failCount = 0;
            StringBuilder errorMessages = new StringBuilder();

            for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
                Object[] rowData = data.get(rowIndex);
                try {
                    // Kiểm tra dữ liệu bắt buộc
                    if (rowData.length < 6 || rowData[0] == null || rowData[1] == null ||
                            rowData[2] == null || rowData[3] == null || rowData[4] == null ||
                            rowData[5] == null) {
                        errorMessages.append("Dòng ").append(rowIndex + 1)
                                .append(": Thiếu thông tin bắt buộc\n");
                        failCount++;
                        continue;
                    }

                    // Tạo đối tượng ImportReceipt từ dữ liệu nhập vào
                    ImportReceipt receipt = new ImportReceipt();
                    receipt.setImportId(rowData[0].toString());
                    receipt.setSupplierId(rowData[1].toString());
                    receipt.setEmployeeId(rowData[2].toString());
                    receipt.setImportDate(rowData[3].toString());
                    receipt.setTotalAmount(rowData[4].toString());
                    receipt.setNote(rowData[5].toString());

                    // Thêm phiếu nhập vào cơ sở dữ liệu
                    if (importReceiptController.insertImportReceipt(receipt)) {
                        successCount++;
                    } else {
                        errorMessages.append("Dòng ").append(rowIndex + 1)
                                .append(": Không thể thêm phiếu nhập vào cơ sở dữ liệu\n");
                        failCount++;
                    }
                } catch (Exception e) {
                    errorMessages.append("Dòng ").append(rowIndex + 1).append(": Lỗi xử lý dữ liệu - ")
                            .append(e.getMessage()).append("\n");
                    failCount++;
                }
            }

            // Hiển thị kết quả
            String message = "Nhập dữ liệu hoàn tất!\n" +
                    "Số phiếu nhập nhập thành công: " + successCount + "\n" +
                    "Số phiếu nhập nhập thất bại: " + failCount;

            if (failCount > 0 && errorMessages.length() > 0) {
                message += "\n\nChi tiết lỗi:\n" + errorMessages.toString();
            }

            JOptionPane.showMessageDialog(this,
                    message,
                    "Kết quả nhập dữ liệu",
                    failCount > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);

            // Cập nhật lại dữ liệu hiển thị
            refreshReceiptData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi nhập dữ liệu từ Excel: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showAddReceiptDialog() {
        // TODO: Implement add receipt dialog
        JOptionPane.showMessageDialog(this,
                "Chức năng thêm phiếu nhập sẽ được triển khai sau",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Hiển thị dialog xem chi tiết phiếu nhập
     */
    private void showReceiptDetails() {
        int selectedRow = receiptTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập để xem chi tiết", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy mã phiếu nhập từ bảng
        String receiptId = receiptTable.getValueAt(selectedRow, 0).toString();
        System.out.println("Đang hiển thị chi tiết cho phiếu nhập: " + receiptId);

        // Lấy thông tin phiếu nhập
        ImportReceipt receipt = importReceiptController.getImportReceiptById(receiptId);
        if (receipt == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin phiếu nhập " + receiptId, "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo dialog hiển thị chi tiết
        JDialog detailDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                "Chi tiết phiếu nhập " + receiptId, true);
        detailDialog.setSize(1000, 600);
        detailDialog.setLocationRelativeTo(this);
        detailDialog.setLayout(new BorderLayout());

        // Tạo panel hiển thị chi tiết phiếu nhập
        ImportReceiptDetailGUI detailGUI = new ImportReceiptDetailGUI(receiptId, false);
        detailGUI.setBackground(Color.WHITE);

        // Thêm vào dialog
        detailDialog.add(detailGUI, BorderLayout.CENTER);

        // Panel nút đóng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> detailDialog.dispose());
        buttonPanel.add(closeButton);

        detailDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Hiển thị dialog
        detailDialog.setVisible(true);
    }

    /**
     * Hiển thị cửa sổ sửa phiếu nhập
     */
    private void showEditReceiptDialog() {
        int row = receiptTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu nhập để sửa");
            return;
        }

        String receiptId = (String) receiptTable.getValueAt(row, 0);
        String status = (String) receiptTable.getValueAt(row, 5);

        // Kiểm tra trạng thái phiếu nhập - chỉ cho phép sửa nếu là "Phiếu nhập mới"
        // hoặc "Đang xử lý"
        if ("Đã hoàn thành".equals(status) || "Đã hủy".equals(status)) {
            JOptionPane.showMessageDialog(this, "Không thể sửa phiếu nhập đã hoàn thành hoặc đã hủy", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra xem phiếu nhập có chi tiết
        ImportReceiptDetailBUS detailBUS = new ImportReceiptDetailBUS();
        List<ImportReceiptDetail> details = detailBUS.getImportReceiptDetailsByReceiptId(receiptId);

        if (details == null || details.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Phiếu nhập này không có chi tiết. Hãy nhập sản phẩm vào phiếu nhập.",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        // Tạo cửa sổ chỉnh sửa
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa phiếu nhập", true);
        dialog.setSize(1200, 700);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout());

        // Tạo panel chi tiết phiếu nhập với chế độ chỉnh sửa
        ImportReceiptDetailGUI detailPanel = new ImportReceiptDetailGUI(receiptId, true);
        dialog.add(detailPanel, BorderLayout.CENTER);

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu thay đổi");
        JButton cancelButton = new JButton("Hủy");

        saveButton.addActionListener(e -> {
            boolean success = detailPanel.saveChanges();
            if (success) {
                dialog.dispose();
                refreshReceiptData(); // Làm mới dữ liệu sau khi lưu
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void deleteReceipt() {
        int selectedRow = receiptTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn phiếu nhập cần xóa",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String receiptId = receiptTable.getValueAt(selectedRow, 0).toString();
        String supplierName = receiptTable.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa phiếu nhập " + receiptId + " của nhà cung cấp " + supplierName + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean result = importReceiptController.deleteImportReceipt(receiptId);
            if (result) {
                JOptionPane.showMessageDialog(this,
                        "Xóa phiếu nhập thành công",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshReceiptData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Xóa phiếu nhập thất bại",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateReceiptNote() {
        int selectedRow = receiptTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập để cập nhật trạng thái.", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String receiptId = (String) tableModel.getValueAt(selectedRow, 0);
        ImportReceipt receipt = importReceiptController.getImportReceiptById(receiptId);
        if (receipt == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String currentStatus = receipt.getNote();

        // Tạo hộp thoại để chọn trạng thái
        String[] statuses = { "Đang xử lý", "Đã hoàn thành", "Đã hủy" };
        String newStatus = (String) JOptionPane.showInputDialog(
                this,
                "Chọn trạng thái mới:",
                "Cập nhật trạng thái phiếu nhập",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statuses,
                currentStatus == null ? statuses[0] : currentStatus);

        if (newStatus != null) {
            // Nếu chọn "Đã hoàn thành", hỏi người dùng có muốn cập nhật số lượng tồn kho
            // không
            boolean updateInventory = false;
            if ("Đã hoàn thành".equals(newStatus) && !"Đã hoàn thành".equals(currentStatus)) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có muốn cập nhật số lượng tồn kho cho sản phẩm trong phiếu nhập này không?",
                        "Xác nhận cập nhật tồn kho",
                        JOptionPane.YES_NO_OPTION);
                updateInventory = (confirm == JOptionPane.YES_OPTION);
            }

            receipt.setNote(newStatus);
            boolean success = importReceiptController.updateImportReceipt(receipt);

            if (success) {
                if (updateInventory) {
                    // Cập nhật tồn kho nếu người dùng đồng ý
                    ImportReceiptDetailBUS importReceiptDetailBUS = new ImportReceiptDetailBUS();
                    InventoryBUS inventoryBUS = new InventoryBUS();

                    List<ImportReceiptDetail> receiptDetails = importReceiptDetailBUS
                            .getImportReceiptDetailsByReceiptId(receiptId);
                    StringBuilder resultMessage = new StringBuilder(
                            "Cập nhật trạng thái phiếu nhập thành công!\n\nKết quả cập nhật tồn kho:\n");
                    boolean allSuccess = true;

                    for (ImportReceiptDetail detail : receiptDetails) {
                        String productId = detail.getProductId();
                        int quantity = detail.getQuantity();

                        boolean updateResult = inventoryBUS.updateInventoryQuantity(productId, quantity);
                        if (updateResult) {
                            resultMessage.append("- ").append(productId).append(": +").append(quantity)
                                    .append(" đơn vị\n");
                        } else {
                            resultMessage.append("- ").append(productId).append(": Lỗi cập nhật\n");
                            allSuccess = false;
                        }
                    }

                    if (allSuccess) {
                        resultMessage.append("\nTất cả sản phẩm đã được cập nhật thành công!");
                    } else {
                        resultMessage.append("\nMột số sản phẩm không thể cập nhật. Vui lòng kiểm tra lại.");
                    }

                    JOptionPane.showMessageDialog(this, resultMessage.toString(), "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thành công!", "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                refreshReceiptData();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Tải dữ liệu phiếu nhập từ CSDL
     */
    private void loadData() {
        // Xóa dữ liệu cũ
        tableModel.setRowCount(0);

        System.out.println("Đang tải dữ liệu phiếu nhập...");

        // Lấy danh sách phiếu nhập
        List<ImportReceipt> receipts = importReceiptController.getAllImportReceipts();
        System.out.println("Tìm thấy " + receipts.size() + " phiếu nhập trong CSDL");

        // Lấy thông tin nhà cung cấp
        SupplierBUS supplierBUS = new SupplierBUS();
        List<SupplierDTO> suppliers = supplierBUS.getAllSuppliers();

        // Map nhà cung cấp
        Map<String, String> supplierMap = new HashMap<>();
        for (SupplierDTO supplier : suppliers) {
            supplierMap.put(supplier.getSupplierId(), supplier.getSupplierName());
        }

        // Khởi tạo ImportReceiptDetailBUS
        ImportReceiptDetailBUS detailBUS = new ImportReceiptDetailBUS();

        // Hiển thị dữ liệu
        for (ImportReceipt receipt : receipts) {
            // Lấy tên nhà cung cấp
            String supplierName = supplierMap.getOrDefault(receipt.getSupplierId(), "Không xác định");

            // Kiểm tra số lượng chi tiết phiếu
            List<ImportReceiptDetail> details = detailBUS.getImportReceiptDetailsByReceiptId(receipt.getImportId());
            int detailCount = details.size();

            // Tính tổng tiền thực tế
            double actualTotal = 0;
            for (ImportReceiptDetail detail : details) {
                actualTotal += detail.getTotal();
            }

            // So sánh với tổng tiền trong phiếu
            double receiptTotal = Double.parseDouble(receipt.getTotalAmount());
            boolean totalMismatch = Math.abs(actualTotal - receiptTotal) > 0.01;

            // Thêm vào bảng
            Object[] row = {
                    receipt.getImportId(),
                    supplierName,
                    receipt.getEmployeeId(),
                    receipt.getImportDate(),
                    String.format("%,.0f", Double.parseDouble(receipt.getTotalAmount())),
                    receipt.getNote(), // Trạng thái được lưu trong note
                    detailCount + " mục"
            };

            tableModel.addRow(row);

            // Gỡ lỗi
            System.out.println("Phiếu nhập: " + receipt.getImportId() +
                    ", NCC: " + supplierName +
                    ", Ngày: " + receipt.getImportDate() +
                    ", Tổng tiền: " + receipt.getTotalAmount() +
                    ", Trạng thái: " + receipt.getNote() +
                    ", Chi tiết: " + detailCount);

            if (totalMismatch) {
                System.out.println("  CẢNH BÁO: Tổng tiền không khớp - Phiếu: " +
                        receiptTotal + ", Thực tế: " + actualTotal);
            }
        }
    }

    private void changeReceiptStatus() {
        int selectedRow = receiptTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập để thay đổi trạng thái");
            return;
        }

        String importId = tableModel.getValueAt(selectedRow, 0).toString();
        ImportReceipt receipt = importReceiptController.getImportReceiptById(importId);

        if (receipt == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin phiếu nhập");
            return;
        }

        String currentStatus = receipt.getStatus();
        if ("Đã hủy".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "Không thể thay đổi trạng thái của phiếu nhập đã hủy");
            return;
        }

        // Tạo JComboBox cho trạng thái
        JComboBox<String> statusComboBox = new JComboBox<>(new String[] { "Đang xử lý", "Đã hoàn thành", "Đã hủy" });
        statusComboBox.setSelectedItem(currentStatus);

        // Hiển thị dialog
        int result = JOptionPane.showConfirmDialog(
                this,
                new Object[] { "Chọn trạng thái mới cho phiếu nhập " + importId + ":", statusComboBox },
                "Thay đổi trạng thái phiếu nhập",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newStatus = statusComboBox.getSelectedItem().toString();

            // Nếu trạng thái không thay đổi, không làm gì cả
            if (newStatus.equals(currentStatus)) {
                return;
            }

            // Nếu chuyển từ "Đã hoàn thành" sang trạng thái khác, hiện cảnh báo
            if ("Đã hoàn thành".equals(currentStatus) && !"Đã hoàn thành".equals(newStatus)) {
                int confirmResult = JOptionPane.showConfirmDialog(
                        this,
                        "Phiếu nhập này đã hoàn thành và có thể đã cập nhật tồn kho.\n" +
                                "Việc thay đổi trạng thái có thể gây ra sự không nhất quán trong dữ liệu.\n" +
                                "Bạn có chắc chắn muốn thay đổi trạng thái không?",
                        "Xác nhận thay đổi trạng thái",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirmResult != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Nếu chuyển sang "Đã hoàn thành", hiện xác nhận
            if ("Đã hoàn thành".equals(newStatus)) {
                int confirmResult = JOptionPane.showConfirmDialog(
                        this,
                        "Khi chuyển trạng thái sang 'Đã hoàn thành', hệ thống sẽ cập nhật tồn kho.\n" +
                                "Bạn có chắc chắn muốn thay đổi trạng thái không?",
                        "Xác nhận thay đổi trạng thái",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);

                if (confirmResult != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Cập nhật trạng thái
            boolean success = importReceiptController.updateImportReceiptStatus(importId, newStatus);

            if (success) {
                JOptionPane.showMessageDialog(
                        this,
                        "Đã cập nhật trạng thái phiếu nhập từ [" + currentStatus + "] thành [" + newStatus + "]",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);

                // Làm mới dữ liệu
                refreshReceiptData();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Lỗi khi cập nhật trạng thái phiếu nhập",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}