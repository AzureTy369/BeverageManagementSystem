package DAO;

import DTO.DetailInvoice;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DetailInvoiceDAO {

    public static ArrayList<DetailInvoice> getAllDetailInvoices(String invoiceID) {
        ArrayList<DetailInvoice> invoices = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnection.getConnection();
            String condition = "";
            if (invoiceID != null) {
                condition += " WHERE MaHoaDon = ?";
            }

            String query = "SELECT * FROM chitiethoadon" + condition;
            statement = connection.prepareStatement(query);
            if (invoiceID != null) {
                statement.setString(1, invoiceID);
            }

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String maHoaDon = resultSet.getString("MaHoaDon");
                String maSanPham = resultSet.getString("MaSanPham");
                int soLuong = resultSet.getInt("SoLuong");
                double donGia = resultSet.getDouble("DonGia");
                double thanhTien = resultSet.getDouble("ThanhTien");

                invoices.add(new DetailInvoice(maHoaDon, maSanPham, donGia, soLuong, thanhTien));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
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
            String sql = "DELETE FROM chitiethoadon";
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

            String sql = "INSERT INTO chitiethoadon (MaHoaDon, MaSanPham, SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?)";
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