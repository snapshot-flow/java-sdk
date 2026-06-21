package com.snapshotflow.account;

import com.snapshotflow.internal.Validate;

/** Body for {@code POST /api/auth/login}. */
public final class LoginRequest {

    private final String email;
    private final String password;
    private final String organizationId;

    private LoginRequest(Builder b) {
        this.email = b.email;
        this.password = b.password;
        this.organizationId = b.organizationId;
    }

    public static LoginRequest of(String email, String password) {
        return new Builder(email, password).build();
    }

    public static Builder builder(String email, String password) {
        return new Builder(email, password);
    }

    public static final class Builder {
        private final String email;
        private final String password;
        private String organizationId;

        private Builder(String email, String password) {
            this.email = Validate.notBlank(email, "email");
            this.password = Validate.notBlank(password, "password");
        }

        public Builder organizationId(String organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public LoginRequest build() {
            return new LoginRequest(this);
        }
    }
}
