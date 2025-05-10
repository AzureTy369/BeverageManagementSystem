package DAO;

import DTO.ImportReceipt; 
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.ArrayList;
import java.util.Collections;
import java.awt.Color;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DatabaseMetaData;

public class ImportReceiptDAO {
    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    // Danh sách tạm thời cho phiếu nhập, sẽ chỉ sử dụng khi kết nối CSDL thất bại
    private static List<ImportReceipt> importReceipts = new ArrayList<>();

    public ImportReceiptDAO() {
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
                String sql = "CREATE TABLE IF NOT EXISTS phieunhap (" +
                        "ma_phieu VARCHAR(10) PRIMARY KEY, " +
                        "ma_ncc VARCHAR(10) NOT NULL, " +
                        "ma_nv VARCHAR(10) NOT NULL, " +
                        "ngay_nhap VARCHAR(50) NOT NULL, " +
                        "tong_tien DECIMAL(10, 2) NOT NULL DEFAULT 0, " +
                        "ghi_chu NVARCHAR(255), " +
                        "trang_thai VARCHAR(20) DEFAULT 'Đang xử lý'" +
                        ")";
                ps = conn.prepareStatement(sql);
                ps.executeUpdate();

                // Kiểm tra xem cột trang_thai đã tồn tại chưa
                try {
                    DatabaseMetaData meta = conn.getMetaData();
                    ResultSet rs = meta.getColumns(null, null, "phieunhap", "trang_thai");

                    if (!rs.next()) {
                        // Nếu cột chưa tồn tại, thêm mới
                        String alterSql = "ALTER TABLE phieunhap ADD COLUMN trang_thai VARCHAR(20) DEFAULT 'Đang xử lý'";
                        ps = conn.prepareStatement(alterSql);
                        ps.executeUpdate();
                        System.out.println("Đã thêm cột trạng thái vào bảng phiếu nhập");
                    }
                    rs.close();
                } catch (SQLException e) {
                    System.out.println("Lỗi khi kiểm tra hoặc thêm cột trang_thai: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi tạo bảng phieunhap: " + e.getMessage());
        }
    }

    /**
     * Lấy tất cả phiếu nhập từ cơ sở dữ liệu hoặc danh sách tạm thời
     */
    public List<ImportReceipt> getAllImportReceipts() {
        List<ImportReceipt> list = new ArrayList<>();
        try {
            if (conn != null) {
                String sql = "SELECT * FROM phieunhap ORDER BY ma_phieu";
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();

                while (rs.next()) {
                    ImportReceipt receipt = new ImportReceipt();
                    receipt.setImportId(rs.getString("ma_phieu"));
                    receipt.setSupplierId(rs.getString("ma_ncc"));
                    receipt.setEmployeeId(rs.getString("ma_nv"));
                    receipt.setImportDate(rs.getString("ngay_nhap"));
                    receipt.setTotalAmount(rs.getString("tong_tien"));
                    receipt.setNote(rs.getString("ghi_chu"));

                    // Lấy trạng thái, nếu null thì mặc định là "Đang xử lý"
                    String status = rs.getString("trang_thai");
                    receipt.setStatus(status != null ? status : "Đang xử lý");

                    list.add(receipt);
                }
                return list;
            } else {
                // Trả về danh sách tạm thời nếu không có kết nối
                return importReceipts;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn getAllImportReceipts: " + e.getMessage());
            // Nếu có lỗi, trả về danh sách tạm thời
            return importReceipts;
        }
    }

    /**
     * Lấy phiếu nhập theo ID
     */
    public ImportReceipt getImportReceiptById(String importId) {
        try {
            if (conn != null) {
                String sql = "SELECT * FROM phieunhap WHERE ma_phieu = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, importId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    ImportReceipt receipt = new ImportReceipt();
                    receipt.setImportId(rs.getString("ma_phieu"));
                    receipt.setSupplierId(rs.getString("ma_ncc"));
                    receipt.setEmployeeId(rs.getString("ma_nv"));
                    receipt.setImportDate(rs.getString("ngay_nhap"));
                    receipt.setTotalAmount(rs.getString("tong_tien"));
                    receipt.setNote(rs.getString("ghi_chu"));

                    // Lấy trạng thái, nếu null thì mặc định là "Đang xử lý"
                    String status = rs.getString("trang_thai");
                    receipt.setStatus(status != null ? status : "Đang xử lý");

                    return receipt;
                }
            } else {
                // Tìm kiếm trong danh sách tạm thời
                for (ImportReceipt receipt : importReceipts) {
                    if (receipt.getImportId().equals(importId)) {
                        return receipt;
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn getImportReceiptById: " + e.getMessage());

            // Tìm kiếm trong danh sách tạm thời nếu có lỗi
            for (ImportReceipt receipt : importReceipts) {
                if (receipt.getImportId().equals(importId)) {
                    return receipt;
                }
            }
            return null;
        }
    }

    /**
     * Thêm phiếu nhập mới
     */
    public boolean insertImportReceipt(ImportReceipt receipt) {
        try {
            if (conn != null) {
                String sql = "INSERT INTO phieunhap(ma_phieu, ma_ncc, ma_nv, ngay_nhap, tong_tien, ghi_chu, trang_thai) VALUES(?,?,?,?,?,?,?)";
                ps = conn.prepareStatement(sql);
                ps.setString(1, receipt.getImportId());
                ps.setString(2, receipt.getSupplierId());
                ps.setString(3, receipt.getEmployeeId());
                ps.setString(4, receipt.getImportDate());
                ps.setString(5, receipt.getTotalAmount());
                ps.setString(6, receipt.getNote());
                ps.setString(7, receipt.getStatus() != null ? receipt.getStatus() : "Đang xử lý");
                return ps.executeUpdate() > 0;
            } else {
                // Kiểm tra trùng ID
                for (ImportReceipt existing : importReceipts) {
                    if (existing.getImportId().equals(receipt.getImportId())) {
                        return false; // ID đã tồn tại
                    }
                }

                // Thêm vào danh sách tạm thời
                importReceipts.add(receipt);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn insertImportReceipt: " + e.getMessage());

            // Thử thêm vào danh sách tạm thời nếu có lỗi
            for (ImportReceipt existing : importReceipts) {
                if (existing.getImportId().equals(receipt.getImportId())) {
                    return false; // ID đã tồn tại
                }
            }
            importReceipts.add(receipt);
            return true;
        }
    }

    /**
     * Cập nhật thông tin phiếu nhập
     */
    public boolean updateImportReceipt(ImportReceipt receipt) {
        try {
            if (conn != null) {
                String sql = "UPDATE phieunhap SET ma_ncc=?, ma_nv=?, ngay_nhap=?, tong_tien=?, ghi_chu=?, trang_thai=? WHERE ma_phieu=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, receipt.getSupplierId());
                ps.setString(2, receipt.getEmployeeId());
                ps.setString(3, receipt.getImportDate());
                ps.setString(4, receipt.getTotalAmount());
                ps.setString(5, receipt.getNote());
                ps.setString(6, receipt.getStatus() != null ? receipt.getStatus() : "Đang xử lý");
                ps.setString(7, receipt.getImportId());
                return ps.executeUpdate() > 0;
            } else {
                // Cập nhật trong danh sách tạm thời
                for (int i = 0; i < importReceipts.size(); i++) {
                    if (importReceipts.get(i).getImportId().equals(receipt.getImportId())) {
                        importReceipts.set(i, receipt);
                        return true;
                    }
                }
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn updateImportReceipt: " + e.getMessage());

            // Thử cập nhật trong danh sách tạm thời nếu có lỗi
            for (int i = 0; i < importReceipts.size(); i++) {
                if (importReceipts.get(i).getImportId().equals(receipt.getImportId())) {
                    importReceipts.set(i, receipt);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Cập nhật trạng thái phiếu nhập
     */
    public boolean updateImportReceiptStatus(String importId, String status) {
        try {
            if (conn != null) {
                String sql = "UPDATE phieunhap SET trang_thai=? WHERE ma_phieu=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, status);
                ps.setString(2, importId);

                return ps.executeUpdate() > 0;
            } else {
                // Cập nhật trong danh sách tạm thời
                for (ImportReceipt receipt : importReceipts) {
                    if (receipt.getImportId().equals(importId)) {
                        receipt.setStatus(status);
                        return true;
                    }
                }
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn updateImportReceiptStatus: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa phiếu nhập theo ID
     */
    public boolean deleteImportReceipt(String importId) {
        try {
            if (conn != null) {
                // Xóa chi tiết phiếu nhập trước
                String deleteDetailsSql = "DELETE FROM chitietphieunhap WHERE ma_phieu=?";
                ps = conn.prepareStatement(deleteDetailsSql);
                ps.setString(1, importId);
                ps.executeUpdate();

                // Sau đó xóa phiếu nhập
                String sql = "DELETE FROM phieunhap WHERE ma_phieu=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, importId);
                return ps.executeUpdate() > 0;
            } else {
                // Xóa trong danh sách tạm thời
                for (int i = 0; i < importReceipts.size(); i++) {
                    if (importReceipts.get(i).getImportId().equals(importId)) {
                        importReceipts.remove(i);
                        return true;
                    }
                }
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn deleteImportReceipt: " + e.getMessage());

            // Thử xóa trong danh sách tạm thời nếu có lỗi
            for (int i = 0; i < importReceipts.size(); i++) {
                if (importReceipts.get(i).getImportId().equals(importId)) {
                    importReceipts.remove(i);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Tìm kiếm phiếu nhập theo từ khóa
     */
    public List<ImportReceipt> search(String keyword) {
        List<ImportReceipt> results = new ArrayList<>();

        try {
            if (conn != null) {
                String sql = "SELECT * FROM phieunhap WHERE ma_phieu LIKE ? OR ma_ncc LIKE ? OR ma_nv LIKE ? OR ngay_nhap LIKE ? OR ghi_chu LIKE ?";
                ps = conn.prepareStatement(sql);
                String searchPattern = "%" + keyword + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);
                ps.setString(3, searchPattern);
                ps.setString(4, searchPattern);
                ps.setString(5, searchPattern);
                rs = ps.executeQuery();

                while (rs.next()) {
                    ImportReceipt receipt = new ImportReceipt();
                    receipt.setImportId(rs.getString("ma_phieu"));
                    receipt.setSupplierId(rs.getString("ma_ncc"));
                    receipt.setEmployeeId(rs.getString("ma_nv"));
                    receipt.setImportDate(rs.getString("ngay_nhap"));
                    receipt.setTotalAmount(rs.getString("tong_tien"));
                    receipt.setNote(rs.getString("ghi_chu"));
                    results.add(receipt);
                }
                return results;
            } else {
                // Tìm kiếm trong danh sách tạm thời
                for (ImportReceipt receipt : importReceipts) {
                    if (receipt.getImportId().toLowerCase().contains(keyword.toLowerCase()) ||
                            receipt.getSupplierId().toLowerCase().contains(keyword.toLowerCase()) ||
                            receipt.getEmployeeId().toLowerCase().contains(keyword.toLowerCase()) ||
                            receipt.getImportDate().toLowerCase().contains(keyword.toLowerCase()) ||
                            receipt.getNote().toLowerCase().contains(keyword.toLowerCase())) {
                        results.add(receipt);
                    }
                }
                return results;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn search: " + e.getMessage());

            // Tìm kiếm trong danh sách tạm thời nếu có lỗi
            for (ImportReceipt receipt : importReceipts) {
                if (receipt.getImportId().toLowerCase().contains(keyword.toLowerCase()) ||
                        receipt.getSupplierId().toLowerCase().contains(keyword.toLowerCase()) ||
                        receipt.getEmployeeId().toLowerCase().contains(keyword.toLowerCase()) ||
                        receipt.getImportDate().toLowerCase().contains(keyword.toLowerCase()) ||
                        receipt.getNote().toLowerCase().contains(keyword.toLowerCase())) {
                    results.add(receipt);
                }
            }
            return results;
        }
    }
    public static JFreeChart createImportChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            Connection connection = DBConnection.getConnection();
            String query = """
                   SELECT SUBSTRING(date, 7, 4) AS year, CAST(SUM(totalCost) AS UNSIGNED) AS total_import_cost
                   FROM Import
                   GROUP BY SUBSTRING(date, 7, 4)
                   ORDER BY year DESC
                   LIMIT 5""";

            try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(query)) {
                // Tạo list để lưu trữ dữ liệu
                List<String[]> dataList = new ArrayList<>();

                // Lưu trữ dữ liệu vào list
                while (rs.next()) {
                    String year = rs.getString("year");
                    int totalImportCost = rs.getInt("total_import_cost");
                    dataList.add(new String[]{year, String.valueOf(totalImportCost)});
                }

                // Đảo ngược thứ tự list
                Collections.reverse(dataList);

                // Thêm dữ liệu từ list đã đảo ngược vào dataset
                for (String[] data : dataList) {
                    dataset.addValue(Integer.parseInt(data[1]), "Chi phí nhập", data[0]);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Thống kê tổng tiền nhập hàng hằng năm",
                "Năm",
                "Tổng tiền nhập hàng",
                dataset
        );
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, Color.CYAN);

        return chart;
    }

}