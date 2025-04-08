/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DTO;

import DAO.DetailInvoiceDAO;
import java.util.ArrayList;

/**
 *
 * @author ASUS
 */
public class ListDetailInvoice {

    private ArrayList<DetailInvoice> list = new ArrayList<>();

    public ListDetailInvoice() {
        this.list = DetailInvoiceDAO.getAllDetailInvoices(null);
    }

    public void setList(String invoiceID) {
        list.clear();
        this.list = DetailInvoiceDAO.getAllDetailInvoices(invoiceID);
    }

    public ArrayList<DetailInvoice> getList() {
        return list;
    }

    public void setList(ArrayList<DetailInvoice> list) {
        this.list = list;
    }

    public void add(String invoiceID, String productID, double price, int quantity, double totalCost) {
        list.add(0, new DetailInvoice(invoiceID, productID, price, quantity, totalCost));
    }

    public void setAllDetailInvoices(ArrayList<DetailInvoice> temp) {
        DetailInvoiceDAO.setAllDetailInvoices(temp);
    }
}
