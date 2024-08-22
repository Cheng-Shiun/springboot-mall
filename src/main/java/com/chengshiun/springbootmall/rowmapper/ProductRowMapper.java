package com.chengshiun.springbootmall.rowmapper;

import com.chengshiun.springbootmall.constant.ProductCategory;
import com.chengshiun.springbootmall.model.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


//RowMapper 負責把資料庫中的數據轉換為Java Object
public class ProductRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setProductName(rs.getString("product_name"));

        //將資料庫中的商品種類 String -> enum
        String categoryStr = rs.getString("category");
        ProductCategory category = ProductCategory.valueOf(categoryStr);
        product.setCategory(category);
//        product.setCategory(ProductCategory.valueOf(rs.getString("category"))); //簡略寫法

        product.setImageUrl(rs.getString("image_url"));
        product.setPrice(rs.getInt("price"));
        product.setStock(rs.getInt("stock"));
        product.setDescription(rs.getString("description"));
        product.setCreatedDate(rs.getTimestamp("created_date"));
        product.setLastModifiedDate(rs.getTimestamp("last_modified_date"));
        return product;
    }
}
