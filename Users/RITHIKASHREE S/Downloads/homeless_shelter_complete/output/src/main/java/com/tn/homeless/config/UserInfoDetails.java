package com.tn.homeless.config;

import com.tn.homeless.entity.Role;
import com.tn.homeless.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wraps our User entity as a Spring Security UserDetails object.
 *
 * CHANGES from original:
 *  - Roles are now prefixed with "ROLE_" (Spring Security standard).
 *    Original had bare role names like "ADMIN"; Spring @PreAuthorize("hasRole('ADMIN')")
 *    internally prepends "ROLE_", so without this fix all role checks silently fail.
 *  - Added isEnabled() check using User.isActive().
 */
public class UserInfoDetails implements UserDetails {

    private final String username;
    private final String password;
    private final boolean active;
    private final List<GrantedAuthority> authorities;

    public UserInfoDetails(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.active = user.isActive();
        this.authorities = user.getRoles().stream()
                .map(Role::getName)
                .map(name -> new SimpleGrantedAuthority("ROLE_" + name))  // FIX: prefix ROLE_
                .collect(Collectors.toList());
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return active; }
}
