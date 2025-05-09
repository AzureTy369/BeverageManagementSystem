package GUI.components;

import BUS.SupplierBUS;
import BUS.ProductCategoryBUS;
import DTO.SupplierDTO;
import DTO.SupplierProductDTO;
import DTO.ProductCategoryDTO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel hiển thị thông tin chi tiết của nhà cung cấp, bao gồm danh sách sản
 * phẩm
 */
public class SupplierDetailPanel extends JPanel {
    private SupplierDTO supplier;
    private SupplierBUS supplierController;

    private JLabel titleLabel;
    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel phoneLabel;
    private JLabel addressLabel;
    private JLabel emailLabel;

    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JButton addProductButton;
    private JButton editProductButton;
    private JButton removeProductButton;

    private final Color primaryColor = new Color(0, 123, 255);
    private final Color dangerColor = new Color(220, 53, 69);
    private final Color successColor = new Color(40, 167, 69);
    private final Color lightColor = new Color(248, 249, 250);

    public SupplierDetailPanel(SupplierDTO supplier, SupplierBUS supplierController) {
        this.supplier = supplier;
        this.supplierController = supplierController;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(lightColor);

        createComponents();
        displaySupplierInfo();
    }

    private void createComponents() {
        // Panel thông tin nhà cung cấp
        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        infoPanel.setBackground(lightColor);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin nhà cung cấp"));

        titleLabel = new JLabel("Chi tiết nhà cung cấp", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        idLabel = new JLabel("Mã nhà cung cấp: ");
        nameLabel = new JLabel("Tên nhà cung cấp: ");
        phoneLabel = new JLabel("Số điện thoại: ");
        addressLabel = new JLabel("Địa chỉ: ");
        emailLabel = new JLabel("Email: ");

        infoPanel.add(idLabel);
        infoPanel.add(new JLabel());
        infoPanel.add(nameLabel);
        infoPanel.add(new JLabel());
        infoPanel.add(phoneLabel);
        infoPanel.add(new JLabel());
        infoPanel.add(addressLabel);
        infoPanel.add(new JLabel());
        infoPanel.add(emailLabel);
        infoPanel.add(new JLabel());

        // Panel danh sách sản phẩm
        JPanel productPanel = new JPanel(new BorderLayout(5, 5));
        productPanel.setBackground(lightColor);
        productPanel.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm của nhà cung cấp"));

        String[] columns = { "Mã sản phẩm", "Tên sản phẩm", "Danh mục", "Đơn vị tính", "Giá", "Mô tả" };
        productTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(productTableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setPreferredSize(new Dimension(500, 200));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(lightColor);

        addProductButton = new JButton("Thêm sản phẩm");
        addProductButton.setBackground(primaryColor);
        addProductButton.setForeground(Color.WHITE);

        editProductButton = new JButton("Sửa sản phẩm");
        editProductButton.setBackground(successColor);
        editProductButton.setForeground(Color.WHITE);

        removeProductButton = new JButton("Xóa sản phẩm");
        removeProductButton.setBackground(dangerColor);
        removeProductButton.setForeground(Color.WHITE);

        buttonPanel.add(addProductButton);
        buttonPanel.add(editProductButton);
        buttonPanel.add(removeProductButton);

        productPanel.add(scrollPane, BorderLayout.CENTER);
        productPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Thêm các thành phần vào panel chính
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(lightColor);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(infoPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(productPanel, BorderLayout.CENTER);

        // Xử lý sự kiện
        addProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddProductDialog();
            }
        });

        editProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedProduct();
            }
        });

        removeProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedProduct();
            }
        });
    }

    private void displaySupplierInfo() {
        if (supplier != null) {
            idLabel.setText("Mã nhà cung cấp: " + supplier.getSupplierId());
            nameLabel.setText("Tên nhà cung cấp: " + supplier.getSupplierName());
            phoneLabel.setText("Số điện thoại: " + supplier.getPhone());
            addressLabel.setText("Địa chỉ: " + supplier.getAddress());
            emailLabel.setText("Email: " + supplier.getEmail());

            displayProductList();
        }
    }

    private void displayProductList() {
        productTableModel.setRowCount(0);

        if (supplier.getProducts() != null) {
            for (SupplierProductDTO product : supplier.getProducts()) {
                Object[] rowData = {
                        product.getProductId(),
                        product.getProductName(),
                        product.getCategoryName(),
                        product.getUnit(),
                        product.getPrice(),
                        product.getDescription()
                };
                productTableModel.addRow(rowData);
            }
        }
    }

    private void showAddProductDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Thêm sản phẩm cho nhà cung cấp");
        dialog.setModal(true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField nameField = new JTextField(20);

        // ComboBox cho danh mục
        JComboBox<ProductCategoryDTO> categoryComboBox = new JComboBox<>();
        loadProductCategories(categoryComboBox, null);

        // ComboBox cho đơn vị tính
        JComboBox<String> unitComboBox = new JComboBox<>(new String[] { "Chai", "Lon", "Hộp", "Thùng" });

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
        panel.add(new JLabel("Danh mục:"), gbc);

        gbc.gridx = 1;
        panel.add(categoryComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Đơn vị tính:"), gbc);

        gbc.gridx = 1;
        panel.add(unitComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Giá:"), gbc);

        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
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

                ProductCategoryDTO selectedCategory = (ProductCategoryDTO) categoryComboBox.getSelectedItem();
                if (selectedCategory == null) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng chọn danh mục sản phẩm!", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double price = 0;
                try {
                    if (!priceField.getText().trim().isEmpty()) {
                        price = Double.parseDouble(priceField.getText().trim());
                        if (price <= 0) {
                            JOptionPane.showMessageDialog(dialog, "Giá phải lớn hơn 0!", "Lỗi",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Giá không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Tạo sản phẩm mới
                String productName = nameField.getText().trim();
                String unit = (String) unitComboBox.getSelectedItem();
                String description = descriptionArea.getText().trim();
                String categoryId = selectedCategory.getCategoryId();
                String categoryName = selectedCategory.getCategoryName();

                // Tạo đối tượng SupplierProductDTO mới
                SupplierProductDTO newProduct = new SupplierProductDTO();
                newProduct.setSupplierId(supplier.getSupplierId());
                newProduct.setProductName(productName);
                newProduct.setUnit(unit);
                newProduct.setDescription(description);
                newProduct.setPrice(price);
                newProduct.setCategoryId(categoryId);
                newProduct.setCategoryName(categoryName);

                // Tạo mã sản phẩm mới
                String productId = supplierController.generateNewSupplierProductId(supplier.getSupplierId());
                newProduct.setProductId(productId);

                // Thêm sản phẩm vào nhà cung cấp
                boolean success = supplierController.addSupplierProduct(newProduct);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Thêm sản phẩm thành công!");
                    // Cập nhật lại thông tin nhà cung cấp
                    supplier = supplierController.getSupplierById(supplier.getSupplierId());
                    displayProductList();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Thêm sản phẩm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
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

    private void editSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần sửa!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String productId = (String) productTableModel.getValueAt(selectedRow, 0);
        SupplierProductDTO product = supplier.getProductById(productId);

        if (product == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog();
        dialog.setTitle("Sửa thông tin sản phẩm");
        dialog.setModal(true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel idLabel = new JLabel("Mã sản phẩm: " + product.getProductId());
        JTextField nameField = new JTextField(product.getProductName(), 20);

        // ComboBox cho danh mục
        JComboBox<ProductCategoryDTO> categoryComboBox = new JComboBox<>();
        loadProductCategories(categoryComboBox, product.getCategoryId());

        // ComboBox cho đơn vị tính
        JComboBox<String> unitComboBox = new JComboBox<>(new String[] { "Chai", "Lon", "Hộp", "Thùng" });

        // Chọn đơn vị tính mặc định nếu có
        if (product.getUnit() != null && !product.getUnit().isEmpty()) {
            for (int i = 0; i < unitComboBox.getItemCount(); i++) {
                if (unitComboBox.getItemAt(i).equals(product.getUnit())) {
                    unitComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        JTextField priceField = new JTextField(String.valueOf(product.getPrice()), 20);
        JTextArea descriptionArea = new JTextArea(product.getDescription(), 3, 20);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(idLabel, gbc);

        gbc.gridy = 1;
        panel.add(new JLabel("Tên sản phẩm:"), gbc);

        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Danh mục:"), gbc);

        gbc.gridx = 1;
        panel.add(categoryComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Đơn vị tính:"), gbc);

        gbc.gridx = 1;
        panel.add(unitComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Giá:"), gbc);

        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
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

                ProductCategoryDTO selectedCategory = (ProductCategoryDTO) categoryComboBox.getSelectedItem();
                if (selectedCategory == null) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng chọn danh mục sản phẩm!", "Lỗi",
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

                // Cập nhật thông tin sản phẩm
                product.setProductName(nameField.getText().trim());
                product.setUnit((String) unitComboBox.getSelectedItem());
                product.setDescription(descriptionArea.getText().trim());
                product.setPrice(price);
                product.setCategoryId(selectedCategory.getCategoryId());
                product.setCategoryName(selectedCategory.getCategoryName());

                // Cập nhật sản phẩm
                boolean success = supplierController.updateSupplierProduct(product);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật sản phẩm thành công!");
                    // Cập nhật lại thông tin nhà cung cấp
                    supplier = supplierController.getSupplierById(supplier.getSupplierId());
                    displayProductList();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật sản phẩm thất bại!", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
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

    /**
     * Tải danh sách danh mục sản phẩm vào combo box và chọn danh mục mặc định
     */
    private void loadProductCategories(JComboBox<ProductCategoryDTO> comboBox, String selectedCategoryId) {
        comboBox.removeAllItems();

        // Sử dụng ProductCategoryBUS để lấy danh sách danh mục
        ProductCategoryBUS categoryController = new ProductCategoryBUS();
        List<ProductCategoryDTO> categories = categoryController.getAllCategories();

        ProductCategoryDTO selectedCategory = null;

        for (ProductCategoryDTO category : categories) {
            comboBox.addItem(category);

            if (category.getCategoryId().equals(selectedCategoryId)) {
                selectedCategory = category;
            }
        }

        if (selectedCategory != null) {
            comboBox.setSelectedItem(selectedCategory);
        }
    }

    private void removeSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String productId = (String) productTableModel.getValueAt(selectedRow, 0);
        String productName = (String) productTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận xóa sản phẩm " + productName + " khỏi danh sách sản phẩm của nhà cung cấp này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = supplierController.deleteSupplierProduct(productId, supplier.getSupplierId());

            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công!");
                // Cập nhật lại thông tin nhà cung cấp
                supplier = supplierController.getSupplierById(supplier.getSupplierId());
                displayProductList();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa sản phẩm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Cập nhật thông tin nhà cung cấp
    public void updateSupplier(SupplierDTO supplier) {
        this.supplier = supplier;
        displaySupplierInfo();
    }
}