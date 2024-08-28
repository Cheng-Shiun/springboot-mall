package com.chengshiun.springbootmall.controller;

import com.chengshiun.springbootmall.constant.ProductCategory;
import com.chengshiun.springbootmall.dto.ProductQueryParams;
import com.chengshiun.springbootmall.dto.ProductRequest;
import com.chengshiun.springbootmall.model.Product;
import com.chengshiun.springbootmall.model.User;
import com.chengshiun.springbootmall.service.ProductService;
import com.chengshiun.springbootmall.util.ProductPageUtil;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Validated
public class WebController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(required = false) ProductCategory category,
                       @RequestParam(required = false) String search,
                       @RequestParam(defaultValue = "created_date") String orderBy,
                       @RequestParam(defaultValue = "desc") String sort,
                       @RequestParam(defaultValue = "5") @Min(0) @Max(1000) Integer limit,
                       @RequestParam(defaultValue = "0") @Min(0) Integer offset
    ) {
        ProductQueryParams productQueryParams = new ProductQueryParams();
        productQueryParams.setCategory(category);
        productQueryParams.setSearch(search);
        productQueryParams.setOrderBy(orderBy);
        productQueryParams.setSort(sort);
        productQueryParams.setLimit(limit);
        productQueryParams.setOffset(offset);

        List<Product> products;
        Integer total;

        // 判斷是否有查詢條件
        if (search != null || category != null) {
            products = productService.getProducts(productQueryParams);
            total = productService.countProduct(productQueryParams);
        } else {
            products = productService.getAllProducts();
            total = products.size();
        }

        model.addAttribute("products", products);
        model.addAttribute("total", total);
        model.addAttribute("foodCategory", ProductCategory.FOOD.name());
        model.addAttribute("carCategory", ProductCategory.CAR.name());
        model.addAttribute("ebookCategory", ProductCategory.E_BOOK.name());

        return "index";
    }
}
