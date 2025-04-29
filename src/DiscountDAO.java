package DAO;

import DTO.Discount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DiscountDAO {
    public static ArrayList<Discount> getAllDiscounts() {
        ArrayList<Discount> discounts = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnection.getConnection();
            String query = "SELECT * FROM khuyenmai";
            pstmt = connection.prepareStatement(query);
            resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String maKhuyenMai = resultSet.getString("MaKhuyenMai");
                String tenKhuyenMai = resultSet.getString("TenKhuyenMai");
                String ngayBatDau = resultSet.getString("NgayBatDau");
                String ngayKetThuc = resultSet.getString("NgayKetThuc");

                discounts.add(new Discount(maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc));
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
                query = "SELECT * FROM khuyenmai WHERE MaKhuyenMai LIKE ? OR TenKhuyenMai LIKE ? OR NgayBatDau LIKE ?";
                pstmt = connection.prepareStatement(query);
                String searchValue = "%" + info + "%";
                pstmt.setString(1, searchValue);
                pstmt.setString(2, searchValue);
                pstmt.setString(3, searchValue);
            } else {
                query = "SELECT * FROM khuyenmai";
                pstmt = connection.prepareStatement(query);
            }

            resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String maKhuyenMai = resultSet.getString("MaKhuyenMai");
                String tenKhuyenMai = resultSet.getString("TenKhuyenMai");
                String ngayBatDau = resultSet.getString("NgayBatDau");
                String ngayKetThuc = resultSet.getString("NgayKetThuc");

                discounts.add(new Discount(maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc));
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
            deleteAllDiscounts();
            connection = DBConnection.getConnection();

            String sql = "INSERT INTO khuyenmai (MaKhuyenMai, TenKhuyenMai, NgayBatDau, NgayKetThuc) VALUES (?, ?, ?, ?)";
            pstmt = connection.prepareStatement(sql);

            for (Discount discount : list) {
                pstmt.setString(1, discount.getDiscountId());
                pstmt.setString(2, discount.getDiscountName());
                pstmt.setString(3, discount.getDiscountBegin());
                pstmt.setString(4, discount.getDiscountEnd());
                pstmt.executeUpdate();
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