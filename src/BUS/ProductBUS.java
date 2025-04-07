package BUS;

import DTO.ProductDTO;
import DAO.ProductDAO;
import java.util.List;
import java.util.ArrayList;

public class ProductBUS {
    private ProductDAO productDAO;

    public ProductBUS() {
        productDAO = new ProductDAO();
    }

    public List<ProductDTO> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public List<ProductDTO> getProductsByCategory(String categoryId) {
        return productDAO.getProductsByCategory(categoryId);
    }

    public ProductDTO getProductById(String productId) {
        return productDAO.getProductById(productId);
    }

    public boolean addProduct(ProductDTO product) {
        return productDAO.addProduct(product);
    }

    public boolean updateProduct(ProductDTO product) {
        return productDAO.updateProduct(product);
    }

    public boolean deleteProduct(String productId) {
        return productDAO.deleteProduct(productId);
    }

    public String generateNewProductId() {
        return productDAO.generateNewProductId();
    }

    // Phương thức tìm kiếm sản phẩm theo từ khóa
    public List<ProductDTO> searchProducts(String keyword) {
        List<ProductDTO> allProducts = getAllProducts();
        List<ProductDTO> results = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return allProducts;
        }

        String lowercaseKeyword = keyword.toLowerCase().trim();

        for (ProductDTO product : allProducts) {
            if (product.getProductId().toLowerCase().contains(lowercaseKeyword) ||
                    product.getProductName().toLowerCase().contains(lowercaseKeyword) ||
                    product.getDescription().toLowerCase().contains(lowercaseKeyword) ||
                    product.getCategoryName().toLowerCase().contains(lowercaseKeyword) ||
                    product.getUnit().toLowerCase().contains(lowercaseKeyword)) {
                results.add(product);
            }
        }

        return results;
    }

    // Phương thức tìm kiếm nâng cao với nhiều điều kiện
    public List<ProductDTO> advancedSearch(String name, String categoryId, String unit, String description,
            boolean isAnd) {
        List<ProductDTO> allProducts = getAllProducts();
        List<ProductDTO> results = new ArrayList<>();

        // Nếu tất cả các điều kiện đều trống, trả về toàn bộ danh sách
        if ((name == null || name.isEmpty()) &&
                (categoryId == null || categoryId.isEmpty()) &&
                (unit == null || unit.isEmpty()) &&
                (description == null || description.isEmpty())) {
            return allProducts;
        }

        // Chuẩn hóa các tham số tìm kiếm
        String nameLower = name != null ? name.toLowerCase() : "";
        String unitLower = unit != null ? unit.toLowerCase() : "";
        String descLower = description != null ? description.toLowerCase() : "";

        for (ProductDTO product : allProducts) {
            boolean nameMatch = nameLower.isEmpty() ||
                    (product.getProductName() != null &&
                            product.getProductName().toLowerCase().contains(nameLower));

            boolean categoryMatch = categoryId == null || categoryId.isEmpty() ||
                    (product.getCategoryId() != null &&
                            product.getCategoryId().equals(categoryId));

            boolean unitMatch = unitLower.isEmpty() ||
                    (product.getUnit() != null &&
                            product.getUnit().toLowerCase().contains(unitLower));

            boolean descMatch = descLower.isEmpty() ||
                    (product.getDescription() != null &&
                            product.getDescription().toLowerCase().contains(descLower));

            if (isAnd) {
                // Phép AND - tất cả điều kiện phải đúng
                if (nameMatch && categoryMatch && unitMatch && descMatch) {
                    results.add(product);
                }
            } else {
                // Phép OR - chỉ cần một điều kiện đúng
                if (nameMatch || categoryMatch || unitMatch || descMatch) {
                    results.add(product);
                }
            }
        }

        return results;
    }

    // Statistics methods
    public List<ProductDTO> getBestSellingProducts(int limit) {
        return productDAO.getBestSellingProducts(limit);
    }

    public List<Object[]> getInventoryStatistics(boolean lowStock) {
        return productDAO.getInventoryStatistics(lowStock);
    }

    public List<Object[]> getInventoryStatistics(boolean lowStock, int limit) {
        return productDAO.getInventoryStatistics(lowStock, limit);
    }

    public List<Object[]> getRevenueByProduct() {
        return productDAO.getRevenueByProduct();
    }

    public List<Object[]> getRevenueByProduct(java.util.Date startDate, java.util.Date endDate) {
        return productDAO.getRevenueByProduct(startDate, endDate);
    }

    public List<Object[]> getRevenueByCategory() {
        return productDAO.getRevenueByCategory();
    }

    public List<Object[]> getRevenueByCategory(java.util.Date startDate, java.util.Date endDate) {
        return productDAO.getRevenueByCategory(startDate, endDate);
    }
}