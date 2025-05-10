package DAO;

import DTO.Customer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerDAO {

    public static ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customer";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet resultSet = pstmt.executeQuery()) {
            
            while (resultSet.next()) {
                String customerID = resultSet.getString("customerID");
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                String address = resultSet.getString("address");
                String phone = resultSet.getString("phone");

                customers.add(new Customer(customerID, firstname, lastname, address, phone));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public static void deleteAllCustomers() {
        String sql = "DELETE FROM customer";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Customer> search(String info) {
        ArrayList<Customer> customers = new ArrayList<>();
        String query;
        
        try (Connection connection = DBConnection.getConnection()) {
            if (info != null && !info.isEmpty()) {
                query = "SELECT * FROM customer WHERE customerID LIKE ? OR " +
                       "CONCAT(firstname, ' ', lastname) LIKE ? OR " +
                       "address LIKE ? OR phone LIKE ? OR customerDate LIKE ?";
                
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    String searchValue = "%" + info + "%";
                    pstmt.setString(1, searchValue);
                    pstmt.setString(2, searchValue);
                    pstmt.setString(3, searchValue);
                    pstmt.setString(4, searchValue);
                    pstmt.setString(5, searchValue);
                    
                    try (ResultSet resultSet = pstmt.executeQuery()) {
                        while (resultSet.next()) {
                            customers.add(createCustomerFromResultSet(resultSet));
                        }
                    }
                }
            } else {
                query = "SELECT * FROM customer";
                try (PreparedStatement pstmt = connection.prepareStatement(query);
                     ResultSet resultSet = pstmt.executeQuery()) {
                    while (resultSet.next()) {
                        customers.add(createCustomerFromResultSet(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public static void setAllCustomers(ArrayList<Customer> list) {
        deleteAllCustomers();
        String sql = "INSERT INTO customer (customerID, firstname, lastname, address, phone, customerDate) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            for (Customer customer : list) {
                pstmt.setString(1, customer.getCustomerId());
                pstmt.setString(2, customer.getFirstname());
                pstmt.setString(3, customer.getLastname());
                pstmt.setString(4, customer.getAddress());
                pstmt.setString(5, customer.getPhone());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Customer createCustomerFromResultSet(ResultSet resultSet) throws SQLException {
        String customerID = resultSet.getString("customerID");
        String firstname = resultSet.getString("firstname");
        String lastname = resultSet.getString("lastname");
        String address = resultSet.getString("address");
        String phone = resultSet.getString("phone");
        String customerDate = resultSet.getString("customerDate");
        
        return new Customer(customerID, firstname, lastname, address, phone);
    }
}