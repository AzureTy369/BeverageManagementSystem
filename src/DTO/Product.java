package DTO;

public class Product {
    private String productId;
    private String productName;
    private String categoryId;
    private String description;
    private String image;
    private double price;

    // For linking to category name in UI
    private String categoryName;

    // For statistics
    private int soldQuantity;

    public Product() {
    }

    public Product(String productId, String productName, String categoryId, String description, String image,
            double price) {
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.description = description;
        this.image = image;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    @Override
    public String toString() {
        return productName;
    }
}