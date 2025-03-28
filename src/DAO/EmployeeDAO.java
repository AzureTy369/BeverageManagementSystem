package DAO;

import DTO.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private Connection connection;

    public EmployeeDAO() {
        connection = DBConnection.getConnection();
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM NhanVien";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Employee employee = new Employee();
                employee.setEmployeeId(rs.getString("MaNhanVien"));
                employee.setUsername(rs.getString("TenTaiKhoan"));
                employee.setPassword(rs.getString("MatKhau"));
                employee.setLastName(rs.getString("Ho"));
                employee.setFirstName(rs.getString("Ten"));
                employee.setPositionId(rs.getString("MaChucVu"));
                employee.setPhone(rs.getString("SoDienThoai"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.err.println("Error getting employees: " + e.getMessage());
        }

        return employees;
    }

    public Employee getEmployeeById(String employeeId) {
        String query = "SELECT * FROM NhanVien WHERE MaNhanVien = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, employeeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Employee employee = new Employee();
                    employee.setEmployeeId(rs.getString("MaNhanVien"));
                    employee.setUsername(rs.getString("TenTaiKhoan"));
                    employee.setPassword(rs.getString("MatKhau"));
                    employee.setLastName(rs.getString("Ho"));
                    employee.setFirstName(rs.getString("Ten"));
                    employee.setPositionId(rs.getString("MaChucVu"));
                    employee.setPhone(rs.getString("SoDienThoai"));
                    return employee;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting employee by ID: " + e.getMessage());
        }

        return null;
    }

    public Employee getEmployeeByUsername(String username) {
        String query = "SELECT * FROM NhanVien WHERE TenTaiKhoan = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Employee employee = new Employee();
                    employee.setEmployeeId(rs.getString("MaNhanVien"));
                    employee.setUsername(rs.getString("TenTaiKhoan"));
                    employee.setPassword(rs.getString("MatKhau"));
                    employee.setLastName(rs.getString("Ho"));
                    employee.setFirstName(rs.getString("Ten"));
                    employee.setPositionId(rs.getString("MaChucVu"));
                    employee.setPhone(rs.getString("SoDienThoai"));
                    return employee;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting employee by username: " + e.getMessage());
        }

        return null;
    }

    public boolean addEmployee(Employee employee) {
        String query = "INSERT INTO NhanVien VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, employee.getEmployeeId());
            pstmt.setString(2, employee.getUsername());
            pstmt.setString(3, employee.getPassword());
            pstmt.setString(4, employee.getLastName());
            pstmt.setString(5, employee.getFirstName());
            pstmt.setString(6, employee.getPositionId());
            pstmt.setString(7, employee.getPhone());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
            return false;
        }
    }

    public boolean updateEmployee(Employee employee) {
        String query = "UPDATE NhanVien SET TenTaiKhoan = ?, MatKhau = ?, Ho = ?, Ten = ?, MaChucVu = ?, SoDienThoai = ? WHERE MaNhanVien = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, employee.getUsername());
            pstmt.setString(2, employee.getPassword());
            pstmt.setString(3, employee.getLastName());
            pstmt.setString(4, employee.getFirstName());
            pstmt.setString(5, employee.getPositionId());
            pstmt.setString(6, employee.getPhone());
            pstmt.setString(7, employee.getEmployeeId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteEmployee(String employeeId) {
        String query = "DELETE FROM NhanVien WHERE MaNhanVien = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, employeeId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
            return false;
        }
    }

    public boolean isUsernameExists(String username) {
        String query = "SELECT COUNT(*) FROM NhanVien WHERE TenTaiKhoan = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }

        return false;
    }

    public Employee login(String username, String password) {
        String query = "SELECT * FROM NhanVien WHERE TenTaiKhoan = ? AND MatKhau = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Employee employee = new Employee();
                    employee.setEmployeeId(rs.getString("MaNhanVien"));
                    employee.setUsername(rs.getString("TenTaiKhoan"));
                    employee.setPassword(rs.getString("MatKhau"));
                    employee.setLastName(rs.getString("Ho"));
                    employee.setFirstName(rs.getString("Ten"));
                    employee.setPositionId(rs.getString("MaChucVu"));
                    employee.setPhone(rs.getString("SoDienThoai"));
                    return employee;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
        }

        return null;
    }
}