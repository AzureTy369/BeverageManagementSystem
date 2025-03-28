package DAO;

import DTO.Position;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PositionDAO {
    private Connection connection;

    public PositionDAO() {
        connection = DBConnection.getConnection();
    }

    public List<Position> getAllPositions() {
        List<Position> positions = new ArrayList<>();
        String query = "SELECT * FROM ChucVu";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Position position = new Position();
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

    public Position getPositionById(String positionId) {
        String query = "SELECT * FROM ChucVu WHERE MaChucVu = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, positionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Position position = new Position();
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

    public boolean addPosition(Position position) {
        String query = "INSERT INTO ChucVu (MaChucVu, TenChucVu, Luong) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, position.getPositionId());
            pstmt.setString(2, position.getPositionName());
            pstmt.setBigDecimal(3, position.getSalary());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding position: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePosition(Position position) {
        String query = "UPDATE ChucVu SET TenChucVu = ?, Luong = ? WHERE MaChucVu = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, position.getPositionName());
            pstmt.setBigDecimal(2, position.getSalary());
            pstmt.setString(3, position.getPositionId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating position: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePosition(String positionId) {
        String query = "DELETE FROM ChucVu WHERE MaChucVu = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, positionId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting position: " + e.getMessage());
            return false;
        }
    }
}