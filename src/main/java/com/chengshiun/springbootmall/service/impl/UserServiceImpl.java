package com.chengshiun.springbootmall.service.impl;

import com.chengshiun.springbootmall.dao.UserDao;
import com.chengshiun.springbootmall.dto.UserRegisterRequest;
import com.chengshiun.springbootmall.model.User;
import com.chengshiun.springbootmall.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService {

    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Override
    public User getUserById(Integer userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public Integer register(UserRegisterRequest userRegisterRequest) {
        //檢查註冊的 email
        User user = userDao.getUserByEmail(userRegisterRequest.getEmail());

        //email已註冊 -> 創建帳號失敗
        if (user != null) {
            log.warn("該 email {} 已經被註冊", userRegisterRequest.getEmail());   //使用log提示警告訊息
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        //創建新帳號
        return userDao.createUser(userRegisterRequest);
    }
}
