package database;

import model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

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
                    "CategoryId VARCHAR(10) NOT NULL," +
                    "MoTa NVARCHAR(255)," +
                    "HinhAnh VARCHAR(255)," +
                    "GiaBan DECIMAL(10, 2) NOT NULL," +
                    "FOREIGN KEY (CategoryId) REFERENCES LoaiSanPham(MaLoaiSP)" +
                    ")";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error creating Product table: " + e.getMessage());
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.*, lsp.TenLoaiSP FROM SanPham p " +
                "JOIN LoaiSanPham lsp ON p.MaLoaiSP = lsp.MaLoaiSP";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getString("MaSanPham"));
                product.setProductName(rs.getString("TenSanPham"));
                product.setCategoryId(rs.getString("MaLoaiSP"));
                product.setDescription(rs.getString("MoTa"));
                product.setImage(rs.getString("HinhAnh"));
                product.setPrice(rs.getDouble("GiaBan"));
                product.setCategoryName(rs.getString("TenLoaiSP"));
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Error getting products: " + e.getMessage());
        }

        return products;
    }

    public List<Product> getProductsByCategory(String categoryId) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.*, lsp.TenLoaiSP FROM SanPham p " +
                "JOIN LoaiSanPham lsp ON p.MaLoaiSP = lsp.MaLoaiSP " +
                "WHERE p.MaLoaiSP = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, categoryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setProductId(rs.getString("MaSanPham"));
                    product.setProductName(rs.getString("TenSanPham"));
                    product.setCategoryId(rs.getString("MaLoaiSP"));
                    product.setDescription(rs.getString("MoTa"));
                    product.setImage(rs.getString("HinhAnh"));
                    product.setPrice(rs.getDouble("GiaBan"));
                    product.setCategoryName(rs.getString("TenLoaiSP"));
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting products by category: " + e.getMessage());
        }

        return products;
    }

    public Product getProductById(String productId) {
        String query = "SELECT p.*, lsp.TenLoaiSP FROM SanPham p " +
                "JOIN LoaiSanPham lsp ON p.MaLoaiSP = lsp.MaLoaiSP " +
                "WHERE p.MaSanPham = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, productId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Product product = new Product();
                    product.setProductId(rs.getString("MaSanPham"));
                    product.setProductName(rs.getString("TenSanPham"));
                    product.setCategoryId(rs.getString("MaLoaiSP"));
                    product.setDescription(rs.getString("MoTa"));
                    product.setImage(rs.getString("HinhAnh"));
                    product.setPrice(rs.getDouble("GiaBan"));
                    product.setCategoryName(rs.getString("TenLoaiSP"));
                    return product;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting product by ID: " + e.getMessage());
        }

        return null;
    }

    public boolean addProduct(Product product) {
        String query = "INSERT INTO SanPham (MaSanPham, TenSanPham, MaLoaiSP, MoTa, HinhAnh, GiaBan) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, product.getProductId());
            pstmt.setString(2, product.getProductName());
            pstmt.setString(3, product.getCategoryId());
            pstmt.setString(4, product.getDescription());
            pstmt.setString(5, product.getImage());
            pstmt.setDouble(6, product.getPrice());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProduct(Product product) {
        String query = "UPDATE SanPham SET TenSanPham = ?, MaLoaiSP = ?, MoTa = ?, HinhAnh = ?, GiaBan = ? WHERE MaSanPham = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, product.getProductName());
            pstmt.setString(2, product.getCategoryId());
            pstmt.setString(3, product.getDescription());
            pstmt.setString(4, product.getImage());
            pstmt.setDouble(5, product.getPrice());
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
    public List<Product> getBestSellingProducts(int limit) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.MaSanPham, p.TenSanPham, p.MaLoaiSP, p.MoTa, p.HinhAnh, p.GiaBan, lsp.TenLoaiSP, " +
                "SUM(ct.SoLuong) AS TongBan " +
                "FROM SanPham p " +
                "JOIN ChiTietHoaDon ct ON p.MaSanPham = ct.MaSanPham " +
                "JOIN LoaiSanPham lsp ON p.MaLoaiSP = lsp.MaLoaiSP " +
                "GROUP BY p.MaSanPham " +
                "ORDER BY TongBan DESC " +
                "LIMIT ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setProductId(rs.getString("MaSanPham"));
                    product.setProductName(rs.getString("TenSanPham"));
                    product.setCategoryId(rs.getString("MaLoaiSP"));
                    product.setDescription(rs.getString("MoTa"));
                    product.setImage(rs.getString("HinhAnh"));
                    product.setPrice(rs.getDouble("GiaBan"));
                    product.setCategoryName(rs.getString("TenLoaiSP"));

                    // Thêm thông tin tổng số lượng đã bán vào đối tượng Product
                    // Sử dụng thuộc tính description tạm thời để lưu thông tin này
                    product.setDescription(String.valueOf(rs.getInt("TongBan")));

                    products.add(product);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting best selling products: " + e.getMessage());
        }

        return products;
    }

    public List<Object[]> getInventoryStatistics(boolean lowStock, int limit) {
        List<Object[]> results = new ArrayList<>();
        String query = "SELECT p.MaSanPham, p.TenSanPham, COALESCE(SUM(pd.SoLuong), 0) AS TonKho, " +
                "lsp.TenLoaiSP " +
                "FROM SanPham p " +
                "LEFT JOIN ChiTietSanPham pd ON p.MaSanPham = pd.MaSanPham " +
                "JOIN LoaiSanPham lsp ON p.MaLoaiSP = lsp.MaLoaiSP " +
                "GROUP BY p.MaSanPham " +
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
                    row[3] = rs.getString("TenLoaiSP");
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting inventory statistics: " + e.getMessage());
        }

        return results;
    }

    public List<Object[]> getInventoryStatistics(boolean lowStock) {
        return getInventoryStatistics(lowStock, 100); // Mặc định lấy 100 bản ghi
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
            System.err.println("Error getting revenue by product with date range: " + e.getMessage());
        }

        return results;
    }

    public List<Object[]> getRevenueByProduct() {
        // Mặc định lấy dữ liệu của 5 năm gần nhất
        Calendar calendar = Calendar.getInstance();
        java.util.Date endDate = calendar.getTime();
        calendar.add(Calendar.YEAR, -5);
        java.util.Date startDate = calendar.getTime();

        return getRevenueByProduct(startDate, endDate);
    }

    public List<Object[]> getRevenueByCategory(java.util.Date startDate, java.util.Date endDate) {
        List<Object[]> results = new ArrayList<>();
        String query = "SELECT lsp.MaLoaiSP, lsp.TenLoaiSP, SUM(ct.SoLuong * ct.DonGia) AS DoanhThu " +
                "FROM LoaiSanPham lsp " +
                "JOIN SanPham p ON lsp.MaLoaiSP = p.MaLoaiSP " +
                "JOIN ChiTietHoaDon ct ON p.MaSanPham = ct.MaSanPham " +
                "JOIN HoaDon h ON ct.MaHoaDon = h.MaHoaDon " +
                "WHERE h.NgayLap BETWEEN ? AND ? " +
                "GROUP BY lsp.MaLoaiSP " +
                "ORDER BY DoanhThu DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDate(1, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(2, new java.sql.Date(endDate.getTime()));

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
            System.err.println("Error getting revenue by category with date range: " + e.getMessage());
        }

        return results;
    }

    public List<Object[]> getRevenueByCategory() {
        // Mặc định lấy dữ liệu của 5 năm gần nhất
        Calendar calendar = Calendar.getInstance();
        java.util.Date endDate = calendar.getTime();
        calendar.add(Calendar.YEAR, -5);
        java.util.Date startDate = calendar.getTime();

        return getRevenueByCategory(startDate, endDate);
    }
}