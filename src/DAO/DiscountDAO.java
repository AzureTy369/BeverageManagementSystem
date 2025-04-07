package DAO;

import java.sql.Connection; 
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import DTO.*;

public class DiscountDAO {
    public static ArrayList<Discount> getAllDiscounts() {
        ArrayList<Discount> discounts = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnection.getConnection();
            String query = "SELECT * FROM discount";
            pstmt = connection.prepareStatement(query);
            resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String discountID = resultSet.getString("discountID");
                String discountName = resultSet.getString("name");
                String percentDiscount = resultSet.getString("percentDiscount");
                String begin = resultSet.getString("begin");
                String end = resultSet.getString("end");

                discounts.add(new Discount(discountID, discountName, begin,end, Double.parseDouble(percentDiscount)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return discounts;
    }

    public static void deleteAllDiscounts() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = DBConnection.getConnection();
            String query = "DELETE FROM discount";
            pstmt = connection.prepareStatement(query);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public static ArrayList<Discount> search(String info) {
        ArrayList<Discount> discounts = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            String query;

            if (info != null && !info.isEmpty()) {
                query = "SELECT * FROM discount WHERE discountID LIKE ? OR name LIKE ? OR percentDiscount LIKE ? OR begin LIKE ?";
                pstmt = connection.prepareStatement(query);
                String searchValue = "%" + info + "%";
                pstmt.setString(1, searchValue);
                pstmt.setString(2, searchValue);
                pstmt.setString(3, searchValue);
                pstmt.setString(4, searchValue);
            } else {
                query = "SELECT * FROM discount";
                pstmt = connection.prepareStatement(query);
            }

            resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String discountId = resultSet.getString("discountID");
                String discountName = resultSet.getString("name");
                double percentDiscount = resultSet.getDouble("percentDiscount");
                String begin = resultSet.getString("begin");
                String end = resultSet.getString("end");

                discounts.add(new Discount( discountId , discountName, begin, end , percentDiscount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return discounts;
    }

    public static void setAllDiscounts(ArrayList<Discount> list) {
}
}
