package com.userAuthentication.service;

import com.dto.CommonDTO.UserLoginRequest;
import com.dto.CommonDTO.UserRegistrationRequest;
import com.userAuthentication.dto.UserLoginRequest;
import com.userAuthentication.dto.UserRegistrationRequest;
import org.springframework.http.ResponseEntity;


public interface UserService {

     ResponseEntity<?> registerWithRoles(UserRegistrationRequest request);
     ResponseEntity<?> login (UserLoginRequest request);
     public ResponseEntity<?> validateToken(String token);
}
