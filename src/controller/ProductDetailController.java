package controller;

import model.ProductDetail;
import database.ProductDetailDAO;
import java.util.List;

public class ProductDetailController {
    private ProductDetailDAO detailDAO;

    public ProductDetailController() {
        detailDAO = new ProductDetailDAO();
    }

    public List<ProductDetail> getAllProductDetails() {
        return detailDAO.getAllProductDetails();
    }

    public List<ProductDetail> getProductDetailsByProduct(String productId) {
        return detailDAO.getProductDetailsByProduct(productId);
    }

    public ProductDetail getProductDetailById(String detailId) {
        return detailDAO.getProductDetailById(detailId);
    }

    public boolean addProductDetail(ProductDetail detail) {
        return detailDAO.addProductDetail(detail);
    }

    public boolean updateProductDetail(ProductDetail detail) {
        return detailDAO.updateProductDetail(detail);
    }

    public boolean deleteProductDetail(String detailId) {
        return detailDAO.deleteProductDetail(detailId);
    }

    public String generateNewDetailId() {
        return detailDAO.generateNewDetailId();
    }
}