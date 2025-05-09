package DAO;

import DTO.ProductDTO;
import DTO.ProductCategoryDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private Connection connection;

    public ProductDAO() {
        connection = DBConnection.getConnection();
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try {
            Statement stmt = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS SanPham (" +
                    "MaSanPham VARCHAR(10) PRIMARY KEY," +
                    "TenSanPham NVARCHAR(100) NOT NULL," +
                    "MaLoaiSP VARCHAR(10) NOT NULL," +
                    "MoTa NVARCHAR(255)," +
                    "HinhAnh VARCHAR(255)," +
                    "DonViTinh VARCHAR(20)," +
                    "GiaBan DECIMAL(10,2) NOT NULL DEFAULT 0," +
                    "FOREIGN KEY (MaLoaiSP) REFERENCES LoaiSanPham(MaLoaiSP)" +
                    ")";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error creating SanPham table: " + e.getMessage());
        }
    }

    public List<ProductDTO> getAllProducts() {
        List<ProductDTO> products = new ArrayList<>();
        String query = "SELECT p.*, l.TenLoaiSP as TenLoai FROM SanPham p " +
                "LEFT JOIN LoaiSanPham l ON p.MaLoaiSP = l.MaLoaiSP";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ProductDTO product = new ProductDTO();
                product.setProductId(rs.getString("MaSanPham"));
                product.setProductName(rs.getString("TenSanPham"));
                product.setCategoryId(rs.getString("MaLoaiSP"));
                product.setDescription(rs.getString("MoTa"));
                product.setImage(rs.getString("HinhAnh"));
                product.setUnit(rs.getString("DonViTinh"));
                product.setCategoryName(rs.getString("TenLoai"));
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Error getting products: " + e.getMessage());
        }

        return products;
    }

    public List<ProductDTO> getProductsByCategory(String categoryId) {
        List<ProductDTO> products = new ArrayList<>();
        String query = "SELECT p.*, l.TenLoaiSP as TenLoai FROM SanPham p " +
                "LEFT JOIN LoaiSanPham l ON p.MaLoaiSP = l.MaLoaiSP " +
                "WHERE p.MaLoaiSP = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, categoryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ProductDTO product = new ProductDTO();
                    product.setProductId(rs.getString("MaSanPham"));
                    product.setProductName(rs.getString("TenSanPham"));
                    product.setCategoryId(rs.getString("MaLoaiSP"));
                    product.setDescription(rs.getString("MoTa"));
                    product.setImage(rs.getString("HinhAnh"));
                    product.setUnit(rs.getString("DonViTinh"));
                    product.setCategoryName(rs.getString("TenLoai"));
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting products by category: " + e.getMessage());
        }

        return products;
    }

    public ProductDTO getProductById(String productId) {
        String query = "SELECT p.*, l.TenLoaiSP as TenLoai FROM SanPham p " +
                "LEFT JOIN LoaiSanPham l ON p.MaLoaiSP = l.MaLoaiSP " +
                "WHERE p.MaSanPham = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, productId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ProductDTO product = new ProductDTO();
                    product.setProductId(rs.getString("MaSanPham"));
                    product.setProductName(rs.getString("TenSanPham"));
                    product.setCategoryId(rs.getString("MaLoaiSP"));
                    product.setDescription(rs.getString("MoTa"));
                    product.setImage(rs.getString("HinhAnh"));
                    product.setUnit(rs.getString("DonViTinh"));
                    product.setCategoryName(rs.getString("TenLoai"));
                    return product;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting product by ID: " + e.getMessage());
        }

        return null;
    }

    public boolean addProduct(ProductDTO product) {
        String query = "INSERT INTO SanPham (MaSanPham, TenSanPham, MaLoaiSP, MoTa, HinhAnh, DonViTinh, GiaBan) VALUES (?, ?, ?, ?, ?, ?, 0)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, product.getProductId());
            pstmt.setString(2, product.getProductName());
            pstmt.setString(3, product.getCategoryId());
            pstmt.setString(4, product.getDescription());
            pstmt.setString(5, product.getImage());
            pstmt.setString(6, product.getUnit());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProduct(ProductDTO product) {
        String query = "UPDATE SanPham SET TenSanPham = ?, MaLoaiSP = ?, MoTa = ?, HinhAnh = ?, DonViTinh = ? WHERE MaSanPham = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, product.getProductName());
            pstmt.setString(2, product.getCategoryId());
            pstmt.setString(3, product.getDescription());
            pstmt.setString(4, product.getImage());
            pstmt.setString(5, product.getUnit());
            pstmt.setString(6, product.getProductId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProduct(String productId) {
        String query = "DELETE FROM SanPham WHERE MaSanPham = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, productId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            return false;
        }
    }

    public String generateNewProductId() {
        String newId = "SP001";
        String query = "SELECT MAX(MaSanPham) FROM SanPham";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next() && rs.getString(1) != null) {
                String lastId = rs.getString(1);
                int number = Integer.parseInt(lastId.substring(2)) + 1;
                newId = "SP" + String.format("%03d", number);
            }
        } catch (SQLException e) {
            System.err.println("Error generating new product ID: " + e.getMessage());
        }

        return newId;
    }

    // Statistics methods
    public List<ProductDTO> getBestSellingProducts(int limit) {
        List<ProductDTO> products = new ArrayList<>();
        String query = "SELECT p.*, l.TenLoaiSP as TenLoai, COALESCE(SUM(ct.SoLuong), 0) AS SoLuongBan " +
                "FROM SanPham p " +
                "LEFT JOIN LoaiSanPham l ON p.MaLoaiSP = l.MaLoaiSP " +
                "LEFT JOIN ChiTietHoaDon ct ON p.MaSanPham = ct.MaSanPham " +
                "GROUP BY p.MaSanPham " +
                "ORDER BY SoLuongBan DESC " +
                "LIMIT ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ProductDTO product = new ProductDTO();
                    product.setProductId(rs.getString("MaSanPham"));
                    product.setProductName(rs.getString("TenSanPham"));
                    product.setCategoryId(rs.getString("MaLoaiSP"));
                    product.setDescription(rs.getString("MoTa"));
                    product.setImage(rs.getString("HinhAnh"));
                    product.setUnit(rs.getString("DonViTinh"));
                    product.setCategoryName(rs.getString("TenLoai"));
                    product.setSoldQuantity(rs.getInt("SoLuongBan"));
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting best selling products: " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }

    public List<Object[]> getInventoryStatistics(boolean lowStock, int limit) {
        List<Object[]> results = new ArrayList<>();
        String query = "SELECT sp.MaSanPham, sp.TenSanPham, " +
                "COUNT(ctsp.MaChiTietSanPham) AS TonKho, l.TenLoaiSP as TenLoai " +
                "FROM SanPham sp " +
                "LEFT JOIN LoaiSanPham l ON sp.MaLoaiSP = l.MaLoaiSP " +
                "LEFT JOIN ChiTietSanPham ctsp ON sp.MaSanPham = ctsp.MaSanPham " +
                "GROUP BY sp.MaSanPham " +
                "ORDER BY TonKho " + (lowStock ? "ASC" : "DESC") + " " +
                "LIMIT ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[4];
                    row[0] = rs.getString("MaSanPham");
                    row[1] = rs.getString("TenSanPham");
                    row[2] = rs.getInt("TonKho");
                    row[3] = rs.getString("TenLoai");
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting inventory statistics: " + e.getMessage());
        }

        return results;
    }

    public List<Object[]> getInventoryStatistics(boolean lowStock) {
        return getInventoryStatistics(lowStock, 10); // Default limit to 10
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
            pstmt.setDate(1, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(2, new java.sql.Date(endDate.getTime()));

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
            e.printStackTrace();
        }

        return results;
    }

    public List<Object[]> getRevenueByProduct() {
        java.util.Date now = new java.util.Date();
        java.util.Date startDate = new java.util.Date(now.getTime() - 30L * 24 * 60 * 60 * 1000); // 30 days ago
        return getRevenueByProduct(startDate, now);
    }

    public List<Object[]> getRevenueByCategory(java.util.Date startDate, java.util.Date endDate) {
        List<Object[]> results = new ArrayList<>();
        String query = "SELECT l.MaLoaiSP, l.TenLoaiSP, SUM(ct.SoLuong * ct.DonGia) AS DoanhThu " +
                "FROM LoaiSanPham l " +
                "JOIN SanPham p ON l.MaLoaiSP = p.MaLoaiSP " +
                "JOIN ChiTietHoaDon ct ON p.MaSanPham = ct.MaSanPham " +
                "JOIN HoaDon h ON ct.MaHoaDon = h.MaHoaDon " +
                "WHERE h.NgayLap BETWEEN ? AND ? " +
                "GROUP BY l.MaLoaiSP " +
                "ORDER BY DoanhThu DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
            pstmt.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[3];
                    row[0] = rs.getString("MaLoaiSP");
                    row[1] = rs.getString("TenLoaiSP");
                    row[2] = rs.getDouble("DoanhThu");
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting revenue by category: " + e.getMessage());
        }

        return results;
    }

    public List<Object[]> getRevenueByCategory() {
        java.util.Date now = new java.util.Date();
        java.util.Date startDate = new java.util.Date(now.getTime() - 30L * 24 * 60 * 60 * 1000); // 30 days ago
        return getRevenueByCategory(startDate, now);
    }

    /**
     * Lấy thông tin loại sản phẩm theo ID
     * 
     * @param categoryId ID loại sản phẩm cần lấy
     * @return Đối tượng loại sản phẩm nếu tìm thấy, null nếu không tìm thấy
     */
    public ProductCategoryDTO getCategoryById(String categoryId) {
        ProductCategoryDTO category = null;
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }

            String query = "SELECT * FROM loaisanpham WHERE MaLoaiSanPham = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, categoryId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                category = new ProductCategoryDTO();
                category.setCategoryId(rs.getString("MaLoaiSanPham"));
                category.setCategoryName(rs.getString("TenLoaiSanPham"));
                category.setDescription(rs.getString("MoTa"));
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin loại sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
        return category;
    }

    /**
     * Thêm loại sản phẩm mới vào CSDL
     * 
     * @param category Đối tượng loại sản phẩm cần thêm
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean addCategory(ProductCategoryDTO category) {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }

            // Kiểm tra xem loại sản phẩm đã tồn tại chưa
            String checkQuery = "SELECT COUNT(*) FROM loaisanpham WHERE MaLoaiSanPham = ?";
            PreparedStatement ps = connection.prepareStatement(checkQuery);
            ps.setString(1, category.getCategoryId());
            ResultSet rs = ps.executeQuery();

            boolean exists = false;
            if (rs.next() && rs.getInt(1) > 0) {
                exists = true;
            }

            rs.close();
            ps.close();

            if (exists) {
                // Loại sản phẩm đã tồn tại, thực hiện cập nhật
                String updateQuery = "UPDATE loaisanpham SET TenLoaiSanPham = ?, MoTa = ? WHERE MaLoaiSanPham = ?";
                ps = connection.prepareStatement(updateQuery);
                ps.setString(1, category.getCategoryName());
                ps.setString(2, category.getDescription());
                ps.setString(3, category.getCategoryId());
                int result = ps.executeUpdate();
                ps.close();
                return result > 0;
            } else {
                // Loại sản phẩm chưa tồn tại, thêm mới
                String insertQuery = "INSERT INTO loaisanpham (MaLoaiSanPham, TenLoaiSanPham, MoTa) VALUES (?, ?, ?)";
                ps = connection.prepareStatement(insertQuery);
                ps.setString(1, category.getCategoryId());
                ps.setString(2, category.getCategoryName());
                ps.setString(3, category.getDescription());
                int result = ps.executeUpdate();
                ps.close();
                return result > 0;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm loại sản phẩm: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}