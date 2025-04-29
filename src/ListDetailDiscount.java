package DTO;

import DAO.DetailDiscountDAO;
import java.util.ArrayList;

/**
 *
 * @author TAINHAT
 */
public class ListDetailDiscount {

    private ArrayList<DetailDiscount> list = new ArrayList<>();

    public ListDetailDiscount() {
        this.list = DetailDiscountDAO.getAllDetailDiscounts(null);
    }

    public void setList(String discountID) {
        list.clear();
        this.list = DetailDiscountDAO.getAllDetailDiscounts(discountID);
    }

    public ArrayList<DetailDiscount> getList() {
        return list;
    }

    public void setList(ArrayList<DetailDiscount> list) {
        this.list = list;
    }

    public void add(String discountID, String productID, double percentDiscount) {
        list.add(0, new DetailDiscount(discountID, productID, percentDiscount));
    }

    public void setAllDetailDiscounts(ArrayList<DetailDiscount> temp) {
        DetailDiscountDAO.setAllDetailDiscounts(temp);
    }
}