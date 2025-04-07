package DAO;

import DTO.PositionDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PositionDAO {
    private Connection connection;

    public PositionDAO() {
        connection = DBConnection.getConnection();
        standardizePositionIds(); // Chuẩn hóa ID trong DB khi khởi tạo DAO
    }

    public List<PositionDTO> getAllPositions() {
        List<PositionDTO> positions = new ArrayList<>();
        String query = "SELECT * FROM ChucVu";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                PositionDTO position = new PositionDTO();
                position.setPositionId(rs.getString("MaChucVu"));
                position.setPositionName(rs.getString("TenChucVu"));
                position.setSalary(rs.getBigDecimal("Luong"));
                positions.add(position);
            }
        } catch (SQLException e) {
            System.err.println("Error getting positions: " + e.getMessage());
        }

        return positions;
    }

    public PositionDTO getPositionById(String positionId) {
        String query = "SELECT * FROM ChucVu WHERE MaChucVu = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, positionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    PositionDTO position = new PositionDTO();
                    position.setPositionId(rs.getString("MaChucVu"));
                    position.setPositionName(rs.getString("TenChucVu"));
                    position.setSalary(rs.getBigDecimal("Luong"));
                    return position;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting position by ID: " + e.getMessage());
        }

        return null;
    }

    public boolean addPosition(PositionDTO position) {
        String query = "INSERT INTO ChucVu (MaChucVu, TenChucVu, Luong) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, position.getPositionId());
            pstmt.setString(2, position.getPositionName());

            // Ensure the salary is formatted correctly (scale of 2)
            BigDecimal formattedSalary = position.getSalary().setScale(2, RoundingMode.HALF_UP);
            if (formattedSalary.compareTo(new BigDecimal("99999999.99")) > 0) {
                System.err.println("Error adding position: Salary value exceeds database limit (99999999.99)");
                return false;
            }
            pstmt.setBigDecimal(3, formattedSalary);

            System.out.println("Executing query: " + query);
            System.out.println("Position ID: " + position.getPositionId());
            System.out.println("Position Name: " + position.getPositionName());
            System.out.println("Salary: " + formattedSalary);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding position: " + e.getMessage());
            e.printStackTrace();
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error while adding position: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePosition(PositionDTO position) {
        // First check if position exists before updating
        if (!positionExists(position.getPositionId())) {
            System.out.println(
                    "Position ID " + position.getPositionId() + " does not exist, attempting to insert instead");
            return addPosition(position);
        }

        String query = "UPDATE ChucVu SET TenChucVu = ?, Luong = ? WHERE MaChucVu = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, position.getPositionName());

            // Ensure the salary is formatted correctly (scale of 2)
            BigDecimal formattedSalary = position.getSalary().setScale(2, RoundingMode.HALF_UP);
            if (formattedSalary.compareTo(new BigDecimal("99999999.99")) > 0) {
                System.err.println("Error updating position: Salary value exceeds database limit (99999999.99)");
                return false;
            }
            pstmt.setBigDecimal(2, formattedSalary);
            pstmt.setString(3, position.getPositionId());

            System.out.println("Executing query: " + query);
            System.out.println("Position ID: " + position.getPositionId());
            System.out.println("Position Name: " + position.getPositionName());
            System.out.println("Salary: " + formattedSalary);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating position: " + e.getMessage());
            e.printStackTrace();
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error while updating position: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePosition(String positionId) {
        // First check if position is in use
        if (isPositionInUse(positionId)) {
            System.err.println("Cannot delete position: Position is in use by employees");
            return false;
        }

        String query = "DELETE FROM ChucVu WHERE MaChucVu = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, positionId);

            System.out.println("Executing query: " + query);
            System.out.println("Position ID: " + positionId);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting position: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        }
    }

    public boolean isPositionInUse(String positionId) {
        String query = "SELECT COUNT(*) FROM NhanVien WHERE MaChucVu = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, positionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if position is in use: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean positionExists(String positionId) {
        String query = "SELECT COUNT(*) FROM ChucVu WHERE MaChucVu = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, positionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if position exists: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Sửa lại mã định danh của chức vụ trong cơ sở dữ liệu
     * 
     * @param oldId ID cũ cần sửa
     * @param newId ID mới sau khi chuẩn hóa
     * @return true nếu sửa thành công, false nếu có lỗi
     */
    public boolean fixPositionId(String oldId, String newId) {
        String query = "UPDATE ChucVu SET MaChucVu = ? WHERE MaChucVu = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newId);
            pstmt.setString(2, oldId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Position ID changed from " + oldId + " to " + newId);
                return true;
            } else {
                System.out.println("No position found with ID: " + oldId);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error fixing position ID: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Chuẩn hóa tất cả các mã chức vụ trong cơ sở dữ liệu
     * 
     * @return true nếu quá trình hoàn tất thành công, false nếu có lỗi
     */
    public boolean standardizePositionIds() {
        boolean success = true;
        String query = "SELECT MaChucVu FROM ChucVu";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String positionId = rs.getString("MaChucVu");

                // Nếu ID không theo định dạng CV###
                if (!positionId.matches("CV\\d{3}")) {
                    // Nếu ID chỉ chứa các số
                    if (positionId.matches("\\d+")) {
                        int idNumber = Integer.parseInt(positionId);
                        String newId = String.format("CV%03d", idNumber);

                        if (!fixPositionId(positionId, newId)) {
                            success = false;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error standardizing position IDs: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return success;
    }

    /**
     * Kiểm tra xem mã chức vụ đã tồn tại trong cơ sở dữ liệu chưa
     * 
     * @param positionId mã chức vụ cần kiểm tra
     * @return true nếu mã đã tồn tại, false nếu chưa tồn tại
     */
    public boolean isDuplicatePositionId(String positionId) {
        String query = "SELECT COUNT(*) FROM ChucVu WHERE MaChucVu = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, positionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking for duplicate position ID: " + e.getMessage());
            e.printStackTrace();
        }

        return false; // Nếu có lỗi, coi như không trùng lặp để tránh ngăn thêm dữ liệu
    }
}