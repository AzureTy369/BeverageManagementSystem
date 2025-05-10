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
        return new ArrayList<>(list); // Trả về bản sao để bảo vệ danh sách
    }

    public void add(String invoiceID, String customerID, String employeeID, String discountID, String date,
            Double tempCost, Double reducedCost, Double totalCost, String payment) {
        // Kiểm tra dữ liệu đầu vào
        if (invoiceID == null || invoiceID.isEmpty() || employeeID == null || employeeID.isEmpty() || totalCost == null) {
            throw new IllegalArgumentException("Dữ liệu hóa đơn không hợp lệ");
        }
        if (searchByInvoiceID(invoiceID) != -1) {
            throw new IllegalArgumentException("Mã hóa đơn đã tồn tại: " + invoiceID);
        }
        // Không thêm percentDiscount vì được tính từ chitietkhuyenmai
        list.add(0, new Invoice(invoiceID, customerID, employeeID, discountID, date, tempCost, reducedCost, totalCost, payment, 0.0));
        // Đồng bộ với cơ sở dữ liệu
        InvoiceDAO.setAllInvoices(list);
    }

    public ArrayList<Invoice> search(String info) {
        ArrayList<Invoice> result = InvoiceDAO.search(info);
        // Cập nhật danh sách nội bộ
        if (info == null || info.trim().isEmpty()) {
            list = new ArrayList<>(result);
        }
        return result;
    }

    public String getInvoiceID(int index) {
        if (index < 0 || index >= list.size()) {
            throw new IndexOutOfBoundsException("Chỉ số không hợp lệ: " + index);
        }
        return list.get(index).getInvoiceID();
    }
    
    public void setAllInvoices(ArrayList<Invoice> temp) {
        InvoiceDAO.setAllInvoices(temp);
        // Đồng bộ danh sách nội bộ
        this.list = new ArrayList<>(temp);
    }
    
    public String createInvoiceID() {
        String invoiceID;
        do {
            invoiceID = "IV" + Tool.randomID();
        } while (searchByInvoiceID(invoiceID) != -1);
        return invoiceID;
    }
    
    public int searchByInvoiceID(String invoiceID) {
        // Sử dụng InvoiceDAO để kiểm tra trong cơ sở dữ liệu
        ArrayList<Invoice> result = InvoiceDAO.search(invoiceID);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getInvoiceID().equals(invoiceID) || !result.isEmpty()) {
                return i;
            }
        }
        return -1;
    }
}