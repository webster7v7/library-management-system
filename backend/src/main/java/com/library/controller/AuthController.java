package com.library.controller;

import com.library.common.Result;
import com.library.dto.LoginRequest;
import com.library.dto.LoginResponse;
import com.library.dto.RegisterRequest;
import com.library.entity.User;
import com.library.service.UserService;
import com.library.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            logger.info("========================================");
            logger.info("Login request received");
            logger.info("Username: {}", request.getUsername());
            logger.info("Password length: {}", request.getPassword() != null ? request.getPassword().length() : 0);
            logger.info("========================================");

            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                logger.warn("Username is empty");
                return Result.error(400, "用户名不能为空");
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                logger.warn("Password is empty");
                return Result.error(400, "密码不能为空");
            }

            User user = userService.login(request.getUsername(), request.getPassword());

            logger.info("========================================");
            logger.info("Login success - userId: {}, username: {}", user.getId(), user.getUsername());
            logger.info("User role: {}", user.getRole());
            logger.info("User status: {}", user.getStatus());
            logger.info("========================================");

            String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());

            logger.info("JWT token generated successfully");

            LoginResponse response = new LoginResponse(token, user);
            return Result.success("登录成功", response);
        } catch (RuntimeException e) {
            logger.error("Login failed - RuntimeException: {}", e.getMessage(), e);
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("Login failed - Unexpected error", e);
            return Result.error(500, "系统错误，请联系管理员");
        }
    }

    @PostMapping("/register")
    public Result<LoginResponse> register(@RequestBody RegisterRequest request) {
        try {
            logger.info("========================================");
            logger.info("Register request received");
            logger.info("Username: {}", request.getUsername());
            logger.info("Real Name: {}", request.getRealName());
            logger.info("Phone: {}", request.getPhone());
            logger.info("Email: {}", request.getEmail());
            logger.info("Password length: {}", request.getPassword() != null ? request.getPassword().length() : 0);
            logger.info("========================================");

            // 参数验证
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                logger.warn("Username is empty");
                return Result.error(400, "用户名不能为空");
            }

            if (request.getUsername().length() < 3 || request.getUsername().length() > 20) {
                logger.warn("Username length invalid: {}", request.getUsername().length());
                return Result.error(400, "用户名长度必须在3-20个字符之间");
            }

            if (request.getPassword() == null || request.getPassword().length() < 6) {
                logger.warn("Password too short");
                return Result.error(400, "密码长度不能少于6位");
            }

            if (request.getRealName() == null || request.getRealName().trim().isEmpty()) {
                logger.warn("Real name is empty");
                return Result.error(400, "真实姓名不能为空");
            }

            if (request.getPhone() == null || !request.getPhone().matches("^1[3-9][0-9]{9}$")) {
                logger.warn("Invalid phone number: {}", request.getPhone());
                return Result.error(400, "请输入正确的手机号");
            }

            if (request.getEmail() == null || !request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                logger.warn("Invalid email: {}", request.getEmail());
                return Result.error(400, "请输入正确的邮箱地址");
            }

            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setRealName(request.getRealName());
            user.setPhone(request.getPhone());
            user.setEmail(request.getEmail());

            User registeredUser = userService.register(user);

            logger.info("========================================");
            logger.info("Register success - userId: {}", registeredUser.getId());
            logger.info("Registered username: {}", registeredUser.getUsername());
            logger.info("========================================");

            String token = jwtUtil.generateToken(registeredUser.getUsername(), registeredUser.getId(), registeredUser.getRole());

            logger.info("JWT token generated successfully");

            LoginResponse response = new LoginResponse(token, registeredUser);
            return Result.success("注册成功", response);
        } catch (RuntimeException e) {
            logger.error("Register failed - RuntimeException: {}", e.getMessage(), e);
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("Register failed - Unexpected error", e);
            return Result.error(500, "注册失败，系统错误");
        }
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success("退出成功", null);
    }
}
