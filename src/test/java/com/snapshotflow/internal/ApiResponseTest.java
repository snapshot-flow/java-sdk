package com.snapshotflow.internal;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiResponseTest {

    private static ApiResponse withRetryAfter(String value) {
        return new ApiResponse(429, Collections.singletonMap("retry-after", value), null);
    }

    @Test
    void parsesDeltaSecondsRetryAfter() {
        assertEquals(Optional.of(Duration.ofSeconds(7)), withRetryAfter(" 7 ").retryAfter());
    }

    @Test
    void clampsNegativeRetryAfterToZero() {
        assertEquals(Optional.of(Duration.ZERO), withRetryAfter("-3").retryAfter());
    }

    @Test
    void ignoresHttpDateAndMissingRetryAfter() {
        assertEquals(Optional.empty(), withRetryAfter("Wed, 21 Oct 2015 07:28:00 GMT").retryAfter());
        assertEquals(Optional.empty(), new ApiResponse(429, null, null).retryAfter());
    }
}
