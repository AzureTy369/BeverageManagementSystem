package DAO;

import DTO.ProductDetailDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailDAO {
    private Connection connection;

    public ProductDetailDAO() {
        connection = DBConnection.getConnection();
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try {
            Statement stmt = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS ChiTietSanPham (" +
                    "MaChiTietSanPham VARCHAR(10) PRIMARY KEY," +
                    "MaSanPham VARCHAR(10) NOT NULL," +
                    "KichThuoc VARCHAR(50)," +
                    "Gia DECIMAL(10, 2) NOT NULL," +
                    "FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)" +
                    ")";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error creating ChiTietSanPham table: " + e.getMessage());
        }
    }

    public List<ProductDetailDTO> getAllProductDetails() {
        List<ProductDetailDTO> details = new ArrayList<>();
        String query = "SELECT ct.*, p.TenSanPham FROM ChiTietSanPham ct " +
                "JOIN SanPham p ON ct.MaSanPham = p.MaSanPham";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ProductDetailDTO detail = new ProductDetailDTO();
                detail.setDetailId(rs.getString("MaChiTietSanPham"));
                detail.setProductId(rs.getString("MaSanPham"));
                detail.setSize(rs.getString("KichThuoc"));
                detail.setPrice(rs.getDouble("Gia"));
                detail.setProductName(rs.getString("TenSanPham"));
                details.add(detail);
            }
        } catch (SQLException e) {
            System.err.println("Error getting product details: " + e.getMessage());
        }

        return details;
    }

    public List<ProductDetailDTO> getProductDetailsByProduct(String productId) {
        List<ProductDetailDTO> details = new ArrayList<>();
        String query = "SELECT ct.*, p.TenSanPham FROM ChiTietSanPham ct " +
                "JOIN SanPham p ON ct.MaSanPham = p.MaSanPham " +
                "WHERE ct.MaSanPham = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, productId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ProductDetailDTO detail = new ProductDetailDTO();
                    detail.setDetailId(rs.getString("MaChiTietSanPham"));
                    detail.setProductId(rs.getString("MaSanPham"));
                    detail.setSize(rs.getString("KichThuoc"));
                    detail.setPrice(rs.getDouble("Gia"));
                    detail.setProductName(rs.getString("TenSanPham"));
                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting product details by product: " + e.getMessage());
        }

        return details;
    }

    public ProductDetailDTO getProductDetailById(String detailId) {
        String query = "SELECT ct.*, p.TenSanPham FROM ChiTietSanPham ct " +
                "JOIN SanPham p ON ct.MaSanPham = p.MaSanPham " +
                "WHERE ct.MaChiTietSanPham = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, detailId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ProductDetailDTO detail = new ProductDetailDTO();
                    detail.setDetailId(rs.getString("MaChiTietSanPham"));
                    detail.setProductId(rs.getString("MaSanPham"));
                    detail.setSize(rs.getString("KichThuoc"));
                    detail.setPrice(rs.getDouble("Gia"));
                    detail.setProductName(rs.getString("TenSanPham"));
                    return detail;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting product detail by ID: " + e.getMessage());
        }

        return null;
    }

    public boolean addProductDetail(ProductDetailDTO detail) {
        String query = "INSERT INTO ChiTietSanPham (MaChiTietSanPham, MaSanPham, KichThuoc, Gia) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, detail.getDetailId());
            pstmt.setString(2, detail.getProductId());
            pstmt.setString(3, detail.getSize());
            pstmt.setDouble(4, detail.getPrice());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding product detail: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProductDetail(ProductDetailDTO detail) {
        String query = "UPDATE ChiTietSanPham SET MaSanPham = ?, KichThuoc = ?, Gia = ? WHERE MaChiTietSanPham = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, detail.getProductId());
            pstmt.setString(2, detail.getSize());
            pstmt.setDouble(3, detail.getPrice());
            pstmt.setString(4, detail.getDetailId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product detail: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProductDetail(String detailId) {
        String query = "DELETE FROM ChiTietSanPham WHERE MaChiTietSanPham = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, detailId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting product detail: " + e.getMessage());
            return false;
        }
    }

    public String generateNewDetailId() {
        String newId = "CTSP001";
        String query = "SELECT MAX(MaChiTietSanPham) FROM ChiTietSanPham";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next() && rs.getString(1) != null) {
                String lastId = rs.getString(1);
                int number = Integer.parseInt(lastId.substring(4)) + 1;
                newId = "CTSP" + String.format("%03d", number);
            }
        } catch (SQLException e) {
            System.err.println("Error generating new detail ID: " + e.getMessage());
        }

        return newId;
    }
}