package DTO ;

public class DetailDiscount {
    private String discountID;        
    private String productID;  
    private Double percentDiscount;   
	public DetailDiscount(String discountID, String productID, Double percentDiscount) {
		super();
		this.discountID = discountID;
		this.productID = productID;
		this.percentDiscount = percentDiscount;
	}
	public DetailDiscount() {
		super();
	}
	public String getDiscountID() {
		return discountID;
	}
	public void setDiscountID(String discountID) {
		this.discountID = discountID;
	}
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
	public Double getPercentDiscount() {
		return percentDiscount;
	}
	public void setPercentDiscount(Double percentDiscount) {
		this.percentDiscount = percentDiscount;
	}
    

}