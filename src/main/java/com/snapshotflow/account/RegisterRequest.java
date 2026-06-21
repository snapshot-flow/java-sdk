package com.snapshotflow.account;

import com.snapshotflow.internal.Validate;

/** Body for {@code POST /api/auth/register}. Only {@code email} and {@code password} are required. */
public final class RegisterRequest {

    private final String email;
    private final String password;
    private final String userName;
    private final String firstName;
    private final String lastName;
    private final String organizationId;
    private final String languageCode;
    private final String position;
    private final String referralSource;

    private RegisterRequest(Builder b) {
        this.email = b.email;
        this.password = b.password;
        this.userName = b.userName;
        this.firstName = b.firstName;
        this.lastName = b.lastName;
        this.organizationId = b.organizationId;
        this.languageCode = b.languageCode;
        this.position = b.position;
        this.referralSource = b.referralSource;
    }

    public static Builder of(String email, String password) {
        return new Builder(email, password);
    }

    public static final class Builder {
        private final String email;
        private final String password;
        private String userName;
        private String firstName;
        private String lastName;
        private String organizationId;
        private String languageCode;
        private String position;
        private String referralSource;

        private Builder(String email, String password) {
            this.email = Validate.notBlank(email, "email");
            this.password = Validate.notBlank(password, "password");
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder organizationId(String organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public Builder languageCode(String languageCode) {
            this.languageCode = languageCode;
            return this;
        }

        public Builder position(String position) {
            this.position = position;
            return this;
        }

        public Builder referralSource(String referralSource) {
            this.referralSource = referralSource;
            return this;
        }

        public RegisterRequest build() {
            return new RegisterRequest(this);
        }
    }
}
