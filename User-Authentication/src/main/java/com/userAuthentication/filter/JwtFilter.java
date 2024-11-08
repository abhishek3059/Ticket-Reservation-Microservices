package com.userAuthentication.filter;

import com.userAuthentication.model.MyUserPrinciple;
import com.userAuthentication.service.UserDetailsService;
import com.userAuthentication.serviceImpl.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService service;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private MyUserPrinciple myUserPrinciple;

// we want to make jwt standard for login so we have created a filter method which would bypass
    // username and pass word authentication filter which is the default authentication filter


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
 // we will get request = Bearer 'some bit code or token contains all info and details like username and newPassword
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            token = authorizationHeader.substring(7);
            username = service.extractUserName(token);
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = context.getBean(UserDetailsService.class).loadUserByUsername(username);
            if(service.validateToken(token,userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, myUserPrinciple,userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}
