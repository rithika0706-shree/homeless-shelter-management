package com.tn.homeless.dto;

import java.util.List;

/**
 * DTO returned after a successful login.
 *
 * WHY NEEDED:
 *  - The original AuthController returned a raw Map<String,String> with just the token.
 *  - Frontend needs to know the user's roles to show/hide menus (Admin vs NGO vs Volunteer).
 *  - Returning a structured DTO is cleaner and easier to extend.
 */
public class AuthResponse {

    private String token;
    private String username;
    private List<String> roles;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String token, String username, List<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
        this.message = "Login successful";
    }

    public AuthResponse(String message) {
        this.message = message;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
