package BUS;

import DTO.ImportReceipt;
import DTO.ImportReceiptDetail;
import DAO.ImportReceiptDAO;
import DAO.ImportReceiptDetailDAO;
<<<<<<< HEAD
import GUI.ImportGoodsGUI;
import DTO.SupplierDTO;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.JFreeChart;

public class ImportReceiptBUS {
    private ImportReceiptDAO importReceiptDAO;
    private ImportReceiptDetailDAO importReceiptDetailDAO;
    private InventoryBUS inventoryBUS;
    private SupplierBUS supplierBUS;
    private ImportGoodsGUI importGoodsGUI; // Có thể null nếu không sử dụng trong ImportReceiptsGUI

    // Constructor mặc định cho ImportReceiptsGUI
=======
import java.util.ArrayList;
import java.util.List;

public class ImportReceiptBUS {

    private ImportReceiptDAO importReceiptDAO;
    private ImportReceiptDetailDAO importReceiptDetailDAO;
    private InventoryBUS inventoryBUS;

>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
    public ImportReceiptBUS() {
        this.importReceiptDAO = new ImportReceiptDAO();
        this.importReceiptDetailDAO = new ImportReceiptDetailDAO();
        this.inventoryBUS = new InventoryBUS();
<<<<<<< HEAD
        this.supplierBUS = new SupplierBUS();
        this.importGoodsGUI = null; // Không cần ImportGoodsGUI
    }

    // Constructor có tham số cho ImportGoodsGUI
    public ImportReceiptBUS(ImportGoodsGUI importGoodsGUI) {
        this.importReceiptDAO = new ImportReceiptDAO();
        this.importReceiptDetailDAO = new ImportReceiptDetailDAO();
        this.inventoryBUS = new InventoryBUS();
        this.supplierBUS = new SupplierBUS();
        this.importGoodsGUI = importGoodsGUI;
    }

    public List<ImportReceipt> getAllImportReceipts() {
=======
    }

    public List<ImportReceipt> getAllImportReceipts() {
        // Đảm bảo luôn lấy dữ liệu mới nhất từ DAO
>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
        return importReceiptDAO.getAllImportReceipts();
    }

    public List<ImportReceipt> search(String keyword) {
        List<ImportReceipt> allReceipts = getAllImportReceipts();
        List<ImportReceipt> result = new ArrayList<>();
<<<<<<< HEAD
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
=======

        for (ImportReceipt receipt : allReceipts) {
            // Tìm kiếm theo mã phiếu nhập, nhà cung cấp, người tạo
            if (receipt.getImportId().toLowerCase().contains(keyword.toLowerCase()) ||
                    receipt.getSupplierId().toLowerCase().contains(keyword.toLowerCase()) ||
                    receipt.getEmployeeId().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(receipt);
            }
        }

>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
        return result;
    }

    public boolean insertImportReceipt(ImportReceipt importReceipt) {
<<<<<<< HEAD
        if (importReceipt.getStatus() == null) {
            importReceipt.setStatus("Đang xử lý");
        }
        return importReceiptDAO.insertImportReceipt(importReceipt);
    }

    public boolean deleteImportReceipt(String importId) {
=======
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
>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
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
<<<<<<< HEAD
=======
        // Kiểm tra nếu phiếu nhập đã hủy thì không cho phép cập nhật
>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
        ImportReceipt existingReceipt = getImportReceiptById(importReceipt.getImportId());
        if (existingReceipt != null && "Đã hủy".equals(existingReceipt.getStatus())) {
            System.out.println("Không thể cập nhật phiếu nhập đã hủy");
            return false;
        }
        return importReceiptDAO.updateImportReceipt(importReceipt);
    }

<<<<<<< HEAD
    public boolean updateImportReceiptStatus(String importId, String status) {
=======
    /**
     * Cập nhật trạng thái phiếu nhập
     * 
     * @param importId Mã phiếu nhập
     * @param status   Trạng thái mới ("Đang xử lý", "Đã hoàn thành", "Đã hủy")
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateImportReceiptStatus(String importId, String status) {
        // Kiểm tra phiếu nhập có tồn tại không
>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
        ImportReceipt receipt = getImportReceiptById(importId);
        if (receipt == null) {
            System.out.println("Không tìm thấy phiếu nhập: " + importId);
            return false;
        }
<<<<<<< HEAD
=======

        // Kiểm tra trạng thái hiện tại
>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
        String currentStatus = receipt.getStatus();
        if ("Đã hủy".equals(currentStatus)) {
            System.out.println("Không thể cập nhật trạng thái cho phiếu nhập đã hủy");
            return false;
        }
<<<<<<< HEAD
        boolean success = importReceiptDAO.updateImportReceiptStatus(importId, status);
        if (success) {
            System.out.println("Đã cập nhật trạng thái phiếu nhập " + importId + " từ [" +
                              currentStatus + "] thành [" + status + "]");
            if ("Đã hoàn thành".equals(status) && !"Đã hoàn thành".equals(currentStatus)) {
                updateInventory(importId);
            }
=======

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

>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
            return true;
        } else {
            System.out.println("Lỗi khi cập nhật trạng thái phiếu nhập");
            return false;
        }
    }

<<<<<<< HEAD
    private boolean updateInventory(String importId) {
        List<ImportReceiptDetail> details = importReceiptDetailDAO.getImportReceiptDetailsByReceiptId(importId);
=======
    /**
     * Cập nhật tồn kho dựa trên chi tiết phiếu nhập
     * 
     * @param importId Mã phiếu nhập
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    private boolean updateInventory(String importId) {
        // Lấy danh sách chi tiết phiếu nhập
        List<ImportReceiptDetail> details = importReceiptDetailDAO.getImportReceiptDetailsByReceiptId(importId);

>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
        if (details.isEmpty()) {
            System.out.println("Không có chi tiết phiếu nhập để cập nhật tồn kho");
            return false;
        }
<<<<<<< HEAD
        boolean success = true;
        int updatedCount = 0;
        for (ImportReceiptDetail detail : details) {
            String productId = detail.getProductId();
            int quantity = detail.getQuantity();
            System.out.println("Cập nhật tồn kho cho sản phẩm " + productId + " với số lượng " + quantity);
=======

        boolean success = true;
        int updatedCount = 0;

        for (ImportReceiptDetail detail : details) {
            String productId = detail.getProductId();
            int quantity = detail.getQuantity();

            System.out.println("Cập nhật tồn kho cho sản phẩm " + productId + " với số lượng " + quantity);

            // Đã chuyển sang trạng thái "Đã hoàn thành", cập nhật tồn kho với tham số
            // fromImportReceipt=false
>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
            boolean result = inventoryBUS.updateInventoryQuantity(productId, quantity, false);
            if (result) {
                updatedCount++;
            } else {
                success = false;
                System.out.println("Lỗi khi cập nhật tồn kho cho sản phẩm " + productId);
            }
        }
<<<<<<< HEAD
=======

>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
        System.out.println("Đã cập nhật tồn kho cho " + updatedCount + "/" + details.size() + " sản phẩm");
        return success;
    }

<<<<<<< HEAD
    public void refreshSupplierAndProducts() {
        if (importGoodsGUI != null) {
            importGoodsGUI.loadSuppliers();
            SupplierDTO selectedSupplier = (SupplierDTO) importGoodsGUI.getSupplierComboBox().getSelectedItem();
            if (selectedSupplier != null) {
                importGoodsGUI.loadProductsForSupplier(selectedSupplier.getSupplierId());
            }
            JOptionPane.showMessageDialog(importGoodsGUI, "Đã làm mới danh sách nhà cung cấp và sản phẩm.");
        } else {
            System.out.println("Không thể làm mới: importGoodsGUI chưa được khởi tạo");
        }
    }

    public ImportReceiptDAO getDAO() {
        return importReceiptDAO;
    }

    public JFreeChart createImportChart() {
        return ImportReceiptDAO.createImportChart();
    }

    public SupplierBUS getSupplierBUS() {
        return supplierBUS;
    }

    public String generateImportId() {
        List<ImportReceipt> receipts = getAllImportReceipts();
        int maxId = 0;
        for (ImportReceipt receipt : receipts) {
            String id = receipt.getImportId();
            if (id != null && id.startsWith("PN")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > maxId) {
                        maxId = num;
                    }
                } catch (NumberFormatException e) {
                    // Bỏ qua
                }
            }
        }
        return String.format("PN%03d", maxId + 1);
    }
=======
    /**
     * Trả về đối tượng DAO để truy cập trực tiếp
     */
    public ImportReceiptDAO getDAO() {
        return importReceiptDAO;
    }
>>>>>>> d9978e96461c8db2003c751a909670c9ff81ff31
}