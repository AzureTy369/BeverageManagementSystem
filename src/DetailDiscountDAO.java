package DAO;

import DTO.DetailDiscount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DetailDiscountDAO {

    public static ArrayList<DetailDiscount> getAllDetailDiscounts(String discountID) {
        ArrayList<DetailDiscount> discounts = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnection.getConnection();
            String condition = "";
            if (discountID != null) {
                condition += " WHERE MaKhuyenMai = ?";
            }

            String query = "SELECT * FROM chitietkhuyenmai" + condition;
            statement = connection.prepareStatement(query);
            if (discountID != null) {
                statement.setString(1, discountID);
            }

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String maKhuyenMai = resultSet.getString("MaKhuyenMai");
                String maSanPham = resultSet.getString("MaSanPham");
                double phanTramKhuyenMai = resultSet.getDouble("PhanTramKhuyenMai");

                discounts.add(new DetailDiscount(maKhuyenMai, maSanPham, phanTramKhuyenMai));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
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
        return discounts;
    }

    public static void addDetailDiscount(DetailDiscount detail) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = DBConnection.getConnection();
            String sql = "INSERT INTO chitietkhuyenmai (MaKhuyenMai, MaSanPham, PhanTramKhuyenMai) VALUES (?, ?, ?)";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, detail.getDiscountID());
            pstmt.setString(2, detail.getProductID());
            pstmt.setDouble(3, detail.getPercentDiscount());
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

    public static void deleteDetailDiscount(String discountID, String productID) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = DBConnection.getConnection();
            String sql = "DELETE FROM chitietkhuyenmai WHERE MaKhuyenMai = ? AND MaSanPham = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, discountID);
            pstmt.setString(2, productID);
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

    public static void deleteAllDiscountDetails() {
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
            deleteAllDiscountDetails();
            connection = DBConnection.getConnection();

            String sql = "INSERT INTO chitietkhuyenmai (MaKhuyenMai, MaSanPham, PhanTramKhuyenMai) VALUES (?, ?, ?)";
            pstmt = connection.prepareStatement(sql);

            for (DetailDiscount discountDTO : list) {
                pstmt.setString(1, discountDTO.getDiscountID());
                pstmt.setString(2, discountDTO.getProductID());
                pstmt.setDouble(3, discountDTO.getPercentDiscount());
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