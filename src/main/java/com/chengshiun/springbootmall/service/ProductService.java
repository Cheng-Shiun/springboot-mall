package com.chengshiun.springbootmall.service;

import com.chengshiun.springbootmall.dto.ProductQueryParams;
import com.chengshiun.springbootmall.dto.ProductRequest;
import com.chengshiun.springbootmall.model.Product;

import java.util.List;

public interface ProductService {

    List<Product> getAllProducts();

    Integer countProduct(ProductQueryParams productQueryParams);
    Product getProductById(Integer productId);

    List<Product> getProducts(ProductQueryParams productQueryParams);

    Integer createProduct(ProductRequest productRequest);

    void updateProduct(Integer productId, ProductRequest productRequest);

    void deleteProductById(Integer productId);
}
