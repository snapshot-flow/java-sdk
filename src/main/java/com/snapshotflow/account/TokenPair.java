package com.snapshotflow.account;

/** Access + refresh token pair returned by login and refresh. */
public final class TokenPair {

    private String token;
    private String refreshToken;

    /** The JWT access token — use it as a Bearer credential. */
    public String token() {
        return token;
    }

    /** Opaque refresh token — exchange it via {@code auth().refresh(...)} for a new pair. */
    public String refreshToken() {
        return refreshToken;
    }

    @Override
    public String toString() {
        return "TokenPair{token=***, refreshToken=***}";
    }
}
