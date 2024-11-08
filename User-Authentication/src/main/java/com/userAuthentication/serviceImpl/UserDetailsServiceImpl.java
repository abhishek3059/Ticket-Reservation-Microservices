package com.userAuthentication.serviceImpl;

import com.userAuthentication.model.MyUserPrinciple;
import com.userAuthentication.model.User;
import com.userAuthentication.repository.UserRepository;
import com.userAuthentication.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUserName(username).orElseThrow(
                () -> new UsernameNotFoundException("Username Not Found"));
        return new MyUserPrinciple(user);
    }


}
