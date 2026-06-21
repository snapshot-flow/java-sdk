package com.snapshotflow;

import com.snapshotflow.exception.ApiException;
import com.snapshotflow.exception.AuthenticationException;
import com.snapshotflow.exception.NotFoundException;
import com.snapshotflow.exception.QuotaExceededException;
import com.snapshotflow.exception.RateLimitException;
import com.snapshotflow.exception.ValidationException;
import com.snapshotflow.support.Responses;
import com.snapshotflow.support.StubTransport;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorMappingTest {

    private static SnapshotFlowClient client(StubTransport transport) {
        return SnapshotFlowClient.builder().apiKey("k").transport(transport).maxRetries(0).build();
    }

    @Test
    void validationError() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(400,
                "{\"error\":\"VALIDATION_ERROR\",\"details\":{\"url\":[\"url or html is required\"]}}"));
        ValidationException ex = assertThrows(ValidationException.class, () -> client(transport).capture("https://e.com"));
        assertEquals(400, ex.statusCode());
        assertEquals("VALIDATION_ERROR", ex.errorCode());
        assertTrue(ex.details().containsKey("url"));
    }

    @Test
    void authenticationError() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(401,
                "{\"error\":\"UNAUTHORIZED\",\"message\":\"Invalid or revoked API key\"}"));
        AuthenticationException ex = assertThrows(AuthenticationException.class, () -> client(transport).capture("https://e.com"));
        assertEquals(401, ex.statusCode());
    }

    @Test
    void notFoundError() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(404,
                "{\"error\":\"NOT_FOUND\",\"message\":\"Job x not found\"}"));
        assertThrows(NotFoundException.class, () -> client(transport).jobs().get("x"));
    }

    @Test
    void rateLimitErrorExposesRetryAfter() {
        StubTransport transport = new StubTransport().enqueue(Responses.binary(429, "application/json",
                "{\"error\":\"RATE_LIMITED\",\"message\":\"Too many requests\"}".getBytes(java.nio.charset.StandardCharsets.UTF_8),
                Responses.headers("Retry-After", "30")));
        RateLimitException ex = assertThrows(RateLimitException.class, () -> client(transport).capture("https://e.com"));
        assertEquals(429, ex.statusCode());
        assertEquals(Duration.ofSeconds(30), ex.retryAfter().orElse(null));
    }

    @Test
    void quotaExceededError() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(403,
                "{\"error\":\"QUOTA_EXCEEDED\",\"message\":\"out of quota\"}"));
        QuotaExceededException ex = assertThrows(QuotaExceededException.class, () -> client(transport).capture("https://e.com"));
        assertEquals("QUOTA_EXCEEDED", ex.errorCode());
    }

    @Test
    void genericServerErrorIsPlainApiException() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(500,
                "{\"error\":\"INTERNAL_ERROR\",\"message\":\"oops\"}"));
        ApiException ex = assertThrows(ApiException.class, () -> client(transport).capture("https://e.com"));
        assertEquals(ApiException.class, ex.getClass());
        assertEquals(500, ex.statusCode());
        assertEquals("INTERNAL_ERROR", ex.errorCode());
    }
}
