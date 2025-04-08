
package BUS;

import DAO.*;
import DTO.*;
import GUI.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author TAINHAT
 */
public class DiscountBUS implements ActionListener {

    private ListDiscount listDc;
    private DiscountPanel jPanelDiscount;
    private String discountID, discountName, percentDiscount, begin, end;

    public DiscountBUS(ListDiscount listDc, DiscountPanel jPanelDiscount) {
        this.listDc = listDc;
        this.jPanelDiscount = jPanelDiscount;
    }

    public DiscountBUS() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jPanelDiscount.getButtonAdd()) {
            add();
        } else if (e.getSource() == jPanelDiscount.getButtonFix()) {
            fix();
        } else if (e.getSource() == jPanelDiscount.getButtonShowAll()) {
            showAll();
        } else {
            jPanelDiscount.showList(searchByDate());
        }
    }

    private void getDiscount() {
        discountName = jPanelDiscount.getTextFieldDiscountName().getText().trim();
        begin = jPanelDiscount.getTextFieldDiscountStartDay().getText().trim();
        end = jPanelDiscount.getTextFieldDiscountEndDay().getText().trim();
        percentDiscount = jPanelDiscount.getTextFieldContent().getText().trim();
    }

    private void clear() {
        setEnabled(true);
        jPanelDiscount.getTextFieldDiscountID().setText("");
        jPanelDiscount.getTextFieldDiscountName().setText("");
        jPanelDiscount.getTextFieldDiscountStartDay().setText("");
        jPanelDiscount.getTextFieldDiscountEndDay().setText("");
        jPanelDiscount.getTextFieldContent().setText("");
        jPanelDiscount.getTextFieldSearch().setText("");
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
        } else if (percentDiscount == null || percentDiscount.isEmpty()) {
            JOptionPane.showMessageDialog(jPanelDiscount, "Phần trăm giảm giá không được để trống.", "Thông báo", JOptionPane.ERROR_MESSAGE);
            jPanelDiscount.getTextFieldContent().requestFocus();
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

        listDc.add(discountID, discountName, Double.parseDouble(percentDiscount), begin, end);
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
        listDc.fix(selectedRow, discountID, discountName, Double.parseDouble(percentDiscount), begin, end);
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

}
