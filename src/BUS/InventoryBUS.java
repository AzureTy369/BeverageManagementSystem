package BUS;

import DAO.InventoryDAO;
import DTO.InventoryDTO;
import java.util.List;

public class InventoryBUS {
    private InventoryDAO inventoryDAO;

    public InventoryBUS() {
        this.inventoryDAO = new InventoryDAO();
    }

    /**
     * Lấy tất cả thông tin tồn kho
     */
    public List<InventoryDTO> getAllInventory() {
        return inventoryDAO.getAllInventory();
    }

    /**
     * Lấy thông tin tồn kho theo mã sản phẩm
     */
    public InventoryDTO getInventoryByProductId(String productId) {
        return inventoryDAO.getInventoryByProductId(productId);
    }

    /**
     * Thêm mới thông tin tồn kho
     */
    public boolean addInventory(InventoryDTO inventory) {
        return inventoryDAO.insertInventory(inventory);
    }

    /**
     * Cập nhật thông tin tồn kho
     */
    public boolean updateInventory(InventoryDTO inventory) {
        return inventoryDAO.updateInventory(inventory);
    }

    /**
     * Cập nhật số lượng tồn kho
     * @param productId - Mã sản phẩm cần cập nhật
     * @param addQuantity - Số lượng thêm vào (có thể âm nếu giảm)
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean updateInventoryQuantity(String productId, int addQuantity) {
        return inventoryDAO.updateInventoryQuantity(productId, addQuantity);
    }

    /**
     * Xóa thông tin tồn kho theo mã sản phẩm
     */
    public boolean deleteInventory(String productId) {
        return inventoryDAO.deleteInventory(productId);
    }
    
    /**
     * Lấy số lượng tồn kho hiện tại của sản phẩm
     * @param productId - Mã sản phẩm cần kiểm tra
     * @return Số lượng tồn kho hiện tại, hoặc 0 nếu sản phẩm không tồn tại
     */
    public int getCurrentQuantity(String productId) {
        InventoryDTO inventory = getInventoryByProductId(productId);
        return inventory != null ? inventory.getQuantity() : 0;
    }
} 