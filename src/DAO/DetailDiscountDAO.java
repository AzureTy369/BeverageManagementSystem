package DAO;

import DTO.DetailDiscount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DetailDiscountDAO {

    public static ArrayList<DetailDiscount> getAllDetailDiscounts(String discountId) {
        ArrayList<DetailDiscount> details = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnection.getConnection();
            String query = "SELECT MaKhuyenMai, MaSanPham, PhanTramKhuyenMai FROM chitietkhuyenmai";
            if (discountId != null && !discountId.isEmpty()) {
                query += " WHERE MaKhuyenMai = ?";
            }

            pstmt = connection.prepareStatement(query);
            if (discountId != null && !discountId.isEmpty()) {
                pstmt.setString(1, discountId);
            }

            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String maKhuyenMai = resultSet.getString("MaKhuyenMai");
                String maSanPham = resultSet.getString("MaSanPham");
                double phanTramKhuyenMai = resultSet.getDouble("PhanTramKhuyenMai");

                details.add(new DetailDiscount(maKhuyenMai, maSanPham, phanTramKhuyenMai));
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
        return details;
    }

    public static void deleteAllDetailDiscounts() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = DBConnection.getConnection();
            String sql = "DELETE FROM chitietkhuyenmai";
            pstmt = connection.prepareStatement(sql);
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

    public static void setAllDetailDiscounts(ArrayList<DetailDiscount> list) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            deleteAllDetailDiscounts();
            connection = DBConnection.getConnection();

            String sql = "INSERT INTO chitietkhuyenmai (MaKhuyenMai, MaSanPham, PhanTramKhuyenMai) VALUES (?, ?, ?)";
            pstmt = connection.prepareStatement(sql);

            for (DetailDiscount detail : list) {
                pstmt.setString(1, detail.getDiscountId());
                pstmt.setString(2, detail.getProductId());
                pstmt.setDouble(3, detail.getPercentDiscount());

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

    public static ArrayList<DetailDiscount> search(String info) {
        ArrayList<DetailDiscount> details = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            String query;
            if (info != null && !info.isEmpty()) {
                query = "SELECT MaKhuyenMai, MaSanPham, PhanTramKhuyenMai FROM chitietkhuyenmai " +
                        "WHERE MaKhuyenMai LIKE ? OR MaSanPham LIKE ?";
                pstmt = connection.prepareStatement(query);
                String searchValue = "%" + info + "%";
                pstmt.setString(1, searchValue);
                pstmt.setString(2, searchValue);
            } else {
                query = "SELECT MaKhuyenMai, MaSanPham, PhanTramKhuyenMai FROM chitietkhuyenmai";
                pstmt = connection.prepareStatement(query);
            }

            resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String maKhuyenMai = resultSet.getString("MaKhuyenMai");
                String maSanPham = resultSet.getString("MaSanPham");
                double phanTramKhuyenMai = resultSet.getDouble("PhanTramKhuyenMai");

                details.add(new DetailDiscount(maKhuyenMai, maSanPham, phanTramKhuyenMai));
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
        return details;
    }
}