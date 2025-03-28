package DAO;

import DTO.ProductCategory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryDAO {
    private Connection connection;

    public ProductCategoryDAO() {
        connection = DBConnection.getConnection();
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try {
            Statement stmt = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS LoaiSanPham (" +
                    "MaLoaiSP VARCHAR(10) PRIMARY KEY," +
                    "TenLoaiSP NVARCHAR(100) NOT NULL," +
                    "MoTa NVARCHAR(255)" +
                    ")";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error creating ProductCategory table: " + e.getMessage());
        }
    }

    public List<ProductCategory> getAllCategories() {
        List<ProductCategory> categories = new ArrayList<>();
        String query = "SELECT * FROM LoaiSanPham";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ProductCategory category = new ProductCategory();
                category.setCategoryId(rs.getString("MaLoaiSP"));
                category.setCategoryName(rs.getString("TenLoaiSP"));
                category.setDescription(rs.getString("MoTa"));
                categories.add(category);
            }
        } catch (SQLException e) {
            System.err.println("Error getting categories: " + e.getMessage());
        }

        return categories;
    }

    public ProductCategory getCategoryById(String categoryId) {
        String query = "SELECT * FROM LoaiSanPham WHERE MaLoaiSP = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, categoryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ProductCategory category = new ProductCategory();
                    category.setCategoryId(rs.getString("MaLoaiSP"));
                    category.setCategoryName(rs.getString("TenLoaiSP"));
                    category.setDescription(rs.getString("MoTa"));
                    return category;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting category by ID: " + e.getMessage());
        }

        return null;
    }

    public boolean addCategory(ProductCategory category) {
        String query = "INSERT INTO LoaiSanPham (MaLoaiSP, TenLoaiSP, MoTa) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, category.getCategoryId());
            pstmt.setString(2, category.getCategoryName());
            pstmt.setString(3, category.getDescription());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding category: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCategory(ProductCategory category) {
        String query = "UPDATE LoaiSanPham SET TenLoaiSP = ?, MoTa = ? WHERE MaLoaiSP = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, category.getCategoryName());
            pstmt.setString(2, category.getDescription());
            pstmt.setString(3, category.getCategoryId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCategory(String categoryId) {
        String query = "DELETE FROM LoaiSanPham WHERE MaLoaiSP = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, categoryId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
            return false;
        }
    }

    public String generateNewCategoryId() {
        String newId = "LSP001";
        String query = "SELECT MAX(MaLoaiSP) FROM LoaiSanPham";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next() && rs.getString(1) != null) {
                String lastId = rs.getString(1);
                int number = Integer.parseInt(lastId.substring(3)) + 1;
                newId = "LSP" + String.format("%03d", number);
            }
        } catch (SQLException e) {
            System.err.println("Error generating new category ID: " + e.getMessage());
        }

        return newId;
    }
}