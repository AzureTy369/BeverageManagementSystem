package GUI;

import BUS.EmployeeBUS;
import BUS.PositionBUS;
import DTO.EmployeeDTO;
import DTO.PositionDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import GUI.utils.ExcelUtils;

public class EmployeeGUI extends JPanel {
    private EmployeeBUS employeeController;
    private PositionBUS positionController;

    private JTable employeeTable;
    private DefaultTableModel tableModel;

    private JTextField idField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField lastNameField;
    private JTextField firstNameField;
    private JComboBox<PositionDTO> positionComboBox;
    private JTextField phoneField;

    private JTextField employeeSearchField;
    private JPanel advancedSearchPanel;

    private final Color primaryColor = new Color(0, 123, 255);
    private final Color successColor = new Color(40, 167, 69);
    private final Color warningColor = new Color(255, 193, 7);
    private final Color dangerColor = new Color(220, 53, 69);
    private final Color lightColor = new Color(248, 249, 250);

    // Dialog cho form thêm/sửa nhân viên
    private JDialog employeeFormDialog;

    public EmployeeGUI(EmployeeBUS employeeController, PositionBUS positionController) {
        this.employeeController = employeeController;
        this.positionController = positionController;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(lightColor);

        createAdvancedSearchPanel();
        createGUI();
        createEmployeeFormDialog();
        refreshEmployeeData();
    }

    private void createGUI() {
        setLayout(new BorderLayout());

        // Panel tiêu đề ở trên cùng
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(lightColor);

        JLabel titleLabel = new JLabel("Quản Lý Nhân Viên");
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
        addBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        addBtn.addActionListener(e -> showAddEmployeeDialog());

        // Nút xóa - giảm kích thước
        JButton deleteBtn = createOutlineButton("Xóa", dangerColor);
        deleteBtn.setMargin(new Insets(3, 8, 3, 8));
        deleteBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        deleteBtn.addActionListener(e -> deleteEmployee());

        // Nút chỉnh sửa - giảm kích thước
        JButton editBtn = createOutlineButton("Chỉnh sửa", warningColor);
        editBtn.setMargin(new Insets(3, 8, 3, 8));
        editBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        editBtn.addActionListener(e -> showEditEmployeeDialog());

        // Separator
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(1, 20));
        separator.setForeground(new Color(200, 200, 200));

        // Nút xuất Excel - giảm kích thước
        JButton exportExcelButton = createOutlineButton("Xuất Excel", primaryColor);
        exportExcelButton.setMargin(new Insets(3, 8, 3, 8));
        exportExcelButton.setFont(new Font("Arial", Font.PLAIN, 12));
        exportExcelButton.addActionListener(e -> exportToExcel());

        // Thêm nút nhập Excel vào panel chứa các nút
        JButton importExcelButton = createOutlineButton("Nhập Excel", successColor);
        importExcelButton.setMargin(new Insets(3, 8, 3, 8));
        importExcelButton.setFont(new Font("Arial", Font.PLAIN, 12));
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
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // TextField tìm kiếm - giảm kích thước
        employeeSearchField = new JTextField(15);
        employeeSearchField.setPreferredSize(new Dimension(180, 25));
        employeeSearchField.setFont(new Font("Arial", Font.PLAIN, 12));
        employeeSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchEmployees();
                }
            }
        });

        // Nút tìm kiếm - giảm kích thước
        JButton searchButton = createOutlineButton("Tìm kiếm", primaryColor);
        searchButton.setMargin(new Insets(3, 8, 3, 8));
        searchButton.setFont(new Font("Arial", Font.PLAIN, 12));
        searchButton.addActionListener(e -> searchEmployees());

        // Nút làm mới - giảm kích thước
        JButton refreshButton = createOutlineButton("Làm mới", new Color(23, 162, 184));
        refreshButton.setMargin(new Insets(3, 8, 3, 8));
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshButton.addActionListener(e -> refreshEmployeeData());

        // Thêm các thành phần vào panel tìm kiếm
        rightSearchPanel.add(searchLabel);
        rightSearchPanel.add(employeeSearchField);
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

        // Tạo bảng nhân viên
        createEmployeeTable();
        JScrollPane tableScrollPane = new JScrollPane(employeeTable);
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

        // Họ
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lastNameLabel = new JLabel("Họ:");
        lastNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(lastNameLabel, gbc);

        gbc.gridx = 1;
        JTextField lastNameSearchField = new JTextField(15);
        lastNameSearchField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(lastNameSearchField, gbc);

        // Tên
        gbc.gridx = 2;
        JLabel firstNameLabel = new JLabel("Tên:");
        firstNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(firstNameLabel, gbc);

        gbc.gridx = 3;
        JTextField firstNameSearchField = new JTextField(15);
        firstNameSearchField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(firstNameSearchField, gbc);

        // Chức vụ
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel positionLabel = new JLabel("Chức vụ:");
        positionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(positionLabel, gbc);

        gbc.gridx = 1;
        JComboBox<PositionDTO> positionSearchField = new JComboBox<>();
        positionSearchField.setFont(new Font("Arial", Font.PLAIN, 14));
        // Thêm một mục rỗng đầu tiên
        positionSearchField.addItem(null);
        // Nạp dữ liệu vào combo box
        List<PositionDTO> positions = positionController.getAllPositions();
        for (PositionDTO position : positions) {
            positionSearchField.addItem(position);
        }
        conditionsPanel.add(positionSearchField, gbc);

        // Số điện thoại
        gbc.gridx = 2;
        JLabel phoneLabel = new JLabel("Số điện thoại:");
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(phoneLabel, gbc);

        gbc.gridx = 3;
        JTextField phoneSearchField = new JTextField(15);
        phoneSearchField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(phoneSearchField, gbc);

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
            String lastName = lastNameSearchField.getText().trim();
            String firstName = firstNameSearchField.getText().trim();
            PositionDTO position = (PositionDTO) positionSearchField.getSelectedItem();
            String phone = phoneSearchField.getText().trim();
            boolean isAnd = andRadio.isSelected();

            // Gọi phương thức tìm kiếm nâng cao (cần bổ sung vào BUS)
            advancedSearchEmployees(lastName, firstName, position, phone, isAnd);
        });

        JButton clearButton = createOutlineButton("Xóa tìm kiếm", new Color(108, 117, 125));
        clearButton.setForeground(Color.BLACK);
        clearButton.addActionListener(e -> {
            lastNameSearchField.setText("");
            firstNameSearchField.setText("");
            positionSearchField.setSelectedIndex(0);
            phoneSearchField.setText("");
            refreshEmployeeData();
        });

        controlPanel.add(andRadio);
        controlPanel.add(orRadio);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(searchButton);
        controlPanel.add(clearButton);

        advancedSearchPanel.add(controlPanel);
    }

    private void createEmployeeTable() {
        String[] columnNames = { "Mã NV", "Tên đăng nhập", "Họ", "Tên", "Chức vụ", "Số điện thoại" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setFont(new Font("Arial", Font.PLAIN, 14));
        employeeTable.setRowHeight(25);
        employeeTable.setGridColor(new Color(230, 230, 230));
        employeeTable.setSelectionBackground(new Color(173, 216, 230));
        employeeTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        employeeTable.getTableHeader().setBackground(primaryColor);
        employeeTable.getTableHeader().setForeground(Color.BLACK);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column width
        employeeTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        employeeTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        employeeTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        employeeTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        employeeTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        employeeTable.getColumnModel().getColumn(5).setPreferredWidth(120);
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(222, 226, 230)),
                BorderFactory.createEmptyBorder(10, 0, 0, 0)));
        statusPanel.setOpaque(false);

        JLabel statusLabel = new JLabel("Tổng số nhân viên: 0");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        statusPanel.add(statusLabel, BorderLayout.WEST);

        // Cập nhật số lượng nhân viên khi dữ liệu thay đổi
        statusLabel.setText("Tổng số nhân viên: " + employeeController.getAllEmployees().size());

        return statusPanel;
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

    private void createEmployeeFormDialog() {
        // Tạo dialog
        employeeFormDialog = new JDialog();
        employeeFormDialog.setTitle("Thông tin nhân viên");
        employeeFormDialog.setSize(600, 550);
        employeeFormDialog.setLocationRelativeTo(this);
        employeeFormDialog.setModal(true);
        employeeFormDialog.setResizable(false);

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

        // Mã nhân viên
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel idLabel = new JLabel("Mã nhân viên:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        idField = new JTextField(20);
        idField.setFont(new Font("Arial", Font.PLAIN, 14));
        idField.setEditable(false);
        formPanel.add(idField, gbc);

        // Tên đăng nhập
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(usernameField, gbc);

        // Mật khẩu
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordField, gbc);

        // Họ
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lastNameLabel = new JLabel("Họ:");
        lastNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(lastNameLabel, gbc);

        gbc.gridx = 1;
        lastNameField = new JTextField(20);
        lastNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(lastNameField, gbc);

        // Tên
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel firstNameLabel = new JLabel("Tên:");
        firstNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(firstNameLabel, gbc);

        gbc.gridx = 1;
        firstNameField = new JTextField(20);
        firstNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(firstNameField, gbc);

        // Chức vụ
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel positionLabel = new JLabel("Chức vụ:");
        positionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(positionLabel, gbc);

        gbc.gridx = 1;
        positionComboBox = new JComboBox<>();
        positionComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        // Nạp dữ liệu vào combo box
        List<PositionDTO> positions = positionController.getAllPositions();
        for (PositionDTO position : positions) {
            positionComboBox.addItem(position);
        }
        formPanel.add(positionComboBox, gbc);

        // Số điện thoại
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel phoneLabel = new JLabel("Số điện thoại:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        phoneField = new JTextField(20);
        phoneField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(phoneField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton saveButton = new JButton("Lưu");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setBackground(successColor);
        saveButton.setForeground(Color.BLACK);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> {
            // Sử dụng tag đặc biệt thay vì kiểm tra idField.isEditable()
            if (employeeFormDialog.getTitle().contains("Thêm nhân viên")) {
                System.out.println("Gọi phương thức addEmployee() từ dialog");
                addEmployee();
            } else {
                System.out.println("Gọi phương thức updateEmployee() từ dialog");
                updateEmployee();
            }
        });

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBackground(dangerColor);
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> employeeFormDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        dialogPanel.add(formPanel, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        employeeFormDialog.add(dialogPanel);
    }

    private void showAddEmployeeDialog() {
        // Xóa form
        clearForm();

        // Tạo ID mới
        idField.setText(employeeController.generateNewEmployeeId());
        idField.setEditable(false);

        // Đặt một tag để nhận biết đây là thêm mới
        System.out.println("Hiển thị dialog thêm nhân viên mới với ID: " + idField.getText());

        // Hiển thị dialog
        employeeFormDialog.setTitle("Thêm nhân viên mới");
        employeeFormDialog.setVisible(true);
    }

    private void showEditEmployeeDialog() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn nhân viên cần sửa",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String employeeId = employeeTable.getValueAt(selectedRow, 0).toString();
        EmployeeDTO employee = employeeController.getEmployeeById(employeeId);

        if (employee != null) {
            // Hiển thị thông tin nhân viên
            idField.setText(employee.getEmployeeId());
            idField.setEditable(false);
            usernameField.setText(employee.getUsername());
            passwordField.setText(employee.getPassword());
            lastNameField.setText(employee.getLastName());
            firstNameField.setText(employee.getFirstName());

            // Chọn vị trí trong combobox
            String positionId = employee.getPositionId();
            for (int i = 0; i < positionComboBox.getItemCount(); i++) {
                PositionDTO position = positionComboBox.getItemAt(i);
                if (position.getPositionId().equals(positionId)) {
                    positionComboBox.setSelectedIndex(i);
                    break;
                }
            }

            phoneField.setText(employee.getPhone());

            // Hiển thị dialog
            employeeFormDialog.setTitle("Cập nhật thông tin nhân viên");
            employeeFormDialog.setVisible(true);
        }
    }

    private void searchEmployees() {
        String keyword = employeeSearchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshEmployeeData();
            return;
        }

        List<EmployeeDTO> results = employeeController.searchEmployees(keyword);
        displayEmployees(results);
    }

    private void advancedSearchEmployees(String lastName, String firstName, PositionDTO position, String phone,
            boolean isAnd) {
        // Lấy ID chức vụ nếu có
        String positionId = position != null ? position.getPositionId() : null;

        // Tìm kiếm nâng cao - Phương thức này cần bổ sung vào EmployeeBUS
        List<EmployeeDTO> allEmployees = employeeController.getAllEmployees();
        List<EmployeeDTO> results = new ArrayList<>();

        // Nếu tất cả điều kiện trống, trả về danh sách đầy đủ
        if ((lastName.isEmpty()) && (firstName.isEmpty()) &&
                (positionId == null) && (phone.isEmpty())) {
            displayEmployees(allEmployees);
            return;
        }

        // Tìm kiếm theo các điều kiện
        for (EmployeeDTO employee : allEmployees) {
            boolean lastNameMatch = lastName.isEmpty() ||
                    (employee.getLastName() != null &&
                            employee.getLastName().toLowerCase().contains(lastName.toLowerCase()));

            boolean firstNameMatch = firstName.isEmpty() ||
                    (employee.getFirstName() != null &&
                            employee.getFirstName().toLowerCase().contains(firstName.toLowerCase()));

            boolean positionMatch = positionId == null ||
                    (employee.getPositionId() != null &&
                            employee.getPositionId().equals(positionId));

            boolean phoneMatch = phone.isEmpty() ||
                    (employee.getPhone() != null &&
                            employee.getPhone().toLowerCase().contains(phone.toLowerCase()));

            if (isAnd) {
                // Phép AND - tất cả điều kiện phải đúng
                if (lastNameMatch && firstNameMatch && positionMatch && phoneMatch) {
                    results.add(employee);
                }
            } else {
                // Phép OR - chỉ cần một điều kiện đúng
                if (lastNameMatch || firstNameMatch || positionMatch || phoneMatch) {
                    results.add(employee);
                }
            }
        }

        // Hiển thị kết quả
        displayEmployees(results);
    }

    private void displayEmployees(List<EmployeeDTO> employees) {
        // Xóa dữ liệu cũ
        tableModel.setRowCount(0);

        // Thêm dữ liệu mới
        for (EmployeeDTO employee : employees) {
            String positionName = "";
            if (employee.getPositionId() != null) {
                PositionDTO position = positionController.getPositionById(employee.getPositionId());
                if (position != null) {
                    positionName = position.getPositionName();
                }
            }

            Object[] row = {
                    employee.getEmployeeId(),
                    employee.getUsername(),
                    employee.getLastName(),
                    employee.getFirstName(),
                    positionName,
                    employee.getPhone()
            };
            tableModel.addRow(row);
        }
    }

    private void refreshEmployeeData() {
        List<EmployeeDTO> employees = employeeController.getAllEmployees();
        displayEmployees(employees);
    }

    private void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn nhân viên cần xóa",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String employeeId = employeeTable.getValueAt(selectedRow, 0).toString();
        String employeeName = employeeTable.getValueAt(selectedRow, 2).toString() + " " +
                employeeTable.getValueAt(selectedRow, 3).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa nhân viên " + employeeName + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean result = employeeController.deleteEmployee(employeeId);
            if (result) {
                JOptionPane.showMessageDialog(this,
                        "Xóa nhân viên thành công",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshEmployeeData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Xóa nhân viên thất bại",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addEmployee() {
        // Lấy dữ liệu từ form
        String id = idField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String lastName = lastNameField.getText();
        String firstName = firstNameField.getText();
        PositionDTO selectedPosition = (PositionDTO) positionComboBox.getSelectedItem();
        String positionId = selectedPosition.getPositionId();
        String phone = phoneField.getText();

        // Kiểm tra dữ liệu
        if (username.isEmpty() || password.isEmpty() || lastName.isEmpty() || firstName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(employeeFormDialog,
                    "Vui lòng nhập đầy đủ thông tin",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo đối tượng EmployeeDTO
        EmployeeDTO employee = new EmployeeDTO(id, username, password, firstName, lastName, positionId, phone);

        System.out.println("Thông tin nhân viên mới:");
        System.out.println("ID: " + id);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Last Name: " + lastName);
        System.out.println("First Name: " + firstName);
        System.out.println("Position ID: " + positionId);
        System.out.println("Phone: " + phone);

        // Thêm nhân viên
        boolean result = employeeController.addEmployee(employee);
        if (result) {
            JOptionPane.showMessageDialog(employeeFormDialog,
                    "Thêm nhân viên thành công",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            employeeFormDialog.dispose();
            refreshEmployeeData();
        } else {
            JOptionPane.showMessageDialog(employeeFormDialog,
                    "Thêm nhân viên thất bại",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployee() {
        // Lấy dữ liệu từ form
        String id = idField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String lastName = lastNameField.getText();
        String firstName = firstNameField.getText();
        PositionDTO selectedPosition = (PositionDTO) positionComboBox.getSelectedItem();
        String positionId = selectedPosition.getPositionId();
        String phone = phoneField.getText();

        // Kiểm tra dữ liệu
        if (username.isEmpty() || password.isEmpty() || lastName.isEmpty() || firstName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(employeeFormDialog,
                    "Vui lòng nhập đầy đủ thông tin",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo đối tượng EmployeeDTO
        EmployeeDTO employee = new EmployeeDTO(id, username, password, firstName, lastName, positionId, phone);

        // Cập nhật nhân viên
        boolean result = employeeController.updateEmployee(employee);
        if (result) {
            JOptionPane.showMessageDialog(employeeFormDialog,
                    "Cập nhật nhân viên thành công",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            employeeFormDialog.dispose();
            refreshEmployeeData();
        } else {
            JOptionPane.showMessageDialog(employeeFormDialog,
                    "Cập nhật nhân viên thất bại",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        idField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        lastNameField.setText("");
        firstNameField.setText("");
        if (positionComboBox.getItemCount() > 0) {
            positionComboBox.setSelectedIndex(0);
        }
        phoneField.setText("");
    }

    /**
     * Xuất dữ liệu nhân viên ra file Excel
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
        ExcelUtils.exportToExcel(headers, data, "Nhân viên", "DANH SÁCH NHÂN VIÊN");
    }

    /**
     * Nhập dữ liệu nhân viên từ file Excel
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
                    "Bạn có chắc chắn muốn nhập " + data.size() + " nhân viên từ file Excel?",
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
                    if (rowData.length < 4 || rowData[0] == null || rowData[1] == null || 
                        rowData[2] == null || rowData[3] == null) {
                        errorMessages.append("Dòng ").append(rowIndex + 1)
                                .append(": Thiếu thông tin bắt buộc (Mã NV, Tên đăng nhập, Họ, Tên)\n");
                        failCount++;
                        continue;
                    }

                    // Tạo đối tượng EmployeeDTO từ dữ liệu nhập vào
                    EmployeeDTO employee = new EmployeeDTO();

                    // Mã nhân viên
                    String employeeId = rowData[0].toString().trim();
                    if (employeeId.isEmpty()) {
                        errorMessages.append("Dòng ").append(rowIndex + 1)
                                .append(": Mã nhân viên không được để trống\n");
                        failCount++;
                        continue;
                    }
                    employee.setEmployeeId(employeeId);

                    // Tên đăng nhập
                    String username = rowData[1].toString().trim();
                    if (username.isEmpty()) {
                        errorMessages.append("Dòng ").append(rowIndex + 1)
                                .append(": Tên đăng nhập không được để trống\n");
                        failCount++;
                        continue;
                    }
                    employee.setUsername(username);

                    // Mật khẩu (mặc định là "123456")
                    employee.setPassword("123456");

                    // Họ
                    String lastName = rowData[2].toString().trim();
                    if (lastName.isEmpty()) {
                        errorMessages.append("Dòng ").append(rowIndex + 1)
                                .append(": Họ không được để trống\n");
                        failCount++;
                        continue;
                    }
                    employee.setLastName(lastName);

                    // Tên
                    String firstName = rowData[3].toString().trim();
                    if (firstName.isEmpty()) {
                        errorMessages.append("Dòng ").append(rowIndex + 1)
                                .append(": Tên không được để trống\n");
                        failCount++;
                        continue;
                    }
                    employee.setFirstName(firstName);

                    // Chức vụ
                    String positionName = rowData.length > 4 && rowData[4] != null ? 
                            rowData[4].toString().trim() : "";
                    boolean positionFound = false;
                    for (PositionDTO position : positionController.getAllPositions()) {
                        if (position.getPositionName().equals(positionName)) {
                            employee.setPositionId(position.getPositionId());
                            positionFound = true;
                            break;
                        }
                    }
                    if (!positionFound && !positionName.isEmpty()) {
                        errorMessages.append("Dòng ").append(rowIndex + 1).append(": Chức vụ '").append(positionName)
                                .append("' không tồn tại\n");
                        failCount++;
                        continue;
                    } else if (positionName.isEmpty()) {
                        // Nếu không có chức vụ, gán chức vụ mặc định (giả sử có chức vụ với ID "CV001")
                        employee.setPositionId("CV001");
                    }

                    // Số điện thoại
                    if (rowData.length > 5 && rowData[5] != null) {
                        employee.setPhone(rowData[5].toString().trim());
                    }

                    // Thêm nhân viên vào cơ sở dữ liệu
                    if (employeeController.addEmployee(employee)) {
                        successCount++;
                    } else {
                        errorMessages.append("Dòng ").append(rowIndex + 1)
                                .append(": Không thể thêm nhân viên vào cơ sở dữ liệu\n");
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
                    "Số nhân viên nhập thành công: " + successCount + "\n" +
                    "Số nhân viên nhập thất bại: " + failCount;

            if (failCount > 0 && errorMessages.length() > 0) {
                message += "\n\nChi tiết lỗi:\n" + errorMessages.toString();
            }

            JOptionPane.showMessageDialog(this,
                    message,
                    "Kết quả nhập dữ liệu",
                    failCount > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);

            // Cập nhật lại dữ liệu hiển thị
            refreshEmployeeData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi nhập dữ liệu từ Excel: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}