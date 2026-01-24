package com.library.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private Long id;
    private String username;
    private String realName;
    private String role;

    public LoginResponse(String token, com.library.entity.User user) {
        this.token = token;
        this.id = user.getId();
        this.username = user.getUsername();
        this.realName = user.getRealName();
        this.role = user.getRole();
    }
}
