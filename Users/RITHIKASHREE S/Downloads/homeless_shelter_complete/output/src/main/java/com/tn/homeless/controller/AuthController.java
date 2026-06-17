package com.tn.homeless.controller;

import com.tn.homeless.dto.ApiResponse;
import com.tn.homeless.dto.AuthRequest;
import com.tn.homeless.dto.RegisterRequest;
import com.tn.homeless.entity.User;
import com.tn.homeless.service.JwtService;
import com.tn.homeless.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * CHANGES from original:
 *  - Added POST /auth/register (was completely missing).
 *  - Login response now uses ApiResponse wrapper with token + role info.
 *  - Added @Valid on both request bodies.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtService jwtService;
    @Autowired private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @Valid @RequestBody AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String token = jwtService.generateToken(request.getUsername());
        String role = auth.getAuthorities().stream().map(Object::toString)
                .findFirst().orElse("UNKNOWN").replace("ROLE_", "");
        Map<String, Object> data = Map.of("token", token, "username",
                request.getUsername(), "role", role, "expiresIn", jwtService.getExpirationTime());
        return ResponseEntity.ok(ApiResponse.ok("Login successful", data));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(
            @Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(request);
        Map<String, Object> data = Map.of("id", user.getId(),
                "username", user.getUsername(), "role", request.getRole());
        return ResponseEntity.ok(ApiResponse.ok("User registered successfully", data));
    }
}
