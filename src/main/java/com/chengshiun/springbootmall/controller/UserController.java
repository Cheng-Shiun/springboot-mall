package com.chengshiun.springbootmall.controller;

import com.chengshiun.springbootmall.dto.UserLoginRequest;
import com.chengshiun.springbootmall.dto.UserRegisterRequest;
import com.chengshiun.springbootmall.model.User;
import com.chengshiun.springbootmall.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // 註冊頁面
    @GetMapping("/users/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userRegisterRequest", new UserRegisterRequest());
        return "register";
    }

    // 註冊表單提交
    @PostMapping("/users/register")
    public String register(@ModelAttribute("userRegisterRequest") @Valid UserRegisterRequest userRegisterRequest, Model model) {
        try {
            Integer userId = userService.register(userRegisterRequest);
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
            return "redirect:/users/login";      //註冊成功後跳轉登入頁面
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    // 登入頁面
    @GetMapping("/users/login")
    public String showLoginForm(Model model) {
        model.addAttribute("userLoginRequest", new UserLoginRequest());
        return "login";
    }

    // 登入表單提交
    @PostMapping("/users/login")
    public String login(@ModelAttribute("userLoginRequest") @Valid UserLoginRequest userLoginRequest, Model model) {
        try {
            User user = userService.login(userLoginRequest);
            model.addAttribute("user", user);
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", "Login failed: " + e.getMessage());
            return "login";
        }
    }
}
