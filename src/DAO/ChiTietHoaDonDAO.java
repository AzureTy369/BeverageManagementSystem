package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {
    private Connection connection;

    public ChiTietHoaDonDAO() {
        connection = DBConnection.getConnection();
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try {
            Statement stmt = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS ChiTietHoaDon (" +
                    "MaHoaDon VARCHAR(10) NOT NULL," +
                    "MaSanPham VARCHAR(10) NOT NULL," +
                    "SoLuong INT NOT NULL," +
                    "DonGia DECIMAL(10, 2) NOT NULL," +
                    "PRIMARY KEY (MaHoaDon, MaSanPham)," +
                    "FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon) ON DELETE CASCADE," +
                    "FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham) ON DELETE CASCADE" +
                    ")";
            stmt.executeUpdate(sql);
            System.out.println("ChiTietHoaDon table created or already exists");
        } catch (SQLException e) {
            System.err.println("Error creating ChiTietHoaDon table: " + e.getMessage());
        }
    }

    public boolean addChiTietHoaDon(String maHoaDon, String maSanPham, int soLuong, double donGia) {
        String query = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaSanPham, SoLuong, DonGia) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, maHoaDon);
            pstmt.setString(2, maSanPham);
            pstmt.setInt(3, soLuong);
            pstmt.setDouble(4, donGia);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding invoice detail: " + e.getMessage());
            return false;
        }
    }

    public List<Object[]> getBestSellingProducts(int limit) {
        List<Object[]> results = new ArrayList<>();
        String query = "SELECT p.MaSanPham, p.TenSanPham, SUM(ct.SoLuong) AS TotalSold " +
                "FROM SanPham p " +
                "LEFT JOIN ChiTietHoaDon ct ON p.MaSanPham = ct.MaSanPham " +
                "GROUP BY p.MaSanPham " +
                "ORDER BY TotalSold DESC " +
                "LIMIT ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[3];
                    row[0] = rs.getString("MaSanPham");
                    row[1] = rs.getString("TenSanPham");
                    row[2] = rs.getInt("TotalSold");
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting best selling products: " + e.getMessage());
        }

        return results;
    }

    public List<Object[]> getRevenueByProduct(java.util.Date startDate, java.util.Date endDate) {
        List<Object[]> results = new ArrayList<>();
        String query = "SELECT p.MaSanPham, p.TenSanPham, SUM(ct.SoLuong * ct.DonGia) AS DoanhThu " +
                "FROM SanPham p " +
                "JOIN ChiTietHoaDon ct ON p.MaSanPham = ct.MaSanPham " +
                "JOIN HoaDon h ON ct.MaHoaDon = h.MaHoaDon " +
                "WHERE h.NgayLap BETWEEN ? AND ? " +
                "GROUP BY p.MaSanPham " +
                "ORDER BY DoanhThu DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
            pstmt.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[3];
                    row[0] = rs.getString("MaSanPham");
                    row[1] = rs.getString("TenSanPham");
                    row[2] = rs.getDouble("DoanhThu");
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting revenue by product: " + e.getMessage());
        }

        return results;
    }
}