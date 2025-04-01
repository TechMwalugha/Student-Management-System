package com.example.backend.dtos;

import com.example.backend.entities.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

public class UserResponseDto {
    private String fullName;
    private String email;
    private boolean accountNonLocked;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private List<String> roles; // If you have roles, map them here

    // Constructor
    public UserResponseDto(User user) {
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.accountNonLocked = user.isAccountNonLocked();
        this.enabled = user.isEnabled();
        this.accountNonExpired = user.isAccountNonExpired();
        this.credentialsNonExpired = user.isCredentialsNonExpired();
        this.roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    // Getters
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public boolean isAccountNonLocked() { return accountNonLocked; }
    public boolean isEnabled() { return enabled; }
    public boolean isAccountNonExpired() { return accountNonExpired; }
    public boolean isCredentialsNonExpired() { return credentialsNonExpired; }
    public List<String> getRoles() { return roles; }
}

