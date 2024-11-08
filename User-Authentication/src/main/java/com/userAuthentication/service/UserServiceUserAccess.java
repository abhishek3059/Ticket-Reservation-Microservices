package com.userAuthentication.service;

import com.userAuthentication.model.UserLoginRequest;
import org.springframework.http.ResponseEntity;

public interface UserServiceUserAccess {

//    ResponseEntity<?> updateYourProfile(UserUpdateRequest UpdateRequest, UserLoginRequest loginRequest);
    ResponseEntity<?> deleteYourProfile(UserLoginRequest request);

}
