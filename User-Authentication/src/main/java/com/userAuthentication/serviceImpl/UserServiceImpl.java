package com.userAuthentication.serviceImpl;

import com.userAuthentication.customExceptions.BadCredentialsException;
import com.userAuthentication.customExceptions.UserAlreadyExistsException;
import com.userAuthentication.enums.UserRole;
import com.userAuthentication.model.User;
import com.userAuthentication.model.UserLoginRequest;
import com.userAuthentication.model.UserRegistrationRequest;
import com.userAuthentication.repository.UserRepository;
import com.userAuthentication.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private AuthenticationManager manager;



    @Override
    public ResponseEntity<?> registerWithRoles(UserRegistrationRequest request) {
        if (repository.existsByUserName(request.getUsername())) {
            throw new UserAlreadyExistsException("User Already Exists in the database please login","409");
        }
        User user = new User();
        user.setUserName(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            for (String role : request.getRoles()) {
                user.getUserRoleSet().add(UserRole.valueOf(role.startsWith("ROLE_") ? role.toUpperCase() : "ROLE_" + role.toUpperCase()));
            }
        } else user.getUserRoleSet().add(UserRole.ROLE_USER);

        repository.save(user);
            return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> login(UserLoginRequest request) {
        Authentication authentication = manager.authenticate(
                                     new UsernamePasswordAuthenticationToken(
                                                 request.getUsername(),request.getPassword()));

        if (authentication.isAuthenticated()){
            return new ResponseEntity<>(jwtService.generateToken(request.getUsername()), HttpStatus.OK);
        }
        else {
            throw new BadCredentialsException("Token not valid");
        }

    }
    @Override
    public ResponseEntity<?> validateToken(String token) {
        if (jwtService.validateToken(token)) {
            return  ResponseEntity.ok().build();
        }
        else{
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }

    }

}

