package com.chengshiun.springbootmall.controller;

import com.chengshiun.springbootmall.constant.ProductCategory;
import com.chengshiun.springbootmall.dto.ProductQueryParams;
import com.chengshiun.springbootmall.dto.ProductRequest;
import com.chengshiun.springbootmall.model.Product;
import com.chengshiun.springbootmall.service.ProductService;
import com.chengshiun.springbootmall.util.ProductPageUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
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
    public ResponseEntity<ProductPageUtil<Product>> getProducts(
            //查詢條件(Filtering)
            @RequestParam (required = false) ProductCategory category,
            @RequestParam (required = false) String search,

            //排序(Sorting)
            @RequestParam (defaultValue = "created_date") String orderBy,
            @RequestParam (defaultValue = "desc") String sort,

            //分頁(Pagination)
            @RequestParam (defaultValue = "5") @Min(0) @Max(1000) Integer limit,
            @RequestParam (defaultValue = "0") @Min(0) Integer offset) {

        //使用dto層建立請求參數 object，並 new 一個實體
        //分別設定請求參數對應的參數值
        ProductQueryParams productQueryParams = new ProductQueryParams();
        productQueryParams.setCategory(category);
        productQueryParams.setSearch(search);
        productQueryParams.setOrderBy(orderBy);
        productQueryParams.setSort(sort);
        productQueryParams.setLimit(limit);
        productQueryParams.setOffset(offset);

        List<Product> productList = productService.getProducts(productQueryParams);

        //取得當前查詢條件下的商品總數
        Integer total = productService.countProduct(productQueryParams);

        //使用util class建立分頁需要返回的數據，需先 new 一個實體
        ProductPageUtil pageUtil = new ProductPageUtil();
        pageUtil.setLimit(limit);
        pageUtil.setOffset(offset);
        pageUtil.setQueryTotal(total);
        pageUtil.setResult(productList);

        //RESTful API的設計理念，查詢列表無論是否有數據，都需要返回200狀態碼 -> 為了確保這個請求資源是正確的
        //與查詢單項商品不同，查無數據則表示無該個請求資源，因此需要回傳404 NOT_FOUND
        return ResponseEntity.status(HttpStatus.OK).body(pageUtil);
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
