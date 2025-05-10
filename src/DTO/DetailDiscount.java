package DTO;

public class DetailDiscount {
    private String discountId;    // MaKhuyenMai
    private String productId;     // MaSanPham
    private double percentDiscount; // PhanTramKhuyenMai

    public DetailDiscount() {
    }

    public DetailDiscount(String discountId, String productId, double percentDiscount) {
        this.discountId = discountId;
        this.productId = productId;
        this.percentDiscount = percentDiscount;
    }

    // Getters và Setters
    public String getDiscountId() {
        return discountId;
    }

    public void setDiscountId(String discountId) {
        this.discountId = discountId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getPercentDiscount() {
        return percentDiscount;
    }

    public void setPercentDiscount(double percentDiscount) {
        this.percentDiscount = percentDiscount;
    }
}