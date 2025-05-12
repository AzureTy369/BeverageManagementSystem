package GUI;

import BUS.PositionBUS;
import DTO.PositionDTO;
import GUI.utils.ExcelUtils;
import GUI.utils.ButtonHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.math.BigDecimal;
import java.util.ArrayList;

public class PositionGUI extends JPanel {
    private PositionBUS positionController;

    private JTable positionTable;
    private DefaultTableModel tableModel;

    private JTextField idField;
    private JTextField nameField;
    private JTextField salaryField;

    private JTextField positionSearchField;
    private JPanel advancedSearchPanel;

    private JButton updateButton;
    private JButton deleteButton;

    // Colors
    private final Color primaryColor = new Color(0, 123, 255);
    private final Color successColor = new Color(40, 167, 69);
    private final Color warningColor = new Color(255, 193, 7);
    private final Color dangerColor = new Color(220, 53, 69);
    private final Color lightColor = new Color(248, 249, 250);

    // Dialog cho form thêm/sửa chức vụ
    private JDialog positionFormDialog;

    public PositionGUI(PositionBUS positionController) {
        this.positionController = positionController;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(lightColor);

        createAdvancedSearchPanel();
        createGUI();
        createPositionFormDialog();
        refreshPositionData();
    }

    private void createGUI() {
        // Panel tiêu đề ở trên cùng
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(lightColor);

        JLabel titleLabel = new JLabel("Quản Lý Chức Vụ");
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

        // Nút thêm - giảm kích thước
        JButton addBtn = createOutlineButton("Thêm", successColor);
        addBtn.setMargin(new Insets(3, 8, 3, 8));
        addBtn.setFont(new Font("Arial", Font.BOLD, 16));
        addBtn.addActionListener(e -> showAddPositionDialog());

        // Nút xóa - giảm kích thước
        JButton deleteBtn = createOutlineButton("Xóa", dangerColor);
        deleteBtn.setMargin(new Insets(3, 8, 3, 8));
        deleteBtn.setFont(new Font("Arial", Font.BOLD, 16));
        deleteBtn.addActionListener(e -> deletePosition());

        // Gán deleteBtn vào biến thành viên để sử dụng ở nơi khác
        this.deleteButton = deleteBtn;

        // Nút chỉnh sửa - giảm kích thước
        JButton editBtn = createOutlineButton("Chỉnh sửa", warningColor);
        editBtn.setMargin(new Insets(3, 8, 3, 8));
        editBtn.setFont(new Font("Arial", Font.BOLD, 16));
        editBtn.addActionListener(e -> showEditPositionDialog());

        // Gán editBtn vào biến thành viên để sử dụng ở nơi khác
        this.updateButton = editBtn;

        // Separator
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(1, 20));
        separator.setForeground(new Color(200, 200, 200));

        // Nút xuất Excel - giảm kích thước
        JButton exportExcelButton = createOutlineButton("Xuất Excel", primaryColor);
        exportExcelButton.setMargin(new Insets(3, 8, 3, 8));
        exportExcelButton.setFont(new Font("Arial", Font.BOLD, 16));
        exportExcelButton.addActionListener(e -> exportToExcel());

        // Thêm nút nhập Excel vào panel chứa các nút
        JButton importExcelButton = createOutlineButton("Nhập Excel", successColor);
        importExcelButton.setMargin(new Insets(3, 8, 3, 8));
        importExcelButton.setFont(new Font("Arial", Font.BOLD, 16));
        importExcelButton.addActionListener(e -> importFromExcel());

        // Thêm các nút vào panel bên trái
        leftFunctionPanel.add(addBtn);
        leftFunctionPanel.add(deleteBtn);
        leftFunctionPanel.add(editBtn);
        leftFunctionPanel.add(separator);
        leftFunctionPanel.add(exportExcelButton);
        leftFunctionPanel.add(importExcelButton);
        // NHÓM 2: Panel tìm kiếm bên phải - căn giữa các phần tử
        JPanel rightSearchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
        rightSearchPanel.setBackground(new Color(240, 240, 240));
        rightSearchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setForeground(Color.BLACK);
        searchLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // TextField tìm kiếm - giảm kích thước
        positionSearchField = new JTextField(15);
        positionSearchField.setPreferredSize(new Dimension(180, 25));
        positionSearchField.setFont(new Font("Arial", Font.BOLD, 16));
        positionSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchPositions();
                }
            }
        });

        // Nút tìm kiếm - giảm kích thước
        JButton searchButton = createOutlineButton("Tìm kiếm", primaryColor);
        searchButton.setMargin(new Insets(3, 8, 3, 8));
        searchButton.setFont(new Font("Arial", Font.PLAIN, 12));
        searchButton.addActionListener(e -> searchPositions());

        // Nút làm mới - giảm kích thước
        JButton refreshButton = createOutlineButton("Làm mới", new Color(23, 162, 184));
        refreshButton.setMargin(new Insets(3, 8, 3, 8));
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshButton.addActionListener(e -> refreshPositionData());

        // Thêm các thành phần vào panel tìm kiếm
        rightSearchPanel.add(searchLabel);
        rightSearchPanel.add(positionSearchField);
        rightSearchPanel.add(searchButton);
        rightSearchPanel.add(refreshButton);

        // Thêm hai panel vào panel chính ở trên cùng - giảm khoảng cách giữa các panel
        JPanel topFunctionContainer = new JPanel(new GridBagLayout());
        topFunctionContainer.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 0, 0, 5); // Giảm margin bên phải
        topFunctionContainer.add(leftFunctionPanel, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 5, 0, 0); // Giảm margin bên trái
        topFunctionContainer.add(rightSearchPanel, gbc);

        topPanel.add(topFunctionContainer, BorderLayout.CENTER);

        // Thêm panel tìm kiếm nâng cao - giảm margin
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

        // Tạo bảng chức vụ
        createPositionTable();
        JScrollPane tableScrollPane = new JScrollPane(positionTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.getViewport().setBackground(lightColor);

        // Panel chứa bảng có viền - giảm margin
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
        advancedSearchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(222, 226, 230)),
                BorderFactory.createEmptyBorder(5, 0, 5, 0)));
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

        // Tên chức vụ
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Tên chức vụ:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(15);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(nameField, gbc);

        // Mức lương
        gbc.gridx = 2;
        JLabel salaryLabel = new JLabel("Mức lương:");
        salaryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(salaryLabel, gbc);

        gbc.gridx = 3;
        JTextField salaryField = new JTextField(15);
        salaryField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(salaryField, gbc);

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
            String name = nameField.getText().trim();
            String salary = salaryField.getText().trim();
            boolean isAnd = andRadio.isSelected();

            // Thực hiện tìm kiếm nâng cao
            advancedSearchPositions(name, salary, isAnd);
        });

        JButton clearButton = createOutlineButton("Xóa tìm kiếm", new Color(108, 117, 125));
        clearButton.setForeground(Color.BLACK);
        clearButton.addActionListener(e -> {
            nameField.setText("");
            salaryField.setText("");
            refreshPositionData();
        });

        controlPanel.add(andRadio);
        controlPanel.add(orRadio);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(searchButton);
        controlPanel.add(clearButton);

        advancedSearchPanel.add(controlPanel);
    }

    private void createPositionTable() {
        String[] columnNames = { "Mã chức vụ", "Tên chức vụ", "Mức lương" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        positionTable = new JTable(tableModel);
        positionTable.setFont(new Font("Arial", Font.PLAIN, 14));
        positionTable.setRowHeight(25);
        positionTable.setGridColor(new Color(230, 230, 230));
        positionTable.setSelectionBackground(new Color(173, 216, 230));
        positionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        positionTable.getTableHeader().setBackground(primaryColor);
        positionTable.getTableHeader().setForeground(Color.BLACK);
        positionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column width
        positionTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        positionTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        positionTable.getColumnModel().getColumn(2).setPreferredWidth(150);

        // Add selection listener
        positionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = positionTable.getSelectedRow();
                if (selectedRow != -1) {
                    String positionId = positionTable.getValueAt(selectedRow, 0).toString();
                    PositionDTO position = positionController.getPositionById(positionId);
                    if (position != null) {
                        displayPositionData(position);
                    }
                }
            }
        });
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusPanel.setBackground(new Color(248, 249, 250));

        JLabel statusLabel = new JLabel("Tổng số chức vụ: " + positionController.getAllPositions().size());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(new Color(33, 37, 41));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));

        statusPanel.add(statusLabel, BorderLayout.WEST);

        return statusPanel;
    }

    private JButton createOutlineButton(String text, Color color) {
        return ButtonHelper.createButton(text, color);
    }

    private void searchPositions() {
        String searchText = positionSearchField.getText().trim();
        if (searchText.isEmpty()) {
            refreshPositionData();
            return;
        }

        List<PositionDTO> results = positionController.searchPositions(searchText);
        displayPositions(results);
    }

    private void advancedSearchPositions(String name, String salary, boolean isAnd) {
        // Tìm kiếm nâng cao - Phương thức này tự triển khai trực tiếp trong GUI
        List<PositionDTO> allPositions = positionController.getAllPositions();
        List<PositionDTO> results = new ArrayList<>();

        // Nếu tất cả điều kiện trống, trả về danh sách đầy đủ
        if (name.isEmpty() && salary.isEmpty()) {
            displayPositions(allPositions);
            return;
        }

        // Tìm kiếm theo các điều kiện
        for (PositionDTO position : allPositions) {
            boolean nameMatch = name.isEmpty() ||
                    (position.getPositionName() != null &&
                            position.getPositionName().toLowerCase().contains(name.toLowerCase()));

            boolean salaryMatch = salary.isEmpty();
            if (!salary.isEmpty() && position.getSalary() != null) {
                try {
                    // So sánh lương, tìm kiếm gần đúng
                    BigDecimal inputSalary = new BigDecimal(salary.replaceAll("[^0-9]", ""));
                    BigDecimal positionSalary = position.getSalary();

                    // Cho phép sai số 10% cho việc tìm kiếm lương
                    BigDecimal lowerBound = inputSalary.multiply(new BigDecimal("0.9"));
                    BigDecimal upperBound = inputSalary.multiply(new BigDecimal("1.1"));

                    salaryMatch = (positionSalary.compareTo(lowerBound) >= 0 &&
                            positionSalary.compareTo(upperBound) <= 0);
                } catch (NumberFormatException e) {
                    // Nếu không thể parse thành số, bỏ qua điều kiện này
                    salaryMatch = true;
                }
            }

            if (isAnd) {
                // Phép AND - tất cả điều kiện phải đúng
                if (nameMatch && salaryMatch) {
                    results.add(position);
                }
            } else {
                // Phép OR - chỉ cần một điều kiện đúng
                if (nameMatch || salaryMatch) {
                    results.add(position);
                }
            }
        }

        // Hiển thị kết quả
        displayPositions(results);
    }

    private void displayPositions(List<PositionDTO> positions) {
        tableModel.setRowCount(0);

        for (PositionDTO position : positions) {
            Object[] row = new Object[3];
            row[0] = position.getPositionId();
            row[1] = position.getPositionName();
            row[2] = String.format("%,.0f VNĐ", position.getSalary());
            tableModel.addRow(row);
        }
    }

    private void createPositionFormDialog() {
        // Tạo dialog
        positionFormDialog = new JDialog();
        positionFormDialog.setTitle("Thông tin chức vụ");
        positionFormDialog.setSize(500, 350);
        positionFormDialog.setLocationRelativeTo(this);
        positionFormDialog.setModal(true);
        positionFormDialog.setResizable(false);

        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        dialogPanel.setBackground(lightColor);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Mã chức vụ
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel idLabel = new JLabel("Mã chức vụ:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        idField = new JTextField(20);
        idField.setFont(new Font("Arial", Font.PLAIN, 14));
        idField.setEditable(true);
        formPanel.add(idField, gbc);

        // Tên chức vụ
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Tên chức vụ:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(nameField, gbc);

        // Mức lương
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel salaryLabel = new JLabel("Mức lương:");
        salaryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(salaryLabel, gbc);

        gbc.gridx = 1;
        salaryField = new JTextField(20);
        salaryField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(salaryField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);

        JButton saveButton = createOutlineButton("Lưu", successColor);
        saveButton.addActionListener(e -> {
            if (idField.getText().isEmpty()) {
                addPosition();
            } else {
                updatePosition();
            }
        });

        JButton cancelButton = createOutlineButton("Hủy", dangerColor);
        cancelButton.addActionListener(e -> positionFormDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add components to main panel
        dialogPanel.add(formPanel, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        positionFormDialog.add(dialogPanel);
        positionFormDialog.pack();
    }

    private void showAddPositionDialog() {
        // Clear form
        clearForm();

        // Generate new ID
        String newId = generateNewPositionId();
        idField.setText(newId);
        idField.setEditable(true); // Không cho phép sửa ID khi thêm mới

        // Set title and show dialog
        positionFormDialog.setTitle("Thêm chức vụ mới");
        positionFormDialog.setVisible(true);
    }

    private void showEditPositionDialog() {
        int selectedRow = positionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chức vụ cần sửa", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Lấy dữ liệu từ hàng đã chọn
        String positionId = positionTable.getValueAt(selectedRow, 0).toString();
        PositionDTO position = positionController.getPositionById(positionId);

        if (position != null) {
            // Set form fields
            idField.setText(position.getPositionId());
            nameField.setText(position.getPositionName());
            salaryField.setText(String.valueOf(position.getSalary()));

            // Set title and show dialog
            positionFormDialog.setTitle("Cập nhật thông tin chức vụ");
            positionFormDialog.setVisible(true);
        }
    }

    private String generateNewPositionId() {
        try {
            List<PositionDTO> positions = positionController.getAllPositions();
            int maxId = 0;

            for (PositionDTO position : positions) {
                String id = position.getPositionId();
                // Trích xuất số từ ID của chức vụ (bỏ qua "CV" và chỉ lấy số)
                if (id.startsWith("CV")) {
                    try {
                        int idNum = Integer.parseInt(id.substring(2));
                        if (idNum > maxId) {
                            maxId = idNum;
                        }
                    } catch (NumberFormatException e) {
                        // Bỏ qua ID không đúng định dạng
                    }
                } else if (id.matches("\\d+")) {
                    // Nếu ID chỉ là số, cũng xem xét
                    try {
                        int idNum = Integer.parseInt(id);
                        if (idNum > maxId) {
                            maxId = idNum;
                        }
                    } catch (NumberFormatException e) {
                        // Bỏ qua ID không đúng định dạng
                    }
                }
            }

            // Tạo ID mới với định dạng CV### (ví dụ: CV001, CV002, ...)
            String newId = String.format("CV%03d", maxId + 1);
            return newId;
        } catch (Exception e) {
            System.err.println("Error generating new position ID: " + e.getMessage());
            e.printStackTrace();
            // Trả về ID mặc định nếu có lỗi
            return "CV001";
        }
    }

    private void refreshPositionData() {
        // Clear table
        tableModel.setRowCount(0);

        // Get all positions
        List<PositionDTO> positions = positionController.getAllPositions();

        // Add positions to table
        for (PositionDTO position : positions) {
            Object[] row = new Object[3];
            row[0] = position.getPositionId();
            row[1] = position.getPositionName();
            row[2] = String.format("%,.0f VNĐ", position.getSalary());
            tableModel.addRow(row);
        }
    }

    private void displayPositionData(PositionDTO position) {
        idField.setText(position.getPositionId());
        nameField.setText(position.getPositionName());
        salaryField.setText(String.valueOf(position.getSalary()));

        // Enable buttons
        updateButton.setEnabled(true);
        deleteButton.setEnabled(true);
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        salaryField.setText("");

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);

        positionTable.clearSelection();
    }

    private void addPosition() {
        if (!validateInput()) {
            return;
        }

        // Lấy thông tin từ form
        String positionId = idField.getText().trim();
        String positionName = nameField.getText().trim();

        // Kiểm tra xem mã chức vụ đã tồn tại chưa
        if (positionController.isDuplicatePositionId(positionId)) {
            JOptionPane.showMessageDialog(this,
                    "Mã chức vụ đã tồn tại. Vui lòng sử dụng mã khác hoặc sử dụng chức năng cập nhật.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            idField.requestFocus();
            return;
        }

        // Xử lý định dạng mức lương
        String salaryText = salaryField.getText().trim();
        String cleanSalary = salaryText.replaceAll("[^0-9]", "");
        BigDecimal salary = new BigDecimal(cleanSalary);

        // Tạo đối tượng PositionDTO để thêm
        PositionDTO position = new PositionDTO(positionId, positionName, salary);

        // Thêm chức vụ
        boolean success = positionController.addPosition(position);

        if (success) {
            JOptionPane.showMessageDialog(this, "Thêm chức vụ thành công", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            positionFormDialog.dispose();
            refreshPositionData();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm chức vụ thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePosition() {
        if (!validateInput()) {
            return;
        }

        String positionId = idField.getText();
        String positionName = nameField.getText();

        // Xử lý định dạng mức lương
        String salaryText = salaryField.getText();
        String cleanSalary = salaryText.replaceAll("[^0-9]", "");
        BigDecimal salary = new BigDecimal(cleanSalary);

        // Tạo đối tượng PositionDTO để cập nhật
        PositionDTO position = new PositionDTO(positionId, positionName, salary);

        // Cập nhật chức vụ
        boolean success = positionController.updatePosition(position);

        if (success) {
            JOptionPane.showMessageDialog(this, "Cập nhật chức vụ thành công", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            positionFormDialog.dispose();
            refreshPositionData();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật chức vụ thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePosition() {
        int selectedRow = positionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chức vụ cần xóa", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String positionId = positionTable.getValueAt(selectedRow, 0).toString();
        String positionName = positionTable.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa chức vụ " + positionName + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = positionController.deletePosition(positionId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa chức vụ thành công", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshPositionData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa chức vụ thất bại. Chức vụ này có thể đang được sử dụng.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInput() {
        // Kiểm tra tên chức vụ
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên chức vụ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        // Kiểm tra độ dài tên chức vụ
        if (nameField.getText().trim().length() > 50) {
            JOptionPane.showMessageDialog(this, "Tên chức vụ không được vượt quá 50 ký tự", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        // Kiểm tra mức lương
        String salary = salaryField.getText().trim();
        if (salary.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mức lương", "Lỗi", JOptionPane.ERROR_MESSAGE);
            salaryField.requestFocus();
            return false;
        }

        // Kiểm tra định dạng mức lương
        try {
            // Xóa bỏ tất cả ký tự không phải là số
            String cleanSalary = salary.replaceAll("[^0-9]", "");
            if (cleanSalary.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mức lương phải là số", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                salaryField.requestFocus();
                return false;
            }

            BigDecimal salaryValue = new BigDecimal(cleanSalary);

            // Kiểm tra giới hạn tối đa của mức lương - decimal(10,2)
            // Số lớn nhất có thể lưu trữ là 99,999,999.99
            if (salaryValue.compareTo(new BigDecimal("9999999999")) > 0) {
                JOptionPane.showMessageDialog(this, "Mức lương quá lớn. Giới hạn tối đa là 99,999,999.99 VNĐ", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                salaryField.requestFocus();
                return false;
            }

            // Kiểm tra giá trị âm
            if (salaryValue.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Mức lương không được là số âm", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                salaryField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mức lương không hợp lệ, vui lòng nhập số", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            salaryField.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Xuất dữ liệu chức vụ ra file Excel
     */
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
        ExcelUtils.exportToExcel(headers, data, "Chức vụ", "DANH SÁCH CHỨC VỤ");
    }

    /**
     * Nhập dữ liệu chức vụ từ file Excel
     */
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
                    "Bạn có chắc chắn muốn nhập " + data.size() + " chức vụ từ file Excel?",
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
                    if (rowData.length < 2 || rowData[0] == null || rowData[1] == null) {
                        errorMessages.append("Dòng ").append(rowIndex + 1)
                                .append(": Thiếu thông tin bắt buộc (Mã chức vụ, Tên chức vụ)\n");
                        failCount++;
                        continue;
                    }

                    // Tạo đối tượng PositionDTO từ dữ liệu nhập vào
                    PositionDTO position = new PositionDTO();

                    // Mã chức vụ
                    String positionId = rowData[0].toString().trim();
                    if (positionId.isEmpty()) {
                        errorMessages.append("Dòng ").append(rowIndex + 1)
                                .append(": Mã chức vụ không được để trống\n");
                        failCount++;
                        continue;
                    }
                    position.setPositionId(positionId);

                    // Tên chức vụ
                    String positionName = rowData[1].toString().trim();
                    if (positionName.isEmpty()) {
                        errorMessages.append("Dòng ").append(rowIndex + 1)
                                .append(": Tên chức vụ không được để trống\n");
                        failCount++;
                        continue;
                    }
                    position.setPositionName(positionName);

                    // Mô tả (nếu có)
                    if (rowData.length > 2 && rowData[2] != null) {
                        // Remove the setDescription call since it doesn't exist in PositionDTO
                        // position.setDescription(rowData[2].toString().trim());
                    }

                    // Thêm chức vụ vào cơ sở dữ liệu
                    if (positionController.addPosition(position)) {
                        successCount++;
                    } else {
                        errorMessages.append("Dòng ").append(rowIndex + 1)
                                .append(": Không thể thêm chức vụ vào cơ sở dữ liệu\n");
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
                    "Số chức vụ nhập thành công: " + successCount + "\n" +
                    "Số chức vụ nhập thất bại: " + failCount;

            if (failCount > 0 && errorMessages.length() > 0) {
                message += "\n\nChi tiết lỗi:\n" + errorMessages.toString();
            }

            JOptionPane.showMessageDialog(this,
                    message,
                    "Kết quả nhập dữ liệu",
                    failCount > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);

            // Cập nhật lại dữ liệu hiển thị
            refreshPositionData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi nhập dữ liệu từ Excel: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}