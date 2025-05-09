package BUS;

import DTO.ImportReceipt;
import DTO.ImportReceiptDetail;
import DAO.ImportReceiptDAO;
import DAO.ImportReceiptDetailDAO;
import java.util.ArrayList;
import java.util.List;

public class ImportReceiptBUS {

    private ImportReceiptDAO importReceiptDAO;
    private ImportReceiptDetailDAO importReceiptDetailDAO;
    private InventoryBUS inventoryBUS;

    public ImportReceiptBUS() {
        this.importReceiptDAO = new ImportReceiptDAO();
        this.importReceiptDetailDAO = new ImportReceiptDetailDAO();
        this.inventoryBUS = new InventoryBUS();
    }

    public List<ImportReceipt> getAllImportReceipts() {
        // Đảm bảo luôn lấy dữ liệu mới nhất từ DAO
        return importReceiptDAO.getAllImportReceipts();
    }

    public List<ImportReceipt> search(String keyword) {
        List<ImportReceipt> allReceipts = getAllImportReceipts();
        List<ImportReceipt> result = new ArrayList<>();

        for (ImportReceipt receipt : allReceipts) {
            // Tìm kiếm theo mã phiếu nhập, nhà cung cấp, người tạo
            if (receipt.getImportId().toLowerCase().contains(keyword.toLowerCase()) ||
                    receipt.getSupplierId().toLowerCase().contains(keyword.toLowerCase()) ||
                    receipt.getEmployeeId().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(receipt);
            }
        }

        return result;
    }

    public boolean insertImportReceipt(ImportReceipt importReceipt) {
        // Đảm bảo trạng thái mặc định khi tạo mới
        if (importReceipt.getStatus() == null) {
            importReceipt.setStatus("Đang xử lý");
        }
        // Thêm phiếu nhập mới và trả về kết quả
        boolean result = importReceiptDAO.insertImportReceipt(importReceipt);
        return result;
    }

    public boolean deleteImportReceipt(String importId) {
        // Chỉ cho phép xóa phiếu nếu trạng thái là "Đã hủy"
        ImportReceipt receipt = getImportReceiptById(importId);
        if (receipt != null && "Đã hủy".equals(receipt.getStatus())) {
            return importReceiptDAO.deleteImportReceipt(importId);
        } else if (receipt != null) {
            System.out.println("Không thể xóa phiếu nhập có trạng thái: " + receipt.getStatus());
            return false;
        }
        return false;
    }

    public ImportReceipt getImportReceiptById(String importId) {
        return importReceiptDAO.getImportReceiptById(importId);
    }

    public boolean updateImportReceipt(ImportReceipt importReceipt) {
        // Kiểm tra nếu phiếu nhập đã hủy thì không cho phép cập nhật
        ImportReceipt existingReceipt = getImportReceiptById(importReceipt.getImportId());
        if (existingReceipt != null && "Đã hủy".equals(existingReceipt.getStatus())) {
            System.out.println("Không thể cập nhật phiếu nhập đã hủy");
            return false;
        }
        return importReceiptDAO.updateImportReceipt(importReceipt);
    }

    /**
     * Cập nhật trạng thái phiếu nhập
     * 
     * @param importId Mã phiếu nhập
     * @param status   Trạng thái mới ("Đang xử lý", "Đã hoàn thành", "Đã hủy")
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateImportReceiptStatus(String importId, String status) {
        // Kiểm tra phiếu nhập có tồn tại không
        ImportReceipt receipt = getImportReceiptById(importId);
        if (receipt == null) {
            System.out.println("Không tìm thấy phiếu nhập: " + importId);
            return false;
        }

        // Kiểm tra trạng thái hiện tại
        String currentStatus = receipt.getStatus();
        if ("Đã hủy".equals(currentStatus)) {
            System.out.println("Không thể cập nhật trạng thái cho phiếu nhập đã hủy");
            return false;
        }

        // Cập nhật trạng thái
        boolean success = importReceiptDAO.updateImportReceiptStatus(importId, status);

        if (success) {
            System.out.println("Đã cập nhật trạng thái phiếu nhập " + importId + " từ ["
                    + currentStatus + "] thành [" + status + "]");

            // Nếu trạng thái mới là "Đã hoàn thành" và trạng thái cũ không phải "Đã hoàn
            // thành"
            // thì cập nhật tồn kho
            if ("Đã hoàn thành".equals(status) && !"Đã hoàn thành".equals(currentStatus)) {
                updateInventory(importId);
            }

            return true;
        } else {
            System.out.println("Lỗi khi cập nhật trạng thái phiếu nhập");
            return false;
        }
    }

    /**
     * Cập nhật tồn kho dựa trên chi tiết phiếu nhập
     * 
     * @param importId Mã phiếu nhập
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    private boolean updateInventory(String importId) {
        // Lấy danh sách chi tiết phiếu nhập
        List<ImportReceiptDetail> details = importReceiptDetailDAO.getImportReceiptDetailsByReceiptId(importId);

        if (details.isEmpty()) {
            System.out.println("Không có chi tiết phiếu nhập để cập nhật tồn kho");
            return false;
        }

        boolean success = true;
        int updatedCount = 0;

        for (ImportReceiptDetail detail : details) {
            String productId = detail.getProductId();
            int quantity = detail.getQuantity();

            System.out.println("Cập nhật tồn kho cho sản phẩm " + productId + " với số lượng " + quantity);

            // Đã chuyển sang trạng thái "Đã hoàn thành", cập nhật tồn kho với tham số
            // fromImportReceipt=false
            boolean result = inventoryBUS.updateInventoryQuantity(productId, quantity, false);
            if (result) {
                updatedCount++;
            } else {
                success = false;
                System.out.println("Lỗi khi cập nhật tồn kho cho sản phẩm " + productId);
            }
        }

        System.out.println("Đã cập nhật tồn kho cho " + updatedCount + "/" + details.size() + " sản phẩm");
        return success;
    }

    /**
     * Trả về đối tượng DAO để truy cập trực tiếp
     */
    public ImportReceiptDAO getDAO() {
        return importReceiptDAO;
    }
}