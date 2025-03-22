package view;

import controller.ProductCategoryController;
import model.ProductCategory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductCategoryManagementPanel extends JPanel {
    private ProductCategoryController categoryController;

    private JTable categoryTable;
    private DefaultTableModel tableModel;

    private JTextField idField;
    private JTextField nameField;
    private JTextField descriptionField;

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
    private Color textColor = Color.BLACK;

    public ProductCategoryManagementPanel(ProductCategoryController categoryController) {
        this.categoryController = categoryController;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(lightColor);

        // Create title panel
        createTitlePanel();

        // Create components
        createTable();
        createForm();

        // Refresh data
        refreshCategoryData();
    }

    private void createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(lightColor);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("\uD83D\uDCCA Quản lý loại sản phẩm");
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
        refreshButton.addActionListener(e -> refreshCategoryData());

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

        tableModel.addColumn("Mã loại");
        tableModel.addColumn("Tên loại sản phẩm");
        tableModel.addColumn("Mô tả");

        // Create table and add to scroll pane
        categoryTable = new JTable(tableModel);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryTable.getTableHeader().setReorderingAllowed(false);
        categoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        categoryTable.setRowHeight(30);
        categoryTable.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryTable.setGridColor(new Color(230, 230, 230));
        categoryTable.setShowGrid(true);

        // Add selection listener
        categoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && categoryTable.getSelectedRow() != -1) {
                int selectedRow = categoryTable.getSelectedRow();
                String id = categoryTable.getValueAt(selectedRow, 0).toString();
                ProductCategory category = categoryController.getCategoryById(id);
                if (category != null) {
                    displayCategoryData(category);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(categoryTable);
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
                        "Thông tin loại sản phẩm"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Form fields panel
        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Initialize form fields
        idField = createTextField();
        nameField = createTextField();
        descriptionField = createTextField();

        // Add fields to panel
        fieldsPanel.add(createLabelWithIcon("Mã loại sản phẩm:", "\uD83C\uDD94"));
        fieldsPanel.add(idField);
        fieldsPanel.add(createLabelWithIcon("Tên loại sản phẩm:", "\uD83D\uDCCA"));
        fieldsPanel.add(nameField);
        fieldsPanel.add(createLabelWithIcon("Mô tả:", "\uD83D\uDCDD"));
        fieldsPanel.add(descriptionField);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setOpaque(false);

        // Initialize buttons
        addButton = createButton("Thêm mới", successColor, "➕");
        addButton.addActionListener(e -> addCategory());

        updateButton = createButton("Cập nhật", warningColor, "✏️");
        updateButton.addActionListener(e -> updateCategory());

        deleteButton = createButton("Xóa", dangerColor, "❌");
        deleteButton.addActionListener(e -> deleteCategory());

        clearButton = createButton("Làm mới form", primaryColor, "🔄");
        clearButton.addActionListener(e -> clearForm());

        // Add buttons to panel
        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(clearButton);

        // Add panels to form
        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Add form to main panel
        add(formPanel, BorderLayout.SOUTH);
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return textField;
    }

    private JLabel createLabelWithIcon(String text, String icon) {
        JLabel label = new JLabel(icon + " " + text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(textColor);
        return label;
    }

    private JButton createButton(String text, Color backgroundColor, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(darker(backgroundColor, 0.8f));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    private Color darker(Color color, float factor) {
        return new Color(
                Math.max((int) (color.getRed() * factor), 0),
                Math.max((int) (color.getGreen() * factor), 0),
                Math.max((int) (color.getBlue() * factor), 0),
                color.getAlpha());
    }

    private void refreshCategoryData() {
        // Clear table
        tableModel.setRowCount(0);

        // Get all categories
        List<ProductCategory> categories = categoryController.getAllCategories();

        // Add categories to table
        for (ProductCategory category : categories) {
            tableModel.addRow(new Object[] {
                    category.getCategoryId(),
                    category.getCategoryName(),
                    category.getDescription()
            });
        }

        // Clear form
        clearForm();
    }

    private void displayCategoryData(ProductCategory category) {
        idField.setText(category.getCategoryId());
        nameField.setText(category.getCategoryName());
        descriptionField.setText(category.getDescription());
    }

    private void clearForm() {
        // Generate new ID
        String newId = categoryController.generateNewCategoryId();

        // Set fields
        idField.setText(newId);
        nameField.setText("");
        descriptionField.setText("");

        // Set focus to name field
        nameField.requestFocus();

        // Deselect any row in the table
        categoryTable.clearSelection();
    }

    private void addCategory() {
        if (!validateInput()) {
            return;
        }

        // Create new category object
        ProductCategory category = new ProductCategory();
        category.setCategoryId(idField.getText().trim());
        category.setCategoryName(nameField.getText().trim());
        category.setDescription(descriptionField.getText().trim());

        // Save to database
        boolean success = categoryController.addCategory(category);

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Thêm loại sản phẩm thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            // Refresh data
            refreshCategoryData();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Thêm loại sản phẩm thất bại! Vui lòng thử lại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCategory() {
        int selectedRow = categoryTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn loại sản phẩm cần cập nhật!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateInput()) {
            return;
        }

        // Confirm update
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn cập nhật loại sản phẩm này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Create category object
        ProductCategory category = new ProductCategory();
        category.setCategoryId(idField.getText().trim());
        category.setCategoryName(nameField.getText().trim());
        category.setDescription(descriptionField.getText().trim());

        // Update in database
        boolean success = categoryController.updateCategory(category);

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cập nhật loại sản phẩm thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            // Refresh data
            refreshCategoryData();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Cập nhật loại sản phẩm thất bại! Vui lòng thử lại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCategory() {
        int selectedRow = categoryTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn loại sản phẩm cần xóa!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get category ID
        String categoryId = categoryTable.getValueAt(selectedRow, 0).toString();

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa loại sản phẩm này?\nTất cả sản phẩm thuộc loại này cũng sẽ bị xóa!",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Delete from database
        boolean success = categoryController.deleteCategory(categoryId);

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Xóa loại sản phẩm thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            // Refresh data
            refreshCategoryData();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Xóa loại sản phẩm thất bại! Loại sản phẩm này có thể đang được sử dụng bởi sản phẩm.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInput() {
        // Validate name
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập tên loại sản phẩm!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        return true;
    }
}