package DTO;



/**
 * DTO cho sản phẩm của nhà cung cấp
 */
public class SupplierProductDTO {
    private String productId;
    private String supplierId;
    private String productName;
    private String unit;
    private String description;
    private double price;
    private String categoryId;
    private String categoryName;

    public SupplierProductDTO() {
    }

    public SupplierProductDTO(String productId, String supplierId, String productName, String unit, String description,
            double price) {
        this.productId = productId;
        this.supplierId = supplierId;
        this.productName = productName;
        this.unit = unit;
        this.description = description;
        this.price = price;
    }

    public SupplierProductDTO(String productId, String supplierId, String productName, String unit, String description,
            double price, String categoryId, String categoryName) {
        this.productId = productId;
        this.supplierId = supplierId;
        this.productName = productName;
        this.unit = unit;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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
        return "SupplierProductDTO{" +
                "productId='" + productId + '\'' +
                ", supplierId='" + supplierId + '\'' +
                ", productName='" + productName + '\'' +
                ", unit='" + unit + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", categoryId='" + categoryId + '\'' +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}