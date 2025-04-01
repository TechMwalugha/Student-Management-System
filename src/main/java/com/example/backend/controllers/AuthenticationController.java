package com.example.backend.controllers;

import com.example.backend.dtos.LoginUserDto;
import com.example.backend.dtos.RegisterUserDto;
import com.example.backend.dtos.UserResponseDto;
import com.example.backend.entities.User;
import com.example.backend.entities.UserSessions;
import com.example.backend.instances.LoginResponse;
import com.example.backend.repositories.UserRepository;
import com.example.backend.repositories.UserSessionsRepository;
import com.example.backend.services.AuthenticationService;
import com.example.backend.services.JwtService;
import com.example.backend.services.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    private final UserService userService;

    private final UserSessionsRepository userSessionsRepository;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, UserService userService, UserSessionsRepository userSessionsRepository) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.userSessionsRepository = userSessionsRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticate(@RequestBody LoginUserDto loginUserDto, HttpServletResponse httpServletResponse) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);
        String refreshToken = jwtService.generateRefreshToken(authenticatedUser);

        // Check if an active session already exists
        Optional<UserSessions> existingSession = userSessionsRepository.findByUser(authenticatedUser);

        UserSessions session;
        if (existingSession.isPresent()) {
            // Update existing session instead of inserting a new one
            session = existingSession.get();
        } else {
            // Create a new session
            session = new UserSessions();
            session.setUser(authenticatedUser);
        }

        session.setAccessToken(jwtToken);
        session.setRefreshToken(refreshToken);
        session.setLoggedIn(true);

        userSessionsRepository.save(session);  // Save or update session

        User userDetails = userService.getUserDetailsByEmail(authenticatedUser.getEmail())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        UserResponseDto userResponse = new UserResponseDto(userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged In successfully");
        response.put("token", jwtToken);
        response.put("refreshToken", refreshToken);
        response.put("maxAge", jwtService.getExpirationTime());
        response.put("user", userResponse);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAuth(HttpServletRequest request) {
        String token = authenticationService.extractToken(request);

        if (token == null) {
            return ResponseEntity.ok(Map.of("authenticated", false, "expired", false));
        }

        Optional<UserSessions> session = userSessionsRepository.findByAccessToken(token);

        if (session.isEmpty() || !session.get().isLoggedIn()) {
            return ResponseEntity.ok(Map.of("authenticated", false, "expired", false));
        }


            String email = jwtService.extractUsername(token);
            Optional<User> userDetails = userService.getUserDetailsByEmail(email);

            if (userDetails.isPresent() && jwtService.isTokenValid(token, userDetails.get())) {
                return ResponseEntity.ok(Map.of("authenticated", true, "expired", false));
            }

        return ResponseEntity.ok(Map.of("authenticated", false, "expired", false));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authHeader, HttpServletResponse response) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid token"));
        }

        String token = authHeader.substring(7);
        userSessionsRepository.findByAccessToken(token).ifPresent(session -> {
            session.setLoggedIn(false);
            userSessionsRepository.save(session);
        });

        return ResponseEntity.ok(Collections.singletonMap("message", "Logged Out successfully"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestParam String refreshToken) {
        Optional<UserSessions> session = userSessionsRepository.findByRefreshToken(refreshToken);

        if (session.isPresent() && session.get().isLoggedIn()) {
            User user = session.get().getUser();
            String newAccessToken = jwtService.generateToken(user);
            session.get().setAccessToken(newAccessToken);
            userSessionsRepository.save(session.get());

            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("refreshToken", refreshToken);

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(403).body(Collections.singletonMap("error", "Invalid refresh token"));
    }



}