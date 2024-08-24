package com.chengshiun.springbootmall.controller;

import com.chengshiun.springbootmall.constant.ProductCategory;
import com.chengshiun.springbootmall.dto.ProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.Null;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    //objectMapper 可以使用writeValueAsString(): Java Object -> Json、readValue(): Json -> Java Object
    private ObjectMapper objectMapper = new ObjectMapper();

    //查詢商品
    @Test
    public void getProduct_success() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products/{productId}", 1);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName", equalTo("蘋果（澳洲）")))
                .andExpect(jsonPath("$.category", equalTo("FOOD")))
                .andExpect(jsonPath("$.imageUrl", notNullValue()))
                .andExpect(jsonPath("$.price", notNullValue()))
                .andExpect(jsonPath("$.stock", notNullValue()))
                .andExpect(jsonPath("$.description", notNullValue()))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));
    }

    @Test
    public void getProduct_notFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products/{productId}", 1000);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(404));
    }

    //創建商品
    @Test
    @Transactional
    public void createProduct_success() throws Exception {
        ProductRequest productRequest = new ProductRequest();

        productRequest.setProductName("test create");
        productRequest.setCategory(ProductCategory.FOOD);
        productRequest.setImageUrl("http://test.com");
        productRequest.setPrice(100);
        productRequest.setStock(5);
        productRequest.setDescription("test");

        //轉換Java Object -> Json
        String json = objectMapper.writeValueAsString(productRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.productName", equalTo("test create")))
                .andExpect(jsonPath("$.category", equalTo("FOOD")))
                .andExpect(jsonPath("$.imageUrl", equalTo("http://test.com")))
                .andExpect(jsonPath("$.price", equalTo(100)))
                .andExpect(jsonPath("$.stock", equalTo(5)))
                .andExpect(jsonPath("$.description", equalTo("test")));
    }

    @Test
    @Transactional
    public void createProduct_illegalRequestParams() throws Exception {
        ProductRequest productRequest = new ProductRequest();

        productRequest.setProductName("test create");

        //轉換Java Object -> Json
        String json = objectMapper.writeValueAsString(productRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @Test
    @Transactional
    public void createProduct_illegalRequestParams_wrongCategory() throws Exception {
        String json = "{\n" +
                "  \"productName\": \"Audi\",\n" +
                "  \"category\": \"TOOL\",\n" +
                "  \"imageUrl\": \"https://cdn.pixabay.com/photo/2015/01/19/13/51/car-604019_1280.jpg\",\n" +
                "  \"price\": 1000000,\n" +
                "  \"stock\": 5\n" +
                "}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    //更新商品
    @Test
    @Transactional
    public void updateProduct_success() throws Exception {
        ProductRequest productRequest = new ProductRequest();

        productRequest.setProductName("test update");
        productRequest.setCategory(ProductCategory.FOOD);
        productRequest.setImageUrl("http://test.com");
        productRequest.setPrice(10000);
        productRequest.setStock(500);
        productRequest.setDescription("test");

        //轉換Java Object -> Json
        String json = objectMapper.writeValueAsString(productRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/products/{productId}", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName", equalTo("test update")))
                .andExpect(jsonPath("$.category", equalTo("FOOD")))
                .andExpect(jsonPath("$.imageUrl", equalTo("http://test.com")))
                .andExpect(jsonPath("$.price", equalTo(10000)))
                .andExpect(jsonPath("$.stock", equalTo(500)))
                .andExpect(jsonPath("$.description", equalTo("test")));
    }

    @Test
    @Transactional
    public void updateProduct_illegalRequestParams() throws Exception {
        ProductRequest productRequest = new ProductRequest();

        productRequest.setProductName("test update");

        //轉換Java Object -> Json
        String json = objectMapper.writeValueAsString(productRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/products/{products}", 3)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @Test
    @Transactional
    public void updateProduct_illegalRequestParams_productIdNotFound() throws Exception {
        ProductRequest productRequest = new ProductRequest();

        productRequest.setProductName("test update");
        productRequest.setCategory(ProductCategory.FOOD);
        productRequest.setImageUrl("http://test.com");
        productRequest.setPrice(10000);
        productRequest.setStock(500);
        productRequest.setDescription("test");

        //轉換Java Object -> Json
        String json = objectMapper.writeValueAsString(productRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/products/{productId}", 10000)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(404));
    }

    //刪除商品
    @Test
    @Transactional
    public void deleteProduct_success() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/products/{productId}", 1);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(204));
    }

    @Test
    @Transactional
    public void deleteProduct_NotExistingProduct() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/products/{productId}", 100);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(204));
    }

    //查詢商品列表
    @Test
    public void getProducts() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.queryTotal", equalTo(7)))
                .andExpect(jsonPath("$.result", hasSize(5)));
    }

    @Test
    public void getProducts_filtering_success() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products")
                .param("category", "FOOD")
                .param("search", "蘋果");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.queryTotal", equalTo(3)))
                .andExpect(jsonPath("$.result", hasSize(3)));
    }

    @Test
    public void getProducts_filtering_notExisting() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products")
                .param("category", "CAR")
                .param("search", "蘋果");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.queryTotal", equalTo(0)))
                .andExpect(jsonPath("$.result", hasSize(0)));
    }

    @Test
    public void getProducts_sorting_success() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products")
                .param("orderBy", "price")
                .param("sort", "asc");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.queryTotal", notNullValue()))
                .andExpect(jsonPath("$.result", hasSize(5)))
                .andExpect(jsonPath("$.result[0].productId", equalTo(3)))
                .andExpect(jsonPath("$.result[1].productId", equalTo(1)))
                .andExpect(jsonPath("$.result[2].productId", equalTo(2)))
                .andExpect(jsonPath("$.result[3].productId", equalTo(4)))
                .andExpect(jsonPath("$.result[4].productId", equalTo(7)));
    }

    @Test
    public void pagination_success() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products")
                .param("limit", "2")
                .param("offset", "2");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", equalTo(2)))
                .andExpect(jsonPath("$.offset", equalTo(2)))
                .andExpect(jsonPath("$.queryTotal", notNullValue()))
                .andExpect(jsonPath("$.result[0].productName", equalTo("BMW")))
                .andExpect(jsonPath("$.result[1].productName", equalTo("Toyota")));
    }
}