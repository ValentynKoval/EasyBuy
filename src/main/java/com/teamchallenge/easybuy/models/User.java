package com.teamchallenge.easybuy.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password")
    private String password;

    @Column(name = "phone_number", length = 11)
    private String phoneNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "is_email_verified")
    private boolean isEmailVerified;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "location_latitude", precision = 8, scale = 6)
    private Double locationLatitude;

    @Column(name = "location_longitude", precision = 8, scale = 6)
    private Double locationLongitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @PrePersist
    private void init() {
        createdAt = LocalDateTime.now();
    }
}
