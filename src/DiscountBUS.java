package BUS;

import DAO.*;
import DTO.*;
import GUI.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class DiscountBUS implements ActionListener {
    private ListDiscount listDc;
    private ListDetailDiscount listDetailDc;
    private DiscountPanel jPanelDiscount;
    private ProductDAO productDAO; // Thêm thể hiện ProductDAO
    private String discountID, discountName, begin, end;

    public DiscountBUS(ListDiscount listDc, DiscountPanel jPanelDiscount) {
        this.listDc = listDc;
        this.listDetailDc = new ListDetailDiscount();
        this.jPanelDiscount = jPanelDiscount;
        this.productDAO = new ProductDAO(); // Khởi tạo ProductDAO
    }

    public DiscountBUS() {
        this.listDetailDc = new ListDetailDiscount();
        this.productDAO = new ProductDAO(); // Khởi tạo ProductDAO
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jPanelDiscount.getButtonAdd()) {
            add();
        } else if (e.getSource() == jPanelDiscount.getButtonFix()) {
            fix();
        } else if (e.getSource() == jPanelDiscount.getButtonShowAll()) {
            showAll();
        } else if (e.getSource() == jPanelDiscount.getButtonSearchDiscount()) {
            jPanelDiscount.showList(searchByDate());
        } else if (e.getSource() == jPanelDiscount.getButtonAddDetail()) {
            addDetail();
        } else if (e.getSource() == jPanelDiscount.getButtonRemoveDetail()) {
            removeDetail();
        }
    }

    private void getDiscount() {
        discountName = jPanelDiscount.getTextFieldDiscountName().getText().trim();
        begin = jPanelDiscount.getTextFieldDiscountStartDay().getText().trim();
        end = jPanelDiscount.getTextFieldDiscountEndDay().getText().trim();
    }

    private void clear() {
        setEnabled(true);
        jPanelDiscount.getTextFieldDiscountID().setText("");
        jPanelDiscount.getTextFieldDiscountName().setText("");
        jPanelDiscount.getTextFieldDiscountStartDay().setText("");
        jPanelDiscount.getTextFieldDiscountEndDay().setText("");
        jPanelDiscount.getTextFieldProductPercent().setText("");
        jPanelDiscount.getTextFieldSearch().setText("");
        jPanelDiscount.getComboBoxProduct().setSelectedIndex(0);
        DefaultTableModel detailModel = (DefaultTableModel) jPanelDiscount.getJTableDiscountDetail().getModel();
        detailModel.setRowCount(0);
    }

    public void setEnabled(boolean bool) {
        jPanelDiscount.getButtonAdd().setEnabled(bool);
        jPanelDiscount.getTextFieldDiscountStartDay().setEnabled(bool);
    }

    private boolean valid() {
        if (discountName == null || discountName.isEmpty()) {
            JOptionPane.showMessageDialog(jPanelDiscount, "Tên khuyến mãi không được để trống.", "Thông báo", JOptionPane.ERROR_MESSAGE);
            jPanelDiscount.getTextFieldDiscountName().requestFocus();
            return false;
        } else if (begin == null || begin.isEmpty()) {
            JOptionPane.showMessageDialog(jPanelDiscount, "Ngày bắt đầu không được để trống.", "Thông báo", JOptionPane.ERROR_MESSAGE);
            jPanelDiscount.getTextFieldDiscountStartDay().requestFocus();
            return false;
        } else if (end == null || end.isEmpty()) {
            JOptionPane.showMessageDialog(jPanelDiscount, "Ngày kết thúc không được để trống.", "Thông báo", JOptionPane.ERROR_MESSAGE);
            jPanelDiscount.getTextFieldDiscountEndDay().requestFocus();
            return false;
        }
        return true;
    }

    private void add() {
        discountID = listDc.createDiscountID();

        if (jPanelDiscount.isFlat()) {
            return;
        }

        getDiscount();
        if (!valid()) {
            return;
        }

        listDc.add(discountID, discountName, begin, end);
        DiscountDAO.setAllDiscounts(listDc.getList());
        jPanelDiscount.showList(listDc.getList());
        clear();
    }

    private void fix() {
        if (jPanelDiscount.isFlat()) {
            return;
        }

        int selectedRow = jPanelDiscount.getJTableDiscount().getSelectedRow();
        getDiscount();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(jPanelDiscount, "Xin hãy chọn phiếu khuyến mãi cần sửa.", "Thông báo", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (!valid()) {
            return;
        }

        discountID = jPanelDiscount.getTextFieldDiscountID().getText().trim();
        listDc.fix(selectedRow, discountID, discountName, begin, end);
        DiscountDAO.setAllDiscounts(listDc.getList());
        jPanelDiscount.showList(listDc.getList());
        clear();
    }

    private void showAll() {
        clear();
        jPanelDiscount.showList(listDc.getList());
    }

    private ArrayList<Discount> searchByDate() {
        String info = jPanelDiscount.getTextFieldSearch().getText().trim();
        ArrayList<Discount> ls = listDc.search(info);

        if (ls.isEmpty()) {
            return null;
        }
        return ls;
    }

    public void loadDetails(String discountID) {
        listDetailDc.setList(discountID);
        DefaultTableModel model = (DefaultTableModel) jPanelDiscount.getJTableDiscountDetail().getModel();
        model.setRowCount(0);
        for (DetailDiscount detail : listDetailDc.getList()) {
            model.addRow(new Object[]{detail.getProductID(), detail.getPercentDiscount()});
        }
    }

    private void addDetail() {
        String discountID = jPanelDiscount.getTextFieldDiscountID().getText().trim();
        Product selectedProduct = (Product) jPanelDiscount.getComboBoxProduct().getSelectedItem();
        String percentStr = jPanelDiscount.getTextFieldProductPercent().getText().trim();

        if (discountID.isEmpty() || selectedProduct == null || percentStr.isEmpty()) {
            JOptionPane.showMessageDialog(jPanelDiscount, "Vui lòng chọn sản phẩm và nhập phần trăm giảm giá!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Double percentDiscount = Double.parseDouble(percentStr);
            if (percentDiscount < 0 || percentDiscount > 100) {
                JOptionPane.showMessageDialog(jPanelDiscount, "Phần trăm giảm giá phải từ 0 đến 100!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            listDetailDc.add(discountID, selectedProduct.getProductId(), percentDiscount);
            DetailDiscountDAO.addDetailDiscount(new DetailDiscount(discountID, selectedProduct.getProductId(), percentDiscount));
            loadDetails(discountID);
            jPanelDiscount.getTextFieldProductPercent().setText("");
            jPanelDiscount.getComboBoxProduct().setSelectedIndex(0);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(jPanelDiscount, "Phần trăm giảm giá phải là số!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void removeDetail() {
        int selectedRow = jPanelDiscount.getJTableDiscountDetail().getSelectedRow();
        if (selectedRow >= 0) {
            String discountID = jPanelDiscount.getTextFieldDiscountID().getText().trim();
            String productID = (String) jPanelDiscount.getJTableDiscountDetail().getValueAt(selectedRow, 0);
            DetailDiscountDAO.deleteDetailDiscount(discountID, productID);
            loadDetails(discountID);
        } else {
            JOptionPane.showMessageDialog(jPanelDiscount, "Vui lòng chọn sản phẩm để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void loadProducts() {
        jPanelDiscount.getComboBoxProduct().removeAllItems();
        List<Product> products = productDAO.getAllProducts();
        if (products.isEmpty()) {
            JOptionPane.showMessageDialog(jPanelDiscount, "Không có sản phẩm nào!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (Product product : products) {
            jPanelDiscount.getComboBoxProduct().addItem(product);
        }
    }
}