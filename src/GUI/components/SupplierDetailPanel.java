package GUI.components;

import DTO.SupplierDTO;
import DTO.SupplierProductDTO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel hiển thị thông tin chi tiết của nhà cung cấp, bao gồm danh sách sản
 * phẩm
 */
public class SupplierDetailPanel extends JPanel {
    private SupplierDTO supplier;

    private JLabel titleLabel;
    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel phoneLabel;
    private JLabel addressLabel;
    private JLabel emailLabel;

    private JTable productTable;
    private DefaultTableModel productTableModel;

    private Color lightColor = new Color(240, 240, 240);

    public SupplierDetailPanel(SupplierDTO supplier) {
        this.supplier = supplier;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));
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

        productPanel.add(scrollPane, BorderLayout.CENTER);

        // Thêm các thành phần vào panel chính
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(lightColor);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(infoPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(productPanel, BorderLayout.CENTER);
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

    // Cập nhật thông tin nhà cung cấp
    public void updateSupplier(SupplierDTO supplier) {
        this.supplier = supplier;
        displaySupplierInfo();
    }
}