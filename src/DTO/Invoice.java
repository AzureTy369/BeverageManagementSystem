package DTO;

public class Invoice {
	  private String invoiceID, customerID, employeeID, discountID, date;
	    private double tempCost, reducedCost, totalCost;
		public String getInvoiceID() {
			return invoiceID;
		}
		public void setInvoiceID(String invoiceID) {
			this.invoiceID = invoiceID;
		}
		public String getCustomerID() {
			return customerID;
		}
		public void setCustomerID(String customerID) {
			this.customerID = customerID;
		}
		public String getEmployeeID() {
			return employeeID;
		}
		public void setEmployeeID(String employeeID) {
			this.employeeID = employeeID;
		}
		public String getDiscountID() {
			return discountID;
		}
		public void setDiscountID(String discountID) {
			this.discountID = discountID;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public double getTempCost() {
			return tempCost;
		}
		public void setTempCost(double tempCost) {
			this.tempCost = tempCost;
		}
		public double getReducedCost() {
			return reducedCost;
		}
		public void setReducedCost(double reducedCost) {
			this.reducedCost = reducedCost;
		}
		public double getTotalCost() {
			return totalCost;
		}
		public void setTotalCost(double totalCost) {
			this.totalCost = totalCost;
		}
		public Invoice(String invoiceID, String customerID, String employeeID, String discountID, String date,
				double tempCost, double reducedCost, double totalCost) {
			super();
			this.invoiceID = invoiceID;
			this.customerID = customerID;
			this.employeeID = employeeID;
			this.discountID = discountID;
			this.date = date;
			this.tempCost = tempCost;
			this.reducedCost = reducedCost;
			this.totalCost = totalCost;
		}
		public Invoice() {
		}
		
}
