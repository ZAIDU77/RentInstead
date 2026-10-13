package com.rentinstead.model;

import java.time.LocalDateTime;

public class User {

    private long userId;
    private String fullName;
    private String phone;
    private String passwordHash;
    private String role;
    private String accountStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(String fullName, String phone, String passwordHash) {
        this.fullName = fullName;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.role = "USER";
        this.accountStatus = "ACTIVE";
    }

    public long getUserId() { return userId; }

    public void setUserId(long userId) {
        if (userId < 0) {
            throw new IllegalArgumentException("User ID cannot be negative.");
        }
        this.userId = userId;
    }

    public String getFullName() { return fullName; }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setName(String name) {
        this.fullName = name;
    }

    public String getPhone() { return phone; }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswordHash() { return passwordHash; }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() { return role; }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAccountStatus() { return accountStatus; }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
