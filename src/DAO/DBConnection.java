package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;

public class DBConnection {
    private static Connection conn = null;

    // Thông tin kết nối cơ sở dữ liệu
    private static final String DB_URL = "jdbc:mysql://localhost:3306/beveragemanagementsystem";
    private static final String USER = "root";
    private static final String PASS = "";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = DB_URL;
    private static final String USERNAME = USER;
    private static final String PASSWORD = PASS;

    /**
     * Phương thức lấy kết nối đến CSDL
     */
    public static Connection getConnection() {
        try {
            // Kiểm tra và tải driver JDBC
            try {
                Class.forName(DRIVER);
                System.out.println("Đã tìm thấy driver JDBC: " + DRIVER);
            } catch (ClassNotFoundException e) {
                System.out.println("Không tìm thấy driver JDBC: " + DRIVER);
                System.out.println("Chi tiết lỗi: " + e.getMessage());
                e.printStackTrace();
                return null;
            }

            // Tạo kết nối đến CSDL
            System.out.println("Đang kết nối đến CSDL với URL: " + URL);
            System.out.println("Username: " + USERNAME);

            // Thêm kiểm tra kết nối hợp lệ
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            if (conn != null) {
                System.out.println("✓ Kết nối thành công đến CSDL!");

                // Kiểm tra metadata của CSDL
                DatabaseMetaData metaData = conn.getMetaData();
                System.out.println("Database Name: " + metaData.getDatabaseProductName());
                System.out.println("Database Version: " + metaData.getDatabaseProductVersion());
                System.out.println("JDBC Driver Name: " + metaData.getDriverName());
                System.out.println("JDBC Driver Version: " + metaData.getDriverVersion());

                return conn;
            } else {
                System.out.println("✗ Kết nối không thành công đến CSDL!");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("✗ Lỗi kết nối đến CSDL!");
            System.out.println("Chi tiết lỗi: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return null;
        }
    }

    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Đã đóng kết nối!");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi đóng kết nối: " + e.getMessage());
        }
    }

    public static boolean setupDatabase() {
        try {
            // Connect to MySQL server without selecting database
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", USER, PASS);
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

            // SanPham (Product) table - Cập nhật để sử dụng MaLoaiSP và thêm GiaBan
            try {
                // Kiểm tra xem bảng SanPham đã tồn tại chưa
                ResultSet tables = conn.getMetaData().getTables(null, null, "SanPham", null);
                if (tables.next()) {
                    // Bảng đã tồn tại, kiểm tra cột GiaBan
                    try {
                        stmt.executeUpdate("ALTER TABLE SanPham ADD COLUMN GiaBan DECIMAL(10, 2) NOT NULL DEFAULT 0");
                    } catch (SQLException e) {
                        // Cột đã tồn tại, không cần xử lý
                    }

                    // Kiểm tra và xóa ràng buộc khóa ngoại cũ (nếu có)
                    try {
                        stmt.executeUpdate("ALTER TABLE SanPham DROP FOREIGN KEY sanpham_ibfk_1");
                    } catch (SQLException e) {
                        // Ràng buộc không tồn tại, không cần xử lý
                    }

                    // Thay đổi cột MaLoai thành MaLoaiSP nếu cần
                    try {
                        stmt.executeUpdate("ALTER TABLE SanPham CHANGE MaLoai MaLoaiSP VARCHAR(10) NOT NULL");
                    } catch (SQLException e) {
                        // Có thể cột MaLoaiSP đã tồn tại
                    }

                    // Thêm ràng buộc khóa ngoại mới
                    try {
                        stmt.executeUpdate(
                                "ALTER TABLE SanPham ADD CONSTRAINT sanpham_ibfk_1 FOREIGN KEY (MaLoaiSP) REFERENCES LoaiSanPham(MaLoaiSP)");
                    } catch (SQLException e) {
                        // Có thể ràng buộc đã tồn tại
                    }
                } else {
                    // Bảng chưa tồn tại, tạo mới với cấu trúc đúng
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS SanPham (" +
                            "MaSanPham VARCHAR(10) PRIMARY KEY," +
                            "TenSanPham NVARCHAR(100) NOT NULL," +
                            "MaLoaiSP VARCHAR(10) NOT NULL," +
                            "MoTa NVARCHAR(255)," +
                            "HinhAnh VARCHAR(255)," +
                            "DonViTinh VARCHAR(20)," +
                            "GiaBan DECIMAL(10, 2) NOT NULL DEFAULT 0," +
                            "FOREIGN KEY (MaLoaiSP) REFERENCES LoaiSanPham(MaLoaiSP)" +
                            ")");
                }
            } catch (SQLException e) {
                System.err.println("Error updating SanPham table: " + e.getMessage());
            }

            // ChiTietSanPham (ProductDetail) table
            try {
                // Kiểm tra xem bảng đã tồn tại chưa
                ResultSet tables = conn.getMetaData().getTables(null, null, "ChiTietSanPham", null);
                if (tables.next()) {
                    // Cập nhật cấu trúc bảng nếu cần
                    try {
                        stmt.executeUpdate("ALTER TABLE ChiTietSanPham ADD COLUMN KichThuoc VARCHAR(50)");
                    } catch (SQLException e) {
                        // Cột đã tồn tại
                    }

                    try {
                        stmt.executeUpdate(
                                "ALTER TABLE ChiTietSanPham ADD COLUMN Gia DECIMAL(10, 2) NOT NULL DEFAULT 0");
                    } catch (SQLException e) {
                        // Cột đã tồn tại
                    }

                    // Kiểm tra và đổi tên cột MaChiTiet thành MaChiTietSanPham nếu cần
                    try {
                        ResultSet columns = conn.getMetaData().getColumns(null, null, "ChiTietSanPham", "MaChiTiet");
                        if (columns.next()) {
                            System.out.println("Attempting to rename column MaChiTiet to MaChiTietSanPham...");
                            stmt.executeUpdate(
                                    "ALTER TABLE ChiTietSanPham CHANGE COLUMN MaChiTiet MaChiTietSanPham VARCHAR(10) NOT NULL");
                            System.out.println("Column renamed successfully.");
                        }
                        columns.close();
                    } catch (SQLException e) {
                        System.err.println("Error checking or renaming MaChiTiet column: " + e.getMessage());
                    }
                } else {
                    // Tạo bảng mới với cấu trúc đúng
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ChiTietSanPham (" +
                            "MaChiTietSanPham VARCHAR(10) PRIMARY KEY," +
                            "MaSanPham VARCHAR(10) NOT NULL," +
                            "KichThuoc VARCHAR(50)," +
                            "Gia DECIMAL(10, 2) NOT NULL," +
                            "FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)" +
                            ")");
                }
            } catch (SQLException e) {
                System.err.println("Error updating ChiTietSanPham table: " + e.getMessage());
            }

            // HoaDon (Invoice) table for revenue statistics
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS HoaDon (" +
                    "MaHoaDon VARCHAR(10) PRIMARY KEY," +
                    "MaNhanVien VARCHAR(10) NOT NULL," +
                    "NgayLap DATETIME NOT NULL," +
                    "TongTien DECIMAL(10, 2) NOT NULL," +
                    "FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)" +
                    ")");

            // ChiTietHoaDon (InvoiceDetail) table for revenue statistics
            try {
                // Kiểm tra bảng đã tồn tại chưa
                ResultSet tables = conn.getMetaData().getTables(null, null, "ChiTietHoaDon", null);
                if (tables.next()) {
                    // Kiểm tra và đổi cấu trúc nếu cần
                    System.out.println("Checking ChiTietHoaDon table structure...");
                } else {
                    // Tạo bảng mới với cấu trúc đúng (sử dụng MaSanPham thay vì MaChiTietSanPham)
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ChiTietHoaDon (" +
                            "MaHoaDon VARCHAR(10) NOT NULL," +
                            "MaSanPham VARCHAR(10) NOT NULL," +
                            "SoLuong INT NOT NULL," +
                            "DonGia DECIMAL(10, 2) NOT NULL," +
                            "PRIMARY KEY (MaHoaDon, MaSanPham)," +
                            "FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon)," +
                            "FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)" +
                            ")");
                    System.out.println("ChiTietHoaDon table created successfully.");
                }
            } catch (SQLException e) {
                System.err.println("Error updating ChiTietHoaDon table: " + e.getMessage());
            }

            // PhieuNhap (ImportReceipt) table for import receipts
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS phieunhap (" +
                    "ma_phieu VARCHAR(10) PRIMARY KEY," +
                    "ma_ncc VARCHAR(10) NOT NULL," +
                    "ma_nv VARCHAR(10) NOT NULL," +
                    "ngay_nhap VARCHAR(50) NOT NULL," +
                    "tong_tien DECIMAL(10, 2) NOT NULL DEFAULT 0," +
                    "ghi_chu NVARCHAR(255)," +
                    "FOREIGN KEY (ma_ncc) REFERENCES NhaCungCap(MaNhaCungCap)," +
                    "FOREIGN KEY (ma_nv) REFERENCES NhanVien(MaNhanVien)" +
                    ")");

            // ChiTietPhieuNhap (ImportReceiptDetail) table for import receipt details
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS chitietphieunhap (" +
                    "ma_phieu VARCHAR(10) NOT NULL," +
                    "ma_sp VARCHAR(10) NOT NULL," +
                    "so_luong INT NOT NULL," +
                    "don_gia DECIMAL(10, 2) NOT NULL," +
                    "PRIMARY KEY (ma_phieu, ma_sp)," +
                    "FOREIGN KEY (ma_phieu) REFERENCES phieunhap(ma_phieu)," +
                    "FOREIGN KEY (ma_sp) REFERENCES SanPham(MaSanPham)" +
                    ")");

            // SanPhamTonKho (Inventory) table for inventory management
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS sanphamtonkho (" +
                    "ma_sp VARCHAR(10) PRIMARY KEY," +
                    "so_luong INT NOT NULL DEFAULT 0," +
                    "ngay_cap_nhat VARCHAR(30) NOT NULL," +
                    "FOREIGN KEY (ma_sp) REFERENCES SanPham(MaSanPham)" +
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