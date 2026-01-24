package com.library.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User login(String username, String password) {
        logger.info("========================================");
        logger.info("Login process started for username: {}", username);
        logger.info("========================================");

        if (username == null || username.trim().isEmpty()) {
            logger.warn("Username is null or empty");
            throw new RuntimeException("用户名不能为空");
        }

        if (password == null || password.trim().isEmpty()) {
            logger.warn("Password is null or empty");
            throw new RuntimeException("密码不能为空");
        }

        logger.info("Step 1: Querying user from database...");
        User user = getUserByUsername(username);

        if (user == null) {
            logger.warn("========================================");
            logger.warn("Step 1 FAILED: User not found in database - {}", username);
            logger.warn("========================================");
            throw new RuntimeException("用户不存在");
        }

        logger.info("Step 1 SUCCESS: User found");
        logger.info("User details: ID={}, Username={}, Role={}, Status={}", 
            user.getId(), user.getUsername(), user.getRole(), user.getStatus());

        logger.info("========================================");
        logger.info("Step 2: Verifying password...");

        try {
            boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
            logger.info("Password match result: {}", passwordMatch);

            if (!passwordMatch) {
                logger.warn("Step 2 FAILED: Password does not match");
                throw new RuntimeException("密码错误");
            }

            logger.info("Step 2 SUCCESS: Password verified");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Step 2 ERROR: Password verification failed", e);
            throw new RuntimeException("密码验证失败: " + e.getMessage());
        }

        logger.info("========================================");
        logger.info("Step 3: Checking user status...");

        if (user.getStatus() == null || user.getStatus() == 0) {
            logger.warn("Step 3 FAILED: Account is disabled or status is null");
            throw new RuntimeException("账户已被禁用");
        }

        logger.info("Step 3 SUCCESS: Account is active");
        logger.info("========================================");
        logger.info("LOGIN SUCCESS: User {} successfully logged in", username);
        logger.info("========================================");

        return user;
    }

    @Override
    @Transactional
    public User register(User user) {
        logger.info("========================================");
        logger.info("Registration started for username: {}", user.getUsername());
        logger.info("========================================");

        // Step 1: Check if user exists
        if (getUserByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // Step 2: Encode password
        logger.info("Step 2: Encoding password...");
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            logger.info("Step 2 SUCCESS: Password encoded");
        } catch (Exception e) {
            logger.error("Step 2 ERROR: Password encoding failed", e);
            throw new RuntimeException("密码加密失败: " + e.getMessage());
        }

        // Step 3: Setting user properties
        logger.info("Step 3: Setting user properties...");
        user.setStatus(1);
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        logger.info("User properties set: Status={}, Role={}", user.getStatus(), user.getRole());

        // Step 4: Saving user to database
        logger.info("Step 4: Saving user to database...");
        try {
            boolean saved = save(user);
            if (!saved) {
                logger.error("Step 4 ERROR: User save returned false");
                throw new RuntimeException("保存用户失败");
            }
            logger.info("Step 4 SUCCESS: User saved to database. Generated ID: {}", user.getId());
        } catch (Exception e) {
            logger.error("Step 4 ERROR: Failed to save user", e);
            throw new RuntimeException("保存用户失败: " + e.getMessage());
        }

        logger.info("========================================");
        logger.info("REGISTRATION SUCCESS: User {} registered successfully", user.getUsername());
        logger.info("========================================");

        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        logger.info("getUserByUsername - Querying: {}", username);

        try {
            User user = lambdaQuery()
                    .eq(User::getUsername, username)
                    .one();

            if (user == null) {
                logger.info("User not found: {}", username);
                return null;
            }

            logger.info("User found: ID={}, Username={}", user.getId(), user.getUsername());
            return user;

        } catch (Exception e) {
            logger.error("getUserByUsername - Error querying user: {}", username, e);
            
            String message = e.getMessage();
            if (message != null && (message.contains("Expected one result") || message.contains("查不到") || message.contains("not found"))) {
                return null;
            }

            throw new RuntimeException("查询用户失败: " + (message != null ? message : "数据库异常"), e);
        }
    }
}
