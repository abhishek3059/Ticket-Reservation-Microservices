package com.userAuthentication.serviceImpl;

import com.userAuthentication.customExceptions.BadCredentialsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.token.expiration}")
    private long expirationTime;




    public String generateToken(String username) {
        Map<String , Object> claims = new HashMap<>();
                return Jwts.builder()
                                .claims()
                                    .add(claims)
                                         .subject(username)
                                            .issuedAt(new Date(System.currentTimeMillis()))
                                             .expiration(new Date(System.currentTimeMillis()+ expirationTime))
                                                    .and()
                                                        .signWith(getSigningKey())
                                                             .compact();
    }


    // this is for the

    private SecretKey getSigningKey() {
        byte[] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }


    public String extractUserName(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())&& !isTokenExpired(token));
    }
    public boolean validateToken(String token){

        try {
            Claims claims = extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid Token");
        }
    }


    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
       return Jwts.parser()
                    .verifyWith(getSigningKey())
                            .build().parseSignedClaims(token)
                                    .getPayload();
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());

    }
    private Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }


}
