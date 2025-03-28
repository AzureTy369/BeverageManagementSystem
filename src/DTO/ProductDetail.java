package DTO;

import java.util.Date;

public class ProductDetail {
    private String detailId;
    private String productId;
    private int quantity;
    private Date importDate;
    private Date expiryDate;
    private String batchNumber;
    private String supplierId;

    // For linking to product and supplier names in UI
    private String productName;
    private String supplierName;

    public ProductDetail() {
    }

    public ProductDetail(String detailId, String productId, int quantity, Date importDate, Date expiryDate,
            String batchNumber, String supplierId) {
        this.detailId = detailId;
        this.productId = productId;
        this.quantity = quantity;
        this.importDate = importDate;
        this.expiryDate = expiryDate;
        this.batchNumber = batchNumber;
        this.supplierId = supplierId;
    }

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getImportDate() {
        return importDate;
    }

    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    @Override
    public String toString() {
        return detailId + " - " + productName;
    }
}