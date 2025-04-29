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
	public void addCustomer (String cusId , String cusFirstName , String cusLastName ,String cusAdd, String cusPhone ) {
		list.add(0, new Customer(cusId , cusFirstName,cusLastName, cusAdd,cusPhone ));
	}
	
	 public void fix(int i, String customerID, String firstname, String lastname, String address, String phone) {
	        Customer ctmDTO = list.get(i);
	        ctmDTO.setAll(customerID, firstname, lastname, address, phone);
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
