package com.userAuthentication.model;

import lombok.Data;

@Data
public class UserUpdateRequest
{

    private User user;
    private final String username;
    private String newPassword;
    private String email;




}
