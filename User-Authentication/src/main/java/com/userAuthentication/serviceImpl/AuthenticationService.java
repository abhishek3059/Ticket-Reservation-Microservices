package com.userAuthentication.serviceImpl;



import com.userAuthentication.repository.UserRepository;
import com.userAuthentication.service.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repository;

    public boolean validateUser(String username, String password) {

        if(username == null || password == null) return false;
        if (!repository.existsByUserName(username)) {
            return false;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Use PasswordEncoder to compare the raw newPassword with the encoded newPassword
        return passwordEncoder.matches(password, userDetails.getPassword());
    }
}
