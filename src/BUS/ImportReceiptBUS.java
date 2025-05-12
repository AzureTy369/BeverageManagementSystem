package BUS;

import DTO.ImportReceipt;
import DTO.ImportReceiptDetail;
import DTO.ProductDTO;
import DAO.ImportReceiptDAO;
import DAO.ImportReceiptDetailDAO;
import java.util.ArrayList;
import java.util.List;

public class ImportReceiptBUS {

    private ImportReceiptDAO importReceiptDAO;
    private ImportReceiptDetailDAO importReceiptDetailDAO;
    private InventoryBUS inventoryBUS;
    private ProductBUS productController;

    public ImportReceiptBUS() {
        this.importReceiptDAO = new ImportReceiptDAO();
        this.importReceiptDetailDAO = new ImportReceiptDetailDAO();
        this.inventoryBUS = new InventoryBUS();
        this.productController = new ProductBUS();
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

        // Không cho phép cập nhật khi phiếu đã ở trạng thái "Đã hoàn thành" hoặc "Đã
        // hủy"
        if ("Đã hủy".equals(currentStatus)) {
            System.out.println("Không thể cập nhật trạng thái cho phiếu nhập đã hủy");
            return false;
        }

        if ("Đã hoàn thành".equals(currentStatus)) {
            System.out.println("Không thể cập nhật trạng thái cho phiếu nhập đã hoàn thành");
            return false;
        }

        // Lưu trạng thái trước khi thay đổi
        boolean wasCompleted = "Đã hoàn thành".equals(currentStatus);
        boolean willBeCompleted = "Đã hoàn thành".equals(status);

        // Cập nhật trạng thái
        boolean success = importReceiptDAO.updateImportReceiptStatus(importId, status);

        if (success) {
            System.out.println("Đã cập nhật trạng thái phiếu nhập " + importId + " từ ["
                    + currentStatus + "] thành [" + status + "]");

            // Chỉ cập nhật tồn kho nếu chuyển sang "Đã hoàn thành" và chưa từng ở trạng
            // thái này trước đó
            if (!wasCompleted && willBeCompleted) {
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

        System.out.println("======== BẮT ĐẦU CẬP NHẬT TỒN KHO VÀ DANH MỤC ========");
        System.out.println("Số lượng sản phẩm cần cập nhật: " + details.size());

        boolean success = true;
        int updatedCount = 0;
        int updatedCategoryCount = 0;

        for (ImportReceiptDetail detail : details) {
            String productId = detail.getProductId();
            int quantity = detail.getQuantity();
            String categoryId = detail.getCategoryId();
            String categoryName = detail.getCategoryName();

            System.out.println("\n----- Xử lý sản phẩm: " + productId + " -----");
            System.out.println("Số lượng cần cập nhật: " + quantity);
            System.out.println("Danh mục từ chi tiết phiếu nhập: " +
                    (categoryId != null ? categoryId : "N/A") + " - " +
                    (categoryName != null ? categoryName : "Không xác định"));

            // Cập nhật danh mục sản phẩm nếu cần
            if (categoryId != null && !categoryId.isEmpty()) {
                ProductDTO product = productController.getProductById(productId);
                if (product != null) {
                    System.out.println("Danh mục hiện tại của sản phẩm: " +
                            (product.getCategoryId() != null ? product.getCategoryId() : "null") + " - " +
                            (product.getCategoryName() != null ? product.getCategoryName() : "null"));

                    if (product.getCategoryId() == null || !product.getCategoryId().equals(categoryId)) {
                        System.out.println("Cập nhật danh mục cho sản phẩm " + productId + " từ [" +
                                (product.getCategoryId() != null ? product.getCategoryId() : "null") + " - " +
                                (product.getCategoryName() != null ? product.getCategoryName() : "null") + "] thành [" +
                                categoryId + " - " + categoryName + "]");

                        product.setCategoryId(categoryId);
                        product.setCategoryName(categoryName);
                        boolean categoryUpdateResult = productController.updateProduct(product);

                        if (categoryUpdateResult) {
                            System.out.println("✓ Cập nhật danh mục thành công");
                            updatedCategoryCount++;
                        } else {
                            System.out.println("✗ Cập nhật danh mục thất bại");
                        }
                    } else {
                        System.out.println("→ Không cần cập nhật danh mục (đã giống nhau)");
                    }
                } else {
                    System.out.println("✗ Không tìm thấy sản phẩm có mã " + productId + " để cập nhật danh mục");
                }
            } else {
                System.out.println("✗ Không có thông tin danh mục để cập nhật");
            }

            // Đã chuyển sang trạng thái "Đã hoàn thành", cập nhật tồn kho với tham số
            // fromImportReceipt=false
            System.out.println("Cập nhật số lượng tồn kho cho sản phẩm " + productId + ": +" + quantity);
            boolean result = inventoryBUS.updateInventoryQuantity(productId, quantity, false);
            if (result) {
                System.out.println("✓ Cập nhật tồn kho thành công");
                updatedCount++;
            } else {
                success = false;
                System.out.println("✗ Cập nhật tồn kho thất bại");
            }
        }

        System.out.println("\n======== KẾT QUẢ CẬP NHẬT TỒN KHO VÀ DANH MỤC ========");
        System.out.println("Đã cập nhật tồn kho cho " + updatedCount + "/" + details.size() + " sản phẩm");
        System.out.println("Đã cập nhật danh mục cho " + updatedCategoryCount + " sản phẩm");
        System.out.println("Kết quả xử lý: " + (success ? "Thành công" : "Thất bại một phần"));

        return success;
    }

    /**
     * Trả về đối tượng DAO để truy cập trực tiếp
     */
    public ImportReceiptDAO getDAO() {
        return importReceiptDAO;
    }
}