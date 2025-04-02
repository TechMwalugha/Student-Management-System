package com.example.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "user_sessions")
@Entity
@Getter
@Setter
public class UserSessions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean isLoggedIn;

    @Column(unique = true)
    private String accessToken;

    @Column()
    private String refreshToken;
}
