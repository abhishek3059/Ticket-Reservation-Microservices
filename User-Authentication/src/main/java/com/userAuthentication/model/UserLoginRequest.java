package com.userAuthentication.model;

import lombok.Data;

import java.util.List;

@Data
public class UserLoginRequest {
    private String username;
    private String password;
}
