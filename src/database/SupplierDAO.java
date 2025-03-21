package database;

import model.Supplier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    private Connection connection;

    public SupplierDAO() {
        connection = DBConnection.getConnection();
        ensureTableStructure();
    }

    private void ensureTableStructure() {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "NhaCungCap", "DiaChi");

            if (!columns.next()) {
                // DiaChi column doesn't exist, add it
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate("ALTER TABLE NhaCungCap ADD DiaChi NVARCHAR(255)");
                }
            }
            columns.close();

            columns = metaData.getColumns(null, null, "NhaCungCap", "Email");
            if (!columns.next()) {
                // Email column doesn't exist, add it
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate("ALTER TABLE NhaCungCap ADD Email NVARCHAR(100)");
                }
            }
            columns.close();
        } catch (SQLException e) {
            System.err.println("Error ensuring table structure: " + e.getMessage());
        }
    }

    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String query = "SELECT * FROM NhaCungCap";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setSupplierId(rs.getString("MaNhaCungCap"));
                supplier.setSupplierName(rs.getString("TenNhaCungCap"));
                supplier.setPhone(rs.getString("SoDienThoai"));

                // Get address and email if they exist
                try {
                    supplier.setAddress(rs.getString("DiaChi"));
                } catch (SQLException e) {
                    supplier.setAddress("");
                }

                try {
                    supplier.setEmail(rs.getString("Email"));
                } catch (SQLException e) {
                    supplier.setEmail("");
                }

                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            System.err.println("Error getting suppliers: " + e.getMessage());
        }

        return suppliers;
    }

    public Supplier getSupplierById(String supplierId) {
        String query = "SELECT * FROM NhaCungCap WHERE MaNhaCungCap = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, supplierId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Supplier supplier = new Supplier();
                    supplier.setSupplierId(rs.getString("MaNhaCungCap"));
                    supplier.setSupplierName(rs.getString("TenNhaCungCap"));
                    supplier.setPhone(rs.getString("SoDienThoai"));

                    // Get address and email if they exist
                    try {
                        supplier.setAddress(rs.getString("DiaChi"));
                    } catch (SQLException e) {
                        supplier.setAddress("");
                    }

                    try {
                        supplier.setEmail(rs.getString("Email"));
                    } catch (SQLException e) {
                        supplier.setEmail("");
                    }

                    return supplier;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting supplier by ID: " + e.getMessage());
        }

        return null;
    }

    public boolean addSupplier(Supplier supplier) {
        String query = "INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, SoDienThoai, DiaChi, Email) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, supplier.getSupplierId());
            pstmt.setString(2, supplier.getSupplierName());
            pstmt.setString(3, supplier.getPhone());
            pstmt.setString(4, supplier.getAddress());
            pstmt.setString(5, supplier.getEmail());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding supplier: " + e.getMessage());
            return false;
        }
    }

    public boolean updateSupplier(Supplier supplier) {
        String query = "UPDATE NhaCungCap SET TenNhaCungCap = ?, SoDienThoai = ?, DiaChi = ?, Email = ? WHERE MaNhaCungCap = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, supplier.getSupplierName());
            pstmt.setString(2, supplier.getPhone());
            pstmt.setString(3, supplier.getAddress());
            pstmt.setString(4, supplier.getEmail());
            pstmt.setString(5, supplier.getSupplierId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating supplier: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSupplier(String supplierId) {
        String query = "DELETE FROM NhaCungCap WHERE MaNhaCungCap = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, supplierId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting supplier: " + e.getMessage());
            return false;
        }
    }
}