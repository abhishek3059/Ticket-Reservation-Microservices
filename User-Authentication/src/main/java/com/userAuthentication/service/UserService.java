package com.userAuthentication.service;

import com.userAuthentication.model.UserLoginRequest;
import com.userAuthentication.model.UserRegistrationRequest;
import org.springframework.http.ResponseEntity;


public interface UserService {

     ResponseEntity<?> registerWithRoles(UserRegistrationRequest request);
     ResponseEntity<?> login (UserLoginRequest request);
     public ResponseEntity<?> validateToken(String token);
}
