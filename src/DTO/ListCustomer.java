/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DTO;

import BUS.Tool;
import DAO.*;
import java.util.ArrayList;

public class ListCustomer {
    private ArrayList<Customer> list = new ArrayList<>();

    public ListCustomer() {
        this.list = CustomerDAO.getAllCustomers();
    }

    public ArrayList<Customer> getList() {
        return list;
    }

    public boolean checkPhoneExist(String phone){
        for(Customer ctmDTO : list) {
              if(ctmDTO.getPhone().equals(phone)){
                return false;
              }
        }
        return true;
    }

    public void add(String customerID, String firstname, String lastname, String address, String phone) {
        list.add(0, new Customer(customerID, firstname, lastname, address, phone));
    }

    public void fix(int i, String customerID, String firstname, String lastname, String address, String phone) {
        Customer ctmDTO = list.get(i);
        ctmDTO.setAll(customerID, firstname, lastname, address, phone);
    }

    public ArrayList<Customer> search(String info) {
        return CustomerDAO.search(info);
    }

    public String createCustomerID() {
        String customerID;
        do {
            customerID = "CTM" + Tool.randomID();
        } while (searchByCustomerID(customerID) != -1);

        return customerID;
    }

    public int searchByCustomerID(String customerID) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCustomerId().equals(customerID)) {
                return i;
            }
        }
        return -1;
    }
    
    public ArrayList<String> getListCustomerID() {
        ArrayList<String> listCustomerID = new ArrayList<>();
        listCustomerID.add("Nhấp để chọn");
        for (Customer ctmDTO : list) {
            listCustomerID.add(ctmDTO.getCustomerId());
        }
        return listCustomerID;
    }
}
