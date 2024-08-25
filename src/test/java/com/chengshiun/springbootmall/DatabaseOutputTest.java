package com.chengshiun.springbootmall;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@AutoConfigureTestDatabase
public class DatabaseOutputTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Test
    public void printDatabaseContents() throws Exception {
        String sql = "SELECT user_id, email, password, created_date, last_modified_date FROM `user`";
        jdbcTemplate.query(sql, (rs, rowNum) -> {
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
}
