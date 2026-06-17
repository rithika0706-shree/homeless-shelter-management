package com.tn.homeless.service.implementation;

import com.tn.homeless.dto.RegisterRequest;
import com.tn.homeless.entity.Role;
import com.tn.homeless.entity.User;
import com.tn.homeless.repository.RoleRepository;
import com.tn.homeless.repository.UserRepository;
import com.tn.homeless.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.Set;

/**
 * CHANGES from original:
 *  - Method signature changed to accept RegisterRequest DTO instead of raw params.
 *  - Added duplicate username/email check throwing IllegalArgumentException.
 *  - Role is now looked up from DB (or created if missing) rather than hardcoded.
 *  - Added @Transactional to ensure user + roles are committed atomically.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerUser(RegisterRequest request) {
        // Duplicate checks
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken");
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setActive(true);

        // Look up role, create it if it doesn't exist yet (first-time setup)
        String roleName = request.getRole() != null ? request.getRole().toUpperCase() : "VOLUNTEER";
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}
