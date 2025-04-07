
package DTO;

import BUS.Tool;
import DAO.InvoiceDAO;
import java.util.ArrayList;

/**
 *
 * @author TaiNhat
 */
public class ListInvoice {

    private ArrayList<Invoice> list = new ArrayList<>();

    public ListInvoice() {
        this.list = InvoiceDAO.getAllInvoices();
    }

    public ArrayList<Invoice> getList() {
        return list;
    }

    public void add(String invoiceID, String customerID, String employeeID, String discountID, String date, double tempCost, double reducedCost, double totalCost) {
        list.add(0, new Invoice(invoiceID, customerID, employeeID, discountID, date, tempCost, reducedCost, totalCost));
    }

    public ArrayList<Invoice> search(String info) {
        return InvoiceDAO.search(info);
    }

    public String getInvoiceID(int index) {
        return list.get(index).getInvoiceID();
    }
    
    public void setAllInvoices(ArrayList<Invoice> temp) {
        InvoiceDAO.setAllInvoices(temp);
    }
    
    public String createInvoiceID() {
        String invoiceID;
        do {
            invoiceID = "IV" + Tool.randomID();
        } while (searchByInvoiceID(invoiceID) != -1);

        return invoiceID;
    }
    
    public int searchByInvoiceID(String invoiceID) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getInvoiceID().equals(invoiceID)) {
                return i;
            }
        }
        return -1;
    }
}
