package DAO;

import DTO.EmployeeDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private Connection connection;

    public EmployeeDAO() {
        connection = DBConnection.getConnection();
        if (connection == null) {
            System.err.println("ERROR: Database connection is null in EmployeeDAO constructor");
        } else {
            System.out.println("EmployeeDAO initialized with a valid database connection");
        }
    }

    public List<EmployeeDTO> getAllEmployees() {
        List<EmployeeDTO> employees = new ArrayList<>();
        String query = "SELECT * FROM NhanVien";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                EmployeeDTO employee = new EmployeeDTO();
                employee.setEmployeeId(rs.getString("MaNhanVien"));
                employee.setUsername(rs.getString("TenTaiKhoan"));
                employee.setPassword(rs.getString("MatKhau"));
                employee.setLastName(rs.getString("Ho"));
                employee.setFirstName(rs.getString("Ten"));
                employee.setPositionId(rs.getString("MaChucVu"));
                employee.setPhone(rs.getString("SoDienThoai"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.err.println("Error getting employees: " + e.getMessage());
        }

        return employees;
    }

    public EmployeeDTO getEmployeeById(String employeeId) {
        String query = "SELECT * FROM NhanVien WHERE MaNhanVien = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, employeeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    EmployeeDTO employee = new EmployeeDTO();
                    employee.setEmployeeId(rs.getString("MaNhanVien"));
                    employee.setUsername(rs.getString("TenTaiKhoan"));
                    employee.setPassword(rs.getString("MatKhau"));
                    employee.setLastName(rs.getString("Ho"));
                    employee.setFirstName(rs.getString("Ten"));
                    employee.setPositionId(rs.getString("MaChucVu"));
                    employee.setPhone(rs.getString("SoDienThoai"));
                    return employee;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting employee by ID: " + e.getMessage());
        }

        return null;
    }

    public EmployeeDTO getEmployeeByUsername(String username) {
        String query = "SELECT * FROM NhanVien WHERE TenTaiKhoan = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    EmployeeDTO employee = new EmployeeDTO();
                    employee.setEmployeeId(rs.getString("MaNhanVien"));
                    employee.setUsername(rs.getString("TenTaiKhoan"));
                    employee.setPassword(rs.getString("MatKhau"));
                    employee.setLastName(rs.getString("Ho"));
                    employee.setFirstName(rs.getString("Ten"));
                    employee.setPositionId(rs.getString("MaChucVu"));
                    employee.setPhone(rs.getString("SoDienThoai"));
                    return employee;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting employee by username: " + e.getMessage());
        }

        return null;
    }

    private boolean checkConnection() {
        if (connection == null) {
            System.err.println("Connection is null, trying to reconnect...");
            connection = DBConnection.getConnection();
        }

        try {
            if (connection != null && !connection.isClosed()) {
                System.out.println("Database connection is valid");
                return true;
            } else {
                System.err.println("Database connection is closed or invalid");
                // Thử kết nối lại
                connection = DBConnection.getConnection();
                return connection != null && !connection.isClosed();
            }
        } catch (SQLException e) {
            System.err.println("Error checking database connection: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean addEmployee(EmployeeDTO employee) {
        // Kiểm tra kết nối trước khi thực hiện thêm
        if (!checkConnection()) {
            System.err.println("Không thể thêm nhân viên do kết nối cơ sở dữ liệu không hợp lệ");
            return false;
        }

        // Kiểm tra cấu trúc bảng nhân viên
        debugEmployeeTable();

        // Debug info
        System.out.println("\n===== THÊM NHÂN VIÊN =====");
        System.out.println("Chi tiết nhân viên:");
        System.out.println("  ID: " + employee.getEmployeeId());
        System.out.println("  Username: " + employee.getUsername());
        System.out.println("  Họ và tên: " + employee.getLastName() + " " + employee.getFirstName());
        System.out.println("  Chức vụ: " + employee.getPositionId());
        System.out.println("  Số điện thoại: " + employee.getPhone());

        // Kiểm tra trùng lặp username
        if (isUsernameExists(employee.getUsername())) {
            System.out.println("  LỖI: Tên đăng nhập đã tồn tại: " + employee.getUsername());
            return false;
        } else {
            System.out.println("  Kiểm tra tên đăng nhập OK: " + employee.getUsername() + " chưa tồn tại");
        }

        // Kiểm tra trùng lặp ID
        EmployeeDTO existingEmp = getEmployeeById(employee.getEmployeeId());
        if (existingEmp != null) {
            System.out.println("  LỖI: Mã nhân viên đã tồn tại: " + employee.getEmployeeId());
            return false;
        } else {
            System.out.println("  Kiểm tra mã nhân viên OK: " + employee.getEmployeeId() + " chưa tồn tại");
        }

        // Kiểm tra chức vụ tồn tại
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT COUNT(*) FROM ChucVu WHERE MaChucVu = ?")) {
            pstmt.setString(1, employee.getPositionId());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("  LỖI: Mã chức vụ không tồn tại: " + employee.getPositionId());
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra chức vụ: " + e.getMessage());
        }

        String query = "INSERT INTO NhanVien (MaNhanVien, TenTaiKhoan, MatKhau, Ho, Ten, MaChucVu, SoDienThoai) VALUES (?, ?, ?, ?, ?, ?, ?)";
        System.out.println("SQL Query: " + query.replace("?", "'{}'"));
        System.out.println("  Giá trị: [" +
                employee.getEmployeeId() + ", " +
                employee.getUsername() + ", " +
                employee.getPassword() + ", " +
                employee.getLastName() + ", " + // Ho
                employee.getFirstName() + ", " + // Ten
                employee.getPositionId() + ", " +
                employee.getPhone() + "]");

        // Hiển thị SQL thực tế sẽ thực thi
        String actualSQL = "INSERT INTO NhanVien (MaNhanVien, TenTaiKhoan, MatKhau, Ho, Ten, MaChucVu, SoDienThoai) VALUES ('"
                +
                employee.getEmployeeId() + "', '" +
                employee.getUsername() + "', '" +
                employee.getPassword() + "', '" +
                employee.getLastName() + "', '" + // Ho
                employee.getFirstName() + "', '" + // Ten
                employee.getPositionId() + "', '" +
                employee.getPhone() + "')";
        System.out.println("  SQL thực tế: " + actualSQL);

        System.out.println("  Cột database: [MaNhanVien, TenTaiKhoan, MatKhau, Ho, Ten, MaChucVu, SoDienThoai]");

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, employee.getEmployeeId());
            pstmt.setString(2, employee.getUsername());
            pstmt.setString(3, employee.getPassword());
            pstmt.setString(4, employee.getLastName()); // Ho
            pstmt.setString(5, employee.getFirstName()); // Ten
            pstmt.setString(6, employee.getPositionId());
            pstmt.setString(7, employee.getPhone());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Số dòng ảnh hưởng: " + rowsAffected);
            boolean success = rowsAffected > 0;
            System.out.println("Thêm nhân viên " + (success ? "THÀNH CÔNG" : "THẤT BẠI"));
            System.out.println("===== KẾT THÚC THÊM NHÂN VIÊN =====\n");
            return success;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm nhân viên: " + e.getMessage());
            e.printStackTrace();

            // Thử thêm trực tiếp với Statement để xem lỗi chi tiết
            try (Statement stmt = connection.createStatement()) {
                int rowsAffected = stmt.executeUpdate(actualSQL);
                System.out.println("Thử lại với Statement - Số dòng ảnh hưởng: " + rowsAffected);
                return rowsAffected > 0;
            } catch (SQLException ex) {
                System.err.println("Lỗi khi thử lại với Statement: " + ex.getMessage());
                ex.printStackTrace();
            }

            System.out.println("===== KẾT THÚC THÊM NHÂN VIÊN (CÓ LỖI) =====\n");
            return false;
        }
    }

    public boolean updateEmployee(EmployeeDTO employee) {
        String query = "UPDATE NhanVien SET TenTaiKhoan = ?, MatKhau = ?, Ho = ?, Ten = ?, MaChucVu = ?, SoDienThoai = ? WHERE MaNhanVien = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, employee.getUsername());
            pstmt.setString(2, employee.getPassword());
            pstmt.setString(3, employee.getLastName());
            pstmt.setString(4, employee.getFirstName());
            pstmt.setString(5, employee.getPositionId());
            pstmt.setString(6, employee.getPhone());
            pstmt.setString(7, employee.getEmployeeId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteEmployee(String employeeId) {
        String query = "DELETE FROM NhanVien WHERE MaNhanVien = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, employeeId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
            return false;
        }
    }

    public boolean isUsernameExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username is null or empty, returning false");
            return false;
        }

        // Kiểm tra kết nối trước khi thực hiện
        if (!checkConnection()) {
            System.err.println("Cannot check username due to invalid database connection");
            return false;
        }

        // Phương pháp đơn giản: truy vấn trực tiếp sử dụng mệnh đề count
        String query = "SELECT COUNT(*) FROM NhanVien WHERE TenTaiKhoan = ?";
        System.out.println("Checking if username exists: " + username);

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            System.out.println("SQL Query: " + query.replace("?", "'" + username + "'"));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Count result for username '" + username + "': " + count);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("No result found when checking username: " + username);
        return false;
    }

    public EmployeeDTO login(String username, String password) {
        String query = "SELECT * FROM NhanVien WHERE TenTaiKhoan = ? AND MatKhau = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    EmployeeDTO employee = new EmployeeDTO();
                    employee.setEmployeeId(rs.getString("MaNhanVien"));
                    employee.setUsername(rs.getString("TenTaiKhoan"));
                    employee.setPassword(rs.getString("MatKhau"));
                    employee.setLastName(rs.getString("Ho"));
                    employee.setFirstName(rs.getString("Ten"));
                    employee.setPositionId(rs.getString("MaChucVu"));
                    employee.setPhone(rs.getString("SoDienThoai"));
                    return employee;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
        }

        return null;
    }

    public void testDatabaseConnection() {
        System.out.println("=== DATABASE CONNECTION TEST ===");

        if (connection == null) {
            System.err.println("ERROR: Database connection is null");
            return;
        }

        try {
            boolean isValid = connection.isValid(5); // Timeout 5 seconds
            System.out.println("Connection is valid: " + isValid);

            if (!isValid) {
                System.err.println("Connection is invalid, trying to reconnect...");
                connection = DBConnection.getConnection();
                if (connection == null) {
                    System.err.println("Still could not connect to database");
                    return;
                }
            }

            try (Statement stmt = connection.createStatement()) {
                // Test database name
                ResultSet rs = stmt.executeQuery("SELECT DATABASE()");
                if (rs.next()) {
                    String dbName = rs.getString(1);
                    System.out.println("Current database: " + dbName);
                }

                // Test tables
                try {
                    System.out.println("\n>>> Testing NhanVien table:");
                    rs = stmt.executeQuery("SHOW CREATE TABLE NhanVien");
                    if (rs.next()) {
                        String createTable = rs.getString(2);
                        System.out.println(createTable);
                    }
                } catch (SQLException e) {
                    System.err.println("Error showing NhanVien table structure: " + e.getMessage());
                }

                // Test data
                try {
                    rs = stmt.executeQuery("SELECT * FROM NhanVien");
                    System.out.println("\n>>> NhanVien records:");

                    int count = 0;
                    while (rs.next()) {
                        count++;
                        System.out.println("Record #" + count);
                        System.out.println("  MaNhanVien: " + rs.getString("MaNhanVien"));
                        System.out.println("  TenTaiKhoan: " + rs.getString("TenTaiKhoan"));
                        System.out.println("  MatKhau: " + rs.getString("MatKhau"));
                        System.out.println("  Ho: " + rs.getString("Ho"));
                        System.out.println("  Ten: " + rs.getString("Ten"));
                        System.out.println("  MaChucVu: " + rs.getString("MaChucVu"));
                        System.out.println("  SoDienThoai: " + rs.getString("SoDienThoai"));
                    }

                    if (count == 0) {
                        System.out.println("  No records found in NhanVien table!");
                    }
                } catch (SQLException e) {
                    System.err.println("Error reading NhanVien data: " + e.getMessage());
                }

                // Test insertion with a direct connection
                try {
                    System.out.println("\n>>> Testing isUsernameExists method:");
                    boolean exists = isUsernameExists("admin");
                    System.out.println("  'admin' username exists: " + exists);

                    exists = isUsernameExists("test_user");
                    System.out.println("  'test_user' username exists: " + exists);
                } catch (Exception e) {
                    System.err.println("Error testing isUsernameExists: " + e.getMessage());
                    e.printStackTrace();
                }

                System.out.println("\n=== END OF DATABASE TEST ===");
            }
        } catch (SQLException e) {
            System.err.println("Error in testDatabaseConnection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Phương thức kiểm tra cấu trúc bảng NhanVien
    public void debugEmployeeTable() {
        System.out.println("\n===== KIỂM TRA BẢNG NHÂN VIÊN =====");
        try {
            DatabaseMetaData metaData = connection.getMetaData();

            // Kiểm tra bảng NhanVien có tồn tại không
            ResultSet tables = metaData.getTables(null, null, "NhanVien", null);
            if (!tables.next()) {
                System.out.println("CẢNH BÁO: Bảng NhanVien không tồn tại!");

                // Tạo bảng nếu chưa tồn tại
                try (Statement stmt = connection.createStatement()) {
                    String createSQL = "CREATE TABLE IF NOT EXISTS NhanVien (" +
                            "MaNhanVien VARCHAR(10) PRIMARY KEY," +
                            "TenTaiKhoan VARCHAR(50) NOT NULL UNIQUE," +
                            "MatKhau VARCHAR(50) NOT NULL," +
                            "Ho NVARCHAR(50) NOT NULL," +
                            "Ten NVARCHAR(50) NOT NULL," +
                            "MaChucVu VARCHAR(10) NOT NULL," +
                            "SoDienThoai VARCHAR(15) NOT NULL," +
                            "FOREIGN KEY (MaChucVu) REFERENCES ChucVu(MaChucVu)" +
                            ")";
                    stmt.executeUpdate(createSQL);
                    System.out.println("Đã tạo mới bảng NhanVien!");
                }
            } else {
                System.out.println("Bảng NhanVien tồn tại.");

                // Kiểm tra cấu trúc cột
                ResultSet columns = metaData.getColumns(null, null, "NhanVien", null);
                System.out.println("Cấu trúc của bảng NhanVien:");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    int columnSize = columns.getInt("COLUMN_SIZE");
                    boolean isNullable = (columns.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                    System.out.println("  " + columnName + " - " + columnType + "(" + columnSize + ")" +
                            (isNullable ? " NULL" : " NOT NULL"));
                }

                // Kiểm tra khóa ngoại
                ResultSet foreignKeys = metaData.getImportedKeys(null, null, "NhanVien");
                System.out.println("Khóa ngoại của bảng NhanVien:");
                boolean hasForeignKeys = false;
                while (foreignKeys.next()) {
                    hasForeignKeys = true;
                    String pkTable = foreignKeys.getString("PKTABLE_NAME");
                    String pkColumn = foreignKeys.getString("PKCOLUMN_NAME");
                    String fkColumn = foreignKeys.getString("FKCOLUMN_NAME");
                    System.out.println("  " + fkColumn + " -> " + pkTable + "." + pkColumn);
                }
                if (!hasForeignKeys) {
                    System.out
                            .println("  CẢNH BÁO: Không tìm thấy khóa ngoại! Có thể thiếu ràng buộc với bảng ChucVu.");
                }

                // Kiểm tra dữ liệu hiện có
                try (Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM NhanVien")) {
                    rs.next();
                    int count = rs.getInt(1);
                    System.out.println("Số lượng nhân viên hiện có: " + count);
                }
            }

            // Kiểm tra bảng ChucVu
            tables = metaData.getTables(null, null, "ChucVu", null);
            if (!tables.next()) {
                System.out.println("CẢNH BÁO: Bảng ChucVu không tồn tại!");
            } else {
                System.out.println("Bảng ChucVu tồn tại.");
                try (Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT * FROM ChucVu")) {
                    System.out.println("Dữ liệu bảng ChucVu:");
                    while (rs.next()) {
                        System.out.println("  " + rs.getString("MaChucVu") + " - " + rs.getString("TenChucVu"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra cấu trúc bảng: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("===== KẾT THÚC KIỂM TRA BẢNG NHÂN VIÊN =====\n");
    }

    // Phương thức tạo dữ liệu mẫu để kiểm tra
    public boolean insertTestData() {
        System.out.println("\n===== THÊM DỮ LIỆU MẪU =====");
        boolean success = false;

        try {
            // Kiểm tra xem bảng ChucVu đã có dữ liệu chưa
            try (Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM ChucVu")) {
                rs.next();
                int count = rs.getInt(1);
                System.out.println("Số lượng chức vụ: " + count);

                if (count == 0) {
                    // Thêm dữ liệu mẫu vào bảng ChucVu
                    String[] positions = {
                            "INSERT INTO ChucVu VALUES ('CV001', 'Quản trị viên', 20000000)",
                            "INSERT INTO ChucVu VALUES ('CV002', 'Quản lý', 15000000)",
                            "INSERT INTO ChucVu VALUES ('CV003', 'Nhân viên bán hàng', 8000000)"
                    };

                    for (String sql : positions) {
                        stmt.executeUpdate(sql);
                        System.out.println("Đã thêm: " + sql);
                    }
                }
            }

            // Thêm dữ liệu nhân viên mẫu
            String testId = "NV" + System.currentTimeMillis() % 1000;
            String testUsername = "test_user_" + System.currentTimeMillis() % 1000;

            EmployeeDTO testEmployee = new EmployeeDTO(
                    testId,
                    testUsername,
                    "password123",
                    "Test", // firstName
                    "User", // lastName
                    "CV001", // positionId
                    "0987654321" // phone
            );

            success = addEmployee(testEmployee);
            if (success) {
                System.out.println("Thêm nhân viên mẫu thành công: " + testId);
            } else {
                System.out.println("Thêm nhân viên mẫu thất bại!");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm dữ liệu mẫu: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("===== KẾT THÚC THÊM DỮ LIỆU MẪU =====\n");
        return success;
    }

    /**
     * Kiểm tra xem số điện thoại đã tồn tại trong hệ thống hay chưa
     * 
     * @param phone             Số điện thoại cần kiểm tra
     * @param excludeEmployeeId ID của nhân viên cần loại trừ khỏi việc kiểm tra
     *                          (dùng khi cập nhật)
     * @return true nếu số điện thoại đã tồn tại, false nếu chưa
     */
    public boolean isPhoneExists(String phone, String excludeEmployeeId) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }

        if (!checkConnection()) {
            System.err.println("Không thể kiểm tra số điện thoại do kết nối cơ sở dữ liệu không hợp lệ");
            return false;
        }

        String query = "SELECT COUNT(*) FROM NhanVien WHERE SoDienThoai = ? AND MaNhanVien != ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, phone);
            pstmt.setString(2, excludeEmployeeId != null ? excludeEmployeeId : "");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra số điện thoại: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Kiểm tra xem số điện thoại đã tồn tại trong hệ thống hay chưa
     * 
     * @param phone Số điện thoại cần kiểm tra
     * @return true nếu số điện thoại đã tồn tại, false nếu chưa
     */
    public boolean isPhoneExists(String phone) {
        return isPhoneExists(phone, null);
    }
}