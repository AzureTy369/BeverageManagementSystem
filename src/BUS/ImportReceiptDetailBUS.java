package BUS;

import DAO.ImportReceiptDAO;
import DAO.ImportReceiptDetailDAO;
import DTO.ImportReceiptDetail;
import java.util.List;

import org.jfree.chart.JFreeChart;

public class ImportReceiptDetailBUS {
    private ImportReceiptDetailDAO importReceiptDetailDAO;

    public ImportReceiptDetailBUS() {
        importReceiptDetailDAO = new ImportReceiptDetailDAO();
    }

    /**
     * Lấy tất cả chi tiết phiếu nhập
     */
    public List<ImportReceiptDetail> getAllImportReceiptDetails() {
        return importReceiptDetailDAO.getAllImportReceiptDetails();
    }

    /**
     * Lấy chi tiết phiếu nhập theo mã phiếu
     */
    public List<ImportReceiptDetail> getImportReceiptDetailsByReceiptId(String receiptId) {
        return importReceiptDetailDAO.getImportReceiptDetailsByReceiptId(receiptId);
    }

    /**
     * Thêm chi tiết phiếu nhập
     */
    public boolean insertImportReceiptDetail(ImportReceiptDetail detail) {
        System.out.println("BUS: Đang thêm chi tiết phiếu nhập - Mã phiếu: " + detail.getReceiptId() +
                ", Mã SP: " + detail.getProductId() +
                ", SL: " + detail.getQuantity() +
                ", Đơn giá: " + detail.getPrice() +
                ", Thành tiền: " + detail.getTotal());
        boolean result = importReceiptDetailDAO.insertImportReceiptDetail(detail);
        if (result) {
            System.out.println("BUS: Thêm chi tiết phiếu nhập thành công!");
        } else {
            System.out.println("BUS: Thêm chi tiết phiếu nhập thất bại!");
        }
        return result;
    }

    /**
     * Thêm nhiều chi tiết phiếu nhập cùng lúc
     * 
     * @param details Danh sách chi tiết phiếu nhập
     * @return true nếu thêm thành công, false nếu có lỗi
     */
    public boolean insertMultipleImportReceiptDetails(List<ImportReceiptDetail> details) {
        System.out.println("BUS: Đang thêm " + details.size() + " chi tiết phiếu nhập");

        if (details.isEmpty()) {
            System.out.println("BUS: Danh sách chi tiết phiếu nhập trống!");
            return true; // Không có gì để thêm, coi như thành công
        }

        boolean allSuccess = true;
        int successCount = 0;

        for (ImportReceiptDetail detail : details) {
            boolean success = importReceiptDetailDAO.insertImportReceiptDetail(detail);

            if (success) {
                successCount++;
                System.out.println("BUS: Thêm chi tiết phiếu nhập thành công: Mã phiếu=" + detail.getReceiptId() +
                        ", Mã SP=" + detail.getProductId() +
                        ", SL=" + detail.getQuantity() +
                        ", Đơn giá=" + detail.getPrice() +
                        ", Thành tiền=" + detail.getTotal());
            } else {
                allSuccess = false;
                System.out.println("BUS: Thêm chi tiết phiếu nhập thất bại: Mã phiếu=" + detail.getReceiptId() +
                        ", Mã SP=" + detail.getProductId());
            }
        }

        System.out.println("BUS: Đã thêm thành công " + successCount + "/" + details.size() + " chi tiết phiếu nhập");
        return allSuccess;
    }

    /**
     * Cập nhật chi tiết phiếu nhập
     */
    public boolean updateImportReceiptDetail(ImportReceiptDetail detail) {
        return importReceiptDetailDAO.updateImportReceiptDetail(detail);
    }

    /**
     * Xóa chi tiết phiếu nhập
     */
    public boolean deleteImportReceiptDetail(String receiptId, String productId) {
        return importReceiptDetailDAO.deleteImportReceiptDetail(receiptId, productId);
    }

    /**
     * Xóa tất cả chi tiết của một phiếu nhập
     */
    public boolean deleteAllImportReceiptDetails(String receiptId) {
        return importReceiptDetailDAO.deleteAllImportReceiptDetails(receiptId);
    }
    public JFreeChart createImportChart() {
        return ImportReceiptDAO.createImportChart();
    }
}