package GUI;

import BUS.SupplierBUS;
import DTO.SupplierDTO;
import BUS.ProductBUS;
import GUI.components.SupplierDetailPanel;
import DTO.SupplierProductDTO;

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
        addBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        addBtn.addActionListener(e -> showAddSupplierDialog());

        // Nút xóa - giảm kích thước
        JButton deleteBtn = createOutlineButton("Xóa", dangerColor);
        deleteBtn.setMargin(new Insets(3, 8, 3, 8));
        deleteBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        deleteBtn.addActionListener(e -> deleteSupplier());

        // Gán deleteBtn vào biến thành viên để sử dụng ở nơi khác
        this.deleteButton = deleteBtn;

        // Nút chỉnh sửa - giảm kích thước
        JButton editBtn = createOutlineButton("Chỉnh sửa", warningColor);
        editBtn.setMargin(new Insets(3, 8, 3, 8));
        editBtn.setFont(new Font("Arial", Font.PLAIN, 12));
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
        exportBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        // Nút Nhập Excel - giảm kích thước
        JButton importBtn = createOutlineButton("Nhập Excel", new Color(108, 117, 125));
        importBtn.setMargin(new Insets(3, 8, 3, 8));
        importBtn.setFont(new Font("Arial", Font.PLAIN, 12));

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
        searchButton.setFont(new Font("Arial", Font.PLAIN, 12));
        searchButton.addActionListener(e -> searchSuppliers());

        // Nút làm mới - giảm kích thước
        JButton refreshButton = createOutlineButton("Làm mới", new Color(23, 162, 184));
        refreshButton.setMargin(new Insets(3, 8, 3, 8));
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
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
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(new Color(33, 37, 41));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));

        statusPanel.add(statusLabel, BorderLayout.WEST);

        // Thêm nút xem chi tiết ở góc phải
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        JButton viewDetailButton = createOutlineButton("Xem chi tiết nhà cung cấp", primaryColor);
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
        // Tạo dialog
        supplierFormDialog = new JDialog();
        supplierFormDialog.setTitle("Thông tin nhà cung cấp");
        supplierFormDialog.setSize(700, 600);
        supplierFormDialog.setLocationRelativeTo(this);
        supplierFormDialog.setModal(true);
        supplierFormDialog.setResizable(false);

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

        // Mã nhà cung cấp
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel idLabel = new JLabel("Mã nhà cung cấp:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        idField = new JTextField(20);
        idField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(idField, gbc);

        // Tên nhà cung cấp
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Tên nhà cung cấp:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(nameField, gbc);

        // Địa chỉ
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel addressLabel = new JLabel("Địa chỉ:");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(addressLabel, gbc);

        gbc.gridx = 1;
        addressField = new JTextField(20);
        addressField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(addressField, gbc);

        // Điện thoại
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel phoneLabel = new JLabel("Điện thoại:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        phoneField = new JTextField(20);
        phoneField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(phoneField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(emailField, gbc);

        // Danh sách sản phẩm của nhà cung cấp
        JPanel productListPanel = new JPanel(new BorderLayout(5, 5));
        productListPanel.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm của nhà cung cấp"));
        productListPanel.setBackground(lightColor);

        // Tạo bảng sản phẩm
        String[] productColumns = { "Tên sản phẩm", "Đơn vị tính", "Giá", "Mô tả" };
        productTableModel = new DefaultTableModel(productColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(productTableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getTableHeader().setReorderingAllowed(false);
        productTable.setFont(new Font("Arial", Font.PLAIN, 12));
        productTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setPreferredSize(new Dimension(400, 150));

        // Panel nút thêm/xóa sản phẩm
        JPanel productButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        productButtonPanel.setBackground(lightColor);

        JButton addProductButton = createOutlineButton("Thêm sản phẩm", primaryColor);
        addProductButton.setPreferredSize(new Dimension(120, 30));
        addProductButton.setFont(new Font("Arial", Font.PLAIN, 12));
        addProductButton.addActionListener(e -> addProductToSupplier());

        JButton removeProductButton = createOutlineButton("Xóa sản phẩm", dangerColor);
        removeProductButton.setPreferredSize(new Dimension(120, 30));
        removeProductButton.setFont(new Font("Arial", Font.PLAIN, 12));
        removeProductButton.addActionListener(e -> removeProductFromSupplier());

        productButtonPanel.add(addProductButton);
        productButtonPanel.add(removeProductButton);

        productListPanel.add(scrollPane, BorderLayout.CENTER);
        productListPanel.add(productButtonPanel, BorderLayout.SOUTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);

        JButton saveButton = createOutlineButton("Lưu", successColor);
        saveButton.addActionListener(e -> {
            if (idField.getText().isEmpty()) {
                addSupplier();
            } else {
                updateSupplier();
            }
        });

        JButton cancelButton = createOutlineButton("Hủy", dangerColor);
        cancelButton.addActionListener(e -> supplierFormDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add components to main panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(formPanel, BorderLayout.NORTH);
        contentPanel.add(productListPanel, BorderLayout.CENTER);

        dialogPanel.add(contentPanel, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        supplierFormDialog.add(dialogPanel);
    }

    // Thêm sản phẩm vào danh sách
    private void addProductToSupplier() {
        JDialog dialog = new JDialog(supplierFormDialog, "Thêm sản phẩm của nhà cung cấp", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(supplierFormDialog);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField nameField = new JTextField(20);
        JTextField unitField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(3, 20);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Tên sản phẩm:"), gbc);

        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Đơn vị tính:"), gbc);

        gbc.gridx = 1;
        panel.add(unitField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Giá:"), gbc);

        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Mô tả:"), gbc);

        gbc.gridx = 1;
        panel.add(descScrollPane, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate input
                if (nameField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên sản phẩm!", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double price = 0;
                try {
                    if (!priceField.getText().trim().isEmpty()) {
                        price = Double.parseDouble(priceField.getText().trim());
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Giá không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Tạo sản phẩm mới
                String idPrefix = "TEMP"; // ID tạm thời, sẽ được thay thế khi lưu vào DB
                if (!idField.getText().trim().isEmpty()) {
                    idPrefix = idField.getText().trim();
                }

                SupplierProductDTO product = new SupplierProductDTO();
                product.setProductId("TEMP" + System.currentTimeMillis()); // ID tạm thời
                product.setSupplierId(idPrefix);
                product.setProductName(nameField.getText().trim());
                product.setUnit(unitField.getText().trim());
                product.setDescription(descriptionArea.getText().trim());
                product.setPrice(price);

                // Thêm vào danh sách sản phẩm tạm thời
                selectedProducts.add(product);
                updateProductTable();

                dialog.dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
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

        // Clear product list
        selectedProducts.clear();
        updateProductTable();

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