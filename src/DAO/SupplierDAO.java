package DAO;

import DTO.SupplierDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    private Connection connection;

    public SupplierDAO() {
        connection = DBConnection.getConnection();
        ensureTableStructure();
        standardizeSupplierIds(); // Chuẩn hóa ID trong DB
    }

    private void ensureTableStructure() {
        try {
            DatabaseMetaData metaData = connection.getMetaData();

            // Check if DiaChi column exists
            ResultSet columns = metaData.getColumns(null, null, "NhaCungCap", "DiaChi");
            boolean diaChiExists = columns.next();
            columns.close();

            // Check if Email column exists
            columns = metaData.getColumns(null, null, "NhaCungCap", "Email");
            boolean emailExists = columns.next();
            columns.close();

            // Add missing columns if needed
            if (!diaChiExists || !emailExists) {
                try (Statement stmt = connection.createStatement()) {
                    if (!diaChiExists) {
                        try {
                            System.out.println("Adding DiaChi column to NhaCungCap table...");
                            stmt.executeUpdate("ALTER TABLE NhaCungCap ADD COLUMN DiaChi NVARCHAR(255)");
                            System.out.println("DiaChi column added successfully.");
                        } catch (SQLException e) {
                            System.err.println("Error adding DiaChi column: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    if (!emailExists) {
                        try {
                            System.out.println("Adding Email column to NhaCungCap table...");
                            stmt.executeUpdate("ALTER TABLE NhaCungCap ADD COLUMN Email NVARCHAR(100)");
                            System.out.println("Email column added successfully.");
                        } catch (SQLException e) {
                            System.err.println("Error adding Email column: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                System.out.println("NhaCungCap table structure is up-to-date, all required columns exist.");
            }
        } catch (SQLException e) {
            System.err.println("Error ensuring table structure: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in ensureTableStructure: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<SupplierDTO> getAllSuppliers() {
        List<SupplierDTO> suppliers = new ArrayList<>();
        String query = "SELECT * FROM NhaCungCap";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                SupplierDTO supplier = new SupplierDTO();
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

    public SupplierDTO getSupplierById(String supplierId) {
        String query = "SELECT * FROM NhaCungCap WHERE MaNhaCungCap = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, supplierId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    SupplierDTO supplier = new SupplierDTO();
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

    public boolean addSupplier(SupplierDTO supplier) {
        String query = "INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, SoDienThoai, DiaChi, Email) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, supplier.getSupplierId());
            pstmt.setString(2, supplier.getSupplierName());
            pstmt.setString(3, supplier.getPhone());
            pstmt.setString(4, supplier.getAddress());
            pstmt.setString(5, supplier.getEmail());

            System.out.println("Executing query: " + query);
            System.out.println("Supplier ID: " + supplier.getSupplierId());
            System.out.println("Supplier Name: " + supplier.getSupplierName());
            System.out.println("Phone: " + supplier.getPhone());
            System.out.println("Address: " + supplier.getAddress());
            System.out.println("Email: " + supplier.getEmail());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding supplier: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSupplier(SupplierDTO supplier) {
        // Check if the supplier exists before updating
        if (!supplierExists(supplier.getSupplierId())) {
            System.out.println(
                    "Supplier ID " + supplier.getSupplierId() + " does not exist, attempting to insert instead");
            return addSupplier(supplier);
        }

        String query = "UPDATE NhaCungCap SET TenNhaCungCap = ?, SoDienThoai = ?, DiaChi = ?, Email = ? WHERE MaNhaCungCap = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, supplier.getSupplierName());
            pstmt.setString(2, supplier.getPhone());
            pstmt.setString(3, supplier.getAddress());
            pstmt.setString(4, supplier.getEmail());
            pstmt.setString(5, supplier.getSupplierId());

            System.out.println("Executing query: " + query);
            System.out.println("Supplier ID: " + supplier.getSupplierId());
            System.out.println("Supplier Name: " + supplier.getSupplierName());
            System.out.println("Phone: " + supplier.getPhone());
            System.out.println("Address: " + supplier.getAddress());
            System.out.println("Email: " + supplier.getEmail());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating supplier: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSupplier(String supplierId) {
        // First check if supplier is in use
        if (isSupplierInUse(supplierId)) {
            System.err.println("Cannot delete supplier: Supplier is in use by purchase orders");
            return false;
        }

        String query = "DELETE FROM NhaCungCap WHERE MaNhaCungCap = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, supplierId);

            System.out.println("Executing query: " + query);
            System.out.println("Supplier ID: " + supplierId);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting supplier: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        }
    }

    public boolean isSupplierInUse(String supplierId) {
        String query = "SELECT COUNT(*) FROM PhieuNhap WHERE MaNhaCungCap = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, supplierId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if supplier is in use: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean supplierExists(String supplierId) {
        String query = "SELECT COUNT(*) FROM NhaCungCap WHERE MaNhaCungCap = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, supplierId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if supplier exists: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Phương thức để chuyển đổi ID nếu cần
    public boolean fixSupplierId(String supplierId) {
        // Nếu ID là dạng số (như '001'), chuyển sang định dạng NCC
        if (supplierId.matches("\\d+")) {
            String query = "UPDATE NhaCungCap SET MaNhaCungCap = ? WHERE MaNhaCungCap = ?";
            String newId = "NCC" + supplierId;

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, newId);
                pstmt.setString(2, supplierId);

                System.out.println("Converting supplier ID " + supplierId + " to " + newId);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                System.err.println("Error fixing supplier ID: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return true; // No need to fix
    }

    // Phương thức kiểm tra tất cả ID trong DB và chuẩn hóa sang định dạng NCC
    public void standardizeSupplierIds() {
        String query = "SELECT MaNhaCungCap FROM NhaCungCap";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String id = rs.getString("MaNhaCungCap");
                fixSupplierId(id);
            }
        } catch (SQLException e) {
            System.err.println("Error standardizing supplier IDs: " + e.getMessage());
        }
    }
}