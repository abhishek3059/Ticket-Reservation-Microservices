package com.userAuthentication.service;

import com.dto.CommonDTO.UserLoginRequest;
import com.dto.CommonDTO.UserUpdateRequest;
import com.userAuthentication.dto.UserLoginRequest;
import com.userAuthentication.dto.UserUpdateRequest;
import com.userAuthentication.model.User;
import org.springframework.http.ResponseEntity;

public interface UserServiceUserAccess {

    ResponseEntity<?> updateYourProfile(String username, String password, UserUpdateRequest updateRequest);
    ResponseEntity<?> deleteYourProfile(UserLoginRequest request);
    ResponseEntity<User> getYourProfile(UserLoginRequest request);
}
