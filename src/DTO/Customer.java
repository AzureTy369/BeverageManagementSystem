package DTO;

public class Customer {
	 private String customerId;
	    private String customerFirstname;
	    private String customerAddress;
	    private String customerPhone;
	    private String customerPoint;
	    private String customerDate ;
	    private String customerLastname;
		public String getCustomerId() {
			return customerId;
		}
		public void setCustomerId(String customerId) {
			this.customerId = customerId;
		}
		public String getCustomerFirstname() {
			return customerFirstname;
		}
		public void setCustomerFirstname(String customerFirstname) {
			this.customerFirstname = customerFirstname;
		}
		public String getCustomerAddress() {
			return customerAddress;
		}
		public void setCustomerAddress(String customerAddress) {
			this.customerAddress = customerAddress;
		}
		public String getCustomerPhone() {
			return customerPhone;
		}
		public void setCustomerPhone(String customerPhone) {
			this.customerPhone = customerPhone;
		}
		public String getCustomerPoint() {
			return customerPoint;
		}
		public void setCustomerPoint(String customerPoint) {
			this.customerPoint = customerPoint;
		}
		public String getCustomerDate() {
			return customerDate;
		}
		public String getCustomerLastname() {
			return customerLastname;
		}
		public void setCustomerLastname(String customerLastname) {
			this.customerLastname = customerLastname;
		}
		public void setAll (String id , String first , String last , String phone ) {
			setCustomerFirstname(first);
			setCustomerId(id);
			setCustomerLastname(last);
			setCustomerPhone(phone);
		}
		public Customer(String customerId, String customerFirstname , String customerLastname, String cusPoint,String customerPhone) {
			super();
			this.customerId = customerId;
			this.customerFirstname = customerFirstname;
			this.customerPoint = cusPoint;
			this.customerPhone = customerPhone;
			this.customerLastname = customerLastname;
		}
		public Customer() {
			super();
		}
	
	    
	    
}
