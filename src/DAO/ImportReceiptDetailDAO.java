package DAO;

import DTO.ImportReceiptDetail;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ImportReceiptDetailDAO {
    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    // Danh sách tạm thời cho chi tiết phiếu nhập, sẽ chỉ sử dụng khi kết nối CSDL
    // thất bại
    private static List<ImportReceiptDetail> receiptDetailsList = new ArrayList<>();

    public ImportReceiptDetailDAO() {
        // Khởi tạo kết nối
        try {
            conn = DBConnection.getConnection();
            createTableIfNotExists();
        } catch (Exception e) {
            System.out.println("Lỗi kết nối đến cơ sở dữ liệu: " + e.getMessage());
        }
    }

    private void createTableIfNotExists() {
        try {
            if (conn != null) {
                // Kiểm tra trạng thái kết nối
                if (conn.isClosed()) {
                    System.out.println("DEBUG: Kết nối đã đóng, đang thử kết nối lại");
                    conn = DBConnection.getConnection();
                }

                // Kiểm tra xem bảng đã tồn tại chưa
                ResultSet tables = conn.getMetaData().getTables(null, null, "chitietphieunhap", null);
                boolean tableExists = tables.next();
                System.out.println("DEBUG: Bảng chitietphieunhap " + (tableExists ? "đã tồn tại" : "chưa tồn tại"));
                tables.close();

                // Tạo bảng nếu chưa tồn tại
                String sql = "CREATE TABLE IF NOT EXISTS chitietphieunhap (" +
                        "ma_phieu VARCHAR(10) NOT NULL, " +
                        "ma_sp VARCHAR(10) NOT NULL, " +
                        "so_luong INT NOT NULL, " +
                        "don_gia DECIMAL(10, 2) NOT NULL, " +
                        "PRIMARY KEY (ma_phieu, ma_sp)" +
                        ")";
                ps = conn.prepareStatement(sql);
                ps.executeUpdate();

                // Kiểm tra lại để xác nhận bảng đã được tạo
                tables = conn.getMetaData().getTables(null, null, "chitietphieunhap", null);
                boolean tableCreated = tables.next();
                System.out.println("DEBUG: Sau khi tạo, bảng chitietphieunhap "
                        + (tableCreated ? "đã tồn tại" : "vẫn chưa tồn tại"));
                tables.close();

                // Kiểm tra các cột của bảng
                if (tableCreated) {
                    ResultSet columns = conn.getMetaData().getColumns(null, null, "chitietphieunhap", null);
                    System.out.println("DEBUG: Các cột trong bảng chitietphieunhap:");
                    while (columns.next()) {
                        System.out.println("DEBUG: - " + columns.getString("COLUMN_NAME") + " ("
                                + columns.getString("TYPE_NAME") + ")");
                    }
                    columns.close();
                }
            } else {
                System.out.println("DEBUG: Không thể tạo bảng vì kết nối là null");
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Lỗi tạo bảng chitietphieunhap: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lấy tất cả chi tiết phiếu nhập
     */
    public List<ImportReceiptDetail> getAllImportReceiptDetails() {
        List<ImportReceiptDetail> list = new ArrayList<>();
        try {
            if (conn != null) {
                String sql = "SELECT * FROM chitietphieunhap";
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();

                while (rs.next()) {
                    ImportReceiptDetail detail = new ImportReceiptDetail();
                    detail.setReceiptId(rs.getString("ma_phieu"));
                    detail.setProductId(rs.getString("ma_sp"));
                    detail.setQuantity(rs.getInt("so_luong"));
                    detail.setPrice(rs.getDouble("don_gia"));

                    // Tính thành tiền từ số lượng và đơn giá
                    double total = rs.getInt("so_luong") * rs.getDouble("don_gia");
                    detail.setTotal(total);

                    list.add(detail);
                }
                return list;
            } else {
                // Trả về danh sách tạm thời nếu không có kết nối
                return receiptDetailsList;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn getAllImportReceiptDetails: " + e.getMessage());
            // Nếu có lỗi, trả về danh sách tạm thời
            return receiptDetailsList;
        }
    }

    /**
     * Lấy chi tiết phiếu nhập theo mã phiếu
     */
    public List<ImportReceiptDetail> getImportReceiptDetailsByReceiptId(String receiptId) {
        List<ImportReceiptDetail> list = new ArrayList<>();
        try {
            if (conn != null) {
                System.out.println("DEBUG: Đang truy vấn chi tiết phiếu nhập với ID: " + receiptId);

                // Kiểm tra kết nối database
                if (conn.isClosed()) {
                    System.out.println("DEBUG: Kết nối đã đóng, đang thử kết nối lại");
                    conn = DBConnection.getConnection();
                }

                String sql = "SELECT * FROM chitietphieunhap WHERE ma_phieu = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, receiptId);
                System.out.println("DEBUG: Thực thi SQL: " + sql + " với ma_phieu = " + receiptId);

                rs = ps.executeQuery();
                int rowCount = 0;

                while (rs.next()) {
                    rowCount++;
                    ImportReceiptDetail detail = new ImportReceiptDetail();
                    detail.setReceiptId(rs.getString("ma_phieu"));
                    detail.setProductId(rs.getString("ma_sp"));
                    detail.setQuantity(rs.getInt("so_luong"));
                    detail.setPrice(rs.getDouble("don_gia"));

                    // Tính thành tiền từ số lượng và đơn giá
                    double total = rs.getInt("so_luong") * rs.getDouble("don_gia");
                    detail.setTotal(total);

                    System.out.println("DEBUG: Tìm thấy chi tiết phiếu nhập: mã SP = " + detail.getProductId()
                            + ", số lượng = " + detail.getQuantity()
                            + ", đơn giá = " + detail.getPrice());

                    list.add(detail);
                }

                System.out.println("DEBUG: Tìm thấy " + rowCount + " dòng chi tiết phiếu nhập cho ID: " + receiptId);

                return list;
            } else {
                System.out.println("DEBUG: Kết nối đến database là null, đang sử dụng dữ liệu từ danh sách tạm thời");
                // Trả về danh sách từ bộ nhớ tạm nếu không có kết nối
                for (ImportReceiptDetail detail : receiptDetailsList) {
                    if (detail.getReceiptId().equals(receiptId)) {
                        list.add(detail);
                    }
                }
                return list;
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Lỗi truy vấn getImportReceiptDetailsByReceiptId: " + e.getMessage());
            e.printStackTrace();

            // Nếu có lỗi, trả về từ danh sách tạm thời
            for (ImportReceiptDetail detail : receiptDetailsList) {
                if (detail.getReceiptId().equals(receiptId)) {
                    list.add(detail);
                }
            }
            return list;
        }
    }

    /**
     * Thêm chi tiết phiếu nhập
     */
    public boolean insertImportReceiptDetail(ImportReceiptDetail detail) {
        try {
            // Kiểm tra kết nối đến CSDL
            if (conn == null || conn.isClosed()) {
                conn = DBConnection.getConnection();
            }

            if (conn != null) {
                // Kiểm tra sản phẩm có tồn tại không
                String checkProductSql = "SELECT * FROM sanpham WHERE MaSanPham = ?";
                ps = conn.prepareStatement(checkProductSql);
                ps.setString(1, detail.getProductId());
                rs = ps.executeQuery();

                if (!rs.next()) {
                    System.out.println("Lỗi: Sản phẩm " + detail.getProductId() + " không tồn tại trong CSDL!");

                    // Hiển thị tất cả sản phẩm để kiểm tra
                    System.out.println("Danh sách sản phẩm hiện có trong CSDL:");
                    ps = conn.prepareStatement("SELECT MaSanPham, TenSanPham FROM sanpham");
                    rs = ps.executeQuery();
                    int count = 0;
                    while (rs.next()) {
                        count++;
                        System.out
                                .println(count + ". " + rs.getString("MaSanPham") + " - " + rs.getString("TenSanPham"));
                    }
                    if (count == 0) {
                        System.out.println("Không có sản phẩm nào trong CSDL!");
                    }

                    return false;
                } else {
                    System.out.println("Sản phẩm " + detail.getProductId() + " tồn tại trong CSDL");

                    // Cập nhật thông tin danh mục nếu có
                    if (detail.getCategoryId() != null && !detail.getCategoryId().isEmpty()) {
                        System.out.println("Cập nhật danh mục cho sản phẩm " + detail.getProductId() +
                                " thành: " + detail.getCategoryId() + " - " + detail.getCategoryName());

                        String updateCategorySql = "UPDATE sanpham SET MaLoaiSP = ? WHERE MaSanPham = ?";
                        PreparedStatement updatePs = conn.prepareStatement(updateCategorySql);
                        updatePs.setString(1, detail.getCategoryId());
                        updatePs.setString(2, detail.getProductId());

                        int result = updatePs.executeUpdate();
                        if (result > 0) {
                            System.out.println("Đã cập nhật danh mục cho sản phẩm " + detail.getProductId());
                        } else {
                            System.out.println("Không thể cập nhật danh mục cho sản phẩm " + detail.getProductId());
                        }
                    }
                }

                // Kiểm tra phiếu nhập có tồn tại không
                String checkReceiptSql = "SELECT * FROM phieunhap WHERE ma_phieu = ?";
                ps = conn.prepareStatement(checkReceiptSql);
                ps.setString(1, detail.getReceiptId());
                rs = ps.executeQuery();

                if (!rs.next()) {
                    System.out.println("Lỗi: Phiếu nhập " + detail.getReceiptId() + " không tồn tại trong CSDL!");
                    return false;
                } else {
                    System.out.println("Phiếu nhập " + detail.getReceiptId() + " tồn tại trong CSDL");
                }

                // Tính lại thành tiền trước khi lưu
                double totalAmount = detail.getQuantity() * detail.getPrice();
                if (Math.abs(totalAmount - detail.getTotal()) > 0.01) {
                    System.out.println("Cảnh báo: Thành tiền không khớp - Cũ: " + detail.getTotal() +
                            ", Mới: " + totalAmount);
                    // Cập nhật lại tổng tiền
                    detail.setTotal(totalAmount);
                }

                // Kiểm tra xem chi tiết phiếu nhập đã tồn tại không (để tránh lỗi khóa chính)
                String checkDetailSql = "SELECT * FROM chitietphieunhap WHERE ma_phieu = ? AND ma_sp = ?";
                ps = conn.prepareStatement(checkDetailSql);
                ps.setString(1, detail.getReceiptId());
                ps.setString(2, detail.getProductId());
                rs = ps.executeQuery();

                if (rs.next()) {
                    System.out.println("Cảnh báo: Chi tiết phiếu nhập cho sản phẩm " + detail.getProductId() +
                            " đã tồn tại. Cập nhật số lượng và đơn giá thay vì thêm mới.");

                    // Lấy thông tin hiện tại
                    int currentQty = rs.getInt("so_luong");
                    double currentPrice = rs.getDouble("don_gia");

                    // Cập nhật thay vì thêm mới
                    String updateSql = "UPDATE chitietphieunhap SET so_luong = ?, don_gia = ? WHERE ma_phieu = ? AND ma_sp = ?";
                    ps = conn.prepareStatement(updateSql);

                    // Cộng dồn số lượng
                    int newQty = currentQty + detail.getQuantity();
                    // Lấy đơn giá mới nếu khác 0
                    double newPrice = (detail.getPrice() > 0) ? detail.getPrice() : currentPrice;

                    ps.setInt(1, newQty);
                    ps.setDouble(2, newPrice);
                    ps.setString(3, detail.getReceiptId());
                    ps.setString(4, detail.getProductId());

                    System.out.println("Thực thi truy vấn: " + updateSql + " với tham số: " +
                            newQty + ", " + newPrice + ", " +
                            detail.getReceiptId() + ", " + detail.getProductId());

                    int result = ps.executeUpdate();

                    if (result > 0) {
                        System.out.println("Cập nhật chi tiết phiếu nhập thành công!");

                        // Cập nhật lại tổng tiền cho phiếu nhập
                        updateReceiptTotalAmount(detail.getReceiptId());

                        return true;
                    } else {
                        System.out.println("Cập nhật chi tiết phiếu nhập thất bại!");
                        return false;
                    }
                } else {
                    // Thêm chi tiết phiếu nhập mới
                    String sql = "INSERT INTO chitietphieunhap(ma_phieu, ma_sp, so_luong, don_gia) VALUES(?,?,?,?)";
                    ps = conn.prepareStatement(sql);

                    ps.setString(1, detail.getReceiptId());
                    ps.setString(2, detail.getProductId());
                    ps.setInt(3, detail.getQuantity());
                    ps.setDouble(4, detail.getPrice());

                    System.out.println("Thực thi truy vấn: " + sql + " với tham số: " +
                            detail.getReceiptId() + ", " + detail.getProductId() + ", " +
                            detail.getQuantity() + ", " + detail.getPrice());
                    System.out.println("Thành tiền tính toán: " + totalAmount);
                    int result = ps.executeUpdate();

                    if (result > 0) {
                        System.out.println("Thêm chi tiết phiếu nhập thành công!");

                        // Cập nhật lại tổng tiền cho phiếu nhập
                        updateReceiptTotalAmount(detail.getReceiptId());

                        return true;
                    } else {
                        System.out.println("Thêm chi tiết phiếu nhập thất bại!");
                        return false;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn insertImportReceiptDetail: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật tổng tiền cho phiếu nhập dựa trên các chi tiết
     */
    private void updateReceiptTotalAmount(String receiptId) {
        try {
            // Lấy tổng tiền từ các chi tiết
            String getTotalSql = "SELECT SUM(so_luong * don_gia) AS total FROM chitietphieunhap WHERE ma_phieu = ?";
            ps = conn.prepareStatement(getTotalSql);
            ps.setString(1, receiptId);
            rs = ps.executeQuery();

            if (rs.next()) {
                double newTotal = rs.getDouble("total");

                // Cập nhật tổng tiền
                String updateSql = "UPDATE phieunhap SET tong_tien = ? WHERE ma_phieu = ?";
                ps = conn.prepareStatement(updateSql);
                ps.setDouble(1, newTotal);
                ps.setString(2, receiptId);
                ps.executeUpdate();

                System.out.println("Đã cập nhật tổng tiền phiếu nhập " + receiptId +
                        " thành " + newTotal);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật tổng tiền phiếu nhập: " + e.getMessage());
        }
    }

    /**
     * Cập nhật chi tiết phiếu nhập
     */
    public boolean updateImportReceiptDetail(ImportReceiptDetail detail) {
        try {
            if (conn != null) {
                String sql = "UPDATE chitietphieunhap SET so_luong=?, don_gia=? WHERE ma_phieu=? AND ma_sp=?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, detail.getQuantity());
                ps.setDouble(2, detail.getPrice());
                ps.setString(3, detail.getReceiptId());
                ps.setString(4, detail.getProductId());
                return ps.executeUpdate() > 0;
            } else {
                // Cập nhật trong danh sách tạm thời
                for (int i = 0; i < receiptDetailsList.size(); i++) {
                    ImportReceiptDetail current = receiptDetailsList.get(i);
                    if (current.getReceiptId().equals(detail.getReceiptId()) &&
                            current.getProductId().equals(detail.getProductId())) {
                        receiptDetailsList.set(i, detail);
                        return true;
                    }
                }
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn updateImportReceiptDetail: " + e.getMessage());

            // Thử cập nhật trong danh sách tạm thời nếu có lỗi
            for (int i = 0; i < receiptDetailsList.size(); i++) {
                ImportReceiptDetail current = receiptDetailsList.get(i);
                if (current.getReceiptId().equals(detail.getReceiptId()) &&
                        current.getProductId().equals(detail.getProductId())) {
                    receiptDetailsList.set(i, detail);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Xóa chi tiết phiếu nhập theo mã phiếu và mã sản phẩm
     */
    public boolean deleteImportReceiptDetail(String receiptId, String productId) {
        try {
            if (conn != null) {
                String sql = "DELETE FROM chitietphieunhap WHERE ma_phieu=? AND ma_sp=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, receiptId);
                ps.setString(2, productId);
                return ps.executeUpdate() > 0;
            } else {
                // Xóa trong danh sách tạm thời
                for (int i = 0; i < receiptDetailsList.size(); i++) {
                    ImportReceiptDetail current = receiptDetailsList.get(i);
                    if (current.getReceiptId().equals(receiptId) &&
                            current.getProductId().equals(productId)) {
                        receiptDetailsList.remove(i);
                        return true;
                    }
                }
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn deleteImportReceiptDetail: " + e.getMessage());

            // Thử xóa trong danh sách tạm thời nếu có lỗi
            for (int i = 0; i < receiptDetailsList.size(); i++) {
                ImportReceiptDetail current = receiptDetailsList.get(i);
                if (current.getReceiptId().equals(receiptId) &&
                        current.getProductId().equals(productId)) {
                    receiptDetailsList.remove(i);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Xóa tất cả chi tiết của một phiếu nhập
     */
    public boolean deleteAllImportReceiptDetails(String receiptId) {
        try {
            if (conn != null) {
                String sql = "DELETE FROM chitietphieunhap WHERE ma_phieu=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, receiptId);
                return ps.executeUpdate() > 0;
            } else {
                // Xóa trong danh sách tạm thời
                List<ImportReceiptDetail> toRemove = new ArrayList<>();
                for (ImportReceiptDetail detail : receiptDetailsList) {
                    if (detail.getReceiptId().equals(receiptId)) {
                        toRemove.add(detail);
                    }
                }
                return receiptDetailsList.removeAll(toRemove);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn deleteAllImportReceiptDetails: " + e.getMessage());

            // Thử xóa trong danh sách tạm thời nếu có lỗi
            List<ImportReceiptDetail> toRemove = new ArrayList<>();
            for (ImportReceiptDetail detail : receiptDetailsList) {
                if (detail.getReceiptId().equals(receiptId)) {
                    toRemove.add(detail);
                }
            }
            return receiptDetailsList.removeAll(toRemove);
        }
    }

    /**
     * Thêm phương thức để tạo dữ liệu mẫu cho testing
     */
    public void createSampleData(String receiptId) {
        try {
            if (conn != null) {
                // Kiểm tra cấu trúc bảng sanpham
                ResultSet columns = conn.getMetaData().getColumns(null, null, "sanpham", null);
                System.out.println("DEBUG: Các cột trong bảng sanpham:");
                while (columns.next()) {
                    System.out.println("DEBUG: - " + columns.getString("COLUMN_NAME") + " ("
                            + columns.getString("TYPE_NAME") + ")");
                }
                columns.close();

                // Kiểm tra xem phiếu nhập có tồn tại không
                String checkSql = "SELECT * FROM phieunhap WHERE ma_phieu = ?";
                ps = conn.prepareStatement(checkSql);
                ps.setString(1, receiptId);
                rs = ps.executeQuery();

                if (!rs.next()) {
                    System.out.println("DEBUG: Không thể tạo dữ liệu mẫu vì không tìm thấy phiếu nhập " + receiptId);
                    return;
                }

                // Lấy danh sách sản phẩm có trong hệ thống để đảm bảo không vi phạm khóa ngoại
                String getProductsSql = "SELECT * FROM sanpham LIMIT 3";
                System.out.println("DEBUG: Tìm sản phẩm thực tế trong cơ sở dữ liệu để tạo mẫu");
                ps = conn.prepareStatement(getProductsSql);
                rs = ps.executeQuery();

                // Hiển thị metadata về các cột trong kết quả
                java.sql.ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();
                System.out.println("DEBUG: Kết quả truy vấn sản phẩm có " + columnCount + " cột:");
                for (int i = 1; i <= columnCount; i++) {
                    System.out.println("DEBUG: - Cột " + i + ": " + meta.getColumnName(i) + " ("
                            + meta.getColumnTypeName(i) + ")");
                }

                // Kiểm tra xem có sản phẩm không
                if (!rs.isBeforeFirst()) {
                    System.out.println("DEBUG: Không tìm thấy sản phẩm nào trong cơ sở dữ liệu! Sẽ thử cách khác.");

                    // Thử kiểm tra xem bảng có dữ liệu không
                    String countSql = "SELECT COUNT(*) as soluong FROM sanpham";
                    ps = conn.prepareStatement(countSql);
                    ResultSet countRs = ps.executeQuery();
                    if (countRs.next()) {
                        int productCount = countRs.getInt("soluong");
                        System.out.println("DEBUG: Có tổng cộng " + productCount + " sản phẩm trong cơ sở dữ liệu.");
                    }
                    countRs.close();

                    return;
                }

                // Xóa dữ liệu cũ nếu có
                String deleteSql = "DELETE FROM chitietphieunhap WHERE ma_phieu = ?";
                ps = conn.prepareStatement(deleteSql);
                ps.setString(1, receiptId);
                ps.executeUpdate();

                // Tạo dữ liệu mẫu từ các sản phẩm thực tế
                String insertSql = "INSERT INTO chitietphieunhap(ma_phieu, ma_sp, so_luong, don_gia) VALUES(?,?,?,?)";
                double totalAmount = 0;
                int count = 0;

                // Di chuyển con trỏ kết quả về đầu
                rs.beforeFirst();

                // Thêm các sản phẩm từ kết quả truy vấn
                while (rs.next() && count < 3) {
                    // Tìm các cột chứa mã sản phẩm và giá dựa vào tên cột
                    String productId = "";
                    double price = 0;
                    String productName = "";

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = meta.getColumnName(i).toLowerCase();
                        if (columnName.contains("ma") && columnName.contains("san") && columnName.contains("pham")) {
                            productId = rs.getString(i);
                            System.out.println("DEBUG: Tìm thấy mã sản phẩm ở cột " + i + ": " + productId);
                        } else if (columnName.contains("gia")
                                && (columnName.contains("ban") || columnName.contains("nhap"))) {
                            price = rs.getDouble(i);
                            System.out.println("DEBUG: Tìm thấy giá sản phẩm ở cột " + i + ": " + price);
                        } else if (columnName.contains("ten")) {
                            productName = rs.getString(i);
                            System.out.println("DEBUG: Tìm thấy tên sản phẩm ở cột " + i + ": " + productName);
                        }
                    }

                    if (productId.isEmpty()) {
                        System.out.println("DEBUG: Không tìm thấy mã sản phẩm, bỏ qua dòng này");
                        continue;
                    }

                    if (price <= 0) {
                        price = 10000 + Math.random() * 50000;
                        System.out.println("DEBUG: Giá không hợp lệ, sử dụng giá ngẫu nhiên: " + price);
                    }

                    // Số lượng ngẫu nhiên từ 1-10
                    int quantity = 1 + (int) (Math.random() * 10);

                    System.out.println("DEBUG: Thêm sản phẩm " + productId + " (" + productName
                            + ") vào phiếu nhập với số lượng " + quantity + " và giá " + price);

                    // Thêm vào chi tiết phiếu nhập
                    ps = conn.prepareStatement(insertSql);
                    ps.setString(1, receiptId);
                    ps.setString(2, productId);
                    ps.setInt(3, quantity);
                    ps.setDouble(4, price);

                    try {
                        ps.executeUpdate();
                        // Cộng vào tổng tiền
                        totalAmount += price * quantity;
                        count++;
                    } catch (SQLException e) {
                        System.out.println("DEBUG: Lỗi khi thêm sản phẩm " + productId + ": " + e.getMessage());
                    }
                }

                if (count == 0) {
                    System.out.println("DEBUG: Không có sản phẩm nào được thêm vào phiếu nhập!");
                    return;
                }

                System.out.println("DEBUG: Đã tạo " + count + " dữ liệu mẫu cho phiếu nhập " + receiptId);

                // Cập nhật tổng tiền cho phiếu nhập
                String updateSql = "UPDATE phieunhap SET tong_tien = ? WHERE ma_phieu = ?";
                ps = conn.prepareStatement(updateSql);
                ps.setDouble(1, totalAmount);
                ps.setString(2, receiptId);
                ps.executeUpdate();

                System.out.println("DEBUG: Đã cập nhật tổng tiền " + totalAmount + " cho phiếu nhập " + receiptId);
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Lỗi khi tạo dữ liệu mẫu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Kiểm tra và hiển thị các sản phẩm có trong database
     */
    public void checkProductsInDatabase() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DBConnection.getConnection();
            }

            if (conn != null) {
                System.out.println("\n==== KIỂM TRA BẢNG SANPHAM ====");

                // Hiển thị cấu trúc bảng
                ResultSet columns = conn.getMetaData().getColumns(null, null, "sanpham", null);
                System.out.println("Cấu trúc bảng sanpham:");
                int colCount = 0;
                while (columns.next()) {
                    colCount++;
                    System.out.println(colCount + ". " + columns.getString("COLUMN_NAME") + " ("
                            + columns.getString("TYPE_NAME") + ")");
                }
                columns.close();

                if (colCount == 0) {
                    System.out.println("KHÔNG TÌM THẤY CẤU TRÚC BẢNG SANPHAM!");
                    return;
                }

                // Đếm số sản phẩm trong bảng
                String countSql = "SELECT COUNT(*) as total FROM sanpham";
                ps = conn.prepareStatement(countSql);
                rs = ps.executeQuery();

                int totalProducts = 0;
                if (rs.next()) {
                    totalProducts = rs.getInt("total");
                }

                System.out.println("Tổng số sản phẩm: " + totalProducts);

                if (totalProducts == 0) {
                    System.out.println("KHÔNG CÓ SẢN PHẨM NÀO TRONG BẢNG SANPHAM!");
                    return;
                }

                // Lấy danh sách sản phẩm
                String sql = "SELECT * FROM sanpham LIMIT 10";
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();

                // Hiển thị metadata về các cột trong kết quả
                java.sql.ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();

                // Hiển thị tên các cột
                System.out.println("\nDanh sách sản phẩm (tối đa 10 sản phẩm):");
                StringBuilder header = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    header.append(String.format("%-20s", meta.getColumnName(i)));
                }
                System.out.println(header.toString());
                System.out.println("-".repeat(columnCount * 20));

                int count = 0;
                while (rs.next()) {
                    count++;
                    StringBuilder row = new StringBuilder();
                    for (int i = 1; i <= columnCount; i++) {
                        String value = rs.getString(i);
                        if (value == null)
                            value = "null";
                        row.append(String.format("%-20s", value));
                    }
                    System.out.println(row.toString());
                }

                if (count == 0) {
                    System.out.println("KHÔNG TÌM THẤY SẢN PHẨM NÀO!");
                }

                System.out.println("\n==== KẾT THÚC KIỂM TRA ====\n");
            } else {
                System.out.println("Không thể kết nối đến database để kiểm tra sản phẩm");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Thêm phương thức để tạo dữ liệu mẫu cho testing dựa trên sản phẩm thực tế
     */
    public boolean createSampleDataFromRealProducts(String receiptId) {
        try {
            System.out.println("===== BẮT ĐẦU TẠO DỮ LIỆU MẪU CHO PHIẾU NHẬP " + receiptId + " =====");
            if (conn == null || conn.isClosed()) {
                conn = DBConnection.getConnection();
                if (conn == null) {
                    System.out.println("Không thể kết nối đến cơ sở dữ liệu!");
                    return false;
                }
            }

            // Hiển thị các bảng trong database để kiểm tra
            ResultSet tables = conn.getMetaData().getTables(null, null, "%", new String[] { "TABLE" });
            System.out.println("Các bảng trong database:");
            int tableCount = 0;
            while (tables.next()) {
                tableCount++;
                System.out.println(tableCount + ". " + tables.getString("TABLE_NAME"));
            }
            tables.close();

            // 1. Kiểm tra phiếu nhập
            String checkReceiptSql = "SELECT * FROM phieunhap WHERE ma_phieu = ?";
            ps = conn.prepareStatement(checkReceiptSql);
            ps.setString(1, receiptId);
            rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("Không thể tạo dữ liệu mẫu vì không tìm thấy phiếu nhập " + receiptId);
                return false;
            }
            System.out.println("Đã tìm thấy phiếu nhập " + receiptId);

            // 2. Kiểm tra loại sản phẩm
            String checkCategorySql = "SELECT COUNT(*) as total FROM loaisanpham";
            ps = conn.prepareStatement(checkCategorySql);
            rs = ps.executeQuery();
            int categoryCount = 0;
            if (rs.next()) {
                categoryCount = rs.getInt("total");
            }
            System.out.println("Số loại sản phẩm hiện có: " + categoryCount);

            // Nếu chưa có loại sản phẩm, tạo loại sản phẩm mẫu
            String categoryId = "L001";
            if (categoryCount == 0) {
                System.out.println("Tạo loại sản phẩm mẫu...");
                String createCategorySql = "INSERT INTO loaisanpham(MaLoaiSP, TenLoai) VALUES(?, ?)";
                ps = conn.prepareStatement(createCategorySql);
                ps.setString(1, categoryId);
                ps.setString(2, "Nước giải khát");
                int result = ps.executeUpdate();
                System.out.println("Đã tạo loại sản phẩm mẫu: " + (result > 0 ? "Thành công" : "Thất bại"));
            } else {
                // Lấy một loại sản phẩm có sẵn
                String getCategorySql = "SELECT MaLoaiSP FROM loaisanpham LIMIT 1";
                ps = conn.prepareStatement(getCategorySql);
                rs = ps.executeQuery();
                if (rs.next()) {
                    categoryId = rs.getString("MaLoaiSP");
                    System.out.println("Đã tìm thấy mã loại sản phẩm: " + categoryId);
                }
            }

            // 3. Kiểm tra sản phẩm
            String checkProductSql = "SELECT COUNT(*) as total FROM sanpham";
            ps = conn.prepareStatement(checkProductSql);
            rs = ps.executeQuery();
            int productCount = 0;
            if (rs.next()) {
                productCount = rs.getInt("total");
            }
            System.out.println("Số sản phẩm hiện có: " + productCount);

            // Tạo danh sách mã sản phẩm mẫu
            List<String> productIds = new ArrayList<>();

            // Nếu chưa có sản phẩm, tạo sản phẩm mẫu
            if (productCount == 0) {
                System.out.println("Tạo sản phẩm mẫu...");

                // Dữ liệu mẫu sản phẩm
                Object[][] productData = {
                        { "SP001", "Nước suối Aquafina", categoryId, "Chai", 8000.0 },
                        { "SP002", "Coca Cola", categoryId, "Lon", 12000.0 },
                        { "SP003", "Sting đỏ", categoryId, "Chai", 10000.0 },
                        { "SP004", "Pepsi", categoryId, "Lon", 11000.0 },
                        { "SP005", "Trà xanh 0 độ", categoryId, "Chai", 13000.0 }
                };

                // Tạo từng sản phẩm mẫu
                String createProductSql = "INSERT INTO sanpham(MaSanPham, TenSanPham, MaLoaiSP, DonViTinh, GiaBan) VALUES(?, ?, ?, ?, ?)";
                for (Object[] product : productData) {
                    try {
                        ps = conn.prepareStatement(createProductSql);
                        ps.setString(1, (String) product[0]);
                        ps.setString(2, (String) product[1]);
                        ps.setString(3, (String) product[2]);
                        ps.setString(4, (String) product[3]);
                        ps.setDouble(5, (Double) product[4]);
                        int result = ps.executeUpdate();

                        if (result > 0) {
                            // Thêm vào danh sách sản phẩm mẫu để dùng sau
                            productIds.add((String) product[0]);
                            System.out.println("Đã tạo sản phẩm " + product[1]);
                        } else {
                            System.out.println("Không thể tạo sản phẩm " + product[1]);
                        }
                    } catch (SQLException e) {
                        System.out.println("Lỗi khi tạo sản phẩm " + product[1] + ": " + e.getMessage());
                    }
                }
            } else {
                // Lấy các sản phẩm có sẵn (tối đa 5 sản phẩm)
                String getProductsSql = "SELECT MaSanPham FROM sanpham LIMIT 5";
                ps = conn.prepareStatement(getProductsSql);
                rs = ps.executeQuery();
                while (rs.next()) {
                    String productId = rs.getString("MaSanPham");
                    productIds.add(productId);
                    System.out.println("Đã tìm thấy sản phẩm: " + productId);
                }
            }

            // Kiểm tra xem có lấy được sản phẩm không
            if (productIds.isEmpty()) {
                System.out.println("Không có sản phẩm nào để tạo chi tiết phiếu nhập");
                return false;
            }

            // 4. Tạo chi tiết phiếu nhập
            // Xóa dữ liệu cũ nếu có
            String deleteSql = "DELETE FROM chitietphieunhap WHERE ma_phieu = ?";
            ps = conn.prepareStatement(deleteSql);
            ps.setString(1, receiptId);
            int deleteResult = ps.executeUpdate();
            System.out.println("Đã xóa " + deleteResult + " chi tiết phiếu nhập cũ");

            // Tạo chi tiết phiếu nhập cho từng sản phẩm
            String insertSql = "INSERT INTO chitietphieunhap(ma_phieu, ma_sp, so_luong, don_gia) VALUES(?, ?, ?, ?)";
            double totalAmount = 0;
            int count = 0;

            // Lấy thông tin chi tiết sản phẩm
            for (String productId : productIds) {
                String getProductSql = "SELECT MaSanPham, TenSanPham, GiaBan FROM sanpham WHERE MaSanPham = ?";
                ps = conn.prepareStatement(getProductSql);
                ps.setString(1, productId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    double price = rs.getDouble("GiaBan");
                    String productName = rs.getString("TenSanPham");
                    int quantity = 1 + (int) (Math.random() * 5); // Số lượng ngẫu nhiên từ 1-5

                    try {
                        // Thêm vào chi tiết phiếu nhập
                        ps = conn.prepareStatement(insertSql);
                        ps.setString(1, receiptId);
                        ps.setString(2, productId);
                        ps.setInt(3, quantity);
                        ps.setDouble(4, price);
                        int result = ps.executeUpdate();

                        if (result > 0) {
                            totalAmount += price * quantity;
                            count++;
                            System.out.println("Đã thêm sản phẩm " + productName + " (" + productId +
                                    ") với số lượng " + quantity + " và giá " + price);
                        } else {
                            System.out.println("Không thể thêm chi tiết phiếu nhập cho sản phẩm " + productName);
                        }
                    } catch (SQLException e) {
                        System.out.println(
                                "Lỗi khi thêm chi tiết phiếu nhập cho sản phẩm " + productName + ": " + e.getMessage());
                    }
                }
            }

            if (count == 0) {
                System.out.println("Không thể thêm chi tiết phiếu nhập");
                return false;
            }

            // 5. Cập nhật tổng tiền cho phiếu nhập
            String updateSql = "UPDATE phieunhap SET tong_tien = ? WHERE ma_phieu = ?";
            ps = conn.prepareStatement(updateSql);
            ps.setDouble(1, totalAmount);
            ps.setString(2, receiptId);
            int updateResult = ps.executeUpdate();

            System.out.println("Đã cập nhật tổng tiền " + totalAmount + " cho phiếu nhập " + receiptId +
                    ": " + (updateResult > 0 ? "Thành công" : "Thất bại"));
            System.out.println("Đã tạo " + count + " chi tiết phiếu nhập mẫu với tổng tiền " + totalAmount);
            System.out.println("===== HOÀN THÀNH TẠO DỮ LIỆU MẪU =====");

            return true;
        } catch (SQLException e) {
            System.out.println("Lỗi khi tạo dữ liệu mẫu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Thêm nhiều chi tiết phiếu nhập cùng lúc
     * 
     * @param detailsList Danh sách chi tiết phiếu nhập
     * @return true nếu thêm thành công tất cả, false nếu có lỗi
     */
    public boolean insertMultipleImportReceiptDetails(List<ImportReceiptDetail> detailsList) {
        if (detailsList == null || detailsList.isEmpty()) {
            System.out.println("Danh sách chi tiết phiếu nhập trống!");
            return false;
        }

        boolean success = true;
        int totalItems = detailsList.size();
        int successCount = 0;

        System.out.println("Bắt đầu thêm " + totalItems + " chi tiết phiếu nhập");

        for (ImportReceiptDetail detail : detailsList) {
            try {
                boolean inserted = insertImportReceiptDetail(detail);
                if (inserted) {
                    successCount++;
                    System.out.println("✓ Thêm thành công chi tiết phiếu nhập: " + detail.getReceiptId() +
                            " - Sản phẩm: " + detail.getProductId() +
                            ", SL: " + detail.getQuantity() +
                            ", Đơn giá: " + detail.getPrice() +
                            ", Thành tiền: " + detail.getTotal());
                } else {
                    success = false;
                    System.out.println("✗ Thêm thất bại chi tiết phiếu nhập: " + detail.getReceiptId() +
                            " - Sản phẩm: " + detail.getProductId() +
                            ", SL: " + detail.getQuantity() +
                            ", Đơn giá: " + detail.getPrice() +
                            ", Thành tiền: " + detail.getTotal());
                }
            } catch (Exception e) {
                success = false;
                System.out.println("✗ Lỗi khi thêm chi tiết phiếu nhập: " + detail.getReceiptId() +
                        " - Sản phẩm: " + detail.getProductId() +
                        " - Lỗi: " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Kết quả thêm chi tiết phiếu nhập: " + successCount + "/" + totalItems + " thành công");

        return success;
    }

    /**
     * Script SQL để tạo bảng chitietphieunhap
     * 
     * CREATE TABLE chitietphieunhap (
     * ma_phieu VARCHAR(10) NOT NULL,
     * ma_sp VARCHAR(10) NOT NULL,
     * so_luong INT NOT NULL,
     * don_gia FLOAT NOT NULL,
     * thanh_tien FLOAT NOT NULL,
     * PRIMARY KEY (ma_phieu, ma_sp),
     * FOREIGN KEY (ma_phieu) REFERENCES phieunhap(ma_phieu),
     * FOREIGN KEY (ma_sp) REFERENCES sanpham(ma_sp)
     * );
     */
}