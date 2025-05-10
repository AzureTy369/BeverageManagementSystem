package DTO;

import BUS.Tool;
import java.util.ArrayList;

import DAO.CustomerDAO;

public class ListCustomer {
	private ArrayList<Customer>  list = new ArrayList<Customer>() ;

	public ArrayList<Customer> getList() {
		return list;
	}
	
	public ListCustomer() {
		this.list = CustomerDAO.getAllCustomers();
	}
	public boolean checkPhoneExist(String phone) {
		for(Customer ctm : list )
			if (ctm.getCustomerPhone().equals(phone))
				return false;
		return true;
	}
<<<<<<< HEAD
	public void addCustomer (String cusId , String cusFirstName , String cusLastName , String cusAddress , String cusPhone ) {
		list.add(0, new Customer(cusId , cusFirstName,cusLastName,cusAddress, cusPhone ));
	}
	
	public void fix (int i , String cusId , String cusFirstName , String cusLastName , String cusAddress , String cusPhone) {
		Customer ctm = list.get(i);
		ctm.setAll(cusId, cusFirstName, cusLastName, cusAddress, cusPhone );
=======
	public void addCustomer (String cusId , String cusFirstName , String cusLastName , String cusAddress , String cusPhone , String cusDate ) {
		list.add(0, new Customer(cusId , cusFirstName,cusLastName,cusAddress, cusPhone , cusDate));
	}
	
	public void fix (int i , String cusId , String cusFirstName , String cusLastName , String cusAddress , String cusPhone ,String cusPoint, String cusDate ) {
		Customer ctm = list.get(i);
		ctm.setAll(cusId, cusFirstName, cusLastName, cusAddress, cusPhone ,cusDate);
>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
	}
	public ArrayList<Customer> search(String info) {
        return CustomerDAO.search(info);
    }

    public String createCustomerID() {
        String customerID;
        do {
            customerID = "CTM" + Tool.randomID();
        } while (searchByCustomerID(customerID) != -1);

        return customerID;
    }

    public int searchByCustomerID(String customerID) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCustomerId().equals(customerID)) {
                return i;
            }
        }
        return -1;
    }
    
    public ArrayList<String> getListCustomerID() {
        ArrayList<String> listCustomerID = new ArrayList<>();
        listCustomerID.add("Nhấp để chọn");
        for (Customer ctmDTO : list) {
            listCustomerID.add(ctmDTO.getCustomerId());
        }
        return listCustomerID;
    }

	
	
}
