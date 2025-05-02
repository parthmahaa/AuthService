package com.AuthService.auth.services;

import com.AuthService.auth.entities.UserEntity;
import com.AuthService.auth.repo.UserRepo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final UserEntity user;

    public CustomUserDetails(UserEntity user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        System.out.println("Roles: " + user.getRoles());
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Change as needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Change as needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Change as needed
    }

    @Override
    public boolean isEnabled() {
        return true; // Change as needed (e.g., user.isVerified())
    }

    // Custom method to expose the original User object if needed
    public UserEntity getUser() {
        return user;
    }
}
