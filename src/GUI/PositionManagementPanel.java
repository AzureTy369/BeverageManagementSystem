package GUI;

import BUS.PositionController;
import DTO.Position;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class PositionManagementPanel extends JPanel {
    private PositionController positionController;

    private JTable positionTable;
    private DefaultTableModel tableModel;

    private JTextField idField;
    private JTextField nameField;
    private JTextField salaryField;

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

    public PositionManagementPanel(PositionController positionController) {
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
        refreshPositionData();
    }

    private void createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(lightColor);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("\uD83D\uDCBC Quản lý chức vụ");
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
        refreshButton.addActionListener(e -> refreshPositionData());

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

        tableModel.addColumn("Mã chức vụ");
        tableModel.addColumn("Tên chức vụ");
        tableModel.addColumn("Mức lương");

        // Create table and add to scroll pane
        positionTable = new JTable(tableModel);
        positionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        positionTable.getTableHeader().setReorderingAllowed(false);
        positionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        positionTable.setRowHeight(30);
        positionTable.setFont(new Font("Arial", Font.PLAIN, 14));
        positionTable.setGridColor(new Color(230, 230, 230));
        positionTable.setShowGrid(true);

        // Add selection listener
        positionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && positionTable.getSelectedRow() != -1) {
                int selectedRow = positionTable.getSelectedRow();
                String id = positionTable.getValueAt(selectedRow, 0).toString();
                Position position = positionController.getPositionById(id);
                if (position != null) {
                    displayPositionData(position);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(positionTable);
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
                        "Thông tin chức vụ"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Form fields panel
        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Initialize form fields
        idField = createTextField();
        nameField = createTextField();
        salaryField = createTextField();

        // Add fields to panel
        fieldsPanel.add(createLabelWithIcon("Mã chức vụ:", "\uD83C\uDD94"));
        fieldsPanel.add(idField);
        fieldsPanel.add(createLabelWithIcon("Tên chức vụ:", "\uD83D\uDCBC"));
        fieldsPanel.add(nameField);
        fieldsPanel.add(createLabelWithIcon("Mức lương:", "\uD83D\uDCB0"));
        fieldsPanel.add(salaryField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        addButton = createButton("Thêm", successColor, "\u002B"); // Plus sign
        updateButton = createButton("Cập nhật", warningColor, "\uD83D\uDD04"); // Reload symbol
        deleteButton = createButton("Xóa", dangerColor, "\u2716"); // X mark
        clearButton = createButton("Làm mới", primaryColor, "\uD83D\uDD01"); // Refresh icon

        // Add button actions
        addButton.addActionListener(e -> addPosition());
        updateButton.addActionListener(e -> updatePosition());
        deleteButton.addActionListener(e -> deletePosition());
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

    private void refreshPositionData() {
        // Clear table
        tableModel.setRowCount(0);

        // Get all positions
        List<Position> positions = positionController.getAllPositions();

        // Add positions to table
        for (Position position : positions) {
            Vector<Object> row = new Vector<>();
            row.add(position.getPositionId());
            row.add(position.getPositionName());
            row.add(String.format("%,.0f VNĐ", position.getSalary()));
            tableModel.addRow(row);
        }
    }

    private void displayPositionData(Position position) {
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
        // Validate input
        if (!validateInput()) {
            return;
        }

        try {
            // Create position object
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            double salary = Double.parseDouble(salaryField.getText().trim().replace(",", ""));

            // Add position
            boolean success = positionController.addPosition(id, name, salary);
            if (success) {
                JOptionPane.showMessageDialog(this, "Thêm chức vụ thành công!");
                refreshPositionData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm chức vụ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mức lương phải là số hợp lệ", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            salaryField.requestFocus();
        }
    }

    private void updatePosition() {
        // Check if a position is selected
        if (positionTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chức vụ để cập nhật", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate input
        if (!validateInput()) {
            return;
        }

        try {
            // Create position object
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            double salary = Double.parseDouble(salaryField.getText().trim().replace(",", ""));

            // Update position
            boolean success = positionController.updatePosition(id, name, salary);
            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật chức vụ thành công!");
                refreshPositionData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật chức vụ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mức lương phải là số hợp lệ", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            salaryField.requestFocus();
        }
    }

    private void deletePosition() {
        // Check if a position is selected
        if (positionTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chức vụ để xóa", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa chức vụ này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String positionId = idField.getText().trim();
            boolean success = positionController.deletePosition(positionId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa chức vụ thành công!");
                refreshPositionData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa chức vụ thất bại! Chức vụ đang được sử dụng bởi nhân viên.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInput() {
        // Validate ID
        if (idField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã chức vụ không được để trống", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            idField.requestFocus();
            return false;
        }

        // Validate name
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên chức vụ không được để trống", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        // Validate salary
        if (salaryField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mức lương không được để trống", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            salaryField.requestFocus();
            return false;
        }

        try {
            double salary = Double.parseDouble(salaryField.getText().trim().replace(",", ""));
            if (salary < 0) {
                JOptionPane.showMessageDialog(this, "Mức lương phải là số dương", "Lỗi dữ liệu",
                        JOptionPane.ERROR_MESSAGE);
                salaryField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mức lương phải là số hợp lệ", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            salaryField.requestFocus();
            return false;
        }

        return true;
    }
}