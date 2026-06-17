package com.tn.homeless.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUsername(String token);
    String generateToken(String username);
    boolean validateToken(String token, UserDetails userDetails);
    long getExpirationTime();
}
