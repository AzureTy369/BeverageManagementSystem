
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

/**
 *
 * @author ASUS
 */
public class InvoiceDAO {

    public static ArrayList<Invoice> getAllInvoices() {
        ArrayList<Invoice> invoices = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnection.getConnection();
            String query = "SELECT * FROM invoice ORDER BY date DESC";
            pstmt = connection.prepareStatement(query);
            resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String invoiceID = resultSet.getString("invoiceID");
                String customerID = resultSet.getString("customerID");
                String employeeID = resultSet.getString("employeeID");
                String discountID = resultSet.getString("discountID");
                String date = resultSet.getString("date");
                Double tempCost = resultSet.getDouble("tempCost");
                Double reducedCost = resultSet.getDouble("reducedCost");
                Double totalCost = resultSet.getDouble("totalCost");

                invoices.add(new Invoice(invoiceID, customerID, employeeID, discountID, date, tempCost, reducedCost, totalCost));
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
            String query = "DELETE FROM invoice";
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

            String query = "INSERT INTO invoice (invoiceID, customerID, employeeID, discountID, date, tempCost, reducedCost, totalCost) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = connection.prepareStatement(query);

            for (Invoice invoice : list) {
                pstmt.setString(1, invoice.getInvoiceID());
                pstmt.setString(2, invoice.getCustomerID());
                pstmt.setString(3, invoice.getEmployeeID());
                pstmt.setString(4, invoice.getDiscountID());
                pstmt.setString(5, invoice.getDate());
                pstmt.setDouble(6, invoice.getTempCost());
                pstmt.setDouble(7, invoice.getReducedCost());
                pstmt.setDouble(8, invoice.getTotalCost());

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

    public static ArrayList<Invoice> search(String info) {
        ArrayList<Invoice> result = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            String query;

            if (info != null && !info.isEmpty()) {
                query = "SELECT * FROM invoice WHERE invoiceID LIKE ? OR customerID LIKE ? OR discountID LIKE ? OR employeeID LIKE ? OR date LIKE ? OR tempCost LIKE ? OR reducedCost LIKE ? OR totalCost LIKE ? ORDER BY SUBSTRING(date, 7, 4) DESC";
                pstmt = connection.prepareStatement(query);
                String searchValue = "%" + info + "%";
                pstmt.setString(1, searchValue);
                pstmt.setString(2, searchValue);
                pstmt.setString(3, searchValue);
                pstmt.setString(4, searchValue);
                pstmt.setString(5, searchValue);
                pstmt.setString(6, searchValue);
                pstmt.setString(7, searchValue);
                pstmt.setString(8, searchValue);
            } else {
                query = "SELECT * FROM invoice ORDER BY SUBSTRING(date, 7, 4) DESC";
                pstmt = connection.prepareStatement(query);
            }

            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String resultInvoiceID = resultSet.getString("invoiceID");
                String resultCustomerID = resultSet.getString("customerID");
                String resultEmployeeID = resultSet.getString("employeeID");
                String resultDiscountID = resultSet.getString("discountID");
                String resultDate = resultSet.getString("date");
                Double resultTempCost = resultSet.getDouble("tempCost");
                Double resultReducedCost = resultSet.getDouble("reducedCost");
                Double resultTotalCost = resultSet.getDouble("totalCost");

                result.add(new Invoice(resultInvoiceID, resultCustomerID, resultEmployeeID, resultDiscountID, resultDate, resultTempCost, resultReducedCost, resultTotalCost));
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
        try {
            Connection connection = DBConnection.getConnection();
            String query = """
                   SELECT SUBSTRING(date, 7, 4) AS year, 
                          CAST(SUM(totalCost) AS UNSIGNED) AS total_invoice_cost
                   FROM carshowroom.Invoice
                   GROUP BY SUBSTRING(date, 7, 4)
                   ORDER BY year DESC
                   LIMIT 5""";

            try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(query)) {
                // Tạo list để lưu trữ dữ liệu
                List<String[]> dataList = new ArrayList<>();

                // Lưu trữ dữ liệu vào list
                while (rs.next()) {
                    String year = rs.getString("year");
                    int totalInvoiceCost = rs.getInt("total_invoice_cost");
                    dataList.add(new String[]{year, String.valueOf(totalInvoiceCost)});
                }

                // Đảo ngược thứ tự list
                Collections.reverse(dataList);

                // Thêm dữ liệu từ list đã đảo ngược vào dataset
                for (String[] data : dataList) {
                    dataset.addValue(Integer.parseInt(data[1]), "Doanh số", data[0]);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Thống kê doanh số bán hàng hằng năm",
                "Khoảng thời gian",
                "Tổng doanh số",
                dataset
        );
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);

        return chart;
    }
}
