package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.User;

public interface UserService extends IService<User> {
    User login(String username, String password);
    User register(User user);
    User getUserByUsername(String username);
}
