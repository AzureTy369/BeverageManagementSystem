package DAO;

import java.sql.*;
import java.util.ArrayList;

public class TestDatabase {

    private static Connection conn;

    public static void main(String[] args) {
        try {
            // Kết nối đến cơ sở dữ liệu
            conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("Kết nối thành công đến cơ sở dữ liệu!");

                // Hiển thị danh sách bảng
                displayTables();

                // Kiểm tra loại sản phẩm
                checkAndCreateProductCategory();

                // Kiểm tra sản phẩm
                checkAndCreateProducts();

                // Kiểm tra phiếu nhập
                checkAndCreateSampleImportReceipt();

                // Kiểm tra chi tiết phiếu nhập
                checkImportReceiptDetails("PN001");

                // Kiểm tra thêm chi tiết phiếu nhập
                testAddImportReceiptDetail();

                System.out.println("Hoàn tất kiểm tra và tạo dữ liệu mẫu!");
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

    private static void displayTables() {
        try {
            System.out.println("\n===== DANH SÁCH BẢNG TRONG CSDL =====");
            ResultSet tables = conn.getMetaData().getTables(null, null, "%", new String[] { "TABLE" });
            int count = 0;
            while (tables.next()) {
                count++;
                System.out.println(count + ". " + tables.getString("TABLE_NAME"));
            }
            if (count == 0) {
                System.out.println("Không tìm thấy bảng nào trong CSDL!");
            }
            tables.close();
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách bảng: " + e.getMessage());
        }
    }

    private static void checkAndCreateProductCategory() {
        try {
            // Kiểm tra xem đã có loại sản phẩm nào chưa
            String checkSql = "SELECT COUNT(*) as total FROM loaisanpham";
            PreparedStatement ps = conn.prepareStatement(checkSql);
            ResultSet rs = ps.executeQuery();

            int count = 0;
            if (rs.next()) {
                count = rs.getInt("total");
            }

            System.out.println("Số loại sản phẩm hiện có: " + count);

            // Nếu chưa có loại sản phẩm nào, tạo loại sản phẩm mẫu
            if (count == 0) {
                System.out.println("Đang tạo loại sản phẩm mẫu...");

                String insertSql = "INSERT INTO loaisanpham(MaLoaiSP, TenLoai) VALUES(?, ?)";
                ps = conn.prepareStatement(insertSql);
                ps.setString(1, "L001");
                ps.setString(2, "Nước giải khát");
                int result = ps.executeUpdate();

                if (result > 0) {
                    System.out.println("Đã tạo loại sản phẩm mẫu thành công!");
                } else {
                    System.out.println("Không thể tạo loại sản phẩm mẫu!");
                }
            }

            // Hiển thị danh sách loại sản phẩm
            System.out.println("\nDanh sách loại sản phẩm:");
            String listSql = "SELECT * FROM loaisanpham";
            ps = conn.prepareStatement(listSql);
            rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("MaLoaiSP") + " - " + rs.getString("TenLoai"));
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra/tạo loại sản phẩm: " + e.getMessage());
        }
    }

    private static void checkAndCreateProducts() {
        try {
            // Kiểm tra xem đã có sản phẩm nào chưa
            String checkSql = "SELECT COUNT(*) as total FROM sanpham";
            PreparedStatement ps = conn.prepareStatement(checkSql);
            ResultSet rs = ps.executeQuery();

            int count = 0;
            if (rs.next()) {
                count = rs.getInt("total");
            }

            System.out.println("\nSố sản phẩm hiện có: " + count);

            // Nếu chưa có sản phẩm nào, tạo sản phẩm mẫu
            if (count == 0) {
                System.out.println("Đang tạo sản phẩm mẫu...");

                // Lấy mã loại sản phẩm
                String categoryId = "L001";
                String getCategorySql = "SELECT MaLoaiSP FROM loaisanpham LIMIT 1";
                ps = conn.prepareStatement(getCategorySql);
                rs = ps.executeQuery();
                if (rs.next()) {
                    categoryId = rs.getString("MaLoaiSP");
                }

                // Dữ liệu mẫu sản phẩm
                Object[][] productData = {
                        { "SP001", "Nước suối Aquafina", categoryId, "Chai", 8000.0 },
                        { "SP002", "Coca Cola", categoryId, "Lon", 12000.0 },
                        { "SP003", "Sting đỏ", categoryId, "Chai", 10000.0 },
                        { "SP004", "Pepsi", categoryId, "Lon", 11000.0 },
                        { "SP005", "Trà xanh 0 độ", categoryId, "Chai", 13000.0 }
                };

                // Tạo từng sản phẩm mẫu
                String insertSql = "INSERT INTO sanpham(MaSanPham, TenSanPham, MaLoaiSP, DonViTinh, GiaBan) VALUES(?, ?, ?, ?, ?)";
                for (Object[] product : productData) {
                    try {
                        ps = conn.prepareStatement(insertSql);
                        ps.setString(1, (String) product[0]);
                        ps.setString(2, (String) product[1]);
                        ps.setString(3, (String) product[2]);
                        ps.setString(4, (String) product[3]);
                        ps.setDouble(5, (Double) product[4]);
                        int result = ps.executeUpdate();

                        if (result > 0) {
                            System.out.println("Đã tạo sản phẩm: " + product[1]);
                        } else {
                            System.out.println("Không thể tạo sản phẩm: " + product[1]);
                        }
                    } catch (SQLException e) {
                        System.out.println("Lỗi khi tạo sản phẩm " + product[1] + ": " + e.getMessage());
                    }
                }
            }

            // Hiển thị danh sách sản phẩm
            System.out.println("\nDanh sách sản phẩm:");
            String listSql = "SELECT * FROM sanpham";
            ps = conn.prepareStatement(listSql);
            rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("MaSanPham") + " - " +
                        rs.getString("TenSanPham") + " - " +
                        rs.getString("MaLoaiSP") + " - " +
                        rs.getString("DonViTinh") + " - " +
                        rs.getDouble("GiaBan"));
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra/tạo sản phẩm: " + e.getMessage());
        }
    }

    private static void checkAndCreateSampleImportReceipt() {
        try {
            // Kiểm tra xem đã có phiếu nhập "PN001" chưa
            String checkSql = "SELECT COUNT(*) as total FROM phieunhap WHERE ma_phieu = 'PN001'";
            PreparedStatement ps = conn.prepareStatement(checkSql);
            ResultSet rs = ps.executeQuery();

            int count = 0;
            if (rs.next()) {
                count = rs.getInt("total");
            }

            System.out.println("\nSố phiếu nhập với mã PN001: " + count);

            // Nếu chưa có phiếu nhập PN001, tạo phiếu nhập mẫu
            if (count == 0) {
                System.out.println("Đang tạo phiếu nhập mẫu PN001...");

                // Tạo phiếu nhập mẫu
                String insertSql = "INSERT INTO phieunhap(ma_phieu, ma_ncc, ma_nv, ngay_nhap, tong_tien, trang_thai) VALUES(?, ?, ?, ?, ?, ?)";
                ps = conn.prepareStatement(insertSql);
                ps.setString(1, "PN001");
                ps.setString(2, "NCC001");
                ps.setString(3, "NV001");
                ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
                ps.setDouble(5, 0.0);
                ps.setString(6, "Đang xử lý");

                int result = ps.executeUpdate();
                if (result > 0) {
                    System.out.println("Đã tạo phiếu nhập mẫu PN001 thành công!");
                } else {
                    System.out.println("Không thể tạo phiếu nhập mẫu PN001!");
                }
            }

            // Hiển thị thông tin phiếu nhập
            System.out.println("\nThông tin phiếu nhập:");
            String listSql = "SELECT * FROM phieunhap WHERE ma_phieu = 'PN001'";
            ps = conn.prepareStatement(listSql);
            rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Mã phiếu: " + rs.getString("ma_phieu"));
                System.out.println("Mã NCC: " + rs.getString("ma_ncc"));
                System.out.println("Mã NV: " + rs.getString("ma_nv"));
                System.out.println("Ngày nhập: " + rs.getDate("ngay_nhap"));
                System.out.println("Tổng tiền: " + rs.getDouble("tong_tien"));
                System.out.println("Trạng thái: " + rs.getString("trang_thai"));
            } else {
                System.out.println("Không tìm thấy phiếu nhập PN001!");
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra/tạo phiếu nhập: " + e.getMessage());
        }
    }

    private static void checkImportReceiptDetails(String receiptId) {
        try {
            // Kiểm tra xem đã có chi tiết phiếu nhập cho phiếu nhập
            String checkSql = "SELECT COUNT(*) as total FROM chitietphieunhap WHERE ma_phieu = ?";
            PreparedStatement ps = conn.prepareStatement(checkSql);
            ps.setString(1, receiptId);
            ResultSet rs = ps.executeQuery();

            int count = 0;
            if (rs.next()) {
                count = rs.getInt("total");
            }

            System.out.println("\nSố chi tiết phiếu nhập cho phiếu " + receiptId + ": " + count);

            // Nếu chưa có chi tiết phiếu nhập, tạo chi tiết phiếu nhập mẫu
            if (count == 0) {
                System.out.println("Đang tạo chi tiết phiếu nhập mẫu...");

                // Lấy danh sách sản phẩm
                String getProductsSql = "SELECT MaSanPham, GiaBan FROM sanpham LIMIT 3";
                ps = conn.prepareStatement(getProductsSql);
                rs = ps.executeQuery();

                double totalAmount = 0;
                int detailCount = 0;

                // Tạo chi tiết phiếu nhập cho từng sản phẩm
                String insertSql = "INSERT INTO chitietphieunhap(ma_phieu, ma_sp, so_luong, don_gia) VALUES(?, ?, ?, ?)";

                while (rs.next()) {
                    String productId = rs.getString("MaSanPham");
                    double price = rs.getDouble("GiaBan");
                    int quantity = 1 + (int) (Math.random() * 5); // Số lượng ngẫu nhiên từ 1-5

                    try {
                        ps = conn.prepareStatement(insertSql);
                        ps.setString(1, receiptId);
                        ps.setString(2, productId);
                        ps.setInt(3, quantity);
                        ps.setDouble(4, price);
                        int result = ps.executeUpdate();

                        if (result > 0) {
                            System.out.println("Đã thêm chi tiết phiếu nhập cho sản phẩm " + productId
                                    + " với số lượng " + quantity);
                            totalAmount += price * quantity;
                            detailCount++;
                        } else {
                            System.out.println("Không thể thêm chi tiết phiếu nhập cho sản phẩm " + productId);
                        }
                    } catch (SQLException e) {
                        System.out.println(
                                "Lỗi khi thêm chi tiết phiếu nhập cho sản phẩm " + productId + ": " + e.getMessage());
                    }
                }

                // Cập nhật tổng tiền cho phiếu nhập
                if (detailCount > 0) {
                    String updateSql = "UPDATE phieunhap SET tong_tien = ? WHERE ma_phieu = ?";
                    ps = conn.prepareStatement(updateSql);
                    ps.setDouble(1, totalAmount);
                    ps.setString(2, receiptId);
                    int result = ps.executeUpdate();

                    if (result > 0) {
                        System.out.println("Đã cập nhật tổng tiền " + totalAmount + " cho phiếu nhập " + receiptId);
                    } else {
                        System.out.println("Không thể cập nhật tổng tiền cho phiếu nhập " + receiptId);
                    }
                }
            }

            // Hiển thị danh sách chi tiết phiếu nhập
            System.out.println("\nDanh sách chi tiết phiếu nhập cho phiếu " + receiptId + ":");
            String listSql = "SELECT c.*, s.TenSanPham FROM chitietphieunhap c JOIN sanpham s ON c.ma_sp = s.MaSanPham WHERE c.ma_phieu = ?";
            ps = conn.prepareStatement(listSql);
            ps.setString(1, receiptId);
            rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("ma_phieu") + " - " +
                        rs.getString("ma_sp") + " - " +
                        rs.getString("TenSanPham") + " - " +
                        rs.getInt("so_luong") + " - " +
                        rs.getDouble("don_gia") + " - " +
                        (rs.getInt("so_luong") * rs.getDouble("don_gia")));
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra/tạo chi tiết phiếu nhập: " + e.getMessage());
        }
    }

    private static void testAddImportReceiptDetail() {
        try {
            System.out.println("\n===== KIỂM TRA THÊM CHI TIẾT PHIẾU NHẬP =====");

            // Kiểm tra xem có phiếu nhập PN001 không
            String checkReceiptSql = "SELECT * FROM phieunhap WHERE ma_phieu = 'PN001'";
            PreparedStatement ps = conn.prepareStatement(checkReceiptSql);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("Không tìm thấy phiếu nhập PN001. Tạo phiếu nhập mới...");

                // Tạo phiếu nhập mẫu
                String insertSql = "INSERT INTO phieunhap(ma_phieu, ma_ncc, ma_nv, ngay_nhap, tong_tien, trang_thai) VALUES(?, ?, ?, ?, ?, ?)";
                ps = conn.prepareStatement(insertSql);
                ps.setString(1, "PN001");
                ps.setString(2, "NCC001");
                ps.setString(3, "NV001");
                ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
                ps.setDouble(5, 0.0);
                ps.setString(6, "Đang xử lý");

                int result = ps.executeUpdate();
                if (result > 0) {
                    System.out.println("Đã tạo phiếu nhập PN001 thành công!");
                } else {
                    System.out.println("Không thể tạo phiếu nhập PN001!");
                    return;
                }
            } else {
                System.out.println("Đã tìm thấy phiếu nhập PN001.");
            }

            // Lấy danh sách sản phẩm
            System.out.println("\nDanh sách sản phẩm hiện có trong CSDL:");
            String getProductsSql = "SELECT MaSanPham, TenSanPham, GiaBan FROM sanpham LIMIT 5";
            ps = conn.prepareStatement(getProductsSql);
            rs = ps.executeQuery();

            int productCount = 0;
            ArrayList<String> productIds = new ArrayList<>();

            while (rs.next()) {
                productCount++;
                String productId = rs.getString("MaSanPham");
                String productName = rs.getString("TenSanPham");
                double price = rs.getDouble("GiaBan");

                System.out.println(productCount + ". " + productId + " - " + productName + " - Giá: " + price);
                productIds.add(productId);
            }

            if (productCount == 0) {
                System.out.println("Không tìm thấy sản phẩm nào trong CSDL. Cần tạo sản phẩm trước!");
                return;
            }

            // Xóa dữ liệu chi tiết phiếu nhập cũ
            String deleteSql = "DELETE FROM chitietphieunhap WHERE ma_phieu = ?";
            ps = conn.prepareStatement(deleteSql);
            ps.setString(1, "PN001");
            int deleteResult = ps.executeUpdate();
            System.out.println("Đã xóa " + deleteResult + " chi tiết phiếu nhập cũ của PN001.");

            // Thêm chi tiết phiếu nhập mới
            System.out.println("\nTiến hành thêm chi tiết phiếu nhập...");
            String insertDetailSql = "INSERT INTO chitietphieunhap(ma_phieu, ma_sp, so_luong, don_gia) VALUES(?, ?, ?, ?)";

            double totalAmount = 0;
            int successCount = 0;

            for (String productId : productIds) {
                // Lấy giá của sản phẩm
                String getProductSql = "SELECT GiaBan FROM sanpham WHERE MaSanPham = ?";
                ps = conn.prepareStatement(getProductSql);
                ps.setString(1, productId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    double price = rs.getDouble("GiaBan");
                    int quantity = 1 + (int) (Math.random() * 5); // Số lượng ngẫu nhiên từ 1-5

                    try {
                        ps = conn.prepareStatement(insertDetailSql);
                        ps.setString(1, "PN001");
                        ps.setString(2, productId);
                        ps.setInt(3, quantity);
                        ps.setDouble(4, price);

                        System.out.println("Thực thi truy vấn: INSERT INTO chitietphieunhap VALUES('PN001', '" +
                                productId + "', " + quantity + ", " + price + ")");

                        int result = ps.executeUpdate();
                        if (result > 0) {
                            System.out.println("✓ Đã thêm chi tiết sản phẩm " + productId + " với số lượng " +
                                    quantity + " và đơn giá " + price);
                            totalAmount += price * quantity;
                            successCount++;
                        } else {
                            System.out.println("✗ Thêm chi tiết sản phẩm " + productId + " thất bại!");
                        }
                    } catch (SQLException e) {
                        System.out.println("✗ Lỗi khi thêm chi tiết sản phẩm " + productId + ": " + e.getMessage());
                    }
                }
            }

            // Cập nhật tổng tiền
            if (successCount > 0) {
                String updateSql = "UPDATE phieunhap SET tong_tien = ? WHERE ma_phieu = ?";
                ps = conn.prepareStatement(updateSql);
                ps.setDouble(1, totalAmount);
                ps.setString(2, "PN001");
                int updateResult = ps.executeUpdate();

                if (updateResult > 0) {
                    System.out.println("\nĐã cập nhật tổng tiền " + totalAmount + " cho phiếu nhập PN001.");
                } else {
                    System.out.println("\nKhông thể cập nhật tổng tiền cho phiếu nhập PN001.");
                }
            }

            System.out.println("\nĐã thêm thành công " + successCount + " chi tiết phiếu nhập mới.");

        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra thêm chi tiết phiếu nhập: " + e.getMessage());
        }
    }
}