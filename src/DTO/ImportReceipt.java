package DTO;

public class ImportReceipt {
    private String importId;
    private String supplierId;
    private String employeeId;
    private String importDate;
    private String totalAmount;
    private String note;
    private String status; // Trạng thái: "Đang xử lý", "Đã hoàn thành", "Đã hủy"

    public ImportReceipt() {
    }

    public ImportReceipt(String importId, String supplierId, String employeeId, String importDate, String totalAmount,
            String note) {
        this.importId = importId;
        this.supplierId = supplierId;
        this.employeeId = employeeId;
        this.importDate = importDate;
        this.totalAmount = totalAmount;
        this.note = note;
        this.status = "Đang xử lý"; // Mặc định là "Đang xử lý"
    }

    public ImportReceipt(String importId, String supplierId, String employeeId, String importDate, String totalAmount,
            String note, String status) {
        this.importId = importId;
        this.supplierId = supplierId;
        this.employeeId = employeeId;
        this.importDate = importDate;
        this.totalAmount = totalAmount;
        this.note = note;
        this.status = status;
    }

    public String getImportId() {
        return importId;
    }

    public void setImportId(String importId) {
        this.importId = importId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getImportDate() {
        return importDate;
    }

    public void setImportDate(String importDate) {
        this.importDate = importDate;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ImportReceipt [importId=" + importId + ", supplierId=" + supplierId + ", employeeId=" + employeeId
                + ", importDate=" + importDate + ", totalAmount=" + totalAmount + ", note=" + note
                + ", status=" + status + "]";
    }
}