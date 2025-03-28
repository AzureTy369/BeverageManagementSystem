package BUS;

import DTO.Product;
import DAO.ProductDAO;
import java.util.List;

public class ProductController {
    private ProductDAO productDAO;

    public ProductController() {
        productDAO = new ProductDAO();
    }

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public List<Product> getProductsByCategory(String categoryId) {
        return productDAO.getProductsByCategory(categoryId);
    }

    public Product getProductById(String productId) {
        return productDAO.getProductById(productId);
    }

    public boolean addProduct(Product product) {
        return productDAO.addProduct(product);
    }

    public boolean updateProduct(Product product) {
        return productDAO.updateProduct(product);
    }

    public boolean deleteProduct(String productId) {
        return productDAO.deleteProduct(productId);
    }

    public String generateNewProductId() {
        return productDAO.generateNewProductId();
    }

    // Statistics methods
    public List<Product> getBestSellingProducts(int limit) {
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