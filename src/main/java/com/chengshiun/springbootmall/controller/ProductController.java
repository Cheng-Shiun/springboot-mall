package com.chengshiun.springbootmall.controller;

import com.chengshiun.springbootmall.constant.ProductCategory;
import com.chengshiun.springbootmall.dto.ProductQueryParams;
import com.chengshiun.springbootmall.dto.ProductRequest;
import com.chengshiun.springbootmall.model.Product;
import com.chengshiun.springbootmall.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    //查詢商品
    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable Integer productId) {
        Product product = productService.getProductById(productId);

        if (product != null) {
            return ResponseEntity.status(HttpStatus.OK).body(product);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //查詢商品列表
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam (required = false) ProductCategory category,
            @RequestParam (required = false) String search) {
        //使用dto層建立請求參數 object，並 new 一個實體
        //分別設定請求參數對應的參數值
        ProductQueryParams productQueryParams = new ProductQueryParams();
        productQueryParams.setCategory(category);
        productQueryParams.setSearch(search);

        List<Product> productList = productService.getProducts(productQueryParams);

        //RESTful API的設計理念，查詢列表無論是否有數據，都需要返回200狀態碼 -> 為了確保這個請求資源是正確的
        //與查詢單項商品不同，查無數據則表示無該個請求資源，因此需要回傳404 NOT_FOUND
        return ResponseEntity.status(HttpStatus.OK).body(productList);
    }

    //新增商品
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        Integer productId = productService.createProduct(productRequest);

        Product product = productService.getProductById(productId);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    //修改商品
    @PutMapping("/products/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer productId,
                                                 @RequestBody @Valid ProductRequest productRequest) {
        //檢查商品是否存在
        Product product = productService.getProductById(productId);
        if (product != null) {
            productService.updateProduct(productId, productRequest);

            Product updatedProduct = productService.getProductById(productId);

            return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //刪除商品
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer productId) {
        productService.deleteProductById(productId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
