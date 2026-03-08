package com.cherrytwins.shop.users.web.dto;

import com.cherrytwins.shop.users.domain.UserRole;

import java.time.OffsetDateTime;

public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private UserRole role;
    private boolean active;
    private boolean emailVerified;
    private OffsetDateTime createdAt;

    public UserResponse(Long id, String email, String fullName, String phone, UserRole role,
                        boolean active, boolean emailVerified, OffsetDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.active = active;
        this.emailVerified = emailVerified;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public UserRole getRole() { return role; }
    public boolean isActive() { return active; }
    public boolean isEmailVerified() { return emailVerified; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}