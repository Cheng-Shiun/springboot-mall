package com.chengshiun.springbootmall.service.impl;

import com.chengshiun.springbootmall.dao.UserDao;
import com.chengshiun.springbootmall.dto.UserLoginRequest;
import com.chengshiun.springbootmall.dto.UserRegisterRequest;
import com.chengshiun.springbootmall.model.User;
import com.chengshiun.springbootmall.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
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

        //使用 MD5 生成密碼的雜湊值(Hash value)
        //須將password -> Byte
        String hashedPassword = DigestUtils.md5DigestAsHex(userRegisterRequest.getPassword().getBytes());
        userRegisterRequest.setPassword(hashedPassword);   //將輸入的密碼改成 Hash value

        //創建新帳號 (password 已經變成使用 Hash value)
        return userDao.createUser(userRegisterRequest);
    }

    @Override
    public User login(UserLoginRequest userLoginRequest) {
        //以登入的 email 取得該 user
        User loginUser = userDao.getUserByEmail(userLoginRequest.getUserEmail());

        //資料庫中無該 email -> 登入失敗
        if (loginUser == null) {
            log.warn("該 email {} 尚未註冊", userLoginRequest.getUserEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        //使用 MD5 生成密碼的雜湊值(Hash value)
        String hashedPassword = DigestUtils.md5DigestAsHex(userLoginRequest.getUserPassword().getBytes());

        //資料庫中有該 email -> 檢查輸入密碼是否正確
        //比較資料庫中的雜湊值 與登入所輸入的密碼雜湊值
        if (loginUser.getPassword().equals(hashedPassword)) {
            log.info("該 email {} 已成功登入", userLoginRequest.getUserEmail());
            return loginUser;
        } else {
            log.warn("該 email {} 輸入不正確的密碼", userLoginRequest.getUserEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
