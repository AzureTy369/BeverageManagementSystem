package DTO;

public class InventoryDTO {
    private String productId;
    private int quantity;
    private String lastUpdated;

    public InventoryDTO() {
    }

    public InventoryDTO(String productId, int quantity, String lastUpdated) {
        this.productId = productId;
        this.quantity = quantity;
        this.lastUpdated = lastUpdated;
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

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "InventoryDTO{" +
                "productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", lastUpdated='" + lastUpdated + '\'' +
                '}';
    }
}