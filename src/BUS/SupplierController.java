package BUS;

import DAO.SupplierDAO;
import DTO.Supplier;
import java.util.List;

public class SupplierController {
    private SupplierDAO supplierDAO;

    public SupplierController() {
        supplierDAO = new SupplierDAO();
    }

    public List<Supplier> getAllSuppliers() {
        return supplierDAO.getAllSuppliers();
    }

    public Supplier getSupplierById(String supplierId) {
        return supplierDAO.getSupplierById(supplierId);
    }

    public boolean addSupplier(String supplierId, String supplierName, String address, String phone, String email) {
        Supplier supplier = new Supplier(supplierId, supplierName, address, phone, email);
        return supplierDAO.addSupplier(supplier);
    }

    public boolean addSupplier(String supplierId, String supplierName, String phone) {
        return addSupplier(supplierId, supplierName, "", phone, "");
    }

    public boolean updateSupplier(String supplierId, String supplierName, String address, String phone, String email) {
        Supplier supplier = new Supplier(supplierId, supplierName, address, phone, email);
        return supplierDAO.updateSupplier(supplier);
    }

    public boolean updateSupplier(String supplierId, String supplierName, String phone) {
        return updateSupplier(supplierId, supplierName, "", phone, "");
    }

    public boolean deleteSupplier(String supplierId) {
        return supplierDAO.deleteSupplier(supplierId);
    }
}