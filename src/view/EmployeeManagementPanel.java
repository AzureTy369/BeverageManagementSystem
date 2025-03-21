package view;

import controller.EmployeeController;
import controller.PositionController;
import model.Employee;
import model.Position;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class EmployeeManagementPanel extends JPanel {
    private EmployeeController employeeController;
    private PositionController positionController;

    private JTable employeeTable;
    private DefaultTableModel tableModel;

    private JTextField idField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField lastNameField;
    private JTextField firstNameField;
    private JComboBox<String> positionComboBox;
    private JTextField phoneField;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    // Colors
    private Color primaryColor = new Color(0, 123, 255);
    private Color successColor = new Color(40, 167, 69);
    private Color warningColor = new Color(255, 193, 7);
    private Color dangerColor = new Color(220, 53, 69);
    private Color lightColor = new Color(248, 249, 250);

    public EmployeeManagementPanel(EmployeeController employeeController, PositionController positionController) {
        this.employeeController = employeeController;
        this.positionController = positionController;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(lightColor);

        // Create title panel
        createTitlePanel();

        // Create components
        createTable();
        createForm();

        // Refresh data
        refreshEmployeeData();
    }

    private void createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(lightColor);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("\uD83D\uDC64 Quản lý nhân viên");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 37, 41));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);

        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.setBackground(primaryColor);
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> refreshEmployeeData());

        actionPanel.add(refreshButton);

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(actionPanel, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);
    }

    private void createTable() {
        // Create table model with columns
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableModel.addColumn("Mã NV");
        tableModel.addColumn("Tên đăng nhập");
        tableModel.addColumn("Họ");
        tableModel.addColumn("Tên");
        tableModel.addColumn("Chức vụ");
        tableModel.addColumn("Số điện thoại");

        // Create table and add to scroll pane
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getTableHeader().setReorderingAllowed(false);
        employeeTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        employeeTable.setRowHeight(30);
        employeeTable.setFont(new Font("Arial", Font.PLAIN, 14));
        employeeTable.setGridColor(new Color(230, 230, 230));
        employeeTable.setShowGrid(true);

        // Add selection listener
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && employeeTable.getSelectedRow() != -1) {
                int selectedRow = employeeTable.getSelectedRow();
                String id = employeeTable.getValueAt(selectedRow, 0).toString();
                Employee employee = employeeController.getEmployeeById(id);
                if (employee != null) {
                    displayEmployeeData(employee);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createForm() {
        // Form panel
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(222, 226, 230)),
                        "Thông tin nhân viên"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Form fields panel
        JPanel fieldsPanel = new JPanel(new GridLayout(7, 2, 15, 15));
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Initialize form fields
        idField = createTextField();
        usernameField = createTextField();
        passwordField = createPasswordField();
        lastNameField = createTextField();
        firstNameField = createTextField();
        positionComboBox = new JComboBox<>();
        positionComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        phoneField = createTextField();

        // Load positions into combo box
        refreshPositionComboBox();

        // Add fields to panel
        fieldsPanel.add(createLabelWithIcon("Mã nhân viên:", "\uD83C\uDD94"));
        fieldsPanel.add(idField);
        fieldsPanel.add(createLabelWithIcon("Tên đăng nhập:", "\uD83D\uDC64"));
        fieldsPanel.add(usernameField);
        fieldsPanel.add(createLabelWithIcon("Mật khẩu:", "\uD83D\uDD12"));
        fieldsPanel.add(passwordField);
        fieldsPanel.add(createLabelWithIcon("Họ:", "\uD83D\uDC64"));
        fieldsPanel.add(lastNameField);
        fieldsPanel.add(createLabelWithIcon("Tên:", "\uD83D\uDC64"));
        fieldsPanel.add(firstNameField);
        fieldsPanel.add(createLabelWithIcon("Chức vụ:", "\uD83D\uDCBC"));
        fieldsPanel.add(positionComboBox);
        fieldsPanel.add(createLabelWithIcon("Số điện thoại:", "\uD83D\uDCDE"));
        fieldsPanel.add(phoneField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        addButton = createButton("Thêm", successColor, "\u002B"); // Plus sign
        updateButton = createButton("Cập nhật", warningColor, "\uD83D\uDD04"); // Reload symbol
        deleteButton = createButton("Xóa", dangerColor, "\u2716"); // X mark
        clearButton = createButton("Làm mới", primaryColor, "\uD83D\uDD01"); // Refresh icon

        // Add button actions
        addButton.addActionListener(e -> addEmployee());
        updateButton.addActionListener(e -> updateEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        // Initially disable update and delete buttons
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);

        // Add components to form panel
        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add form panel to main panel
        add(formPanel, BorderLayout.SOUTH);
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return textField;
    }

    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return passwordField;
    }

    private JLabel createLabelWithIcon(String text, String icon) {
        JLabel label = new JLabel(icon + " " + text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }

    private JButton createButton(String text, Color backgroundColor, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(darker(backgroundColor, 0.9f));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(backgroundColor);
                }
            }
        });

        return button;
    }

    // Helper method to create darker color for hover effects
    private Color darker(Color color, float factor) {
        return new Color(
                Math.max((int) (color.getRed() * factor), 0),
                Math.max((int) (color.getGreen() * factor), 0),
                Math.max((int) (color.getBlue() * factor), 0),
                color.getAlpha());
    }

    private void refreshEmployeeData() {
        // Clear table
        tableModel.setRowCount(0);

        // Get all employees
        List<Employee> employees = employeeController.getAllEmployees();

        // Add employees to table
        for (Employee employee : employees) {
            Vector<Object> row = new Vector<>();
            row.add(employee.getEmployeeId());
            row.add(employee.getUsername());
            row.add(employee.getLastName());
            row.add(employee.getFirstName());

            // Get position name instead of ID
            String positionName = "Không xác định";
            Position position = positionController.getPositionById(employee.getPositionId());
            if (position != null) {
                positionName = position.getPositionName();
            }
            row.add(positionName);

            row.add(employee.getPhone());
            tableModel.addRow(row);
        }
    }

    private void refreshPositionComboBox() {
        positionComboBox.removeAllItems();

        List<Position> positions = positionController.getAllPositions();
        for (Position position : positions) {
            positionComboBox.addItem(position.getPositionId() + " - " + position.getPositionName());
        }
    }

    private void displayEmployeeData(Employee employee) {
        idField.setText(employee.getEmployeeId());
        usernameField.setText(employee.getUsername());
        passwordField.setText(employee.getPassword());
        lastNameField.setText(employee.getLastName());
        firstNameField.setText(employee.getFirstName());
        phoneField.setText(employee.getPhone());

        // Set position combo box
        String positionId = employee.getPositionId();
        for (int i = 0; i < positionComboBox.getItemCount(); i++) {
            String item = positionComboBox.getItemAt(i);
            if (item.startsWith(positionId)) {
                positionComboBox.setSelectedIndex(i);
                break;
            }
        }

        // Enable/disable buttons
        updateButton.setEnabled(true);
        deleteButton.setEnabled(true);
    }

    private void clearForm() {
        idField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        lastNameField.setText("");
        firstNameField.setText("");
        phoneField.setText("");
        positionComboBox.setSelectedIndex(0);

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);

        employeeTable.clearSelection();
    }

    private void addEmployee() {
        // Validate input
        if (!validateInput()) {
            return;
        }

        // Create employee object
        Employee employee = new Employee();
        employee.setEmployeeId(idField.getText().trim());
        employee.setUsername(usernameField.getText().trim());
        employee.setPassword(new String(passwordField.getPassword()));
        employee.setLastName(lastNameField.getText().trim());
        employee.setFirstName(firstNameField.getText().trim());
        employee.setPhone(phoneField.getText().trim());

        // Get position ID from combo box
        String selectedPosition = (String) positionComboBox.getSelectedItem();
        String positionId = selectedPosition.split(" - ")[0];
        employee.setPositionId(positionId);

        // Add employee
        boolean success = employeeController.addEmployee(employee);
        if (success) {
            JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
            refreshEmployeeData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm nhân viên thất bại. Tên đăng nhập có thể đã tồn tại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployee() {
        // Check if an employee is selected
        if (employeeTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để cập nhật", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate input
        if (!validateInput()) {
            return;
        }

        // Create employee object
        Employee employee = new Employee();
        employee.setEmployeeId(idField.getText().trim());
        employee.setUsername(usernameField.getText().trim());
        employee.setPassword(new String(passwordField.getPassword()));
        employee.setLastName(lastNameField.getText().trim());
        employee.setFirstName(firstNameField.getText().trim());
        employee.setPhone(phoneField.getText().trim());

        // Get position ID from combo box
        String selectedPosition = (String) positionComboBox.getSelectedItem();
        String positionId = selectedPosition.split(" - ")[0];
        employee.setPositionId(positionId);

        // Update employee
        boolean success = employeeController.updateEmployee(employee);
        if (success) {
            JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
            refreshEmployeeData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thất bại. Tên đăng nhập có thể đã tồn tại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee() {
        // Check if an employee is selected
        if (employeeTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để xóa", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa nhân viên này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String employeeId = idField.getText().trim();
            boolean success = employeeController.deleteEmployee(employeeId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
                refreshEmployeeData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Xóa nhân viên thất bại. Nhân viên có thể đang được tham chiếu bởi dữ liệu khác.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInput() {
        // Validate ID
        if (idField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã nhân viên không được để trống", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            idField.requestFocus();
            return false;
        }

        // Validate username
        if (usernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập không được để trống", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            usernameField.requestFocus();
            return false;
        }

        // Validate password
        if (passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.requestFocus();
            return false;
        }

        // Validate last name
        if (lastNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ không được để trống", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            lastNameField.requestFocus();
            return false;
        }

        // Validate first name
        if (firstNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên không được để trống", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            firstNameField.requestFocus();
            return false;
        }

        // Validate phone
        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không được để trống", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            phoneField.requestFocus();
            return false;
        }

        return true;
    }
}