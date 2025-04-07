package BUS;

import DTO.ProductDetailDTO;
import DAO.ProductDetailDAO;
import java.util.List;
import java.util.ArrayList;

public class ProductDetailBUS {
    private ProductDetailDAO detailDAO;

    public ProductDetailBUS() {
        detailDAO = new ProductDetailDAO();
    }

    public List<ProductDetailDTO> getAllProductDetails() {
        return detailDAO.getAllProductDetails();
    }

    public List<ProductDetailDTO> getProductDetailsByProduct(String productId) {
        return detailDAO.getProductDetailsByProduct(productId);
    }

    public ProductDetailDTO getProductDetailById(String detailId) {
        return detailDAO.getProductDetailById(detailId);
    }

    public boolean addProductDetail(ProductDetailDTO detail) {
        return detailDAO.addProductDetail(detail);
    }

    public boolean updateProductDetail(ProductDetailDTO detail) {
        return detailDAO.updateProductDetail(detail);
    }

    public boolean deleteProductDetail(String detailId) {
        return detailDAO.deleteProductDetail(detailId);
    }

    public String generateNewDetailId() {
        return detailDAO.generateNewDetailId();
    }

    // Phương thức tìm kiếm chi tiết sản phẩm theo từ khóa
    public List<ProductDetailDTO> searchProductDetails(String keyword) {
        List<ProductDetailDTO> allDetails = getAllProductDetails();
        List<ProductDetailDTO> results = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return allDetails;
        }

        String lowercaseKeyword = keyword.toLowerCase().trim();

        for (ProductDetailDTO detail : allDetails) {
            if (detail.getDetailId().toLowerCase().contains(lowercaseKeyword) ||
                    detail.getProductId().toLowerCase().contains(lowercaseKeyword) ||
                    detail.getProductName().toLowerCase().contains(lowercaseKeyword) ||
                    (detail.getSize() != null &&
                            detail.getSize().toLowerCase().contains(lowercaseKeyword))
                    ||
                    String.valueOf(detail.getPrice()).contains(lowercaseKeyword)) {
                results.add(detail);
            }
        }

        return results;
    }
}