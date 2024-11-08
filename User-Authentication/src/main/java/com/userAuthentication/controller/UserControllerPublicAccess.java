package com.userAuthentication.controller;

import com.userAuthentication.model.UserLoginRequest;
import com.userAuthentication.model.UserRegistrationRequest;
import com.userAuthentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class UserControllerPublicAccess {

    @Autowired
    private UserService userService;


    @PostMapping("/auth/register")
    public ResponseEntity<?> registerWithRoles(@RequestBody UserRegistrationRequest request){
        return userService.registerWithRoles(request);
    }

    @GetMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request){
        return userService.login(request);
    }
    @GetMapping("/auth/validate/{token}")
    public ResponseEntity<?> validateToken(@PathVariable String token){
        return userService.validateToken(token);
    }




}
