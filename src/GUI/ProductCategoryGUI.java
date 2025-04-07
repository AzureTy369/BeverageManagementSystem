package GUI;

import BUS.ProductCategoryBUS;
import DTO.ProductCategoryDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class ProductCategoryGUI extends JPanel {
    private ProductCategoryBUS categoryController;

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

    private boolean isDialogMode = false;

    public ProductCategoryGUI(ProductCategoryBUS categoryController) {
        this(categoryController, false);
    }

    public ProductCategoryGUI(ProductCategoryBUS categoryController, boolean isDialogMode) {
        this.categoryController = categoryController;
        this.isDialogMode = isDialogMode;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(lightColor);

        if (isDialogMode) {
            createDialogContent();
        } else {
            // Create title panel
            createTitlePanel();

            // Create components
            createTable();

            // Create form
            createForm();

            // Refresh data
            refreshCategoryData();
        }
    }

    private void createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(lightColor);

        JLabel titleLabel = new JLabel("Quản Lý Danh Mục Sản Phẩm");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(primaryColor);

        titlePanel.add(titleLabel);

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
                ProductCategoryDTO category = categoryController.getCategoryById(id);
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
        try {
            // Clear table
            if (tableModel != null) {
                tableModel.setRowCount(0);

                // Get all categories
                List<ProductCategoryDTO> categories = categoryController.getAllCategories();

                // Add categories to table
                for (ProductCategoryDTO category : categories) {
                    tableModel.addRow(new Object[] {
                            category.getCategoryId(),
                            category.getCategoryName(),
                            category.getDescription()
                    });
                }
            }

            // Clear form only if not in dialog mode and if the form exists
            if (!isDialogMode && idField != null) {
                clearForm();
            }
        } catch (Exception e) {
            System.err.println("Error refreshing category data: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Có lỗi khi làm mới dữ liệu danh mục: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayCategoryData(ProductCategoryDTO category) {
        // Only update fields if not in dialog mode
        if (!isDialogMode && idField != null) {
            idField.setText(category.getCategoryId());
            nameField.setText(category.getCategoryName());
            descriptionField.setText(category.getDescription());
        }
    }

    private void clearForm() {
        // Skip form clearing if in dialog mode or if idField is null
        if (isDialogMode || idField == null) {
            return;
        }

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
        ProductCategoryDTO category = new ProductCategoryDTO();
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
        ProductCategoryDTO category = new ProductCategoryDTO();
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

    private void createDialogContent() {
        // Create simple title panel for dialog mode
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(lightColor);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("\uD83D\uDCCA Quản lý Danh Mục Sản Phẩm");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(textColor);

        // Panel tìm kiếm và thêm mới
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);

        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                searchField.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        searchField.setToolTipText("Tìm kiếm loại sản phẩm");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String keyword = searchField.getText().trim();
                    if (keyword.isEmpty()) {
                        refreshCategoryData();
                    } else {
                        List<ProductCategoryDTO> results = categoryController.searchCategories(keyword);
                        displayCategories(results);
                    }
                }
            }
        });

        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 14));
        searchButton.setBackground(primaryColor);
        searchButton.setForeground(Color.BLACK);
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                refreshCategoryData();
            } else {
                List<ProductCategoryDTO> results = categoryController.searchCategories(keyword);
                displayCategories(results);
            }
        });

        JButton addButton = new JButton("Thêm mới");
        addButton.setFont(new Font("Arial", Font.PLAIN, 14));
        addButton.setBackground(successColor);
        addButton.setForeground(Color.BLACK);
        addButton.setFocusPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> {
            showCategoryFormDialog(null); // null for add new
        });

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(addButton);

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(searchPanel, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        // Create table
        createTable();

        // Buttons panel for table actions
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton editButton = new JButton("Chỉnh sửa");
        editButton.setFont(new Font("Arial", Font.PLAIN, 14));
        editButton.setBackground(warningColor);
        editButton.setForeground(Color.BLACK);
        editButton.setFocusPainted(false);
        editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editButton.addActionListener(e -> {
            int selectedRow = categoryTable.getSelectedRow();
            if (selectedRow >= 0) {
                String categoryId = categoryTable.getValueAt(selectedRow, 0).toString();
                ProductCategoryDTO category = categoryController.getCategoryById(categoryId);
                showCategoryFormDialog(category);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn loại sản phẩm cần chỉnh sửa!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton deleteButton = new JButton("Xóa");
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 14));
        deleteButton.setBackground(dangerColor);
        deleteButton.setForeground(Color.BLACK);
        deleteButton.setFocusPainted(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.addActionListener(e -> {
            int selectedRow = categoryTable.getSelectedRow();
            if (selectedRow >= 0) {
                String categoryId = categoryTable.getValueAt(selectedRow, 0).toString();
                deleteCategory(categoryId);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn loại sản phẩm cần xóa!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);

        // Add components to main panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(new JScrollPane(categoryTable), BorderLayout.CENTER);
        centerPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Load data
        refreshCategoryData();
    }

    private void showCategoryFormDialog(ProductCategoryDTO category) {
        JDialog formDialog = new JDialog();
        formDialog.setTitle(category == null ? "Thêm Loại Sản Phẩm Mới" : "Cập Nhật Loại Sản Phẩm");
        formDialog.setSize(400, 300);
        formDialog.setLocationRelativeTo(this);
        formDialog.setModal(true);
        formDialog.setResizable(false);

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

        // Mã loại
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel idLabel = new JLabel("Mã loại sản phẩm:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        JTextField idField = new JTextField(20);
        idField.setFont(new Font("Arial", Font.PLAIN, 14));
        if (category == null) {
            idField.setText(categoryController.generateNewCategoryId());
            idField.setEditable(false);
        } else {
            idField.setText(category.getCategoryId());
            idField.setEditable(false);
        }
        formPanel.add(idField, gbc);

        // Tên loại
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Tên loại sản phẩm:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        if (category != null) {
            nameField.setText(category.getCategoryName());
        }
        formPanel.add(nameField, gbc);

        // Mô tả
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel descLabel = new JLabel("Mô tả:");
        descLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(descLabel, gbc);

        gbc.gridx = 1;
        JTextField descField = new JTextField(20);
        descField.setFont(new Font("Arial", Font.PLAIN, 14));
        if (category != null) {
            descField.setText(category.getDescription());
        }
        formPanel.add(descField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        JButton saveButton = new JButton("Lưu");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setBackground(successColor);
        saveButton.setForeground(Color.BLACK);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> {
            // Validate
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(formDialog,
                        "Vui lòng nhập tên loại sản phẩm!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                nameField.requestFocus();
                return;
            }

            // Save
            ProductCategoryDTO newCategory = new ProductCategoryDTO();
            newCategory.setCategoryId(idField.getText().trim());
            newCategory.setCategoryName(nameField.getText().trim());
            newCategory.setDescription(descField.getText().trim());

            boolean success;
            if (category == null) {
                success = categoryController.addCategory(newCategory);
            } else {
                success = categoryController.updateCategory(newCategory);
            }

            if (success) {
                JOptionPane.showMessageDialog(formDialog,
                        category == null ? "Thêm loại sản phẩm thành công!" : "Cập nhật loại sản phẩm thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshCategoryData();
                formDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(formDialog,
                        category == null ? "Thêm loại sản phẩm thất bại!" : "Cập nhật loại sản phẩm thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBackground(dangerColor);
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> formDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialogPanel.add(formPanel, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        formDialog.add(dialogPanel);
        formDialog.pack();
        formDialog.setVisible(true);
    }

    private void deleteCategory(String categoryId) {
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

    private void displayCategories(List<ProductCategoryDTO> categories) {
        tableModel.setRowCount(0);
        for (ProductCategoryDTO category : categories) {
            tableModel.addRow(new Object[] {
                    category.getCategoryId(),
                    category.getCategoryName(),
                    category.getDescription()
            });
        }
    }
}