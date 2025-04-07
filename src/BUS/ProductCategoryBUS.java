package BUS;

import DTO.ProductCategoryDTO;
import DAO.ProductCategoryDAO;
import java.util.List;
import java.util.ArrayList;

public class ProductCategoryBUS {
    private ProductCategoryDAO categoryDAO;

    public ProductCategoryBUS() {
        categoryDAO = new ProductCategoryDAO();
    }

    public List<ProductCategoryDTO> getAllCategories() {
        return categoryDAO.getAllCategories();
    }

    public ProductCategoryDTO getCategoryById(String categoryId) {
        return categoryDAO.getCategoryById(categoryId);
    }

    public boolean addCategory(ProductCategoryDTO category) {
        return categoryDAO.addCategory(category);
    }

    public boolean updateCategory(ProductCategoryDTO category) {
        return categoryDAO.updateCategory(category);
    }

    public boolean deleteCategory(String categoryId) {
        return categoryDAO.deleteCategory(categoryId);
    }

    public String generateNewCategoryId() {
        List<ProductCategoryDTO> categories = categoryDAO.getAllCategories();
        int maxId = 0;

        for (ProductCategoryDTO category : categories) {
            String idStr = category.getCategoryId();
            if (idStr.startsWith("DM")) {
                try {
                    int id = Integer.parseInt(idStr.substring(2));
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                    // Ignore if not a number
                }
            }
        }

        return "DM" + String.format("%03d", maxId + 1);
    }

    // Phương thức tìm kiếm danh mục sản phẩm theo từ khóa
    public List<ProductCategoryDTO> searchCategories(String keyword) {
        List<ProductCategoryDTO> allCategories = getAllCategories();
        List<ProductCategoryDTO> results = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return allCategories;
        }

        String lowercaseKeyword = keyword.toLowerCase().trim();

        for (ProductCategoryDTO category : allCategories) {
            if (category.getCategoryId().toLowerCase().contains(lowercaseKeyword) ||
                    category.getCategoryName().toLowerCase().contains(lowercaseKeyword) ||
                    (category.getDescription() != null
                            && category.getDescription().toLowerCase().contains(lowercaseKeyword))) {
                results.add(category);
            }
        }

        return results;
    }
}