package GUI;

import BUS.SupplierBUS;
import BUS.ProductCategoryBUS;
import DTO.SupplierDTO;
import GUI.components.SupplierDetailPanel;
import DTO.SupplierProductDTO;
import DTO.ProductCategoryDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class SupplierGUI extends JPanel {
    private SupplierBUS supplierController;

    private JTable supplierTable;
    private DefaultTableModel tableModel;

    private JTextField idField;
    private JTextField nameField;
    private JTextField addressField;
    private JTextField phoneField;
    private JTextField emailField;

    private JTextField supplierSearchField;
    private JPanel advancedSearchPanel;

    private JButton updateButton;
    private JButton deleteButton;

    // Thêm các thành phần cho quản lý sản phẩm
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private List<SupplierProductDTO> selectedProducts;

    // Colors
    private final Color primaryColor = new Color(0, 123, 255);
    private final Color successColor = new Color(40, 167, 69);
    private final Color warningColor = new Color(255, 193, 7);
    private final Color dangerColor = new Color(220, 53, 69);
    private final Color lightColor = new Color(248, 249, 250);

    // Dialog cho form thêm/sửa nhà cung cấp
    private JDialog supplierFormDialog;

    public SupplierGUI(SupplierBUS supplierController) {
        this.supplierController = supplierController;
        this.selectedProducts = new ArrayList<>();

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(lightColor);

        createAdvancedSearchPanel();
        createGUI();
        createSupplierFormDialog();
        refreshSupplierData();
    }

    private void createGUI() {
        // Panel tiêu đề ở trên cùng
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(lightColor);

        JLabel titleLabel = new JLabel("Quản Lý Nhà Cung Cấp");
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
        addBtn.addActionListener(e -> showAddSupplierDialog());

        // Nút xóa - giảm kích thước
        JButton deleteBtn = createOutlineButton("Xóa", dangerColor);
        deleteBtn.setMargin(new Insets(3, 8, 3, 8));
        deleteBtn.setFont(new Font("Arial", Font.BOLD, 16));
        deleteBtn.addActionListener(e -> deleteSupplier());

        // Gán deleteBtn vào biến thành viên để sử dụng ở nơi khác
        this.deleteButton = deleteBtn;

        // Nút chỉnh sửa - giảm kích thước
        JButton editBtn = createOutlineButton("Chỉnh sửa", warningColor);
        editBtn.setMargin(new Insets(3, 8, 3, 8));
        editBtn.setFont(new Font("Arial", Font.BOLD, 16));
        editBtn.addActionListener(e -> showEditSupplierDialog());

        // Gán editBtn vào biến thành viên để sử dụng ở nơi khác
        this.updateButton = editBtn;

        // Separator
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(1, 20));
        separator.setForeground(new Color(200, 200, 200));

        // Nút xuất Excel - giảm kích thước
        JButton exportBtn = createOutlineButton("Xuất Excel", new Color(108, 117, 125));
        exportBtn.setMargin(new Insets(3, 8, 3, 8));
        exportBtn.setFont(new Font("Arial", Font.BOLD, 16));
        // Nút Nhập Excel - giảm kích thước
        JButton importBtn = createOutlineButton("Nhập Excel", new Color(108, 117, 125));
        importBtn.setMargin(new Insets(3, 8, 3, 8));
        importBtn.setFont(new Font("Arial", Font.BOLD, 16));

        // Thêm các nút vào panel bên trái
        leftFunctionPanel.add(addBtn);
        leftFunctionPanel.add(deleteBtn);
        leftFunctionPanel.add(editBtn);
        leftFunctionPanel.add(separator);
        leftFunctionPanel.add(exportBtn);
        leftFunctionPanel.add(importBtn);

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
        supplierSearchField = new JTextField(15);
        supplierSearchField.setPreferredSize(new Dimension(180, 25));
        supplierSearchField.setFont(new Font("Arial", Font.PLAIN, 12));
        supplierSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchSuppliers();
                }
            }
        });

        // Nút tìm kiếm - giảm kích thước
        JButton searchButton = createOutlineButton("Tìm kiếm", primaryColor);
        searchButton.setMargin(new Insets(3, 8, 3, 8));
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));
        searchButton.addActionListener(e -> searchSuppliers());

        // Nút làm mới - giảm kích thước
        JButton refreshButton = createOutlineButton("Làm mới", new Color(23, 162, 184));
        refreshButton.setMargin(new Insets(3, 8, 3, 8));
        refreshButton.setFont(new Font("Arial", Font.BOLD, 16));
        refreshButton.addActionListener(e -> refreshSupplierData());

        // Thêm các thành phần vào panel tìm kiếm
        rightSearchPanel.add(searchLabel);
        rightSearchPanel.add(supplierSearchField);
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

        // Tạo bảng nhà cung cấp
        createSupplierTable();
        JScrollPane tableScrollPane = new JScrollPane(supplierTable);
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

        // Tên nhà cung cấp
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Tên nhà cung cấp:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(15);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(nameField, gbc);

        // Địa chỉ
        gbc.gridx = 2;
        JLabel addressLabel = new JLabel("Địa chỉ:");
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(addressLabel, gbc);

        gbc.gridx = 3;
        JTextField addressField = new JTextField(15);
        addressField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(addressField, gbc);

        // Điện thoại
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel phoneLabel = new JLabel("Điện thoại:");
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        JTextField phoneField = new JTextField(15);
        phoneField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionsPanel.add(phoneField, gbc);

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
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));
        searchButton.addActionListener(e -> {
            // Lấy thông tin từ các ô tìm kiếm
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            String phone = phoneField.getText().trim();
            boolean isAnd = andRadio.isSelected();

            // Thực hiện tìm kiếm nâng cao
            advancedSearchSuppliers(name, address, phone, isAnd);
        });

        JButton clearButton = createOutlineButton("Xóa tìm kiếm", new Color(108, 117, 125));
        clearButton.setForeground(Color.BLACK);
        clearButton.setFont(new Font("Arial", Font.BOLD, 16));
        clearButton.addActionListener(e -> {
            nameField.setText("");
            addressField.setText("");
            phoneField.setText("");
            refreshSupplierData();
        });

        controlPanel.add(andRadio);
        controlPanel.add(orRadio);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(searchButton);
        controlPanel.add(clearButton);

        advancedSearchPanel.add(controlPanel);
    }

    private void createSupplierTable() {
        String[] columns = { "Mã nhà cung cấp", "Tên nhà cung cấp", "Địa chỉ", "Số điện thoại", "Email" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trong bảng
            }
        };

        supplierTable = new JTable(tableModel);
        supplierTable.setFont(new Font("Arial", Font.PLAIN, 14));
        supplierTable.setRowHeight(25);
        supplierTable.setGridColor(new Color(230, 230, 230));
        supplierTable.setSelectionBackground(new Color(173, 216, 230));
        supplierTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        supplierTable.getTableHeader().setBackground(primaryColor);
        supplierTable.getTableHeader().setForeground(Color.BLACK);
        supplierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add selection listener
        supplierTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = supplierTable.getSelectedRow();
                if (selectedRow != -1) {
                    String supplierId = supplierTable.getValueAt(selectedRow, 0).toString();
                    SupplierDTO supplier = supplierController.getSupplierById(supplierId);
                    if (supplier != null) {
                        displaySupplierData(supplier);
                    }
                }
            }
        });
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusPanel.setBackground(new Color(248, 249, 250));

        JLabel statusLabel = new JLabel("Tổng số nhà cung cấp: " + supplierController.getAllSuppliers().size());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(new Color(33, 37, 41));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));

        statusPanel.add(statusLabel, BorderLayout.WEST);

        // Thêm nút xem chi tiết ở góc phải
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        JButton viewDetailButton = createOutlineButton("Xem chi tiết nhà cung cấp", primaryColor);
        // Tăng kích thước nút
        viewDetailButton.setPreferredSize(new Dimension(200, 35));
        viewDetailButton.setFont(new Font("Arial", Font.BOLD, 16));
        viewDetailButton.addActionListener(e -> {
            int selectedRow = supplierTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp để xem chi tiết", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String supplierId = supplierTable.getValueAt(selectedRow, 0).toString();
            showSupplierDetail(supplierId);
        });

        rightPanel.add(viewDetailButton);
        statusPanel.add(rightPanel, BorderLayout.EAST);

        return statusPanel;
    }

    private JButton createOutlineButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                // Xác định màu nền dựa theo loại nút
                Color bgColor;
                if (text.toLowerCase().contains("thêm")) {
                    bgColor = new Color(0x4CAF50); // Xanh lá
                } else if (text.toLowerCase().contains("xóa")) {
                    bgColor = new Color(0xF44336); // Đỏ
                } else if (text.toLowerCase().contains("sửa") || text.toLowerCase().contains("chỉnh sửa")) {
                    bgColor = new Color(0xFFCA28); // Vàng
                } else if (text.toLowerCase().contains("tìm kiếm")) {
                    bgColor = primaryColor; // Màu menu của đồ án
                } else if (text.toLowerCase().contains("excel")) {
                    bgColor = new Color(0x455A64); // Xám đậm
                } else if (text.toLowerCase().contains("làm mới") || text.toLowerCase().contains("refresh")) {
                    bgColor = new Color(0x26A69A); // Xanh ngọc
                } else {
                    // Các nút khác
                    bgColor = color;
                }

                // Vẽ background
                g.setColor(bgColor);
                g.fillRect(0, 0, getWidth(), getHeight());

                // Vẽ text
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();

                Color textColor;
                if (text.toLowerCase().contains("sửa") || text.toLowerCase().contains("chỉnh sửa")) {
                    textColor = new Color(0x000000); // Đen
                } else {
                    textColor = new Color(0xFFFFFF); // Trắng
                }

                g.setColor(textColor);
                g.drawString(getText(), (getWidth() - textWidth) / 2,
                        (getHeight() - textHeight) / 2 + fm.getAscent());
            }
        };

        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(color, 1));
        button.setToolTipText(text);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 35));

        return button;
    }

    private void searchSuppliers() {
        String searchText = supplierSearchField.getText().trim();
        if (searchText.isEmpty()) {
            refreshSupplierData();
            return;
        }

        List<SupplierDTO> results = supplierController.searchSuppliers(searchText);
        displaySuppliers(results);
    }

    private void advancedSearchSuppliers(String name, String address, String phone, boolean isAnd) {
        // Tìm kiếm nâng cao - Phương thức này tự triển khai trực tiếp trong GUI
        List<SupplierDTO> allSuppliers = supplierController.getAllSuppliers();
        List<SupplierDTO> results = new ArrayList<>();

        // Nếu tất cả điều kiện trống, trả về danh sách đầy đủ
        if (name.isEmpty() && address.isEmpty() && phone.isEmpty()) {
            displaySuppliers(allSuppliers);
            return;
        }

        // Tìm kiếm theo các điều kiện
        for (SupplierDTO supplier : allSuppliers) {
            boolean nameMatch = name.isEmpty() ||
                    (supplier.getSupplierName() != null &&
                            supplier.getSupplierName().toLowerCase().contains(name.toLowerCase()));

            boolean addressMatch = address.isEmpty() ||
                    (supplier.getAddress() != null &&
                            supplier.getAddress().toLowerCase().contains(address.toLowerCase()));

            boolean phoneMatch = phone.isEmpty() ||
                    (supplier.getPhone() != null &&
                            supplier.getPhone().contains(phone));

            if (isAnd) {
                // Phép AND - tất cả điều kiện phải đúng
                if (nameMatch && addressMatch && phoneMatch) {
                    results.add(supplier);
                }
            } else {
                // Phép OR - chỉ cần một điều kiện đúng
                if (nameMatch || addressMatch || phoneMatch) {
                    results.add(supplier);
                }
            }
        }

        // Hiển thị kết quả
        displaySuppliers(results);
    }

    private void displaySuppliers(List<SupplierDTO> suppliers) {
        tableModel.setRowCount(0);

        for (SupplierDTO supplier : suppliers) {
            Object[] row = {
                    supplier.getSupplierId(),
                    supplier.getSupplierName(),
                    supplier.getAddress(),
                    supplier.getPhone(),
                    supplier.getEmail()
            };
            tableModel.addRow(row);
        }
    }

    private void createSupplierFormDialog() {
        supplierFormDialog = new JDialog();
        supplierFormDialog.setTitle("Thông tin nhà cung cấp");
        supplierFormDialog.setSize(600, 600);
        supplierFormDialog.setLocationRelativeTo(this);
        supplierFormDialog.setModal(true);
        supplierFormDialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel idLabel = new JLabel("Mã nhà cung cấp:");
        idField = new JTextField();

        JLabel nameLabel = new JLabel("Tên nhà cung cấp:");
        nameField = new JTextField();

        JLabel addressLabel = new JLabel("Địa chỉ:");
        addressField = new JTextField();

        JLabel phoneLabel = new JLabel("Số điện thoại:");
        phoneField = new JTextField();

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();

        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(addressLabel);
        formPanel.add(addressField);
        formPanel.add(phoneLabel);
        formPanel.add(phoneField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);

        // Bảng sản phẩm
        JPanel productPanel = new JPanel(new BorderLayout());
        productPanel.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm của nhà cung cấp"));

        String[] productColumns = { "Tên sản phẩm", "Danh mục", "Đơn vị tính", "Giá", "Mô tả" };
        productTableModel = new DefaultTableModel(productColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(productTableModel);
        JScrollPane productScrollPane = new JScrollPane(productTable);
        productScrollPane.setPreferredSize(new Dimension(500, 150));

        JPanel productButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addProductButton = new JButton("Thêm sản phẩm");
        JButton removeProductButton = new JButton("Xóa sản phẩm");

        productButtonPanel.add(addProductButton);
        productButtonPanel.add(removeProductButton);

        productPanel.add(productScrollPane, BorderLayout.CENTER);
        productPanel.add(productButtonPanel, BorderLayout.SOUTH);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(productPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        supplierFormDialog.add(mainPanel);

        // Init selected products list
        selectedProducts = new ArrayList<>();

        // Events
        addProductButton.addActionListener(e -> addProductToSupplier());
        removeProductButton.addActionListener(e -> removeProductFromSupplier());

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (supplierFormDialog.getTitle().contains("Thêm")) {
                    addSupplier();
                } else {
                    updateSupplier();
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                supplierFormDialog.dispose();
            }
        });

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    // Thêm sản phẩm vào danh sách
    private void addProductToSupplier() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Thêm sản phẩm mới");
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout());
        dialog.setModal(true);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel("Tên sản phẩm:");
        JTextField nameField = new JTextField();

        JLabel categoryLabel = new JLabel("Danh mục:");

        // Combo box cho danh mục sản phẩm
        JComboBox<ProductCategoryDTO> categoryComboBox = new JComboBox<>();
        loadProductCategories(categoryComboBox);

        JLabel unitLabel = new JLabel("Đơn vị tính:");

        // Combo box cho đơn vị tính
        JComboBox<String> unitComboBox = new JComboBox<>(new String[] { "Chai", "Lon", "Hộp", "Thùng" });

        JLabel descLabel = new JLabel("Mô tả:");
        JTextField descField = new JTextField();

        JLabel priceLabel = new JLabel("Giá:");
        JTextField priceField = new JTextField();

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryComboBox);
        formPanel.add(unitLabel);
        formPanel.add(unitComboBox);
        formPanel.add(descLabel);
        formPanel.add(descField);
        formPanel.add(priceLabel);
        formPanel.add(priceField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Kiểm tra dữ liệu nhập
                String name = nameField.getText().trim();
                ProductCategoryDTO selectedCategory = (ProductCategoryDTO) categoryComboBox.getSelectedItem();
                String unit = (String) unitComboBox.getSelectedItem();
                String desc = descField.getText().trim();
                String priceText = priceField.getText().trim();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên sản phẩm", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (selectedCategory == null) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng chọn danh mục sản phẩm", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double price = 0;
                try {
                    price = Double.parseDouble(priceText);
                    if (price <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Giá phải lớn hơn 0", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Giá không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Thêm sản phẩm vào danh sách
                SupplierProductDTO product = new SupplierProductDTO();
                product.setProductName(name);
                product.setUnit(unit);
                product.setDescription(desc);
                product.setPrice(price);
                product.setCategoryId(selectedCategory.getCategoryId());
                product.setCategoryName(selectedCategory.getCategoryName());

                if (selectedProducts == null) {
                    selectedProducts = new ArrayList<>();
                }
                selectedProducts.add(product);

                // Cập nhật bảng sản phẩm
                updateProductTable();

                // Đóng dialog
                dialog.dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    /**
     * Tải danh sách danh mục sản phẩm vào combo box
     */
    private void loadProductCategories(JComboBox<ProductCategoryDTO> comboBox) {
        comboBox.removeAllItems();

        // Sử dụng ProductCategoryBUS để lấy danh sách danh mục
        ProductCategoryBUS categoryController = new ProductCategoryBUS();
        List<ProductCategoryDTO> categories = categoryController.getAllCategories();

        for (ProductCategoryDTO category : categories) {
            comboBox.addItem(category);
        }
    }

    // Xóa sản phẩm khỏi danh sách
    private void removeProductFromSupplier() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(supplierFormDialog,
                    "Vui lòng chọn sản phẩm cần xóa!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String productName = (String) productTableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(supplierFormDialog,
                "Xác nhận xóa sản phẩm " + productName + " khỏi danh sách?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Xóa sản phẩm khỏi danh sách
            selectedProducts.remove(selectedRow);
            updateProductTable();
        }
    }

    // Cập nhật bảng sản phẩm
    private void updateProductTable() {
        productTableModel.setRowCount(0);

        for (SupplierProductDTO product : selectedProducts) {
            Object[] rowData = {
                    product.getProductName(),
                    product.getCategoryName(),
                    product.getUnit(),
                    product.getPrice(),
                    product.getDescription()
            };
            productTableModel.addRow(rowData);
        }
    }

    private void showAddSupplierDialog() {
        // Clear form
        clearForm();

        // // Clear product list
        // selectedProducts.clear();
        // updateProductTable();

        // Generate new ID
        String newId = generateNewSupplierId();
        idField.setText(newId);
        idField.setEditable(false);

        // Set title and show dialog
        supplierFormDialog.setTitle("Thêm nhà cung cấp mới");
        supplierFormDialog.setVisible(true);
    }

    private void showEditSupplierDialog() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp cần sửa", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Lấy dữ liệu từ hàng đã chọn
        String supplierId = supplierTable.getValueAt(selectedRow, 0).toString();
        SupplierDTO supplier = supplierController.getSupplierById(supplierId);

        if (supplier != null) {
            // Set form fields
            idField.setText(supplier.getSupplierId());
            idField.setEditable(false);
            nameField.setText(supplier.getSupplierName());
            addressField.setText(supplier.getAddress());
            phoneField.setText(supplier.getPhone());
            emailField.setText(supplier.getEmail());

            // Set product list
            selectedProducts.clear();
            selectedProducts.addAll(supplier.getProducts());
            updateProductTable();

            // Set title and show dialog
            supplierFormDialog.setTitle("Cập nhật thông tin nhà cung cấp");
            supplierFormDialog.setVisible(true);
        }
    }

    private String generateNewSupplierId() {
        // Lấy tất cả nhà cung cấp
        List<SupplierDTO> suppliers = supplierController.getAllSuppliers();

        // Tìm ID lớn nhất hiện tại
        int maxId = 0;
        for (SupplierDTO supplier : suppliers) {
            String id = supplier.getSupplierId();
            // Nếu ID có định dạng NCC001, lấy phần số
            if (id != null && id.startsWith("NCC")) {
                try {
                    int idNumber = Integer.parseInt(id.substring(3));
                    if (idNumber > maxId) {
                        maxId = idNumber;
                    }
                } catch (NumberFormatException e) {
                    // Bỏ qua ID không đúng định dạng
                }
            }
        }

        // Tạo ID mới bằng cách tăng maxId lên 1 và định dạng
        return String.format("NCC%03d", maxId + 1);
    }

    private void refreshSupplierData() {
        // Clear table
        tableModel.setRowCount(0);

        // Get all suppliers
        List<SupplierDTO> suppliers = supplierController.getAllSuppliers();

        // Add suppliers to table
        displaySuppliers(suppliers);
    }

    private void displaySupplierData(SupplierDTO supplier) {
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

        // Clear selected products
        selectedProducts.clear();
        updateProductTable();
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

        SupplierDTO supplier = new SupplierDTO(id, name, address, phone, email);

        // Add supplier without products first to get the supplier ID
        boolean success = supplierController.addSupplier(supplier);

        if (success && !selectedProducts.isEmpty()) {
            // Add products to supplier
            for (SupplierProductDTO product : selectedProducts) {
                product.setSupplierId(id);
                // Assign proper product ID
                String productId = supplierController.generateNewSupplierProductId(id);
                product.setProductId(productId);

                supplierController.addSupplierProduct(product);
            }
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Thêm nhà cung cấp thành công!");
            supplierFormDialog.dispose();
            refreshSupplierData();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm nhà cung cấp thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSupplier() {
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

        SupplierDTO supplier = new SupplierDTO(id, name, address, phone, email);

        // Update supplier with products
        boolean success = supplierController.updateSupplier(supplier);

        if (success) {
            // Update products
            supplierController.deleteAllSupplierProducts(id);

            for (SupplierProductDTO product : selectedProducts) {
                product.setSupplierId(id);

                // If it's a temp ID, generate a new one
                if (product.getProductId().startsWith("TEMP")) {
                    String productId = supplierController.generateNewSupplierProductId(id);
                    product.setProductId(productId);
                }

                supplierController.addSupplierProduct(product);
            }

            JOptionPane.showMessageDialog(this, "Cập nhật nhà cung cấp thành công!");
            supplierFormDialog.dispose();
            refreshSupplierData();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật nhà cung cấp thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp cần xóa", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String supplierId = supplierTable.getValueAt(selectedRow, 0).toString();
        String supplierName = supplierTable.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa nhà cung cấp " + supplierName + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = supplierController.deleteSupplier(supplierId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa nhà cung cấp thành công", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshSupplierData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Xóa nhà cung cấp thất bại. Nhà cung cấp này có thể đang được sử dụng.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInput() {
        // Validate name
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên nhà cung cấp", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        // Validate name length
        if (nameField.getText().trim().length() > 100) {
            JOptionPane.showMessageDialog(this, "Tên nhà cung cấp không được vượt quá 100 ký tự", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        // Validate phone
        String phone = phoneField.getText().trim();
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            phoneField.requestFocus();
            return false;
        }

        if (!phone.matches("\\d{10,11}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải có 10-11 chữ số", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            phoneField.requestFocus();
            return false;
        }

        // Validate address length
        String address = addressField.getText().trim();
        if (!address.isEmpty() && address.length() > 255) {
            JOptionPane.showMessageDialog(this, "Địa chỉ không được vượt quá 255 ký tự", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            addressField.requestFocus();
            return false;
        }

        // Validate email
        String email = emailField.getText().trim();
        if (!email.isEmpty()) {
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                JOptionPane.showMessageDialog(this, "Email không hợp lệ", "Lỗi dữ liệu",
                        JOptionPane.ERROR_MESSAGE);
                emailField.requestFocus();
                return false;
            }

            if (email.length() > 100) {
                JOptionPane.showMessageDialog(this, "Email không được vượt quá 100 ký tự", "Lỗi dữ liệu",
                        JOptionPane.ERROR_MESSAGE);
                emailField.requestFocus();
                return false;
            }
        }

        return true;
    }

    // Phương thức hiển thị chi tiết nhà cung cấp
    private void showSupplierDetail(String supplierId) {
        SupplierDTO supplier = supplierController.getSupplierById(supplierId);
        if (supplier != null) {
            JDialog detailDialog = new JDialog();
            detailDialog.setTitle("Chi tiết nhà cung cấp");
            detailDialog.setSize(800, 600);
            detailDialog.setLocationRelativeTo(this);
            detailDialog.setModal(true);

            // Tạo supplier detail panel
            SupplierDetailPanel detailPanel = new SupplierDetailPanel(supplier, supplierController);

            detailDialog.add(detailPanel);
            detailDialog.setVisible(true);
        }
    }
}