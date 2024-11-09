package com.userAuthentication.controller;

import com.dto.CommonDTO.UserLoginRequest;
import com.dto.CommonDTO.UserUpdateRequest;
import com.userAuthentication.dto.UserLoginRequest;
import com.userAuthentication.dto.UserUpdateRequest;
import com.userAuthentication.model.User;
import com.userAuthentication.service.UserServiceUserAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth/user")
public class UserControllerUserAccess {

    @Autowired
    private UserServiceUserAccess userAccess;

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/update/{username}/{password}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> updateProfile
            (@PathVariable String username,
             @PathVariable String password,
             @RequestBody UserUpdateRequest updateRequest){
        return userAccess.updateYourProfile(username,password,updateRequest);
    }
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProfile
            (@RequestBody UserLoginRequest request){
        return userAccess.deleteYourProfile(request);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public ResponseEntity<User> getYourProfile(@RequestBody UserLoginRequest request){
        return userAccess.getYourProfile(request);
    }

}
