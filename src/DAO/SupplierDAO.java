package DAO;

import DTO.SupplierDTO;
import DTO.SupplierProductDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

            // Kiểm tra bảng sản phẩm của nhà cung cấp
            ResultSet tables = metaData.getTables(null, null, "SanPhamNCC", null);
            if (!tables.next()) {
                System.out.println("Creating SanPhamNCC table...");
                try (Statement stmt = connection.createStatement()) {
                    String createTableSQL = "CREATE TABLE SanPhamNCC (" +
                            "MaSanPhamNCC VARCHAR(10) NOT NULL, " +
                            "MaNhaCungCap VARCHAR(10) NOT NULL, " +
                            "TenSanPham NVARCHAR(255) NOT NULL, " +
                            "DonViTinh NVARCHAR(50), " +
                            "MoTa NVARCHAR(500), " +
                            "Gia DOUBLE, " +
                            "PRIMARY KEY (MaSanPhamNCC), " +
                            "FOREIGN KEY (MaNhaCungCap) REFERENCES NhaCungCap(MaNhaCungCap)" +
                            ")";
                    stmt.executeUpdate(createTableSQL);
                    System.out.println("SanPhamNCC table created successfully.");
                }
            }
            tables.close();

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

                // Lấy danh sách sản phẩm của nhà cung cấp
                List<SupplierProductDTO> products = getProductsBySupplier(supplier.getSupplierId());
                supplier.setProducts(products);

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

                    // Lấy danh sách sản phẩm của nhà cung cấp
                    List<SupplierProductDTO> products = getProductsBySupplier(supplier.getSupplierId());
                    supplier.setProducts(products);

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

            // Nếu thêm thành công và có sản phẩm, thêm các sản phẩm
            if (rowsAffected > 0 && supplier.getProducts() != null && !supplier.getProducts().isEmpty()) {
                for (SupplierProductDTO product : supplier.getProducts()) {
                    addSupplierProduct(product);
                }
            }

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

            // Nếu cập nhật thành công và có sản phẩm, cập nhật các sản phẩm
            if (rowsAffected > 0 && supplier.getProducts() != null) {
                // Xóa tất cả các sản phẩm cũ
                deleteAllSupplierProducts(supplier.getSupplierId());

                // Thêm lại các sản phẩm mới
                for (SupplierProductDTO product : supplier.getProducts()) {
                    addSupplierProduct(product);
                }
            }

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

        // Xóa tất cả sản phẩm của nhà cung cấp
        deleteAllSupplierProducts(supplierId);

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

    // Các phương thức quản lý sản phẩm của nhà cung cấp

    /**
     * Lấy danh sách sản phẩm của một nhà cung cấp
     * 
     * @param supplierId Mã nhà cung cấp
     * @return Danh sách sản phẩm
     */
    public List<SupplierProductDTO> getProductsBySupplier(String supplierId) {
        List<SupplierProductDTO> products = new ArrayList<>();
        String query = "SELECT * FROM SanPhamNCC WHERE MaNhaCungCap = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, supplierId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SupplierProductDTO product = new SupplierProductDTO();
                    product.setProductId(rs.getString("MaSanPhamNCC"));
                    product.setSupplierId(rs.getString("MaNhaCungCap"));
                    product.setProductName(rs.getString("TenSanPham"));
                    product.setUnit(rs.getString("DonViTinh"));
                    product.setDescription(rs.getString("MoTa"));
                    product.setPrice(rs.getDouble("Gia"));
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting products by supplier: " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }

    /**
     * Thêm sản phẩm mới cho nhà cung cấp
     * 
     * @param product Thông tin sản phẩm
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean addSupplierProduct(SupplierProductDTO product) {
        String query = "INSERT INTO SanPhamNCC (MaSanPhamNCC, MaNhaCungCap, TenSanPham, DonViTinh, MoTa, Gia) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, product.getProductId());
            pstmt.setString(2, product.getSupplierId());
            pstmt.setString(3, product.getProductName());
            pstmt.setString(4, product.getUnit());
            pstmt.setString(5, product.getDescription());
            pstmt.setDouble(6, product.getPrice());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding supplier product: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật thông tin sản phẩm của nhà cung cấp
     * 
     * @param product Thông tin sản phẩm mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateSupplierProduct(SupplierProductDTO product) {
        String query = "UPDATE SanPhamNCC SET TenSanPham = ?, DonViTinh = ?, MoTa = ?, Gia = ? WHERE MaSanPhamNCC = ? AND MaNhaCungCap = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, product.getProductName());
            pstmt.setString(2, product.getUnit());
            pstmt.setString(3, product.getDescription());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setString(5, product.getProductId());
            pstmt.setString(6, product.getSupplierId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating supplier product: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa một sản phẩm của nhà cung cấp
     * 
     * @param productId  Mã sản phẩm
     * @param supplierId Mã nhà cung cấp
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteSupplierProduct(String productId, String supplierId) {
        String query = "DELETE FROM SanPhamNCC WHERE MaSanPhamNCC = ? AND MaNhaCungCap = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, productId);
            pstmt.setString(2, supplierId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting supplier product: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa tất cả sản phẩm của một nhà cung cấp
     * 
     * @param supplierId Mã nhà cung cấp
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteAllSupplierProducts(String supplierId) {
        String query = "DELETE FROM SanPhamNCC WHERE MaNhaCungCap = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, supplierId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected >= 0; // Có thể không có sản phẩm nào để xóa
        } catch (SQLException e) {
            System.err.println("Error deleting all supplier products: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra xem sản phẩm của nhà cung cấp đã tồn tại chưa
     * 
     * @param productId  Mã sản phẩm
     * @param supplierId Mã nhà cung cấp
     * @return true nếu đã tồn tại, false nếu chưa
     */
    public boolean supplierProductExists(String productId, String supplierId) {
        String query = "SELECT COUNT(*) FROM SanPhamNCC WHERE MaSanPhamNCC = ? AND MaNhaCungCap = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, productId);
            pstmt.setString(2, supplierId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if supplier product exists: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Tạo mã sản phẩm mới cho nhà cung cấp
     * Format đơn giản: SPNCC + mã nhà cung cấp + số thứ tự
     */
    public String generateNewSupplierProductId(String supplierId) {
        System.out.println("Đang tạo mã sản phẩm mới cho nhà cung cấp " + supplierId);

        // Lấy mã số nhà cung cấp (ví dụ: NCC001 -> 001)
        String nccNum = supplierId.substring(3); // Lấy "001" từ "NCC001"
        System.out.println("Mã số nhà cung cấp: " + nccNum);

        // Đếm các sản phẩm hiện có của nhà cung cấp này
        int currentCount = 0;
        try {
            String countQuery = "SELECT COUNT(*) FROM SanPhamNCC WHERE MaNhaCungCap = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(countQuery)) {
                pstmt.setString(1, supplierId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        currentCount = rs.getInt(1);
                    }
                }
            }
            System.out.println("Số lượng sản phẩm hiện có của NCC " + supplierId + ": " + currentCount);
        } catch (SQLException e) {
            System.err.println("Lỗi khi đếm sản phẩm nhà cung cấp: " + e.getMessage());
        }

        // Tạo mã mới với định dạng SPNCC001xx
        String newId = "SPNCC" + nccNum + String.format("%02d", currentCount + 1);

        // Kiểm tra xem mã này đã tồn tại chưa
        boolean exists = false;
        try {
            String checkQuery = "SELECT COUNT(*) FROM SanPhamNCC WHERE MaSanPhamNCC = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(checkQuery)) {
                pstmt.setString(1, newId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        exists = rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra mã sản phẩm: " + e.getMessage());
        }

        // Nếu mã đã tồn tại, thử với số thứ tự tiếp theo
        if (exists) {
            for (int i = currentCount + 2; i <= 99; i++) {
                String altId = "SPNCC" + nccNum + String.format("%02d", i);
                try {
                    String checkQuery = "SELECT COUNT(*) FROM SanPhamNCC WHERE MaSanPhamNCC = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(checkQuery)) {
                        pstmt.setString(1, altId);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            if (rs.next() && rs.getInt(1) == 0) {
                                newId = altId;
                                System.out.println("Mã ban đầu đã tồn tại, sử dụng mã thay thế: " + newId);
                                break;
                            }
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Lỗi khi kiểm tra mã thay thế: " + e.getMessage());
                }
            }
        }

        System.out.println("Mã sản phẩm mới được tạo: " + newId);
        return newId;
    }

    /**
     * Xóa tất cả sản phẩm của tất cả nhà cung cấp
     * 
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteAllSupplierProducts() {
        String query = "DELETE FROM SanPhamNCC";

        try (Statement stmt = connection.createStatement()) {
            int rowsAffected = stmt.executeUpdate(query);
            System.out.println("Đã xóa " + rowsAffected + " sản phẩm nhà cung cấp");
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa tất cả sản phẩm nhà cung cấp: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa tất cả nhà cung cấp
     * 
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteAllSuppliers() {
        String query = "DELETE FROM NhaCungCap";

        try (Statement stmt = connection.createStatement()) {
            int rowsAffected = stmt.executeUpdate(query);
            System.out.println("Đã xóa " + rowsAffected + " nhà cung cấp");
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa tất cả nhà cung cấp: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}