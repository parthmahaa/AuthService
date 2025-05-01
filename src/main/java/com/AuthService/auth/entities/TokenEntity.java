package com.AuthService.auth.entities;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Date;

@Entity
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Table(name = "tokens")
public class TokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String token;

    private String username;

    private Date expiryDate;
    @OneToOne
    @JoinColumn(
            name = "id" ,referencedColumnName = "user_id"
    )
    private UserEntity userInfo;
}
