package com.userAuthentication.serviceImpl;

import com.dto.CommonDTO.UserLoginRequest;
import com.dto.CommonDTO.UserUpdateRequest;
import com.userAuthentication.customExceptions.BadCredentialsException;
import com.userAuthentication.customExceptions.UserNotFoundException;
import com.userAuthentication.model.User;
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

    @Override
    public ResponseEntity<?> updateYourProfile(String username, String password, UserUpdateRequest updateRequest) {
        if(!authenticationService.validateUser(username, password)) {
            throw new BadCredentialsException("Bad Credentials");
        }

        User user = repository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("User with username "+username+" not found"));

            if(updateRequest.getEmail() != null) {
                user.setEmail(updateRequest.getEmail());
            }
            if (updateRequest.getPhoneNumber() != null) {
                user.setContactDetails(updateRequest.getPhoneNumber());
            }
            repository.save(user);

        return ResponseEntity.ok(user);


    }

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

    @Override
    public ResponseEntity<User> getYourProfile(UserLoginRequest request) {
        if(!authenticationService.validateUser(request.getUsername(), request.getPassword())) {
            throw new BadCredentialsException("Bad Credentials");
        }
        User user = repository.findByUserName(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("No such user exists in Database Create new user "));
        return  ResponseEntity.ok(user);
    }


}
