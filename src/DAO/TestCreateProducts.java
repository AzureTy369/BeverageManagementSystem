package DAO;

import java.sql.*;

public class TestCreateProducts {
    private static Connection conn;

    public static void main(String[] args) {
        try {
            // Kết nối đến cơ sở dữ liệu
            conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("Kết nối thành công đến cơ sở dữ liệu!");

                // Hiển thị cấu trúc bảng sanpham
                displayTableStructure("sanpham");

                // Kiểm tra và tạo loại sản phẩm
                checkAndCreateProductCategory();

                // Tạo các sản phẩm cần thiết cho việc nhập hàng
                createRequiredProducts();

                // Hiển thị danh sách sản phẩm đã tạo
                displayProducts();

                System.out.println("Hoàn tất tạo sản phẩm!");
            } else {
                System.out.println("Không thể kết nối đến cơ sở dữ liệu!");
            }
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void displayTableStructure(String tableName) {
        try {
            System.out.println("\n===== CẤU TRÚC BẢNG " + tableName.toUpperCase() + " =====");

            // Lấy metadata của bảng
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, tableName, null);

            int count = 0;
            while (columns.next()) {
                count++;
                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("TYPE_NAME");
                String nullable = columns.getInt("NULLABLE") == 1 ? "NULL" : "NOT NULL";
                String isAutoIncrement = "YES".equals(columns.getString("IS_AUTOINCREMENT")) ? "AUTO_INCREMENT" : "";

                System.out.println(
                        count + ". " + columnName + " - " + columnType + " " + nullable + " " + isAutoIncrement);
            }

            if (count == 0) {
                System.out.println("Không tìm thấy bảng " + tableName + " trong CSDL");
            }

            // Lấy thông tin khóa chính
            ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
            System.out.println("\nKhóa chính:");
            while (primaryKeys.next()) {
                String pkName = primaryKeys.getString("PK_NAME");
                String columnName = primaryKeys.getString("COLUMN_NAME");
                System.out.println("- " + columnName + " (" + pkName + ")");
            }

            // Lấy thông tin khóa ngoại
            ResultSet foreignKeys = metaData.getImportedKeys(null, null, tableName);
            System.out.println("\nKhóa ngoại:");
            while (foreignKeys.next()) {
                String fkName = foreignKeys.getString("FK_NAME");
                String columnName = foreignKeys.getString("FKCOLUMN_NAME");
                String refTable = foreignKeys.getString("PKTABLE_NAME");
                String refColumn = foreignKeys.getString("PKCOLUMN_NAME");
                System.out.println("- " + columnName + " -> " + refTable + "." + refColumn + " (" + fkName + ")");
            }

            columns.close();
            primaryKeys.close();
            foreignKeys.close();
        } catch (SQLException e) {
            System.out.println("Lỗi khi hiển thị cấu trúc bảng " + tableName + ": " + e.getMessage());
        }
    }

    private static void checkAndCreateProductCategory() {
        try {
            // Kiểm tra xem đã có loại sản phẩm chưa
            String countQuery = "SELECT COUNT(*) FROM loaisanpham";
            PreparedStatement ps = conn.prepareStatement(countQuery);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count == 0) {
                // Nếu chưa có loại sản phẩm nào, thêm mới
                String insertQuery = "INSERT INTO loaisanpham(ma_loai, ten_loai) VALUES(?, ?)";
                ps = conn.prepareStatement(insertQuery);
                ps.setString(1, "LSP001");
                ps.setString(2, "Nước giải khát");
                ps.executeUpdate();

                System.out.println("Đã tạo loại sản phẩm: LSP001 - Nước giải khát");
            } else {
                System.out.println("Đã có " + count + " loại sản phẩm trong CSDL");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra/tạo loại sản phẩm: " + e.getMessage());
        }
    }

    private static void createRequiredProducts() {
        try {
            displayTableStructure("sanpham");

            // Kiểm tra xem có sản phẩm SP001 và SP002 không
            String checkQuery = "SELECT MaSanPham FROM sanpham WHERE MaSanPham = ?";
            PreparedStatement ps = conn.prepareStatement(checkQuery);

            String[][] products = {
                    { "SP001", "Nước suối Aquafina", "Chai", "8000", "LSP001" },
                    { "SP002", "Coca Cola", "Lon", "12000", "LSP001" },
                    { "SP003", "Sting đỏ", "Chai", "10000", "LSP001" },
                    { "SP004", "Pepsi", "Lon", "11000", "LSP001" },
                    { "SP005", "Trà xanh 0 độ", "Chai", "13000", "LSP001" }
            };

            // Thử truy vấn SELECT để xem có thể làm việc được với bảng không
            try {
                String testQuery = "SELECT * FROM sanpham LIMIT 1";
                Statement stmt = conn.createStatement();
                ResultSet testRs = stmt.executeQuery(testQuery);
                System.out.println("\nKiểm tra truy vấn SELECT:");
                ResultSetMetaData rsmd = testRs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    System.out.println("- " + rsmd.getColumnName(i) + " (" + rsmd.getColumnTypeName(i) + ")");
                }
            } catch (SQLException e) {
                System.out.println("Lỗi khi kiểm tra bảng sanpham: " + e.getMessage());
            }

            // Tạo SQL INSERT dựa trên cấu trúc bảng thực tế
            try {
                int addedCount = 0;
                for (String[] product : products) {
                    ps.setString(1, product[0]);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        // Nếu sản phẩm chưa tồn tại, thêm mới
                        String insertQuery = "INSERT INTO sanpham(MaSanPham, TenSanPham, DonViTinh, GiaBan, MaLoaiSP) VALUES(?, ?, ?, ?, ?)";
                        PreparedStatement insertPs = conn.prepareStatement(insertQuery);
                        insertPs.setString(1, product[0]);
                        insertPs.setString(2, product[1]);
                        insertPs.setString(3, product[2]);
                        insertPs.setDouble(4, Double.parseDouble(product[3]));
                        insertPs.setString(5, product[4]);
                        insertPs.executeUpdate();
                        addedCount++;
                        System.out.println("Đã tạo sản phẩm: " + product[0] + " - " + product[1]);
                    }
                }

                if (addedCount == 0) {
                    System.out.println("Tất cả sản phẩm cần thiết đã tồn tại trong CSDL");
                } else {
                    System.out.println("Đã thêm " + addedCount + " sản phẩm mới");
                }
            } catch (SQLException e) {
                System.out.println("Lỗi khi thêm sản phẩm: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi tạo sản phẩm: " + e.getMessage());
        }
    }

    private static void displayProducts() {
        try {
            // Thử lấy thông tin sản phẩm với các cột cụ thể
            String query = "SELECT MaSanPham, TenSanPham, DonViTinh, GiaBan FROM sanpham";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nDanh sách sản phẩm hiện có trong CSDL:");
            int count = 0;
            while (rs.next()) {
                count++;
                String productId = rs.getString("MaSanPham");
                String productName = rs.getString("TenSanPham");
                String unit = rs.getString("DonViTinh");
                double price = rs.getDouble("GiaBan");

                System.out.println(count + ". " + productId + " - " + productName + " - " + unit + " - " + price);
            }

            if (count == 0) {
                System.out.println("Không có sản phẩm nào trong CSDL");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi hiển thị sản phẩm: " + e.getMessage());
        }
    }
}