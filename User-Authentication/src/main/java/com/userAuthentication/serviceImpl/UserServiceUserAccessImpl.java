package com.userAuthentication.serviceImpl;

import com.userAuthentication.customExceptions.BadCredentialsException;
import com.userAuthentication.customExceptions.UserNotFoundException;
import com.userAuthentication.model.User;
import com.userAuthentication.model.UserLoginRequest;
import com.userAuthentication.repository.UserRepository;
import com.userAuthentication.service.UserServiceUserAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceUserAccessImpl implements UserServiceUserAccess {


    @Autowired
    private UserRepository repository;
    @Autowired
    private AuthenticationService authenticationService;


    public String createEncodedPassword(String password){
        return new BCryptPasswordEncoder().encode(password);
    }

//    @Override
//    public ResponseEntity<?> updateYourProfile(UserUpdateRequest UpdateRequest,UserLoginRequest LoginRequest) {
//        if(!authenticationService.validateUser(LoginRequest.getUsername(), LoginRequest.getPassword())) {
//                   throw new BadCredentialsException("Bad Credentials");
//        }
//        else {
//            // will complete/update  logic after modifying models
//            User user = repository.findByUserName(LoginRequest.getUsername())
//                    .orElseThrow(() -> new UserNotFoundException("Not found"));
//            user.setPassword(createEncodedPassword(UpdateRequest.getNewPassword()));
//            repository.save(user);
//            return new ResponseEntity<>(user,HttpStatus.OK);
//        }
//    }

    @Override
    public ResponseEntity<?> deleteYourProfile(UserLoginRequest request) {
        if(!authenticationService.validateUser(request.getUsername(), request.getPassword())) {
            throw new BadCredentialsException("Bad Credentials");
        }
        else {
            // will complete logic after modifying models
            User user = repository.findByUserName(request.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("Not found"));
            repository.delete(user);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }


}
