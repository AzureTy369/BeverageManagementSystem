package DTO;

import java.util.ArrayList;
import java.util.List;

public class SupplierDTO {
    private String supplierId;
    private String supplierName;
    private String address;
    private String phone;
    private String email;
    private List<SupplierProductDTO> products;

    public SupplierDTO() {
        this.products = new ArrayList<>();
    }

    public SupplierDTO(String supplierId, String supplierName, String address, String phone, String email) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.products = new ArrayList<>();
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<SupplierProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<SupplierProductDTO> products) {
        this.products = products != null ? products : new ArrayList<>();
    }

    public void addProduct(SupplierProductDTO product) {
        if (product != null && !containsProduct(product.getProductId())) {
            product.setSupplierId(this.supplierId);
            this.products.add(product);
        }
    }

    public void removeProduct(String productId) {
        this.products.removeIf(product -> product.getProductId().equals(productId));
    }

    public boolean containsProduct(String productId) {
        return this.products.stream().anyMatch(product -> product.getProductId().equals(productId));
    }

    public SupplierProductDTO getProductById(String productId) {
        for (SupplierProductDTO product : products) {
            if (product.getProductId().equals(productId)) {
                return product;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return supplierName;
    }
}