package com.kpo.restaurant_project.domain.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "sessions")
@Accessors(chain = true)
public class Session {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "session_token")
    private String sessionToken;

    @Column(name = "expires_at")
    private Timestamp expiresAt;

    public Session(User user, String sessionToken, Timestamp expiresAt) {
        this.user = user;
        this.sessionToken = sessionToken;
        this.expiresAt = expiresAt;
    }
}
