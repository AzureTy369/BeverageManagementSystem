package BUS;

import DAO.SupplierDAO;
import DTO.SupplierDTO;
import DTO.SupplierProductDTO;
import DTO.ProductCategoryDTO;
import java.util.List;
import java.util.ArrayList;

public class SupplierBUS {
    private SupplierDAO supplierDAO;

    public SupplierBUS() {
        supplierDAO = new SupplierDAO();
    }

    public List<SupplierDTO> getAllSuppliers() {
        return supplierDAO.getAllSuppliers();
    }

    public SupplierDTO getSupplierById(String supplierId) {
        return supplierDAO.getSupplierById(supplierId);
    }

    public boolean addSupplier(String supplierId, String supplierName, String address, String phone, String email) {
        SupplierDTO supplier = new SupplierDTO(supplierId, supplierName, address, phone, email);
        return supplierDAO.addSupplier(supplier);
    }

    public boolean addSupplier(SupplierDTO supplier) {
        return supplierDAO.addSupplier(supplier);
    }

    public boolean addSupplier(String supplierId, String supplierName, String phone) {
        return addSupplier(supplierId, supplierName, "", phone, "");
    }

    public boolean updateSupplier(String supplierId, String supplierName, String address, String phone, String email) {
        SupplierDTO supplier = new SupplierDTO(supplierId, supplierName, address, phone, email);
        return supplierDAO.updateSupplier(supplier);
    }

    public boolean updateSupplier(SupplierDTO supplier) {
        return supplierDAO.updateSupplier(supplier);
    }

    public boolean updateSupplier(String supplierId, String supplierName, String phone) {
        return updateSupplier(supplierId, supplierName, "", phone, "");
    }

    public boolean deleteSupplier(String supplierId) {
        return supplierDAO.deleteSupplier(supplierId);
    }

    public String generateNewSupplierId() {
        List<SupplierDTO> suppliers = supplierDAO.getAllSuppliers();
        int maxId = 0;

        for (SupplierDTO supplier : suppliers) {
            String idStr = supplier.getSupplierId();
            if (idStr.startsWith("NCC")) {
                try {
                    int id = Integer.parseInt(idStr.substring(3));
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                    // Ignore if not a number
                }
            }
        }

        return "NCC" + String.format("%03d", maxId + 1);
    }

    // Phương thức tìm kiếm nhà cung cấp theo từ khóa
    public List<SupplierDTO> searchSuppliers(String keyword) {
        List<SupplierDTO> allSuppliers = getAllSuppliers();
        List<SupplierDTO> results = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return allSuppliers;
        }

        String lowercaseKeyword = keyword.toLowerCase().trim();

        for (SupplierDTO supplier : allSuppliers) {
            if (supplier.getSupplierId().toLowerCase().contains(lowercaseKeyword) ||
                    supplier.getSupplierName().toLowerCase().contains(lowercaseKeyword) ||
                    supplier.getAddress().toLowerCase().contains(lowercaseKeyword) ||
                    supplier.getPhone().toLowerCase().contains(lowercaseKeyword) ||
                    supplier.getEmail().toLowerCase().contains(lowercaseKeyword)) {
                results.add(supplier);
            }
        }

        return results;
    }

    // Các phương thức quản lý sản phẩm của nhà cung cấp

    /**
     * Lấy danh sách sản phẩm của một nhà cung cấp
     * 
     * @param supplierId Mã nhà cung cấp
     * @return Danh sách sản phẩm
     */
    public List<SupplierProductDTO> getSupplierProducts(String supplierId) {
        return supplierDAO.getProductsBySupplier(supplierId);
    }

    /**
     * Thêm sản phẩm mới cho nhà cung cấp
     * 
     * @param product Thông tin sản phẩm
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean addSupplierProduct(SupplierProductDTO product) {
        return supplierDAO.addSupplierProduct(product);
    }

    /**
     * Thêm sản phẩm mới cho nhà cung cấp
     * 
     * @param supplierId  Mã nhà cung cấp
     * @param productName Tên sản phẩm
     * @param unit        Đơn vị tính
     * @param description Mô tả
     * @param price       Giá
     * @param categoryId  Mã danh mục
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean addSupplierProduct(String supplierId, String productName, String unit, String description,
            double price, String categoryId) {
        // Tạo mã sản phẩm mới
        String productId = supplierDAO.generateNewSupplierProductId(supplierId);

        // Lấy tên danh mục
        String categoryName = "";
        if (categoryId != null && !categoryId.isEmpty()) {
            ProductCategoryBUS categoryBUS = new ProductCategoryBUS();
            ProductCategoryDTO category = categoryBUS.getCategoryById(categoryId);
            if (category != null) {
                categoryName = category.getCategoryName();
            }
        }

        SupplierProductDTO product = new SupplierProductDTO(
                productId, supplierId, productName, unit, description, price, categoryId, categoryName);

        return supplierDAO.addSupplierProduct(product);
    }

    /**
     * Cập nhật thông tin sản phẩm của nhà cung cấp
     * 
     * @param product Thông tin sản phẩm mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateSupplierProduct(SupplierProductDTO product) {
        return supplierDAO.updateSupplierProduct(product);
    }

    /**
     * Xóa một sản phẩm của nhà cung cấp
     * 
     * @param productId  Mã sản phẩm
     * @param supplierId Mã nhà cung cấp
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteSupplierProduct(String productId, String supplierId) {
        return supplierDAO.deleteSupplierProduct(productId, supplierId);
    }

    /**
     * Xóa tất cả sản phẩm của một nhà cung cấp
     * 
     * @param supplierId Mã nhà cung cấp
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteAllSupplierProducts(String supplierId) {
        return supplierDAO.deleteAllSupplierProducts(supplierId);
    }

    /**
     * Xóa tất cả sản phẩm của tất cả nhà cung cấp
     * 
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteAllSupplierProducts() {
        return supplierDAO.deleteAllSupplierProducts();
    }

    /**
     * Kiểm tra xem sản phẩm của nhà cung cấp đã tồn tại chưa
     * 
     * @param productId  Mã sản phẩm
     * @param supplierId Mã nhà cung cấp
     * @return true nếu đã tồn tại, false nếu chưa
     */
    public boolean supplierProductExists(String productId, String supplierId) {
        return supplierDAO.supplierProductExists(productId, supplierId);
    }

    /**
     * Tạo mã sản phẩm mới cho nhà cung cấp
     * 
     * @param supplierId Mã nhà cung cấp
     * @return Mã sản phẩm mới
     */
    public String generateNewSupplierProductId(String supplierId) {
        return supplierDAO.generateNewSupplierProductId(supplierId);
    }

    /**
     * Xóa tất cả nhà cung cấp
     * 
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteAllSuppliers() {
        return supplierDAO.deleteAllSuppliers();
    }

    /**
     * Kiểm tra số điện thoại đã tồn tại chưa
     * 
     * @param phone             Số điện thoại cần kiểm tra
     * @param excludeSupplierId ID nhà cung cấp cần loại trừ (dùng khi cập nhật)
     * @return true nếu số điện thoại đã tồn tại, false nếu chưa
     */
    public boolean isPhoneExists(String phone, String excludeSupplierId) {
        return supplierDAO.isPhoneExists(phone, excludeSupplierId);
    }

    /**
     * Kiểm tra số điện thoại đã tồn tại chưa (dùng khi thêm mới)
     * 
     * @param phone Số điện thoại cần kiểm tra
     * @return true nếu số điện thoại đã tồn tại, false nếu chưa
     */
    public boolean isPhoneExists(String phone) {
        return supplierDAO.isPhoneExists(phone);
    }
}