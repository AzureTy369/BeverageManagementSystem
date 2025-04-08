package DAO;

import DTO.DetailInvoice;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author ASUS
 */
public class DetailInvoiceDAO {

    public static ArrayList<DetailInvoice> getAllDetailInvoices(String invoiceID) {
        ArrayList<DetailInvoice> invoices = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBConnection.getConnection();
            String condition = "";
            if (invoiceID != null) {
                condition += " WHERE invoiceID = ?";
            }

            String query = "SELECT * FROM invoicedetail" + condition;
            statement = connection.prepareStatement(query);
            if (invoiceID != null) {
                statement.setString(1, invoiceID);
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                invoiceID = resultSet.getString("invoiceID");
                String productId = resultSet.getString("productID");
                int quantity = resultSet.getInt("quantity");
                Double price = resultSet.getDouble("price");
                Double cost = resultSet.getDouble("cost");

                invoices.add(new DetailInvoice(invoiceID, productId, price, quantity, cost));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return invoices;
    }

    public static void deleteAllInvoiceDetails() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = DBConnection.getConnection();
            String sql = "DELETE FROM invoicedetail";
            pstmt = connection.prepareStatement(sql);
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
                e.printStackTrace();
            }
        }
    }

    public static void setAllDetailInvoices(ArrayList<DetailInvoice> list) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            deleteAllInvoiceDetails();
            connection = DBConnection.getConnection();

            String sql = "INSERT INTO invoicedetail (invoiceID, productID, quantity, price, cost) VALUES (?, ?, ?, ?, ?)";
            pstmt = connection.prepareStatement(sql);

            for (DetailInvoice divDTO : list) {
                pstmt.setString(1, divDTO.getInvoiceID());
                pstmt.setString(2, divDTO.getProductID());
                pstmt.setInt(3, divDTO.getQuantity());
                pstmt.setDouble(4, divDTO.getPrice());
                pstmt.setDouble(5, divDTO.getCost());

                pstmt.executeUpdate();
            }
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
                e.printStackTrace();
            }
        }
    }
}
