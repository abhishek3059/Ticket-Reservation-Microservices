package com.userAuthentication.controller;

import com.userAuthentication.model.UserLoginRequest;
import com.userAuthentication.model.UserUpdateRequest;
import com.userAuthentication.service.UserServiceUserAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth/user")
public class UserControllerUserAccess {

    @Autowired
    private UserServiceUserAccess userAccess;

//    @PreAuthorize("USER") @PutMapping("/update")
//    public ResponseEntity<?> updateProfile(@RequestBody UserLoginRequest loginRequest, @RequestBody UserUpdateRequest updateRequest){
//        return userAccess.updateYourProfile(updateRequest,loginRequest);
//    }

    @PreAuthorize("USER") @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProfile(@RequestBody UserLoginRequest request){
        return userAccess.deleteYourProfile(request);
    }


}
