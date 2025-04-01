package com.example.backend.repositories;

import com.example.backend.entities.User;
import com.example.backend.entities.UserSessions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSessionsRepository extends JpaRepository<UserSessions, Long> {
    Optional<UserSessions> findByAccessToken(String accessToken);
    Optional<UserSessions> findByRefreshToken(String refreshToken);
    Optional<UserSessions> findByUser(User user);
}
