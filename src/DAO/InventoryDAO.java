package DAO;

import DTO.InventoryDTO;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InventoryDAO {
    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    // Danh sách tạm thời cho tồn kho, sẽ chỉ sử dụng khi kết nối CSDL thất bại
    private static List<InventoryDTO> inventoryList = new ArrayList<>();

    public InventoryDAO() {
        // Khởi tạo kết nối
        try {
            conn = DBConnection.getConnection();
            createTableIfNotExists();
        } catch (Exception e) {
            System.out.println("Lỗi kết nối đến cơ sở dữ liệu: " + e.getMessage());
        }
    }

    private void createTableIfNotExists() {
        try {
            if (conn != null) {
                String sql = "CREATE TABLE IF NOT EXISTS sanphamtonkho (" +
                        "ma_sp VARCHAR(10) PRIMARY KEY, " +
                        "so_luong INT NOT NULL DEFAULT 0, " +
                        "ngay_cap_nhat VARCHAR(30) NOT NULL" +
                        ")";
                ps = conn.prepareStatement(sql);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Lỗi tạo bảng sanphamtonkho: " + e.getMessage());
        }
    }

    /**
     * Lấy tất cả sản phẩm tồn kho
     */
    public List<InventoryDTO> getAllInventory() {
        List<InventoryDTO> list = new ArrayList<>();
        try {
            if (conn != null) {
                String sql = "SELECT * FROM sanphamtonkho";
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();

                while (rs.next()) {
                    InventoryDTO inventory = new InventoryDTO();
                    inventory.setProductId(rs.getString("ma_sp"));
                    inventory.setQuantity(rs.getInt("so_luong"));
                    inventory.setLastUpdated(rs.getString("ngay_cap_nhat"));
                    list.add(inventory);
                }
                return list;
            } else {
                // Trả về danh sách tạm thời trong trường hợp chưa có kết nối cơ sở dữ liệu
                return inventoryList;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn getAllInventory: " + e.getMessage());
            // Nếu có lỗi, trả về danh sách tạm thời
            return inventoryList;
        }
    }

    /**
     * Lấy thông tin tồn kho của sản phẩm theo mã sản phẩm
     */
    public InventoryDTO getInventoryByProductId(String productId) {
        try {
            if (conn != null) {
                String sql = "SELECT * FROM sanphamtonkho WHERE ma_sp = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, productId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    InventoryDTO inventory = new InventoryDTO();
                    inventory.setProductId(rs.getString("ma_sp"));
                    inventory.setQuantity(rs.getInt("so_luong"));
                    inventory.setLastUpdated(rs.getString("ngay_cap_nhat"));
                    return inventory;
                }
            } else {
                // Tìm kiếm trong danh sách tạm thời
                for (InventoryDTO inventory : inventoryList) {
                    if (inventory.getProductId().equals(productId)) {
                        return inventory;
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn getInventoryByProductId: " + e.getMessage());

            // Tìm kiếm trong danh sách tạm thời nếu có lỗi
            for (InventoryDTO inventory : inventoryList) {
                if (inventory.getProductId().equals(productId)) {
                    return inventory;
                }
            }
            return null;
        }
    }

    /**
     * Thêm sản phẩm tồn kho mới
     */
    public boolean insertInventory(InventoryDTO inventory) {
        try {
            if (conn != null) {
                String sql = "INSERT INTO sanphamtonkho(ma_sp, so_luong, ngay_cap_nhat) VALUES(?,?,?)";
                ps = conn.prepareStatement(sql);
                ps.setString(1, inventory.getProductId());
                ps.setInt(2, inventory.getQuantity());
                ps.setString(3, inventory.getLastUpdated());
                return ps.executeUpdate() > 0;
            } else {
                // Kiểm tra sản phẩm đã tồn tại chưa
                InventoryDTO existingInventory = getInventoryByProductId(inventory.getProductId());
                if (existingInventory != null) {
                    return false; // Sản phẩm đã tồn tại, không thể thêm mới
                }

                // Thêm vào danh sách tạm thời
                inventoryList.add(inventory);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn insertInventory: " + e.getMessage());

            // Thử thêm vào danh sách tạm thời nếu có lỗi
            InventoryDTO existingInventory = getInventoryByProductId(inventory.getProductId());
            if (existingInventory != null) {
                return false; // Sản phẩm đã tồn tại, không thể thêm mới
            }
            inventoryList.add(inventory);
            return true;
        }
    }

    /**
     * Cập nhật thông tin tồn kho
     */
    public boolean updateInventory(InventoryDTO inventory) {
        try {
            if (conn != null) {
                String sql = "UPDATE sanphamtonkho SET so_luong=?, ngay_cap_nhat=? WHERE ma_sp=?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, inventory.getQuantity());
                ps.setString(2, inventory.getLastUpdated());
                ps.setString(3, inventory.getProductId());
                return ps.executeUpdate() > 0;
            } else {
                // Cập nhật trong danh sách tạm thời
                for (int i = 0; i < inventoryList.size(); i++) {
                    if (inventoryList.get(i).getProductId().equals(inventory.getProductId())) {
                        inventoryList.set(i, inventory);
                        return true;
                    }
                }
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn updateInventory: " + e.getMessage());

            // Thử cập nhật trong danh sách tạm thời nếu có lỗi
            for (int i = 0; i < inventoryList.size(); i++) {
                if (inventoryList.get(i).getProductId().equals(inventory.getProductId())) {
                    inventoryList.set(i, inventory);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Cập nhật số lượng tồn kho
     * 
     * @param productId         - Mã sản phẩm cần cập nhật
     * @param addQuantity       - Số lượng thêm vào (có thể âm nếu giảm)
     * @param fromImportReceipt - true nếu gọi từ ImportReceiptBUS khi tạo phiếu
     *                          nhập, false trong các trường hợp khác
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean updateInventoryQuantity(String productId, int addQuantity, boolean fromImportReceipt) {
        // Nếu gọi từ quá trình tạo phiếu nhập mới (chưa duyệt),
        // thì không thực hiện thao tác nào với tồn kho
        if (fromImportReceipt) {
            System.out.println("Bỏ qua thao tác với tồn kho cho sản phẩm " + productId +
                    " vì phiếu nhập chưa được duyệt");
            return true;
        }

        try {
            // Lấy thông tin tồn kho hiện tại
            InventoryDTO inventory = getInventoryByProductId(productId);

            // Định dạng ngày hiện tại
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String currentDate = sdf.format(new Date());

            // Đảm bảo rằng mã danh mục của sản phẩm được giữ nguyên khi cập nhật tồn kho
            // Danh mục sản phẩm được cập nhật riêng thông qua ProductBUS.updateProduct

            if (inventory == null) {
                // Nếu sản phẩm chưa có trong tồn kho, tạo mới
                inventory = new InventoryDTO(productId, addQuantity, currentDate);
                return insertInventory(inventory);
            } else {
                // Nếu sản phẩm đã có trong tồn kho, cập nhật số lượng
                inventory.setQuantity(inventory.getQuantity() + addQuantity);
                inventory.setLastUpdated(currentDate);
                return updateInventory(inventory);
            }
        } catch (Exception e) {
            System.out.println("Lỗi truy vấn updateInventoryQuantity: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật số lượng tồn kho (phương thức cũ để tương thích)
     * 
     * @param productId   - Mã sản phẩm cần cập nhật
     * @param addQuantity - Số lượng thêm vào (có thể âm nếu giảm)
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean updateInventoryQuantity(String productId, int addQuantity) {
        // Mặc định, giả sử đây không phải là phương thức từ ImportReceiptBUS khi tạo
        // phiếu nhập mới
        return updateInventoryQuantity(productId, addQuantity, false);
    }

    /**
     * Xóa thông tin tồn kho theo mã sản phẩm
     */
    public boolean deleteInventory(String productId) {
        try {
            if (conn != null) {
                String sql = "DELETE FROM sanphamtonkho WHERE ma_sp=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, productId);
                return ps.executeUpdate() > 0;
            } else {
                // Xóa trong danh sách tạm thời
                for (int i = 0; i < inventoryList.size(); i++) {
                    if (inventoryList.get(i).getProductId().equals(productId)) {
                        inventoryList.remove(i);
                        return true;
                    }
                }
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn deleteInventory: " + e.getMessage());

            // Thử xóa trong danh sách tạm thời nếu có lỗi
            for (int i = 0; i < inventoryList.size(); i++) {
                if (inventoryList.get(i).getProductId().equals(productId)) {
                    inventoryList.remove(i);
                    return true;
                }
            }
            return false;
        }
    }
}