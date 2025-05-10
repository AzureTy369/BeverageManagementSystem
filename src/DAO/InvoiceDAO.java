package DAO;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import DTO.Invoice;

public class InvoiceDAO {

    public static ArrayList<Invoice> getAllInvoices() {
        ArrayList<Invoice> invoices = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnection.getConnection();
            String query = "SELECT h.MaHoaDon, h.MaKhachHang, h.MaNhanVien, h.MaKhuyenMai, h.NgayLapHoaDon, " +
                          "h.TamTinh, h.GiamGia, h.TongHoaDon, h.PhuongThucThanhToan, " +
                          "MAX(ct.ThanhTien * c.PhanTramKhuyenMai / 100) AS MaxDiscountValue, " +
                          "MIN(CASE WHEN ct.ThanhTien * c.PhanTramKhuyenMai / 100 = " +
                          "(SELECT MAX(ct2.ThanhTien * c2.PhanTramKhuyenMai / 100) " +
                          "FROM chitiethoadon ct2 LEFT JOIN chitietkhuyenmai c2 ON h.MaKhuyenMai = c2.MaKhuyenMai AND ct2.MaSanPham = c2.MaSanPham " +
                          "WHERE ct2.MaHoaDon = h.MaHoaDon) " +
                          "THEN c.PhanTramKhuyenMai END) AS PhanTramKhuyenMai " +
                          "FROM hoadon h " +
                          "LEFT JOIN chitiethoadon ct ON h.MaHoaDon = ct.MaHoaDon " +
                          "LEFT JOIN chitietkhuyenmai c ON h.MaKhuyenMai = c.MaKhuyenMai AND ct.MaSanPham = c.MaSanPham " +
                          "GROUP BY h.MaHoaDon " +
                          "ORDER BY h.NgayLapHoaDon DESC";
            pstmt = connection.prepareStatement(query);
            resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String invoiceID = resultSet.getString("MaHoaDon");
                String customerID = resultSet.getString("MaKhachHang");
                String employeeID = resultSet.getString("MaNhanVien");
                String discountID = resultSet.getString("MaKhuyenMai");
                String date = resultSet.getString("NgayLapHoaDon");
                Double tempCost = resultSet.getDouble("TamTinh");
                if (resultSet.wasNull()) tempCost = null;
                Double reducedCost = resultSet.getDouble("GiamGia");
                if (resultSet.wasNull()) reducedCost = null;
                Double totalCost = resultSet.getDouble("TongHoaDon");
                String payment = resultSet.getString("PhuongThucThanhToan");
                double percentDiscount = resultSet.getDouble("PhanTramKhuyenMai");
                if (resultSet.wasNull()) percentDiscount = 0.0;

                invoices.add(new Invoice(invoiceID, customerID, employeeID, discountID, date, tempCost, reducedCost, totalCost, payment, percentDiscount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return invoices;
    }

    public static void deleteAllInvoices() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = DBConnection.getConnection();
            // Xóa chi tiết hóa đơn trước do ràng buộc khóa ngoại
            DetailInvoiceDAO.deleteAllInvoiceDetails();
            String query = "DELETE FROM hoadon";
            pstmt = connection.prepareStatement(query);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setAllInvoices(ArrayList<Invoice> list) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            deleteAllInvoices();
            connection = DBConnection.getConnection();

            String query = "INSERT INTO hoadon (MaHoaDon, MaKhachHang, MaNhanVien, MaKhuyenMai, NgayLapHoaDon, TamTinh, GiamGia, TongHoaDon, PhuongThucThanhToan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = connection.prepareStatement(query);

            for (Invoice invoice : list) {
                pstmt.setString(1, invoice.getInvoiceID());
                pstmt.setString(2, invoice.getCustomerID());
                pstmt.setString(3, invoice.getEmployeeID());
                pstmt.setString(4, invoice.getDiscountID());
                pstmt.setString(5, invoice.getDate());
                if (invoice.getTempCost() != null) {
                    pstmt.setDouble(6, invoice.getTempCost());
                } else {
                    pstmt.setNull(6, java.sql.Types.DECIMAL);
                }
                if (invoice.getReducedCost() != null) {
                    pstmt.setDouble(7, invoice.getReducedCost());
                } else {
                    pstmt.setNull(7, java.sql.Types.DECIMAL);
                }
                pstmt.setDouble(8, invoice.getTotalCost());
                pstmt.setString(9, invoice.getPayment());

                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<Invoice> search(String info) {
        ArrayList<Invoice> result = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            String query;
            if (info != null && !info.isEmpty()) {
                query = "SELECT h.MaHoaDon, h.MaKhachHang, h.MaNhanVien, h.MaKhuyenMai, h.NgayLapHoaDon, " +
                        "h.TamTinh, h.GiamGia, h.TongHoaDon, h.PhuongThucThanhToan, " +
                        "MAX(ct.ThanhTien * c.PhanTramKhuyenMai / 100) AS MaxDiscountValue, " +
                        "MIN(CASE WHEN ct.ThanhTien * c.PhanTramKhuyenMai / 100 = " +
                        "(SELECT MAX(ct2.ThanhTien * c2.PhanTramKhuyenMai / 100) " +
                        "FROM chitiethoadon ct2 LEFT JOIN chitietkhuyenmai c2 ON h.MaKhuyenMai = c2.MaKhuyenMai AND ct2.MaSanPham = c2.MaSanPham " +
                        "WHERE ct2.MaHoaDon = h.MaHoaDon) " +
                        "THEN c.PhanTramKhuyenMai END) AS PhanTramKhuyenMai " +
                        "FROM hoadon h " +
                        "LEFT JOIN chitiethoadon ct ON h.MaHoaDon = ct.MaHoaDon " +
                        "LEFT JOIN chitietkhuyenmai c ON h.MaKhuyenMai = c.MaKhuyenMai AND ct.MaSanPham = c.MaSanPham " +
                        "WHERE h.MaHoaDon LIKE ? OR h.MaKhachHang LIKE ? OR h.MaNhanVien LIKE ? OR h.MaKhuyenMai LIKE ? OR h.NgayLapHoaDon LIKE ? OR h.PhuongThucThanhToan LIKE ? " +
                        "GROUP BY h.MaHoaDon " +
                        "ORDER BY h.NgayLapHoaDon DESC";
                pstmt = connection.prepareStatement(query);
                String searchValue = "%" + info + "%";
                pstmt.setString(1, searchValue);
                pstmt.setString(2, searchValue);
                pstmt.setString(3, searchValue);
                pstmt.setString(4, searchValue);
                pstmt.setString(5, searchValue);
                pstmt.setString(6, searchValue);
            } else {
                query = "SELECT h.MaHoaDon, h.MaKhachHang, h.MaNhanVien, h.MaKhuyenMai, h.NgayLapHoaDon, " +
                        "h.TamTinh, h.GiamGia, h.TongHoaDon, h.PhuongThucThanhToan, " +
                        "MAX(ct.ThanhTien * c.PhanTramKhuyenMai / 100) AS MaxDiscountValue, " +
                        "MIN(CASE WHEN ct.ThanhTien * c.PhanTramKhuyenMai / 100 = " +
                        "(SELECT MAX(ct2.ThanhTien * c2.PhanTramKhuyenMai / 100) " +
                        "FROM chitiethoadon ct2 LEFT JOIN chitietkhuyenmai c2 ON h.MaKhuyenMai = c2.MaKhuyenMai AND ct2.MaSanPham = c2.MaSanPham " +
                        "WHERE ct2.MaHoaDon = h.MaHoaDon) " +
                        "THEN c.PhanTramKhuyenMai END) AS PhanTramKhuyenMai " +
                        "FROM hoadon h " +
                        "LEFT JOIN chitiethoadon ct ON h.MaHoaDon = ct.MaHoaDon " +
                        "LEFT JOIN chitietkhuyenmai c ON h.MaKhuyenMai = c.MaKhuyenMai AND ct.MaSanPham = c.MaSanPham " +
                        "GROUP BY h.MaHoaDon " +
                        "ORDER BY h.NgayLapHoaDon DESC";
                pstmt = connection.prepareStatement(query);
            }

            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String invoiceID = resultSet.getString("MaHoaDon");
                String customerID = resultSet.getString("MaKhachHang");
                String employeeID = resultSet.getString("MaNhanVien");
                String discountID = resultSet.getString("MaKhuyenMai");
                String date = resultSet.getString("NgayLapHoaDon");
                Double tempCost = resultSet.getDouble("TamTinh");
                if (resultSet.wasNull()) tempCost = null;
                Double reducedCost = resultSet.getDouble("GiamGia");
                if (resultSet.wasNull()) reducedCost = null;
                Double totalCost = resultSet.getDouble("TongHoaDon");
                String payment = resultSet.getString("PhuongThucThanhToan");
                double percentDiscount = resultSet.getDouble("PhanTramKhuyenMai");
                if (resultSet.wasNull()) percentDiscount = 0.0;

                result.add(new Invoice(invoiceID, customerID, employeeID, discountID, date, tempCost, reducedCost, totalCost, payment, percentDiscount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static JFreeChart createInvoiceChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            connection = DBConnection.getConnection();
            String query = """
                   SELECT SUBSTRING(NgayLapHoaDon, 1, 4) AS year, 
                          SUM(TongHoaDon) AS total_invoice_cost
                   FROM hoadon
                   GROUP BY SUBSTRING(NgayLapHoaDon, 1, 4)
                   ORDER BY year DESC
                   LIMIT 5""";

            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            // Tạo list để lưu trữ dữ liệu
            List<String[]> dataList = new ArrayList<>();

            // Lưu trữ dữ liệu vào list
            while (rs.next()) {
                String year = rs.getString("year");
                double totalInvoiceCost = rs.getDouble("total_invoice_cost");
                dataList.add(new String[]{year, String.valueOf(totalInvoiceCost)});
            }

            // Đảo ngược thứ tự list
            Collections.reverse(dataList);

            // Thêm dữ liệu từ list đã đảo ngược vào dataset
            for (String[] data : dataList) {
                dataset.addValue(Double.parseDouble(data[1]) / 1_000_000, "Doanh thu", data[0]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Thống kê doanh thu bán hàng hằng năm",
                "Năm",
                "Doanh thu (triệu VND)",
                dataset
        );
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);

        return chart;
    }
}