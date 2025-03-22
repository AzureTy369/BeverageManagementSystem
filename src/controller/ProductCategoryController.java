package controller;

import model.ProductCategory;
import database.ProductCategoryDAO;
import java.util.List;

public class ProductCategoryController {
    private ProductCategoryDAO categoryDAO;

    public ProductCategoryController() {
        categoryDAO = new ProductCategoryDAO();
    }

    public List<ProductCategory> getAllCategories() {
        return categoryDAO.getAllCategories();
    }

    public ProductCategory getCategoryById(String categoryId) {
        return categoryDAO.getCategoryById(categoryId);
    }

    public boolean addCategory(ProductCategory category) {
        return categoryDAO.addCategory(category);
    }

    public boolean updateCategory(ProductCategory category) {
        return categoryDAO.updateCategory(category);
    }

    public boolean deleteCategory(String categoryId) {
        return categoryDAO.deleteCategory(categoryId);
    }

    public String generateNewCategoryId() {
        return categoryDAO.generateNewCategoryId();
    }
}