package DAO;

import DTO.DetailInvoice;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DetailInvoiceDAO {

    public static ArrayList<DetailInvoice> getAllDetailInvoices(String invoiceId) {
        ArrayList<DetailInvoice> invoices = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnection.getConnection();
            String query = "SELECT MaHoaDon, MaSanPham, SoLuong, DonGia, ThanhTien FROM chitiethoadon";
            if (invoiceId != null && !invoiceId.isEmpty()) {
                query += " WHERE MaHoaDon = ?";
            }

            statement = connection.prepareStatement(query);
            if (invoiceId != null && !invoiceId.isEmpty()) {
                statement.setString(1, invoiceId);
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
            // Xóa tất cả chi tiết hóa đơn hiện có
            deleteAllInvoiceDetails();
            connection = DBConnection.getConnection();

            String sql = "INSERT INTO chitiethoadon (MaHoaDon, MaSanPham, SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?)";
            pstmt = connection.prepareStatement(sql);

            for (DetailInvoice detail : list) {
                pstmt.setString(1, detail.getInvoiceID());
                pstmt.setString(2, detail.getProductID());
                pstmt.setInt(3, detail.getQuantity());
                pstmt.setDouble(4, detail.getPrice());
                pstmt.setDouble(5, detail.getCost());

                pstmt.addBatch(); // Thêm vào batch để tối ưu hóa
            }
            pstmt.executeBatch(); // Thực thi batch
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