package BUS;

import DAO.SupplierDAO;
import DTO.SupplierDTO;
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

    public boolean addSupplier(String supplierId, String supplierName, String phone) {
        return addSupplier(supplierId, supplierName, "", phone, "");
    }

    public boolean updateSupplier(String supplierId, String supplierName, String address, String phone, String email) {
        SupplierDTO supplier = new SupplierDTO(supplierId, supplierName, address, phone, email);
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
}