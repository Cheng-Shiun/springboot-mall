package com.chengshiun.springbootmall.controller;

import com.chengshiun.springbootmall.dao.UserDao;
import com.chengshiun.springbootmall.dto.UserLoginRequest;
import com.chengshiun.springbootmall.dto.UserRegisterRequest;
import com.chengshiun.springbootmall.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDao userDao;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    //宣告轉換Java object -> Json 變數
    private ObjectMapper objectMapper = new ObjectMapper();

    //提煉 register()
    private void register(UserRegisterRequest userRegisterRequest) throws Exception {
        String json = objectMapper.writeValueAsString(userRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());
    }

    //確認每個 test case 執行後的數據
    @AfterEach
    public void printH2DataBaseContent() {
        System.out.println("H2 資料庫的數據：");
        String sql = "SELECT user_id, email, password, created_date, last_modified_date FROM `user`";
        namedParameterJdbcTemplate.query(sql, (rs, rowNum) -> {

            System.out.println("{"
                    + " usr_id: " + rs.getString("user_id")
                    + ", email: " + rs.getString("email")
                    + ", password: " + rs.getString("password")
                    + ", created_date: " + rs.getString("created_date")
                    + ", last_modified_date: " + rs.getString("last_modified_date")
                    + " }"); // 一行行輸出
            return null;
        });
    }

    //註冊帳號
    @Test
    @Transactional
    public void register_success() throws Exception {
        //新增一個註冊帳號 object
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setEmail("test1@gmail.com");
        userRegisterRequest.setPassword("123");

        //轉換 Json
        String json =  objectMapper.writeValueAsString(userRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.email", equalTo("test1@gmail.com")))
                .andExpect(jsonPath("$.created_date", notNullValue()))
                .andExpect(jsonPath("$.last_modified_date", notNullValue()));

        //驗證資料庫中的密碼值是否為 Hash value
        //前端使用 POST users/register 傳遞的請求參數 password 會轉換成雜湊值存入資料庫
        User user = userDao.getUserByEmail(userRegisterRequest.getEmail());
        System.out.println("註冊存在資料庫的密碼: " + user.getPassword());
        System.out.println("註冊所輸入的密碼:" + userRegisterRequest.getPassword());
        assertNotEquals(user.getPassword(), userRegisterRequest.getPassword());
    }

    @Test
    @Transactional
    public void register_illegalEmailFormat() throws Exception {
        //新增一個註冊帳號 object
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setEmail("test11111111");
        userRegisterRequest.setPassword("123");

        //轉換 Json
        String json =  objectMapper.writeValueAsString(userRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @Test
    @Transactional
    public void register_emailAlreadyRegistered() throws Exception {
        //新增一個已註冊帳號 object
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setEmail("test100@gmail.com");
        userRegisterRequest.setPassword("123");

        //轉換 Json
        String json =  objectMapper.writeValueAsString(userRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @Test
    @Transactional
    public void register_emailAlreadyExist() throws Exception {
        //新增一個新註冊帳號 object
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setEmail("test2@gmail.com");
        userRegisterRequest.setPassword("123");

        //轉換 Json
        String json =  objectMapper.writeValueAsString(userRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201));

        //再次使用相同 email 註冊
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    //登入
    @Test
    @Transactional
    public void login_success() throws Exception {
        //先註冊新帳號
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setEmail("test3@gmail.com");
        userRegisterRequest.setPassword("333");
        register(userRegisterRequest);     //實作提煉的 register() 方法會噴出一個 Exception

        //再測試登入
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUserEmail("test3@gmail.com");   //或可使用 userLoginRequest.getUserEmail()
        userLoginRequest.setUserPassword("333");            //或可使用 userLoginRequest.getUserPassword()
        System.out.println("登入輸入密碼: " + userLoginRequest.getUserPassword());

        //將 Java Object -> Json
        String loginJson = objectMapper.writeValueAsString(userLoginRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", equalTo("test3@gmail.com")))
                .andExpect(jsonPath("$.created_date", notNullValue()))
                .andExpect(jsonPath("$.last_modified_date", notNullValue()));
    }

    @Test
    @Transactional
    public void login_userEmailNotExist() throws Exception {
        //使用資料庫中未註冊 email 進行登入
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUserEmail("unknown@gmail.com");
        userLoginRequest.setUserPassword("123");

        String loginJson = objectMapper.writeValueAsString(userLoginRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @Test
    @Transactional
    public void login_illegalEmailFormat() throws Exception {
        //使用錯誤格式 email 進行登入
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUserEmail("error");
        userLoginRequest.setUserPassword("123");

        String loginJson = objectMapper.writeValueAsString(userLoginRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @Test
    @Transactional
    public void login_wrongPassword() throws Exception {
        //先註冊新帳號
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setEmail("test4@gmail.com");
        userRegisterRequest.setPassword("444");
        register(userRegisterRequest);     //會噴出一個Exception
        System.out.println("註冊設定密碼: " + userRegisterRequest.getPassword());

        //再測試登入
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUserEmail("test4@gmail.com");   //或可使用 userLoginRequest.getUserEmail()
        userLoginRequest.setUserPassword("000");            //或可使用 userLoginRequest.getUserPassword()
        System.out.println("登入輸入密碼: " + userLoginRequest.getUserPassword());

        //將 Java Object -> Json
        String loginJson = objectMapper.writeValueAsString(userLoginRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));

        //使用 Assert 驗證註冊與登入密碼
        assertNotEquals(userRegisterRequest.getPassword(), userLoginRequest.getUserPassword());
    }

}