package DTO;

public class Invoice {
    private String invoiceID;    // MaHoaDon
    private String customerID;   // MaKhachHang
    private String employeeID;   // MaNhanVien
    private String discountID;   // MaKhuyenMai
    private String date;         // NgayLapHoaDon (chuỗi)
    private Double tempCost;     // TamTinh
    private Double reducedCost;  // GiamGia
    private Double totalCost;    // TongHoaDon
    private String payment;      // PhuongThucThanhToan
    private double percentDiscount; // PhanTramKhuyenMai từ chitietkhuyenmai

    public Invoice() {
    }

    public Invoice(String invoiceID, String customerID, String employeeID, String discountID, String date,
                   Double tempCost, Double reducedCost, Double totalCost, String payment, double percentDiscount) {
        this.invoiceID = invoiceID;
        this.customerID = customerID;
        this.employeeID = employeeID;
        this.discountID = discountID;
        this.date = date;
        this.tempCost = tempCost;
        this.reducedCost = reducedCost;
        this.totalCost = totalCost;
        this.payment = payment;
        this.percentDiscount = percentDiscount;
    }

    // Getters và Setters
    public String getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getDiscountID() {
        return discountID;
    }

    public void setDiscountID(String discountID) {
        this.discountID = discountID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getTempCost() {
        return tempCost;
    }

    public void setTempCost(Double tempCost) {
        this.tempCost = tempCost;
    }

    public Double getReducedCost() {
        return reducedCost;
    }

    public void setReducedCost(Double reducedCost) {
        this.reducedCost = reducedCost;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public double getPercentDiscount() {
        return percentDiscount;
    }

    public void setPercentDiscount(double percentDiscount) {
        this.percentDiscount = percentDiscount;
    }
}