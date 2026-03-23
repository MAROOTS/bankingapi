package com.example.banking.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private String id;
    @Column(nullable = false)
    private String fullName;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        if (role == null) role = Role.CUSTOMER;
    }

    public enum Role{
        CUSTOMER, ADMIN
    }
}
