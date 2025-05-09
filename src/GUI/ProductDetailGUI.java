package GUI;

import BUS.ProductBUS;
import BUS.ProductDetailBUS;
import DTO.ProductDTO;
import DTO.ProductDetailDTO;
import GUI.utils.ButtonHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class ProductDetailGUI extends JPanel {
    private ProductDetailBUS detailController;
    private ProductBUS productController;

    private JTable detailTable;
    private DefaultTableModel detailTableModel;

    private JTextField detailIdField;
    private JComboBox<ProductDTO> productComboBox;
    private JTextField sizeField;
    private JTextField priceField;

    private JButton addDetailButton;
    private JButton editDetailButton;
    private JButton deleteDetailButton;

    private JTextField detailSearchField;

    private JPanel advancedSearchPanel;

    // Colors
    private final Color primaryColor = new Color(0, 123, 255);
    private final Color successColor = new Color(40, 167, 69);
    private final Color warningColor = new Color(255, 193, 7);
    private final Color dangerColor = new Color(220, 53, 69);
    private final Color lightColor = new Color(248, 249, 250);

    // Form dialog
    private JDialog detailFormDialog;

    public ProductDetailGUI(ProductDetailBUS detailController,
            ProductBUS productController) {
        this.detailController = detailController;
        this.productController = productController;

        setLayout(new BorderLayout());
        setBackground(lightColor);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        createAdvancedSearchPanel();
        createGUI();
        createDetailFormDialog();
        refreshAllProductDetails();
    }

    private void createGUI() {
        // Panel tiêu đề ở trên cùng
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(lightColor);

        // Panel tìm kiếm và thêm mới
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);

        detailSearchField = new JTextField(20);
        detailSearchField.setFont(new Font("Arial", Font.PLAIN, 16));
        detailSearchField.setBorder(BorderFactory.createCompoundBorder(
                detailSearchField.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        detailSearchField.setToolTipText("Tìm kiếm chi tiết sản phẩm");
        detailSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchProductDetails();
                }
            }
        });

        JButton searchButton = ButtonHelper.createButton("Tìm kiếm", primaryColor);
        searchButton.addActionListener(e -> searchProductDetails());

        addDetailButton = ButtonHelper.createButton("Thêm mới", successColor);
        addDetailButton.addActionListener(e -> showAddDetailDialog());

        editDetailButton = ButtonHelper.createButton("Sửa", warningColor);
        editDetailButton.addActionListener(e -> showEditDetailDialog());

        deleteDetailButton = ButtonHelper.createButton("Xóa", dangerColor);
        deleteDetailButton.addActionListener(e -> deleteProductDetail());

        searchPanel.add(new JLabel("Tìm kiếm: "));
        searchPanel.add(detailSearchField);
        searchPanel.add(searchButton);
        searchPanel.add(addDetailButton);
        searchPanel.add(editDetailButton);
        searchPanel.add(deleteDetailButton);

        // Panel trên cùng chứa tiêu đề và tìm kiếm
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titlePanel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);

        // Panel chính ở giữa
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        // Thêm panel tìm kiếm nâng cao
        JPanel advancedSearchBordered = new JPanel(new BorderLayout());
        advancedSearchBordered.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        advancedSearchBordered.setBackground(new Color(240, 243, 247));
        advancedSearchBordered.add(advancedSearchPanel, BorderLayout.CENTER);

        // Tạo bảng chi tiết sản phẩm
        createDetailTable();
        JScrollPane tableScrollPane = new JScrollPane(detailTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Thêm các thành phần vào panel chính
        centerPanel.add(advancedSearchBordered, BorderLayout.NORTH);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Thêm tất cả vào panel chính
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void createDetailTable() {
        String[] columnNames = { "Mã chi tiết", "Tên sản phẩm", "Kích thước", "Giá" };
        detailTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        detailTable = new JTable(detailTableModel);
        detailTable.setFont(new Font("Arial", Font.PLAIN, 16));
        detailTable.setRowHeight(25);
        detailTable.setGridColor(new Color(230, 230, 230));
        detailTable.setSelectionBackground(new Color(173, 216, 230));
        detailTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        detailTable.getTableHeader().setBackground(primaryColor);
        detailTable.getTableHeader().setForeground(Color.BLACK);
        detailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column width
        detailTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        detailTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        detailTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        detailTable.getColumnModel().getColumn(3).setPreferredWidth(150);
    }

    private void createDetailFormDialog() {
        // Tạo dialog
        detailFormDialog = new JDialog();
        detailFormDialog.setTitle("Thông tin chi tiết sản phẩm");
        detailFormDialog.setSize(500, 400);
        detailFormDialog.setLocationRelativeTo(this);
        detailFormDialog.setModal(true);
        detailFormDialog.setResizable(false);

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

        // Mã chi tiết
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel idLabel = new JLabel("Mã chi tiết:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        detailIdField = new JTextField(20);
        detailIdField.setFont(new Font("Arial", Font.PLAIN, 16));
        detailIdField.setEditable(false);
        formPanel.add(detailIdField, gbc);

        // Sản phẩm
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel productLabel = new JLabel("Sản phẩm:");
        productLabel.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(productLabel, gbc);

        gbc.gridx = 1;
        productComboBox = new JComboBox<>();
        productComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(productComboBox, gbc);

        // Kích thước
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel sizeLabel = new JLabel("Kích thước:");
        sizeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(sizeLabel, gbc);

        gbc.gridx = 1;
        sizeField = new JTextField(20);
        sizeField.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(sizeField, gbc);

        // Giá
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel priceLabel = new JLabel("Giá:");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(priceLabel, gbc);

        gbc.gridx = 1;
        priceField = new JTextField(20);
        priceField.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(priceField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton saveButton = ButtonHelper.createButton("Lưu", successColor);
        saveButton.addActionListener(e -> {
            if (detailIdField.isEditable()) {
                addProductDetail();
            } else {
                updateProductDetail();
            }
        });

        JButton cancelButton = ButtonHelper.createButton("Hủy", dangerColor);
        cancelButton.addActionListener(e -> detailFormDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        dialogPanel.add(formPanel, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        detailFormDialog.add(dialogPanel);
    }

    private void showAddDetailDialog() {
        // Reset form fields
        detailIdField.setText(detailController.generateNewDetailId());
        detailIdField.setEditable(true);
        sizeField.setText("");
        priceField.setText("");

        // Load products
        loadProducts();

        // Show dialog
        detailFormDialog.setTitle("Thêm chi tiết sản phẩm");
        detailFormDialog.setVisible(true);
    }

    private void showEditDetailDialog() {
        // Check if a row is selected
        int selectedRow = detailTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn chi tiết sản phẩm để chỉnh sửa",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the selected detail ID
        String detailId = detailTable.getValueAt(selectedRow, 0).toString();
        ProductDetailDTO detail = detailController.getProductDetailById(detailId);

        if (detail == null) {
            JOptionPane.showMessageDialog(this,
                    "Không thể tìm thấy thông tin chi tiết sản phẩm",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Đặt dữ liệu vào form
        detailIdField.setText(detail.getDetailId());
        detailIdField.setEditable(false);
        sizeField.setText(detail.getSize());
        priceField.setText(String.valueOf(detail.getPrice()));

        // Load sản phẩm
        loadProducts();

        // Chọn sản phẩm hiện tại
        for (int i = 0; i < productComboBox.getItemCount(); i++) {
            ProductDTO product = productComboBox.getItemAt(i);
            if (product.getProductId().equals(detail.getProductId())) {
                productComboBox.setSelectedIndex(i);
                break;
            }
        }

        // Show dialog
        detailFormDialog.setTitle("Chỉnh sửa chi tiết sản phẩm");
        detailFormDialog.setVisible(true);
    }

    private void loadProducts() {
        productComboBox.removeAllItems();
        List<ProductDTO> products = productController.getAllProducts();
        for (ProductDTO product : products) {
            productComboBox.addItem(product);
        }
    }

    private void searchProductDetails() {
        String keyword = detailSearchField.getText().trim();
        List<ProductDetailDTO> details = detailController.searchProductDetails(keyword);
        displayProductDetails(details);
    }

    private void addProductDetail() {
        // Validate input
        if (!validateDetailInput()) {
            return;
        }

        // Get the detail from form
        ProductDetailDTO detail = getDetailFromForm();

        // Add the detail
        boolean success = detailController.addProductDetail(detail);

        if (success) {
            JOptionPane.showMessageDialog(detailFormDialog,
                    "Thêm chi tiết sản phẩm thành công",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            detailFormDialog.dispose();
            refreshAllProductDetails();
        } else {
            JOptionPane.showMessageDialog(detailFormDialog,
                    "Không thể thêm chi tiết sản phẩm",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProductDetail() {
        // Validate input
        if (!validateDetailInput()) {
            return;
        }

        // Get the detail from form
        ProductDetailDTO detail = getDetailFromForm();

        // Update the detail
        boolean success = detailController.updateProductDetail(detail);

        if (success) {
            JOptionPane.showMessageDialog(detailFormDialog,
                    "Cập nhật chi tiết sản phẩm thành công",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            detailFormDialog.dispose();
            refreshAllProductDetails();
        } else {
            JOptionPane.showMessageDialog(detailFormDialog,
                    "Không thể cập nhật chi tiết sản phẩm",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProductDetail() {
        // Check if a row is selected
        int selectedRow = detailTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn chi tiết sản phẩm để xóa",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm delete
        int option = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa chi tiết sản phẩm này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            // Get the selected detail ID
            String detailId = detailTable.getValueAt(selectedRow, 0).toString();

            // Delete the detail
            boolean success = detailController.deleteProductDetail(detailId);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Xóa chi tiết sản phẩm thành công",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshAllProductDetails();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể xóa chi tiết sản phẩm",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private ProductDetailDTO getDetailFromForm() {
        ProductDetailDTO detail = new ProductDetailDTO();

        // Lấy thông tin cơ bản
        detail.setDetailId(detailIdField.getText().trim());
        detail.setSize(sizeField.getText().trim());

        // Lấy giá
        try {
            double price = Double.parseDouble(priceField.getText().trim().replace(",", ""));
            detail.setPrice(price);
        } catch (NumberFormatException e) {
            detail.setPrice(0);
        }

        // Lấy sản phẩm
        ProductDTO selectedProduct = (ProductDTO) productComboBox.getSelectedItem();
        if (selectedProduct != null) {
            detail.setProductId(selectedProduct.getProductId());
            detail.setProductName(selectedProduct.getProductName());
        }

        return detail;
    }

    private boolean validateDetailInput() {
        // Kiểm tra mã chi tiết
        if (detailIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(detailFormDialog,
                    "Vui lòng nhập mã chi tiết sản phẩm",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            detailIdField.requestFocus();
            return false;
        }

        // Kiểm tra giá
        try {
            String priceText = priceField.getText().trim().replace(",", "");
            if (priceText.isEmpty()) {
                JOptionPane.showMessageDialog(detailFormDialog,
                        "Vui lòng nhập giá sản phẩm",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                priceField.requestFocus();
                return false;
            }

            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                JOptionPane.showMessageDialog(detailFormDialog,
                        "Giá sản phẩm phải lớn hơn 0",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                priceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(detailFormDialog,
                    "Giá sản phẩm không hợp lệ",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
            return false;
        }

        // Kiểm tra sản phẩm
        if (productComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(detailFormDialog,
                    "Vui lòng chọn sản phẩm",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public void refreshAllProductDetails() {
        // Clear table
        detailTableModel.setRowCount(0);

        // Get all details
        List<ProductDetailDTO> details = detailController.getAllProductDetails();

        // Add details to table
        for (ProductDetailDTO detail : details) {
            detailTableModel.addRow(new Object[] {
                    detail.getDetailId(),
                    detail.getProductName(),
                    detail.getSize(),
                    String.format("%,.0f VNĐ", detail.getPrice())
            });
        }
    }

    public void showDetailsForProduct(String productId) {
        // Clear table
        detailTableModel.setRowCount(0);

        // Get details for product
        List<ProductDetailDTO> details = detailController.getProductDetailsByProduct(productId);

        // Add details to table
        for (ProductDetailDTO detail : details) {
            detailTableModel.addRow(new Object[] {
                    detail.getDetailId(),
                    detail.getProductName(),
                    detail.getSize(),
                    String.format("%,.0f VNĐ", detail.getPrice())
            });
        }
    }

    // Hiển thị danh sách chi tiết sản phẩm vào bảng
    private void displayProductDetails(List<ProductDetailDTO> details) {
        // Clear table
        detailTableModel.setRowCount(0);

        // Add details to table
        for (ProductDetailDTO detail : details) {
            detailTableModel.addRow(new Object[] {
                    detail.getDetailId(),
                    detail.getProductName(),
                    detail.getSize(),
                    String.format("%,.0f VNĐ", detail.getPrice())
            });
        }
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
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
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
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        conditionsPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(15);
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        conditionsPanel.add(nameField, gbc);

        // Kích thước
        gbc.gridx = 2;
        JLabel sizeLabel = new JLabel("Kích thước:");
        sizeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        conditionsPanel.add(sizeLabel, gbc);

        gbc.gridx = 3;
        JTextField sizeField = new JTextField(15);
        sizeField.setFont(new Font("Arial", Font.PLAIN, 16));
        conditionsPanel.add(sizeField, gbc);

        // Giá
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel priceLabel = new JLabel("Giá (từ):");
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        conditionsPanel.add(priceLabel, gbc);

        gbc.gridx = 1;
        JTextField priceFromField = new JTextField(15);
        priceFromField.setFont(new Font("Arial", Font.PLAIN, 16));
        conditionsPanel.add(priceFromField, gbc);

        // Giá đến
        gbc.gridx = 2;
        JLabel priceToLabel = new JLabel("Giá (đến):");
        priceToLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        conditionsPanel.add(priceToLabel, gbc);

        gbc.gridx = 3;
        JTextField priceToField = new JTextField(15);
        priceToField.setFont(new Font("Arial", Font.PLAIN, 16));
        conditionsPanel.add(priceToField, gbc);

        advancedSearchPanel.add(conditionsPanel);

        // Panel chứa nút tìm kiếm và điều kiện kết hợp
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.setOpaque(false);

        JRadioButton andRadio = new JRadioButton("Tất cả điều kiện");
        andRadio.setFont(new Font("Arial", Font.PLAIN, 16));
        andRadio.setOpaque(false);
        andRadio.setSelected(true);

        JRadioButton orRadio = new JRadioButton("Bất kỳ điều kiện nào");
        orRadio.setFont(new Font("Arial", Font.PLAIN, 16));
        orRadio.setOpaque(false);

        ButtonGroup group = new ButtonGroup();
        group.add(andRadio);
        group.add(orRadio);

        JButton searchButton = ButtonHelper.createButton("Tìm kiếm", primaryColor);
        searchButton.addActionListener(e -> {
            // Lấy thông tin từ các ô tìm kiếm
            String name = nameField.getText().trim();
            String size = sizeField.getText().trim();
            String priceFrom = priceFromField.getText().trim();
            String priceTo = priceToField.getText().trim();
            boolean isAnd = andRadio.isSelected();

            // Thực hiện tìm kiếm nâng cao
            advancedSearchProductDetails(name, size, priceFrom, priceTo, isAnd);
        });

        JButton clearButton = ButtonHelper.createButton("Xóa tìm kiếm", new Color(108, 117, 125));
        clearButton.addActionListener(e -> {
            nameField.setText("");
            sizeField.setText("");
            priceFromField.setText("");
            priceToField.setText("");
            refreshAllProductDetails();
        });

        controlPanel.add(andRadio);
        controlPanel.add(orRadio);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(searchButton);
        controlPanel.add(clearButton);

        advancedSearchPanel.add(controlPanel);
    }

    private void advancedSearchProductDetails(String name, String size, String priceFrom, String priceTo,
            boolean isAnd) {
        // Tìm kiếm nâng cao - Phương thức này tự triển khai trực tiếp trong GUI
        List<ProductDetailDTO> allDetails = detailController.getAllProductDetails();
        List<ProductDetailDTO> results = new ArrayList<>();

        // Nếu tất cả điều kiện trống, trả về danh sách đầy đủ
        if (name.isEmpty() && size.isEmpty() && priceFrom.isEmpty() && priceTo.isEmpty()) {
            displayProductDetails(allDetails);
            return;
        }

        double minPrice = -1;
        double maxPrice = Double.MAX_VALUE;

        try {
            if (!priceFrom.isEmpty()) {
                minPrice = Double.parseDouble(priceFrom);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá tối thiểu không hợp lệ. Vui lòng nhập số.", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (!priceTo.isEmpty()) {
                maxPrice = Double.parseDouble(priceTo);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá tối đa không hợp lệ. Vui lòng nhập số.", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tìm kiếm theo các điều kiện
        for (ProductDetailDTO detail : allDetails) {
            boolean nameMatch = name.isEmpty() ||
                    (detail.getProductName() != null
                            && detail.getProductName().toLowerCase().contains(name.toLowerCase()));

            boolean sizeMatch = size.isEmpty() ||
                    (detail.getSize() != null && detail.getSize().toLowerCase().contains(size.toLowerCase()));

            boolean priceMatch = detail.getPrice() >= minPrice && detail.getPrice() <= maxPrice;

            if (isAnd) {
                // Phép AND - tất cả điều kiện phải đúng
                if (nameMatch && sizeMatch && priceMatch) {
                    results.add(detail);
                }
            } else {
                // Phép OR - chỉ cần một điều kiện đúng
                if (nameMatch || sizeMatch || priceMatch) {
                    results.add(detail);
                }
            }
        }

        // Hiển thị kết quả
        displayProductDetails(results);
    }
}