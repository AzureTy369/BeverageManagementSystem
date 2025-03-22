package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/beveragemanagementsystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static Connection connection;

    private DBConnection() {
        // Private constructor to prevent instantiation
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("Connection failed: " + e.getMessage());
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static boolean setupDatabase() {
        try {
            // Connect to MySQL server without selecting database
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();

            // Create database if not exists
            stmt.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS beveragemanagementsystem CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");

            // Use the database
            stmt.executeUpdate("USE beveragemanagementsystem");

            // Create tables
            // ChucVu (Position) table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ChucVu (" +
                    "MaChucVu VARCHAR(10) PRIMARY KEY," +
                    "TenChucVu NVARCHAR(50) NOT NULL," +
                    "Luong DECIMAL(10, 2) NOT NULL" +
                    ")");

            // NhanVien (Employee) table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS NhanVien (" +
                    "MaNhanVien VARCHAR(10) PRIMARY KEY," +
                    "TenTaiKhoan VARCHAR(50) NOT NULL UNIQUE," +
                    "MatKhau VARCHAR(50) NOT NULL," +
                    "Ho NVARCHAR(50) NOT NULL," +
                    "Ten NVARCHAR(50) NOT NULL," +
                    "MaChucVu VARCHAR(10) NOT NULL," +
                    "SoDienThoai VARCHAR(15) NOT NULL," +
                    "FOREIGN KEY (MaChucVu) REFERENCES ChucVu(MaChucVu)" +
                    ")");

            // NhaCungCap (Supplier) table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS NhaCungCap (" +
                    "MaNhaCungCap VARCHAR(10) PRIMARY KEY," +
                    "TenNhaCungCap NVARCHAR(100) NOT NULL," +
                    "SoDienThoai VARCHAR(15) NOT NULL," +
                    "DiaChi NVARCHAR(255)," +
                    "Email NVARCHAR(100)" +
                    ")");

            // LoaiSanPham (ProductCategory) table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS LoaiSanPham (" +
                    "MaLoaiSP VARCHAR(10) PRIMARY KEY," +
                    "TenLoaiSP NVARCHAR(100) NOT NULL," +
                    "MoTa NVARCHAR(255)" +
                    ")");

            // SanPham (Product) table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS SanPham (" +
                    "MaSanPham VARCHAR(10) PRIMARY KEY," +
                    "TenSanPham NVARCHAR(100) NOT NULL," +
                    "MaLoaiSP VARCHAR(10) NOT NULL," +
                    "MoTa NVARCHAR(255)," +
                    "HinhAnh VARCHAR(255)," +
                    "GiaBan DECIMAL(10, 2) NOT NULL," +
                    "FOREIGN KEY (MaLoaiSP) REFERENCES LoaiSanPham(MaLoaiSP)" +
                    ")");

            // ChiTietSanPham (ProductDetail) table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ChiTietSanPham (" +
                    "MaChiTiet VARCHAR(10) PRIMARY KEY," +
                    "MaSanPham VARCHAR(10) NOT NULL," +
                    "SoLuong INT NOT NULL," +
                    "NgayNhap DATE NOT NULL," +
                    "HanSuDung DATE," +
                    "SoLo VARCHAR(50)," +
                    "MaNhaCungCap VARCHAR(10) NOT NULL," +
                    "FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)," +
                    "FOREIGN KEY (MaNhaCungCap) REFERENCES NhaCungCap(MaNhaCungCap)" +
                    ")");

            // HoaDon (Invoice) table for revenue statistics
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS HoaDon (" +
                    "MaHoaDon VARCHAR(10) PRIMARY KEY," +
                    "MaNhanVien VARCHAR(10) NOT NULL," +
                    "NgayLap DATETIME NOT NULL," +
                    "TongTien DECIMAL(10, 2) NOT NULL," +
                    "FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)" +
                    ")");

            // ChiTietHoaDon (InvoiceDetail) table for revenue statistics
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ChiTietHoaDon (" +
                    "MaHoaDon VARCHAR(10) NOT NULL," +
                    "MaSanPham VARCHAR(10) NOT NULL," +
                    "SoLuong INT NOT NULL," +
                    "DonGia DECIMAL(10, 2) NOT NULL," +
                    "PRIMARY KEY (MaHoaDon, MaSanPham)," +
                    "FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon)," +
                    "FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)" +
                    ")");

            // Insert default data if not exists
            // Check if there's any data in ChucVu table
            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM ChucVu");
            rs.next();
            if (rs.getInt(1) == 0) {
                // Insert positions
                stmt.executeUpdate("INSERT INTO ChucVu VALUES ('CV001', 'Quản trị viên', 20000000)");
                stmt.executeUpdate("INSERT INTO ChucVu VALUES ('CV002', 'Quản lý', 15000000)");
                stmt.executeUpdate("INSERT INTO ChucVu VALUES ('CV003', 'Nhân viên bán hàng', 8000000)");
            }

            // Check if there's any data in NhanVien table
            rs = stmt.executeQuery("SELECT COUNT(*) FROM NhanVien");
            rs.next();
            if (rs.getInt(1) == 0) {
                // Insert admin employee
                stmt.executeUpdate(
                        "INSERT INTO NhanVien VALUES ('NV001', 'admin', 'admin123', 'Admin', 'User', 'CV001', '0987654321')");
            }

            // Check if there's any data in LoaiSanPham table
            rs = stmt.executeQuery("SELECT COUNT(*) FROM LoaiSanPham");
            rs.next();
            if (rs.getInt(1) == 0) {
                // Insert some default product categories
                stmt.executeUpdate(
                        "INSERT INTO LoaiSanPham VALUES ('LSP001', 'Nước giải khát', 'Các loại đồ uống giải khát')");
                stmt.executeUpdate("INSERT INTO LoaiSanPham VALUES ('LSP002', 'Bia', 'Các loại bia')");
                stmt.executeUpdate(
                        "INSERT INTO LoaiSanPham VALUES ('LSP003', 'Nước suối', 'Các loại nước suối đóng chai')");
            }

            stmt.close();
            conn.close();

            System.out.println("Database setup completed successfully.");
            return true;

        } catch (SQLException e) {
            System.err.println("Error setting up database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}