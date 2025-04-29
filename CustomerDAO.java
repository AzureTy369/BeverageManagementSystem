/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DTO.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author ASUS
 */
public class CustomerDAO {

    public static ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnection.getConnection();
            String query = "SELECT * FROM customer";
            pstmt = connection.prepareStatement(query);
            resultSet = pstmt.executeQuery();
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
        return customers;
    }

    public static void deleteAllCustomers() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = DBConnection.getConnection();
            String sql = "DELETE FROM customer";
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

    public static ArrayList<Customer> search(String info) {
        ArrayList<Customer> customers = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        
        try {
            connection = DBConnection.getConnection();
            String query;

            if (info != null && !info.isEmpty()) {
                query = "SELECT * FROM customer WHERE customerID LIKE ? OR CONCAT(firstname, ' ', lastname) LIKE ? OR address LIKE ? OR phone LIKE ?";
                pstmt = connection.prepareStatement(query);
                String searchValue = "%" + info + "%";
                pstmt.setString(1, searchValue);
                pstmt.setString(2, searchValue);
                pstmt.setString(3, searchValue);
                pstmt.setString(4, searchValue);
            } else {
                query = "SELECT * FROM customer";
                pstmt = connection.prepareStatement(query);
            }

            resultSet = pstmt.executeQuery();

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
        return customers;
    }

    public static void setAllCustomers(ArrayList<Customer> list) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            deleteAllCustomers();
            connection = DBConnection.getConnection();

            String sql = "INSERT INTO customer (customerID, firstname, lastname, address, phone) VALUES (?, ?, ?, ?, ?)";
            pstmt = connection.prepareStatement(sql);

            for (Customer customer : list) {
                pstmt.setString(1, customer.getCustomerId());
                pstmt.setString(2, customer.getCustomerFirstname());
                pstmt.setString(3, customer.getCustomerLastname());
                pstmt.setString(4, customer.getCustomerAddress());
                pstmt.setString(5, customer.getCustomerPhone());

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
