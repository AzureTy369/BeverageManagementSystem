package DAO;

import DTO.Discount;
import DTO.DetailDiscount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DiscountDAO {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static ArrayList<Discount> getAllDiscounts() {
        ArrayList<Discount> discounts = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnection.getConnection();
            String query = "SELECT MaKhuyenMai, TenKhuyenMai, NgayBatDau, NgayKetThuc FROM khuyenmai";
            pstmt = connection.prepareStatement(query);
            resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String discountId = resultSet.getString("MaKhuyenMai");
                String discountName = resultSet.getString("TenKhuyenMai");
                Timestamp beginTimestamp = resultSet.getTimestamp("NgayBatDau");
                Timestamp endTimestamp = resultSet.getTimestamp("NgayKetThuc");

                String begin = beginTimestamp != null ? DATE_FORMAT.format(beginTimestamp) : null;
                String end = endTimestamp != null ? DATE_FORMAT.format(endTimestamp) : null;

                // Lấy percentDiscount từ DetailDiscountDAO
                ArrayList<DetailDiscount> details = DetailDiscountDAO.getAllDetailDiscounts(discountId);
                double percentDiscount = details.isEmpty() ? 0.0 : details.get(0).getPercentDiscount();

                discounts.add(new Discount(discountId, discountName, begin, end, percentDiscount));
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
        return discounts;
    }

    public static void deleteAllDiscounts() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = DBConnection.getConnection();
            // Xóa chi tiết khuyến mãi trước do ràng buộc khóa ngoại
            DetailDiscountDAO.deleteAllDetailDiscounts();
            String query = "DELETE FROM khuyenmai";
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

    public static ArrayList<Discount> search(String info) {
        ArrayList<Discount> discounts = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            String query;
            if (info != null && !info.isEmpty()) {
                query = "SELECT MaKhuyenMai, TenKhuyenMai, NgayBatDau, NgayKetThuc FROM khuyenmai " +
                        "WHERE MaKhuyenMai LIKE ? OR TenKhuyenMai LIKE ?";
                pstmt = connection.prepareStatement(query);
                String searchValue = "%" + info + "%";
                pstmt.setString(1, searchValue);
                pstmt.setString(2, searchValue);
            } else {
                query = "SELECT MaKhuyenMai, TenKhuyenMai, NgayBatDau, NgayKetThuc FROM khuyenmai";
                pstmt = connection.prepareStatement(query);
            }

            resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String discountId = resultSet.getString("MaKhuyenMai");
                String discountName = resultSet.getString("TenKhuyenMai");
                Timestamp beginTimestamp = resultSet.getTimestamp("NgayBatDau");
                Timestamp endTimestamp = resultSet.getTimestamp("NgayKetThuc");

                String begin = beginTimestamp != null ? DATE_FORMAT.format(beginTimestamp) : null;
                String end = endTimestamp != null ? DATE_FORMAT.format(endTimestamp) : null;

                // Lấy percentDiscount từ DetailDiscountDAO
                ArrayList<DetailDiscount> details = DetailDiscountDAO.getAllDetailDiscounts(discountId);
                double percentDiscount = details.isEmpty() ? 0.0 : details.get(0).getPercentDiscount();

                discounts.add(new Discount(discountId, discountName, begin, end, percentDiscount));
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
        return discounts;
    }

    public static void setAllDiscounts(ArrayList<Discount> list) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            // Xóa tất cả khuyến mãi và chi tiết khuyến mãi hiện có
            deleteAllDiscounts();
            connection = DBConnection.getConnection();

            String sql = "INSERT INTO khuyenmai (MaKhuyenMai, TenKhuyenMai, NgayBatDau, NgayKetThuc) VALUES (?, ?, ?, ?)";
            pstmt = connection.prepareStatement(sql);

            ArrayList<DetailDiscount> detailDiscounts = new ArrayList<>();
            for (Discount discount : list) {
                pstmt.setString(1, discount.getDiscountId());
                pstmt.setString(2, discount.getDiscountName());
                pstmt.setString(3, discount.getBegin());
                pstmt.setString(4, discount.getEnd());

                pstmt.addBatch();

                // Lưu percentDiscount vào detailDiscounts để chèn vào chitietkhuyenmai
                if (discount.getPercentDiscount() > 0) {
                    // Giả định MaSanPham mặc định hoặc cần cung cấp
                    detailDiscounts.add(new DetailDiscount(discount.getDiscountId(), "SP_DEFAULT", discount.getPercentDiscount()));
                }
            }
            pstmt.executeBatch();

            // Chèn chi tiết khuyến mãi
            if (!detailDiscounts.isEmpty()) {
                DetailDiscountDAO.setAllDetailDiscounts(detailDiscounts);
            }
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
}