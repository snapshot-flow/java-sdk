package com.snapshotflow.account;

import com.snapshotflow.internal.ApiInvoker;
import com.snapshotflow.internal.ApiRequest;
import com.snapshotflow.internal.ApiResponse;
import com.snapshotflow.internal.Json;
import com.snapshotflow.internal.QueryParams;
import com.snapshotflow.internal.Validate;

import java.util.Collections;
import java.util.Map;

/** Account and session endpoints under {@code /api/auth}. */
public final class AuthApi {

    private final ApiInvoker invoker;

    public AuthApi(ApiInvoker invoker) {
        this.invoker = invoker;
    }

    /** Register a new account. With email verification enabled the account stays inactive until verified. */
    public void register(RegisterRequest request) {
        Validate.notNull(request, "request");
        invoker.invoke(ApiRequest.post("/api/auth/register").jsonBody(request).build());
    }

    /** Log in and receive an access + refresh token pair. */
    public TokenPair login(LoginRequest request) {
        Validate.notNull(request, "request");
        ApiResponse response = invoker.invoke(ApiRequest.post("/api/auth/login").jsonBody(request).build());
        return Json.read(response.body(), TokenPair.class);
    }

    /** Log in with email and password. */
    public TokenPair login(String email, String password) {
        return login(LoginRequest.of(email, password));
    }

    /** Exchange a refresh token for a fresh access + refresh token pair (the old one is invalidated). */
    public TokenPair refresh(String refreshToken) {
        Validate.notBlank(refreshToken, "refreshToken");
        Map<String, Object> body = Collections.singletonMap("refreshToken", refreshToken);
        ApiResponse response = invoker.invoke(ApiRequest.post("/api/auth/refresh").jsonBody(body).build());
        return Json.read(response.body(), TokenPair.class);
    }

    /** Confirm an email-verification token. */
    public void verifyEmail(String token) {
        Validate.notBlank(token, "token");
        QueryParams query = new QueryParams().add("token", token);
        invoker.invoke(ApiRequest.get("/api/auth/verify-email").query(query).build());
    }

    /** Request a fresh verification email (silently succeeds whether or not the address is registered). */
    public void resendVerification(String email) {
        Validate.notBlank(email, "email");
        Map<String, Object> body = Collections.singletonMap("email", email);
        invoker.invoke(ApiRequest.post("/api/auth/resend-verification").jsonBody(body).build());
    }

    /** Fetch the authenticated user's profile and quota. Requires a Bearer (JWT) credential. */
    public UserProfile me() {
        ApiResponse response = invoker.invoke(ApiRequest.get("/api/auth/me").build());
        return Json.read(response.body(), UserProfile.class);
    }
}
