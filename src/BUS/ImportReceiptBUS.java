package BUS;

import DAO.ImportReceiptDAO;
import DAO.ImportReceiptDetailDAO;
import DTO.ImportReceipt;
import DTO.ImportReceiptDetail;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.JFreeChart;

public class ImportReceiptBUS {
    private ImportReceiptDAO importReceiptDAO;
    private ImportReceiptDetailDAO importReceiptDetailDAO;
    private SupplierBUS supplierBUS;
    private InventoryBUS inventoryBUS;

    public ImportReceiptBUS() {
        this.importReceiptDAO = new ImportReceiptDAO();
        this.importReceiptDetailDAO = new ImportReceiptDetailDAO();
        this.inventoryBUS = new InventoryBUS();
    }

    public List<ImportReceipt> getAllImportReceipts() {
        return importReceiptDAO.getAllImportReceipts();
    }

    public List<ImportReceipt> search(String keyword) {
        List<ImportReceipt> allReceipts = getAllImportReceipts();
        List<ImportReceipt> result = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return allReceipts;
        }

        String lowerKeyword = keyword.toLowerCase();
        for (ImportReceipt receipt : allReceipts) {
            if ((receipt.getImportId() != null && receipt.getImportId().toLowerCase().contains(lowerKeyword)) ||
               (receipt.getSupplierId() != null && receipt.getSupplierId().toLowerCase().contains(lowerKeyword)) ||
               (receipt.getEmployeeId() != null && receipt.getEmployeeId().toLowerCase().contains(lowerKeyword))) {
                result.add(receipt);
            }
        }
        return result;
    }

    public boolean insertImportReceipt(ImportReceipt importReceipt) {
        if (importReceipt.getStatus() == null) {
            importReceipt.setStatus("Đang xử lý");
        }
        return importReceiptDAO.insertImportReceipt(importReceipt);
    }

    public boolean deleteImportReceipt(String importId) {
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
        ImportReceipt existingReceipt = getImportReceiptById(importReceipt.getImportId());
        if (existingReceipt != null && "Đã hủy".equals(existingReceipt.getStatus())) {
            System.out.println("Không thể cập nhật phiếu nhập đã hủy");
            return false;
        }
        return importReceiptDAO.updateImportReceipt(importReceipt);
    }

    public boolean updateImportReceiptStatus(String importId, String status) {
        ImportReceipt receipt = getImportReceiptById(importId);
        if (receipt == null) {
            System.out.println("Không tìm thấy phiếu nhập: " + importId);
            return false;
        }

        String currentStatus = receipt.getStatus();
        if ("Đã hủy".equals(currentStatus)) {
            System.out.println("Không thể cập nhật trạng thái cho phiếu nhập đã hủy");
            return false;
        }

        boolean success = importReceiptDAO.updateImportReceiptStatus(importId, status);
        if (success && "Đã hoàn thành".equals(status) && !"Đã hoàn thành".equals(currentStatus)) {
            updateInventory(importId);
        }
        return success;
    }

    private boolean updateInventory(String importId) {
        List<ImportReceiptDetail> details = importReceiptDetailDAO.getImportReceiptDetailsByReceiptId(importId);
        if (details.isEmpty()) {
            System.out.println("Không có chi tiết phiếu nhập để cập nhật tồn kho");
            return false;
        }

        boolean success = true;
        for (ImportReceiptDetail detail : details) {
            String productId = detail.getProductId();
            int quantity = detail.getQuantity();
            
            if (!inventoryBUS.updateInventoryQuantity(productId, quantity, false)) {
                success = false;
                System.out.println("Lỗi khi cập nhật tồn kho cho sản phẩm " + productId);
            }
        }
        return success;
    }

    public ImportReceiptDAO getDAO() {
        return importReceiptDAO;
    }
    public SupplierBUS getSupplierBUS() {
        return supplierBUS;
    }
    public JFreeChart createImportChart() {
        return ImportReceiptDAO.createImportChart();
    }
}