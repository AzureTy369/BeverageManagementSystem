package DTO;

public class Discount {
  private String discountName , discountId ,discountBegin, discountEnd;
  private double discountPercnt;
public String getDiscountName() {
	return discountName;
}
public void setDiscountName(String discountName) {
	this.discountName = discountName;
}
public String getDiscountId() {
	return discountId;
}
public void setDiscountId(String discountId) {
	this.discountId = discountId;
}
public String getDiscountBegin() {
	return discountBegin;
}
public void setDiscountBegin(String discountBegin) {
	this.discountBegin = discountBegin;
}
public String getDiscountEnd() {
	return discountEnd;
}
public void setDiscountEnd(String discountEnd) {
	this.discountEnd = discountEnd;
}
public double getDiscountPercnt() {
	return discountPercnt;
}
public void setDiscountPercnt(double discountPercnt) {
	this.discountPercnt = discountPercnt;
}
public void setAll(String discountID, String discountName, double percentDiscount, String begin, String end){
    setDiscountId(discountID);
    setDiscountName(discountName);
    setDiscountPercnt(percentDiscount);
    setDiscountBegin(begin);
    setDiscountEnd(end);
}
public Discount( String discountId,String discountName, String discountBegin, String discountEnd,
		double discountPercnt) {
	super();
	this.discountName = discountName;
	this.discountId = discountId;
	this.discountBegin = discountBegin;
	this.discountEnd = discountEnd;
	this.discountPercnt = discountPercnt;
}
public Discount() {
} 
}
