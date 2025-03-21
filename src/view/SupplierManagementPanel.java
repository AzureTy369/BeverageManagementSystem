package view;

import controller.SupplierController;
import model.Supplier;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SupplierManagementPanel extends JPanel {
    private SupplierController supplierController;

    private JTable supplierTable;
    private DefaultTableModel tableModel;

    private JTextField idField;
    private JTextField nameField;
    private JTextField addressField;
    private JTextField phoneField;
    private JTextField emailField;

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

    public SupplierManagementPanel(SupplierController supplierController) {
        this.supplierController = supplierController;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(lightColor);

        // Create title panel
        createTitlePanel();

        // Create components
        createTable();
        createForm();

        // Refresh data
        refreshSupplierData();
    }

    private void createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(lightColor);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("\uD83C\uDFEC Quản lý nhà cung cấp");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 37, 41));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);

        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.setBackground(primaryColor);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> refreshSupplierData());

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

        tableModel.addColumn("Mã NCC");
        tableModel.addColumn("Tên nhà cung cấp");
        tableModel.addColumn("Địa chỉ");
        tableModel.addColumn("Điện thoại");
        tableModel.addColumn("Email");

        // Create table and add to scroll pane
        supplierTable = new JTable(tableModel);
        supplierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        supplierTable.getTableHeader().setReorderingAllowed(false);
        supplierTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        supplierTable.setRowHeight(30);
        supplierTable.setFont(new Font("Arial", Font.PLAIN, 14));
        supplierTable.setGridColor(new Color(230, 230, 230));
        supplierTable.setShowGrid(true);

        // Add selection listener
        supplierTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && supplierTable.getSelectedRow() != -1) {
                int selectedRow = supplierTable.getSelectedRow();
                String id = supplierTable.getValueAt(selectedRow, 0).toString();
                Supplier supplier = supplierController.getSupplierById(id);
                if (supplier != null) {
                    displaySupplierData(supplier);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(supplierTable);
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
                        "Thông tin nhà cung cấp"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Form fields panel
        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 15, 15));
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Initialize form fields
        idField = createTextField();
        nameField = createTextField();
        addressField = createTextField();
        phoneField = createTextField();
        emailField = createTextField();

        // Add fields to panel
        fieldsPanel.add(createLabelWithIcon("Mã nhà cung cấp:", "\uD83C\uDD94"));
        fieldsPanel.add(idField);
        fieldsPanel.add(createLabelWithIcon("Tên nhà cung cấp:", "\uD83C\uDFEC"));
        fieldsPanel.add(nameField);
        fieldsPanel.add(createLabelWithIcon("Địa chỉ:", "\uD83D\uDCCD"));
        fieldsPanel.add(addressField);
        fieldsPanel.add(createLabelWithIcon("Điện thoại:", "\uD83D\uDCDE"));
        fieldsPanel.add(phoneField);
        fieldsPanel.add(createLabelWithIcon("Email:", "\uD83D\uDCE7"));
        fieldsPanel.add(emailField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        addButton = createButton("Thêm", successColor, "\u002B"); // Plus sign
        updateButton = createButton("Cập nhật", warningColor, "\uD83D\uDD04"); // Reload symbol
        deleteButton = createButton("Xóa", dangerColor, "\u2716"); // X mark
        clearButton = createButton("Làm mới", primaryColor, "\uD83D\uDD01"); // Refresh icon

        // Add button actions
        addButton.addActionListener(e -> addSupplier());
        updateButton.addActionListener(e -> updateSupplier());
        deleteButton.addActionListener(e -> deleteSupplier());
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

    private void refreshSupplierData() {
        // Clear table
        tableModel.setRowCount(0);

        // Get all suppliers
        List<Supplier> suppliers = supplierController.getAllSuppliers();

        // Add suppliers to table
        for (Supplier supplier : suppliers) {
            Object[] row = new Object[5];
            row[0] = supplier.getSupplierId();
            row[1] = supplier.getSupplierName();
            row[2] = supplier.getAddress();
            row[3] = supplier.getPhone();
            row[4] = supplier.getEmail();
            tableModel.addRow(row);
        }
    }

    private void displaySupplierData(Supplier supplier) {
        idField.setText(supplier.getSupplierId());
        nameField.setText(supplier.getSupplierName());
        addressField.setText(supplier.getAddress());
        phoneField.setText(supplier.getPhone());
        emailField.setText(supplier.getEmail());

        // Enable buttons
        updateButton.setEnabled(true);
        deleteButton.setEnabled(true);
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        addressField.setText("");
        phoneField.setText("");
        emailField.setText("");

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);

        supplierTable.clearSelection();
    }

    private void addSupplier() {
        // Validate input
        if (!validateInput()) {
            return;
        }

        // Create supplier object
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        // Add supplier
        boolean success = supplierController.addSupplier(id, name, address, phone, email);
        if (success) {
            JOptionPane.showMessageDialog(this, "Thêm nhà cung cấp thành công!");
            refreshSupplierData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm nhà cung cấp thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSupplier() {
        // Check if a supplier is selected
        if (supplierTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp để cập nhật", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate input
        if (!validateInput()) {
            return;
        }

        // Create supplier object
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        // Update supplier
        boolean success = supplierController.updateSupplier(id, name, address, phone, email);
        if (success) {
            JOptionPane.showMessageDialog(this, "Cập nhật nhà cung cấp thành công!");
            refreshSupplierData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật nhà cung cấp thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSupplier() {
        // Check if a supplier is selected
        if (supplierTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp để xóa", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa nhà cung cấp này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String supplierId = idField.getText().trim();
            boolean success = supplierController.deleteSupplier(supplierId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa nhà cung cấp thành công!");
                refreshSupplierData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa nhà cung cấp thất bại! Nhà cung cấp đang được sử dụng.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInput() {
        // Validate ID
        if (idField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã nhà cung cấp không được để trống", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            idField.requestFocus();
            return false;
        }

        // Validate name
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên nhà cung cấp không được để trống", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        // Validate phone
        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && !phone.matches("\\d{10,11}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải có 10-11 chữ số", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            phoneField.requestFocus();
            return false;
        }

        // Validate email
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return false;
        }

        return true;
    }
}