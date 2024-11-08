package com.userAuthentication.model;

import lombok.Data;

import java.util.List;
@Data
public class UserRegistrationRequest {

    private String username;
    private String password;
    private List<String> roles;
}
