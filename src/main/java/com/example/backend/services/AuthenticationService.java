package com.example.backend.services;

import com.example.backend.dtos.LoginUserDto;
import com.example.backend.dtos.RegisterUserDto;
import com.example.backend.entities.User;
import com.example.backend.entities.UserSessions;
import com.example.backend.repositories.UserRepository;
import com.example.backend.repositories.UserSessionsRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final UserSessionsRepository userSessionsRepository;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            UserSessionsRepository userSessionsRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSessionsRepository = userSessionsRepository;
    }

    public User signup(RegisterUserDto input) {
        User user = new User();
        user.setFullName(input.getFullName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }

    public String extractToken(HttpServletRequest request) {
        // Extract token from Authorization
        String jwt = null;

        final String authHeader = request.getHeader("Authorization");

        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        }

        return jwt;
    }

    public void logout(String token) {
        Optional<UserSessions> session = Optional.of(userSessionsRepository.findByAccessToken(token)
                .orElseThrow());


            userSessionsRepository.delete(session.get());
            SecurityContextHolder.clearContext();

    }



}
