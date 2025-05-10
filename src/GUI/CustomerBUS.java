<<<<<<< HEAD
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BUS;

import DAO.*;
import DAO.CustomerDAO;
import DTO.*;
import GUI.CustomerPanel;
=======

package BUS;

import DAO.*;
import DTO.*;
>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
import GUI.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
<<<<<<< HEAD
 * @author ASUS
=======
 * @author TAINHAT
>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
 */
public class CustomerBUS implements ActionListener {

    private ListCustomer listCtm;
<<<<<<< HEAD
    private CustomerPanel  jPanelCustomer;
    private String customerID, firstname, lastname, address, phone;
=======
    private CustomerPanel jPanelCustomer;
    private String customerID, firstname, lastname, point, phone;
>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31

    public CustomerBUS(ListCustomer listCtm, CustomerPanel jPanelCustomer) {
        this.listCtm = listCtm;
        this.jPanelCustomer = jPanelCustomer;
    }

    public CustomerBUS() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jPanelCustomer.getButtonAdd()) {
            add();
        } else if (e.getSource() == jPanelCustomer.getButtonFix()) {
            fix();
        } else if (e.getSource() == jPanelCustomer.getButtonShowAll()) {
            showAll();
        } else {
            jPanelCustomer.showList(search());
        }
    }

    private void getCustomer() {
        customerID = jPanelCustomer.getTextFieldCustomerID().getText().trim();
        firstname = jPanelCustomer.getTextFieldCustomerFirstname().getText().trim();
        lastname = jPanelCustomer.getTextFieldCustomerLastname().getText().trim();
<<<<<<< HEAD
        address = jPanelCustomer.getTextFieldCustomerAddress().getText().trim();
=======
        point= Integer.toString(0);
>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
        phone = jPanelCustomer.getTextFieldCustomerPhone().getText().trim();
    }

    private void clear() {
        setEnabled(true);
        jPanelCustomer.getTextFieldCustomerID().setText("");
        jPanelCustomer.getTextFieldCustomerFirstname().setText("");
        jPanelCustomer.getTextFieldCustomerLastname().setText("");
<<<<<<< HEAD
        jPanelCustomer.getTextFieldCustomerAddress().setText("");
=======
>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
        jPanelCustomer.getTextFieldCustomerPhone().setText("");
        jPanelCustomer.getTextFieldSearch().setText("");
    }

    public void setEnabled(boolean bool) {
        jPanelCustomer.getButtonAdd().setEnabled(bool);
    }

    private boolean valid() {
        if (firstname == null || firstname.isEmpty()) {
            JOptionPane.showMessageDialog(jPanelCustomer, "Họ của khách hàng không được để trống.", "Thông báo",
                    JOptionPane.ERROR_MESSAGE);
            jPanelCustomer.getTextFieldCustomerFirstname().requestFocus();
            return false;
        } else if (lastname == null || lastname.isEmpty()) {
            JOptionPane.showMessageDialog(jPanelCustomer, "Tên của khách hàng không được để trống.", "Thông báo",
                    JOptionPane.ERROR_MESSAGE);
            jPanelCustomer.getTextFieldCustomerLastname().requestFocus();
            return false;
<<<<<<< HEAD
        } else if (address == null || address.isEmpty()) {
            JOptionPane.showMessageDialog(jPanelCustomer, "Địa chỉ của khách hàng không được để trống.", "Thông báo",
                    JOptionPane.ERROR_MESSAGE);
            jPanelCustomer.getTextFieldCustomerAddress().requestFocus();
            return false;
=======

>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
        } else if (phone == null || phone.isEmpty()) {
            JOptionPane.showMessageDialog(jPanelCustomer, "Số điện thoại của khách hàng không được để trống.", "Thông báo",
                    JOptionPane.ERROR_MESSAGE);
            jPanelCustomer.getTextFieldCustomerPhone().requestFocus();
            return false;
        }
        return true;
    }

    private void add() {
        if (jPanelCustomer.isFlat()) {
            return;
        }

        getCustomer();
        if (!valid()) {
            return;
        }

        customerID = listCtm.createCustomerID();
<<<<<<< HEAD
        listCtm.addCustomer(customerID, firstname, lastname, address, phone);
=======
        listCtm.addCustomer(customerID, firstname, lastname, point,phone);
>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
        CustomerDAO.setAllCustomers(listCtm.getList());
        jPanelCustomer.showList(listCtm.getList());
        clear();
    }

    private void fix() {
        if (jPanelCustomer.isFlat()) {
            return;
        }

        int selectedRow = jPanelCustomer.getJTableCustomer().getSelectedRow();
        getCustomer();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(jPanelCustomer, "Xin hãy chọn khách hàng cần sửa.", "Thông báo",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } else if (!valid()) {
            return;
        }

<<<<<<< HEAD
        listCtm.fix(selectedRow, customerID, firstname, lastname, address, phone);
=======
        listCtm.fix(selectedRow, customerID, firstname, lastname, phone);
>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
        CustomerDAO.setAllCustomers(listCtm.getList());
        jPanelCustomer.showList(listCtm.getList());
        clear();
    }

    private void showAll() {
        clear();
        jPanelCustomer.showList(listCtm.getList());
    }

    private ArrayList<Customer> search() {
        String info = jPanelCustomer.getTextFieldSearch().getText().trim();
        ArrayList<Customer> ls = listCtm.search(info);

        if (ls.isEmpty()) {
            return null;
        }
        return ls;
    }
}
