package com.anurag.library.dto;

import java.time.LocalDateTime;

// Admin-facing view of a student account. Never includes the password hash.
public class StudentView {
    private Long id;
    private String name;
    private String email;
    private String role;
    private boolean locked;
    private LocalDateTime createdAt;

    public StudentView(Long id, String name, String email, String role, boolean locked, LocalDateTime createdAt) {
        this.id = id; this.name = name; this.email = email; this.role = role;
        this.locked = locked; this.createdAt = createdAt;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isLocked() { return locked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
