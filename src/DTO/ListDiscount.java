package DTO;

import BUS.Tool; 
import DAO.*;
import java.util.ArrayList;

public class ListDiscount {

    private ArrayList<Discount> list = new ArrayList<>();

    public ListDiscount() {
        this.list = DiscountDAO.getAllDiscounts();
    }

    public ArrayList<Discount> getList() {
        return list;
    }
    
    public boolean checkNameExist(String name){
        for(Discount dcDTO : list) {
              if(dcDTO.getDiscountName().equalsIgnoreCase(name)){
                return false;
              }
        }
        return true;
    }

    public void add(String discountID, String discountName, double percentDiscount, String begin, String end) {
        list.add(0, new Discount(discountID, discountName, end, begin, percentDiscount));
    }

    public void fix(int i, String discountID, String discountName, double percentDiscount, String begin, String end) {
        Discount discount = list.get(i);
        discount.setAll(discountID, discountName, percentDiscount, begin, end);
    }

    public ArrayList<Discount> search(String info) {
        return DiscountDAO.search(info);
    }

    public String createDiscountID() {
        String discountID;
        do {
            discountID = "DC" + Tool.randomID();
        } while (searchByDiscountID(discountID) != -1);

        return discountID;
    }

    public int searchByDiscountID(String discountID) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDiscountId().equals(discountID)) {
                return i;
            }
        }
        return -1;
    }

    public Discount searchByName(String name) {
        for (Discount dcDTO : list) {
            if (dcDTO.getDiscountName().equals(name)) {
                return dcDTO;
            }
        }
        return null;
    }

    public ArrayList<String> getListDiscountName() {
        ArrayList<String> listDiscountName = new ArrayList<>();
        listDiscountName.add("Nhấp để chọn");
        String date = Tool.getCurrentDate();
        for (Discount dcDTO : list) {
            if (!Tool.strToDate(dcDTO.getDiscountEnd()).isBefore(Tool.strToDate(date))) {
                listDiscountName.add(dcDTO.getDiscountName());
            }
        }
        return listDiscountName;
    }

}
