package com.anurag.library.dto;

import java.time.LocalDateTime;

public class LoginAttemptView {
    private Long id;
    private String email;
    private String ipAddress;
    private boolean success;
    private boolean flagged;
    private String reason;
    private LocalDateTime attemptedAt;

    public LoginAttemptView(Long id, String email, String ipAddress, boolean success, boolean flagged, String reason, LocalDateTime attemptedAt) {
        this.id = id; this.email = email; this.ipAddress = ipAddress; this.success = success;
        this.flagged = flagged; this.reason = reason; this.attemptedAt = attemptedAt;
    }
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getIpAddress() { return ipAddress; }
    public boolean isSuccess() { return success; }
    public boolean isFlagged() { return flagged; }
    public String getReason() { return reason; }
    public LocalDateTime getAttemptedAt() { return attemptedAt; }
}
