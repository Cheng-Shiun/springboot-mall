package com.chengshiun.springbootmall.dao;

import com.chengshiun.springbootmall.dto.UserRegisterRequest;
import com.chengshiun.springbootmall.model.User;

public interface UserDao {
    User getUserById(Integer userId);

    User getUserByEmail(String email);
    Integer createUser(UserRegisterRequest userRegisterRequest);
}
