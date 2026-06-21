package com.snapshotflow.account;

import java.util.Optional;

/** The authenticated user's profile, from {@code GET /api/auth/me}. */
public final class UserProfile {

    private String userId;
    private String email;
    private String userName;
    private String firstName;
    private String lastName;
    private String role;
    private String organizationId;
    private boolean emailVerified;
    private Quota quota;

    public String userId() {
        return userId;
    }

    public String email() {
        return email;
    }

    public Optional<String> userName() {
        return Optional.ofNullable(userName);
    }

    public Optional<String> firstName() {
        return Optional.ofNullable(firstName);
    }

    public Optional<String> lastName() {
        return Optional.ofNullable(lastName);
    }

    /** {@code "USER"} or {@code "ADMIN"}. */
    public String role() {
        return role;
    }

    public Optional<String> organizationId() {
        return Optional.ofNullable(organizationId);
    }

    public boolean emailVerified() {
        return emailVerified;
    }

    public Quota quota() {
        return quota;
    }

    @Override
    public String toString() {
        return "UserProfile{userId=" + userId + ", email=" + email + ", role=" + role + '}';
    }
}
