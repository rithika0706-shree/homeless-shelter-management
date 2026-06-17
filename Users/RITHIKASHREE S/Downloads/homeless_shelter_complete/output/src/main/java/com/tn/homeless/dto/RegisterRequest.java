package com.tn.homeless.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * NEW DTO — was completely missing from the original project.
 * Needed so that the /auth/register endpoint has a typed request body
 * with proper validation instead of reading raw parameters.
 */
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Email(message = "Must be a valid email")
    private String email;

    /**
     * Role name to assign: ADMIN, NGO, or VOLUNTEER.
     * Defaults to VOLUNTEER if not provided.
     */
    private String role = "VOLUNTEER";

    public RegisterRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
