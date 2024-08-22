package com.chengshiun.springbootmall.dao;

import com.chengshiun.springbootmall.dto.ProductRequest;
import com.chengshiun.springbootmall.model.Product;

public interface ProductDao {
    Product getProductById(Integer productId);

    Integer createProduct(ProductRequest productRequest);
}
