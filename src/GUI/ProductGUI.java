package GUI;

import BUS.ProductBUS;
import BUS.ProductCategoryBUS;
import DTO.ProductCategoryDTO;
import DTO.ProductDTO;
import GUI.utils.ExcelUtils;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.io.File;

public class ProductGUI extends JPanel {
    private ProductBUS productController;
    private ProductCategoryBUS categoryController;

    private JTable productTable;
    private DefaultTableModel tableModel;

    private JTextField productIdField;
    private JTextField productNameField;
    private JComboBox<ProductCategoryDTO> categoryComboBox;
    private JTextField descriptionField;
    private JTextField imageField;
    private JComboBox<String> unitField;

    private JTextField productSearchField;
    private JPanel advancedSearchPanel;
    private boolean showAdvancedSearch = false;

    // Colors
    private final Color primaryColor = new Color(0, 123, 255);
    private final Color successColor = new Color(40, 167, 69);
    private final Color warningColor = new Color(255, 193, 7);
    private final Color dangerColor = new Color(220, 53, 69);
    private final Color lightColor = new Color(248, 249, 250);

    // Dialog cho form thêm/sửa sản phẩm
    private JDialog productFormDialog;

    // Listener for product selection
    private List<Consumer<String>> productSelectionListeners = new ArrayList<>();

    public ProductGUI(ProductBUS productController, ProductCategoryBUS categoryController) {
        this.productController = productController;
        this.categoryController = categoryController;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(lightColor);

        createAdvancedSearchPanel();
        createGUI();
        createProductFormDialog();
        refreshProductData();
    }

    private void createGUI() {
        // Panel tiêu đề ở trên cùng
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(lightColor);

        JLabel titleLabel = new JLabel("Quản Lý Sản Phẩm");
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
        addBtn.addActionListener(e -> showAddProductDialog());

        // Nút xóa - giảm kích thước
        JButton deleteBtn = createOutlineButton("Xóa", dangerColor);
        deleteBtn.setMargin(new Insets(3, 8, 3, 8));
        deleteBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        deleteBtn.addActionListener(e -> deleteProduct());

        // Nút chỉnh sửa - giảm kích thước
        JButton editBtn = createOutlineButton("Chỉnh sửa", warningColor);
        editBtn.setMargin(new Insets(3, 8, 3, 8));
        editBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        editBtn.addActionListener(e -> showEditProductDialog());

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
        productSearchField = new JTextField(15);
        productSearchField.setPreferredSize(new Dimension(180, 25));
        productSearchField.setFont(new Font("Arial", Font.PLAIN, 12));
        productSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchProducts();
                }
            }
        });

        // Nút tìm kiếm - giảm kích thước
        JButton searchButton = createOutlineButton("Tìm kiếm", primaryColor);
        searchButton.setMargin(new Insets(3, 8, 3, 8));
        searchButton.setFont(new Font("Arial", Font.PLAIN, 12));
        searchButton.addActionListener(e -> searchProducts());

        // Nút làm mới - giảm kích thước
        JButton refreshButton = createOutlineButton("Làm mới", new Color(23, 162, 184));
        refreshButton.setMargin(new Insets(3, 8, 3, 8));
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshButton.addActionListener(e -> refreshProductData());

        // Thêm các thành phần vào panel tìm kiếm
        rightSearchPanel.add(searchLabel);
        rightSearchPanel.add(productSearchField);
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
        showAdvancedSearch = true;

        // Tạo bảng sản phẩm
        createProductTable();
        JScrollPane tableScrollPane = new JScrollPane(productTable);
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

    @SuppressWarnings("unused")
    private void refreshGridView() {
        // Remove problematic code that references cardPanel
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setName("statusPanel");
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusPanel.setBackground(new Color(248, 249, 250));

        JLabel statusLabel = new JLabel("Tổng số sản phẩm: " + productController.getAllProducts().size());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(new Color(33, 37, 41));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));

        // Thêm panel cho các nút ở dưới cùng
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomButtonPanel.setName("bottomButtonPanel"); // Đặt tên cho panel
        bottomButtonPanel.setOpaque(false);

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(bottomButtonPanel, BorderLayout.EAST);

        return statusPanel;
    }

    /**
     * Thêm nút vào panel dưới cùng của giao diện
     * 
     * @param button nút cần thêm vào
     */

    /**
     * Thêm nút xem chi tiết vào giao diện sản phẩm
     * 
     * @param button nút xem chi tiết cần thêm vào
     */
    public void addViewDetailsButton(JButton button) {
        try {
            JPanel bottomButtonPanel = findBottomButtonPanel();
            if (bottomButtonPanel != null) {
                bottomButtonPanel.add(button);
                bottomButtonPanel.revalidate();
                bottomButtonPanel.repaint();
            } else {
                System.err.println("Warning: bottomButtonPanel not found in ProductGUI. Button not added.");
            }
        } catch (Exception e) {
            System.err.println("Error adding view details button: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Thêm nút hành động bổ sung vào giao diện sản phẩm
     * 
     * @param button nút hành động cần thêm vào
     */
    public void addExtraActionButton(JButton button) {
        try {
            JPanel bottomButtonPanel = findBottomButtonPanel();
            if (bottomButtonPanel != null) {
                bottomButtonPanel.add(button);
                bottomButtonPanel.revalidate();
                bottomButtonPanel.repaint();
            } else {
                System.err.println("Warning: bottomButtonPanel not found in ProductGUI. Button not added.");
            }
        } catch (Exception e) {
            System.err.println("Error adding extra action button: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JPanel findBottomButtonPanel() {
        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component innerComp : panel.getComponents()) {
                    if (innerComp instanceof JPanel) {
                        JPanel innerPanel = (JPanel) innerComp;
                        for (Component innerMostComp : innerPanel.getComponents()) {
                            if (innerMostComp instanceof JPanel &&
                                    innerMostComp.getName() != null &&
                                    innerMostComp.getName().equals("bottomButtonPanel")) {
                                return (JPanel) innerMostComp;
                            }
                        }
                    }
                }
            }
        }
        return null;
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

        // Tên sản phẩm
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Tên sản phẩm:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(15);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(nameField, gbc);

        // Danh mục
        gbc.gridx = 2;
        JLabel categoryLabel = new JLabel("Danh mục:");
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(categoryLabel, gbc);

        gbc.gridx = 3;
        JComboBox<ProductCategoryDTO> categoryField = new JComboBox<>();
        categoryField.setFont(new Font("Arial", Font.PLAIN, 14));
        // Thêm một mục rỗng đầu tiên
        categoryField.addItem(null);
        // Nạp dữ liệu vào combo box
        List<ProductCategoryDTO> categories = categoryController.getAllCategories();
        for (ProductCategoryDTO category : categories) {
            categoryField.addItem(category);
        }
        conditionsPanel.add(categoryField, gbc);

        // Đơn vị tính
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel unitLabel = new JLabel("Đơn vị tính:");
        unitLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(unitLabel, gbc);

        gbc.gridx = 1;
        String[] unitOptions = { "Chai", "Lon", "Hộp", "Thùng" };
        unitField = new JComboBox<>(unitOptions);
        unitField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(unitField, gbc);

        // Mô tả
        gbc.gridx = 2;
        JLabel descLabel = new JLabel("Mô tả:");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(descLabel, gbc);

        gbc.gridx = 3;
        JTextField descField = new JTextField(15);
        descField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(descField, gbc);

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
            ProductCategoryDTO category = (ProductCategoryDTO) categoryField.getSelectedItem();
            String unit = unitField.getSelectedItem().toString();
            String desc = descField.getText().trim();
            boolean isAnd = andRadio.isSelected();

            // Thực hiện tìm kiếm nâng cao
            advancedSearch(name, category, unit, desc, isAnd);
        });

        JButton clearButton = createOutlineButton("Xóa tìm kiếm", new Color(108, 117, 125));
        clearButton.setForeground(Color.BLACK);
        clearButton.addActionListener(e -> {
            nameField.setText("");
            categoryField.setSelectedIndex(0);
            unitField.setSelectedIndex(0);
            descField.setText("");
            refreshProductData();
        });

        controlPanel.add(andRadio);
        controlPanel.add(orRadio);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(searchButton);
        controlPanel.add(clearButton);

        advancedSearchPanel.add(controlPanel);
    }

    /**
     * Phương thức toggleAdvancedSearch() có thể được sử dụng trong tương lai
     * để bật/tắt bảng tìm kiếm nâng cao
     */
    @SuppressWarnings("unused")
    private void toggleAdvancedSearch() {
        showAdvancedSearch = !showAdvancedSearch;
        advancedSearchPanel.setVisible(showAdvancedSearch);
        revalidate();
        repaint();
    }

    private void advancedSearch(String name, ProductCategoryDTO category, String unit, String desc, boolean isAnd) {
        // Lấy ID danh mục nếu có
        String categoryId = category != null ? category.getCategoryId() : null;

        // Gọi phương thức tìm kiếm nâng cao từ lớp BUS
        List<ProductDTO> results = productController.advancedSearch(name, categoryId, unit, desc, isAnd);

        // Hiển thị kết quả
        displayProducts(results);
    }

    private void createProductTable() {
        String[] columnNames = { "Mã SP", "Tên sản phẩm", "Danh mục", "Đơn vị tính", "Mô tả" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel);
        productTable.setFont(new Font("Arial", Font.PLAIN, 14));
        productTable.setRowHeight(25);
        productTable.setGridColor(new Color(230, 230, 230));
        productTable.setSelectionBackground(new Color(173, 216, 230));
        productTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        productTable.getTableHeader().setBackground(primaryColor);
        productTable.getTableHeader().setForeground(Color.BLACK);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column width
        productTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(300);

        // Add selection listener
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && productTable.getSelectedRow() != -1) {
                int selectedRow = productTable.getSelectedRow();
                String id = productTable.getValueAt(selectedRow, 0).toString();
                ProductDTO product = productController.getProductById(id);
                if (product != null) {
                    notifyProductSelected(id);
                }
            }
        });
    }

    private void createProductFormDialog() {
        // Tạo dialog
        productFormDialog = new JDialog();
        productFormDialog.setTitle("Thông tin sản phẩm");
        productFormDialog.setSize(600, 500);
        productFormDialog.setLocationRelativeTo(this);
        productFormDialog.setModal(true);
        productFormDialog.setResizable(false);

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

        // Mã sản phẩm
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel idLabel = new JLabel("Mã sản phẩm:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        productIdField = new JTextField(20);
        productIdField.setFont(new Font("Arial", Font.PLAIN, 14));
        productIdField.setEditable(false);
        formPanel.add(productIdField, gbc);

        // Tên sản phẩm
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Tên sản phẩm:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        productNameField = new JTextField(20);
        productNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(productNameField, gbc);

        // Danh mục
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel categoryLabel = new JLabel("Danh mục:");
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(categoryLabel, gbc);

        gbc.gridx = 1;
        categoryComboBox = new JComboBox<>();
        categoryComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(categoryComboBox, gbc);

        // Đơn vị tính
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel unitLabel = new JLabel("Đơn vị tính:");
        unitLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(unitLabel, gbc);

        gbc.gridx = 1;
        String[] unitOptions = { "Chai", "Lon", "Hộp", "Thùng" };
        unitField = new JComboBox<>(unitOptions);
        unitField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(unitField, gbc);

        // Mô tả
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel descriptionLabel = new JLabel("Mô tả:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(descriptionLabel, gbc);

        gbc.gridx = 1;
        descriptionField = new JTextField(20);
        descriptionField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(descriptionField, gbc);

        // Hình ảnh
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel imageLabel = new JLabel("Hình ảnh:");
        imageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(imageLabel, gbc);

        gbc.gridx = 1;
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setOpaque(false);

        imageField = new JTextField(20);
        imageField.setFont(new Font("Arial", Font.PLAIN, 14));
        imagePanel.add(imageField, BorderLayout.CENTER);

        JButton browseButton = new JButton("Chọn ảnh");
        browseButton.setFont(new Font("Arial", Font.PLAIN, 14));
        browseButton.addActionListener(e -> browseImage());
        imagePanel.add(browseButton, BorderLayout.EAST);

        formPanel.add(imagePanel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton saveButton = new JButton("Lưu");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setBackground(successColor);
        saveButton.setForeground(Color.BLACK);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> {
            if (productFormDialog.getTitle().equals("Thêm sản phẩm mới")) {
                addProduct();
            } else {
                updateProduct();
            }
        });

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBackground(dangerColor);
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> productFormDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        dialogPanel.add(formPanel, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        productFormDialog.add(dialogPanel);
    }

    private void showAddProductDialog() {
        // Reset form fields
        productIdField.setText(productController.generateNewProductId());
        productIdField.setEditable(false);
        productNameField.setText("");
        descriptionField.setText("");
        imageField.setText("");
        unitField.setSelectedIndex(0);

        // Load product categories
        loadProductCategories();

        // Show dialog
        productFormDialog.setTitle("Thêm sản phẩm mới");
        productFormDialog.setVisible(true);
    }

    private void showEditProductDialog() {
        // Check if a row is selected
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn sản phẩm để chỉnh sửa",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the selected product ID
        String productId = productTable.getValueAt(selectedRow, 0).toString();
        ProductDTO product = productController.getProductById(productId);

        if (product == null) {
            JOptionPane.showMessageDialog(this,
                    "Không thể tìm thấy thông tin sản phẩm",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Set data to form
        productIdField.setText(product.getProductId());
        productIdField.setEditable(false);
        productNameField.setText(product.getProductName());
        descriptionField.setText(product.getDescription());
        imageField.setText(product.getImage());
        unitField.setSelectedItem(product.getUnit());

        // Load loại sản phẩm
        loadProductCategories();

        // Chọn loại sản phẩm hiện tại
        for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
            ProductCategoryDTO category = categoryComboBox.getItemAt(i);
            if (category.getCategoryId().equals(product.getCategoryId())) {
                categoryComboBox.setSelectedIndex(i);
                break;
            }
        }

        // Show dialog
        productFormDialog.setTitle("Chỉnh sửa sản phẩm");
        productFormDialog.setVisible(true);
    }

    private void loadProductCategories() {
        categoryComboBox.removeAllItems();
        List<ProductCategoryDTO> categories = categoryController.getAllCategories();
        for (ProductCategoryDTO category : categories) {
            categoryComboBox.addItem(category);
        }
    }

    private void browseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn hình ảnh");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (fileChooser.showOpenDialog(productFormDialog) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imageField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void searchProducts() {
        String keyword = productSearchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshProductData();
            return;
        }

        List<ProductDTO> results = productController.searchProducts(keyword);
        displayProducts(results);
    }

    private void displayProducts(List<ProductDTO> products) {
        // Clear table
        tableModel.setRowCount(0);

        // Add products to table
        for (ProductDTO product : products) {
            tableModel.addRow(new Object[] {
                    product.getProductId(),
                    product.getProductName(),
                    product.getCategoryName(),
                    product.getUnit(),
                    product.getDescription()
            });
        }
    }

    private void addProduct() {
        // Kiểm tra dữ liệu nhập
        if (!validateProductInput()) {
            return;
        }

        // Lấy thông tin từ form
        ProductDTO product = getProductFromForm();

        // Thêm sản phẩm
        boolean success = productController.addProduct(product);

        if (success) {
            JOptionPane.showMessageDialog(productFormDialog,
                    "Thêm sản phẩm thành công",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            productFormDialog.dispose();
            refreshProductData();
        } else {
            JOptionPane.showMessageDialog(productFormDialog,
                    "Thêm sản phẩm thất bại",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProduct() {
        // Validate input
        if (!validateProductInput()) {
            return;
        }

        // Get the product from form
        ProductDTO product = getProductFromForm();

        // Update the product
        boolean success = productController.updateProduct(product);

        if (success) {
            JOptionPane.showMessageDialog(productFormDialog,
                    "Cập nhật sản phẩm thành công",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            productFormDialog.dispose();
            refreshProductData();
        } else {
            JOptionPane.showMessageDialog(productFormDialog,
                    "Không thể cập nhật sản phẩm",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        // Check if a row is selected
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn sản phẩm để xóa",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm delete
        int option = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa sản phẩm này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            // Get the selected product ID
            String productId = productTable.getValueAt(selectedRow, 0).toString();

            // Delete the product
            boolean success = productController.deleteProduct(productId);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Xóa sản phẩm thành công",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshProductData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể xóa sản phẩm. Sản phẩm này có thể đang được sử dụng trong chi tiết sản phẩm hoặc hóa đơn.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private ProductDTO getProductFromForm() {
        ProductDTO product = new ProductDTO();

        // Lấy thông tin cơ bản
        product.setProductId(productIdField.getText().trim());
        product.setProductName(productNameField.getText().trim());
        product.setDescription(descriptionField.getText().trim());
        product.setImage(imageField.getText().trim());
        product.setUnit(unitField.getSelectedItem().toString());

        // Lấy loại sản phẩm
        ProductCategoryDTO selectedCategory = (ProductCategoryDTO) categoryComboBox.getSelectedItem();
        if (selectedCategory != null) {
            product.setCategoryId(selectedCategory.getCategoryId());
        }

        return product;
    }

    private boolean validateProductInput() {
        // Mã sản phẩm luôn có sẵn và không thể chỉnh sửa nên không cần kiểm tra

        // Kiểm tra tên sản phẩm
        if (productNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(productFormDialog,
                    "Vui lòng nhập tên sản phẩm",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            productNameField.requestFocus();
            return false;
        }

        // Kiểm tra đơn vị tính
        if (unitField.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(productFormDialog,
                    "Vui lòng chọn đơn vị tính",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Kiểm tra loại sản phẩm
        if (categoryComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(productFormDialog,
                    "Vui lòng chọn danh mục sản phẩm",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public void refreshProductData() {
        // Clear table
        tableModel.setRowCount(0);

        // Get all products
        List<ProductDTO> products = productController.getAllProducts();

        // Add products to table
        for (ProductDTO product : products) {
            tableModel.addRow(new Object[] {
                    product.getProductId(),
                    product.getProductName(),
                    product.getCategoryName(),
                    product.getUnit(),
                    product.getDescription()
            });
        }
    }

    // Helper methods for product selection for other components
    public void addProductSelectionListener(Consumer<String> listener) {
        productSelectionListeners.add(listener);
    }

    private void notifyProductSelected(String productId) {
        for (Consumer<String> listener : productSelectionListeners) {
            listener.accept(productId);
        }
    }

    public int getSelectedProductRow() {
        return productTable.getSelectedRow();
    }

    public String getSelectedProductId() {
        int selectedRow = getSelectedProductRow();
        if (selectedRow != -1) {
            return productTable.getValueAt(selectedRow, 0).toString();
        }
        return null;
    }

    public void selectProduct(String productId) {
        for (int i = 0; i < productTable.getRowCount(); i++) {
            if (productTable.getValueAt(i, 0).equals(productId)) {
                productTable.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    /**
     * Creates a darker version of the specified color
     * 
     * @param color  the color to make darker
     * @param factor the factor to darken by (0.0 to 1.0)
     * @return a darker color
     */
    @SuppressWarnings("unused")
    private Color darker(Color color, float factor) {
        return new Color(
                Math.max((int) (color.getRed() * factor), 0),
                Math.max((int) (color.getGreen() * factor), 0),
                Math.max((int) (color.getBlue() * factor), 0),
                color.getAlpha());
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

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(color);
                    button.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(255, 255, 255));
                    button.setForeground(color);
                }
            }
        });

        return button;
    }

    /**
     * Xuất dữ liệu sản phẩm ra file Excel
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
        ExcelUtils.exportToExcel(headers, data, "Sản phẩm", "DANH SÁCH SẢN PHẨM");
    }

    /**
     * Nhập dữ liệu sản phẩm từ file Excel
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
                    "Bạn có chắc chắn muốn nhập " + data.size() + " sản phẩm từ file Excel?",
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
                    if (rowData.length < 3 || rowData[0] == null || rowData[1] == null || rowData[2] == null) {
                        errorMessages.append("Dòng ").append(rowIndex + 1)
                                .append(": Thiếu thông tin bắt buộc (Mã SP, Tên sản phẩm, Danh mục)\n");
                        failCount++;
                        continue;
                    }

                    // Tạo đối tượng ProductDTO từ dữ liệu nhập vào
                    ProductDTO product = new ProductDTO();

                    // Mã sản phẩm
                    String productId = rowData[0].toString().trim();
                    if (productId.isEmpty()) {
                        errorMessages.append("Dòng ").append(rowIndex + 1)
                                .append(": Mã sản phẩm không được để trống\n");
                        failCount++;
                        continue;
                    }
                    product.setProductId(productId);

                    // Tên sản phẩm
                    String productName = rowData[1].toString().trim();
                    if (productName.isEmpty()) {
                        errorMessages.append("Dòng ").append(rowIndex + 1)
                                .append(": Tên sản phẩm không được để trống\n");
                        failCount++;
                        continue;
                    }
                    product.setProductName(productName);

                    // Danh mục
                    String categoryName = rowData[2].toString().trim();
                    boolean categoryFound = false;
                    for (ProductCategoryDTO category : categoryController.getAllCategories()) {
                        if (category.getCategoryName().equals(categoryName)) {
                            product.setCategoryId(category.getCategoryId());
                            categoryFound = true;
                            break;
                        }
                    }
                    if (!categoryFound) {
                        errorMessages.append("Dòng ").append(rowIndex + 1).append(": Danh mục '").append(categoryName)
                                .append("' không tồn tại\n");
                        failCount++;
                        continue;
                    }

                    // Mô tả (nếu có)
                    if (rowData.length > 3 && rowData[3] != null) {
                        product.setDescription(rowData[3].toString().trim());
                    }

                    // Hình ảnh (nếu có)
                    if (rowData.length > 4 && rowData[4] != null) {
                        product.setImage(rowData[4].toString().trim());
                    }

                    // Đơn vị tính (nếu có)
                    if (rowData.length > 5 && rowData[5] != null) {
                        String unit = rowData[5].toString().trim();
                        if (!unit.isEmpty()) {
                            product.setUnit(unit);
                        } else {
                            product.setUnit("Cái"); // Giá trị mặc định
                        }
                    } else {
                        product.setUnit("Cái"); // Giá trị mặc định
                    }

                    // Thêm sản phẩm vào cơ sở dữ liệu
                    if (productController.addProduct(product)) {
                        successCount++;
                    } else {
                        errorMessages.append("Dòng ").append(rowIndex + 1)
                                .append(": Không thể thêm sản phẩm vào cơ sở dữ liệu\n");
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
                    "Số sản phẩm nhập thành công: " + successCount + "\n" +
                    "Số sản phẩm nhập thất bại: " + failCount;

            if (failCount > 0 && errorMessages.length() > 0) {
                message += "\n\nChi tiết lỗi:\n" + errorMessages.toString();
            }

            JOptionPane.showMessageDialog(this,
                    message,
                    "Kết quả nhập dữ liệu",
                    failCount > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);

            // Cập nhật lại dữ liệu hiển thị
            refreshProductData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi nhập dữ liệu từ Excel: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

}