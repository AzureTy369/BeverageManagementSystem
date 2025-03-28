package DAO;

import DTO.ProductDetail;
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
                    "MaChiTiet VARCHAR(10) PRIMARY KEY," +
                    "MaSanPham VARCHAR(10) NOT NULL," +
                    "SoLuong INT NOT NULL," +
                    "NgayNhap DATE NOT NULL," +
                    "HanSuDung DATE," +
                    "SoLo VARCHAR(50)," +
                    "MaNhaCungCap VARCHAR(10) NOT NULL," +
                    "FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)," +
                    "FOREIGN KEY (MaNhaCungCap) REFERENCES NhaCungCap(MaNhaCungCap)" +
                    ")";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error creating ProductDetail table: " + e.getMessage());
        }
    }

    public List<ProductDetail> getAllProductDetails() {
        List<ProductDetail> details = new ArrayList<>();
        String query = "SELECT pd.*, p.TenSanPham, ncc.TenNhaCungCap " +
                "FROM ChiTietSanPham pd " +
                "JOIN SanPham p ON pd.MaSanPham = p.MaSanPham " +
                "JOIN NhaCungCap ncc ON pd.MaNhaCungCap = ncc.MaNhaCungCap";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ProductDetail detail = new ProductDetail();
                detail.setDetailId(rs.getString("MaChiTiet"));
                detail.setProductId(rs.getString("MaSanPham"));
                detail.setQuantity(rs.getInt("SoLuong"));
                detail.setImportDate(rs.getDate("NgayNhap"));
                detail.setExpiryDate(rs.getDate("HanSuDung"));
                detail.setBatchNumber(rs.getString("SoLo"));
                detail.setSupplierId(rs.getString("MaNhaCungCap"));
                detail.setProductName(rs.getString("TenSanPham"));
                detail.setSupplierName(rs.getString("TenNhaCungCap"));
                details.add(detail);
            }
        } catch (SQLException e) {
            System.err.println("Error getting product details: " + e.getMessage());
        }

        return details;
    }

    public List<ProductDetail> getProductDetailsByProduct(String productId) {
        List<ProductDetail> details = new ArrayList<>();
        String query = "SELECT pd.*, p.TenSanPham, ncc.TenNhaCungCap " +
                "FROM ChiTietSanPham pd " +
                "JOIN SanPham p ON pd.MaSanPham = p.MaSanPham " +
                "JOIN NhaCungCap ncc ON pd.MaNhaCungCap = ncc.MaNhaCungCap " +
                "WHERE pd.MaSanPham = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, productId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ProductDetail detail = new ProductDetail();
                    detail.setDetailId(rs.getString("MaChiTiet"));
                    detail.setProductId(rs.getString("MaSanPham"));
                    detail.setQuantity(rs.getInt("SoLuong"));
                    detail.setImportDate(rs.getDate("NgayNhap"));
                    detail.setExpiryDate(rs.getDate("HanSuDung"));
                    detail.setBatchNumber(rs.getString("SoLo"));
                    detail.setSupplierId(rs.getString("MaNhaCungCap"));
                    detail.setProductName(rs.getString("TenSanPham"));
                    detail.setSupplierName(rs.getString("TenNhaCungCap"));
                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting product details by product: " + e.getMessage());
        }

        return details;
    }

    public ProductDetail getProductDetailById(String detailId) {
        String query = "SELECT pd.*, p.TenSanPham, ncc.TenNhaCungCap " +
                "FROM ChiTietSanPham pd " +
                "JOIN SanPham p ON pd.MaSanPham = p.MaSanPham " +
                "JOIN NhaCungCap ncc ON pd.MaNhaCungCap = ncc.MaNhaCungCap " +
                "WHERE pd.MaChiTiet = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, detailId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ProductDetail detail = new ProductDetail();
                    detail.setDetailId(rs.getString("MaChiTiet"));
                    detail.setProductId(rs.getString("MaSanPham"));
                    detail.setQuantity(rs.getInt("SoLuong"));
                    detail.setImportDate(rs.getDate("NgayNhap"));
                    detail.setExpiryDate(rs.getDate("HanSuDung"));
                    detail.setBatchNumber(rs.getString("SoLo"));
                    detail.setSupplierId(rs.getString("MaNhaCungCap"));
                    detail.setProductName(rs.getString("TenSanPham"));
                    detail.setSupplierName(rs.getString("TenNhaCungCap"));
                    return detail;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting product detail by ID: " + e.getMessage());
        }

        return null;
    }

    public boolean addProductDetail(ProductDetail detail) {
        String query = "INSERT INTO ChiTietSanPham (MaChiTiet, MaSanPham, SoLuong, NgayNhap, HanSuDung, SoLo, MaNhaCungCap) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, detail.getDetailId());
            pstmt.setString(2, detail.getProductId());
            pstmt.setInt(3, detail.getQuantity());
            pstmt.setDate(4, new java.sql.Date(detail.getImportDate().getTime()));
            if (detail.getExpiryDate() != null) {
                pstmt.setDate(5, new java.sql.Date(detail.getExpiryDate().getTime()));
            } else {
                pstmt.setNull(5, Types.DATE);
            }
            pstmt.setString(6, detail.getBatchNumber());
            pstmt.setString(7, detail.getSupplierId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding product detail: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProductDetail(ProductDetail detail) {
        String query = "UPDATE ChiTietSanPham SET MaSanPham = ?, SoLuong = ?, NgayNhap = ?, HanSuDung = ?, SoLo = ?, MaNhaCungCap = ? "
                +
                "WHERE MaChiTiet = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, detail.getProductId());
            pstmt.setInt(2, detail.getQuantity());
            pstmt.setDate(3, new java.sql.Date(detail.getImportDate().getTime()));
            if (detail.getExpiryDate() != null) {
                pstmt.setDate(4, new java.sql.Date(detail.getExpiryDate().getTime()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }
            pstmt.setString(5, detail.getBatchNumber());
            pstmt.setString(6, detail.getSupplierId());
            pstmt.setString(7, detail.getDetailId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product detail: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProductDetail(String detailId) {
        String query = "DELETE FROM ChiTietSanPham WHERE MaChiTiet = ?";

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
        String query = "SELECT MAX(MaChiTiet) FROM ChiTietSanPham";

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