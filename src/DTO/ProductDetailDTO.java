package DTO;

public class ProductDetailDTO {
    private String detailId;
    private String productId;
    private String size;
    private double price;

    // For linking to product name in UI
    private String productName;

    public ProductDetailDTO() {
    }

    public ProductDetailDTO(String detailId, String productId, String size, double price) {
        this.detailId = detailId;
        this.productId = productId;
        this.size = size;
        this.price = price;
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return detailId + " - " + productName + (size != null ? " - " + size : "");
    }
}