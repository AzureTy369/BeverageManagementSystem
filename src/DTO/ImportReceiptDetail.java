package DTO;

public class ImportReceiptDetail {
    private String receiptId;
    private String productId;
    private int quantity;
    private double price;
    private double total;
    private String categoryId;
    private String categoryName;

    public ImportReceiptDetail() {
    }

    public ImportReceiptDetail(String receiptId, String productId, int quantity, double price, double total) {
        this.receiptId = receiptId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
    }

    public ImportReceiptDetail(String receiptId, String productId, int quantity, double price, double total,
            String categoryId, String categoryName) {
        this.receiptId = receiptId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "ImportReceiptDetail{" +
                "receiptId='" + receiptId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", total=" + total +
                ", categoryId='" + categoryId + '\'' +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}