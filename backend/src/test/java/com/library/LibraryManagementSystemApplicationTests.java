package com.library;

import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.service.UserService;
import com.library.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class LibraryManagementSystemApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void contextLoads() {
        System.out.println("Spring Boot application loaded successfully!");
    }

    @Test
    void testDatabaseConnection() {
        User user = userMapper.selectById(1L);
        System.out.println("User found: " + user);
        System.out.println("Username: " + (user != null ? user.getUsername() : "null"));
    }

    @Test
    void testGetUserByUsername() {
        User user = userService.getUserByUsername("admin");
        System.out.println("User by username: " + user);
        if (user != null) {
            System.out.println("Username: " + user.getUsername());
            System.out.println("Password (hashed): " + user.getPassword());
            System.out.println("Role: " + user.getRole());
            System.out.println("Status: " + user.getStatus());
        }
    }

    @Test
    void testPasswordMatch() {
        User user = userService.getUserByUsername("admin");
        if (user != null) {
            String plainPassword = "admin";
            boolean matches = passwordEncoder.matches(plainPassword, user.getPassword());
            System.out.println("Password matches for 'admin': " + matches);
            
            if (!matches) {
                String newHash = passwordEncoder.encode("admin");
                System.out.println("New hash for 'admin': " + newHash);
            }
        }
    }

    @Test
    void testJwtGeneration() {
        User user = userService.getUserByUsername("admin");
        if (user != null) {
            String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
            System.out.println("Generated token: " + token);
            
            String username = jwtUtil.getUsernameFromToken(token);
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            
            System.out.println("Username from token: " + username);
            System.out.println("UserId from token: " + userId);
            System.out.println("Role from token: " + role);
        }
    }
}
