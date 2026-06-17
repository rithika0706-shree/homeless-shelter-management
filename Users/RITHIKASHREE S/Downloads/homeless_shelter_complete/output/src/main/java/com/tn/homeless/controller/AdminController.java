package com.tn.homeless.controller;

import com.tn.homeless.dto.ApiResponse;
import com.tn.homeless.entity.User;
import com.tn.homeless.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Admin-only user management endpoints.
 *
 * CHANGES from original:
 *  - Original AdminController had a single empty placeholder method.
 *  - Added GET /admin/users — list all users.
 *  - Added PUT /admin/users/{id}/deactivate — soft-disable a user.
 *  - Added DELETE /admin/users/{id} — hard delete.
 *  - All guarded with @PreAuthorize("hasRole('ADMIN')").
 */
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    /** GET /admin/users — list all registered users */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok("All users", userRepository.findAll()));
    }

    /** PUT /admin/users/{id}/deactivate — disable a user account */
    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(false);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.ok("User deactivated", user.getUsername()));
    }

    /** PUT /admin/users/{id}/activate — re-enable a user account */
    @PutMapping("/users/{id}/activate")
    public ResponseEntity<ApiResponse<String>> activateUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(true);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.ok("User activated", user.getUsername()));
    }

    /** DELETE /admin/users/{id} — permanently remove a user */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("User deleted", null));
    }
}
