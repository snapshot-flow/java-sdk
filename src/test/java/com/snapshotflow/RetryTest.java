package com.snapshotflow;

import com.snapshotflow.exception.ApiException;
import com.snapshotflow.exception.NetworkException;
import com.snapshotflow.screenshot.ScreenshotRequest;
import com.snapshotflow.support.Responses;
import com.snapshotflow.support.StubTransport;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RetryTest {

    private static SnapshotFlowClient client(StubTransport transport, int maxRetries) {
        return SnapshotFlowClient.builder()
                .apiKey("k")
                .transport(transport)
                .maxRetries(maxRetries)
                .retryBackoff(Duration.ofMillis(1))
                .build();
    }

    @Test
    void retriesIdempotentGetOnTransientStatus() {
        StubTransport transport = new StubTransport()
                .enqueue(Responses.json(503, "{\"error\":\"INTERNAL_ERROR\"}"))
                .enqueue(Responses.binary(200, "image/png", new byte[]{1}, null));

        byte[] bytes = client(transport, 2).capture("https://e.com").bytes();

        assertEquals(1, bytes.length);
        assertEquals(2, transport.callCount());
    }

    @Test
    void retriesOnNetworkError() {
        StubTransport transport = new StubTransport()
                .enqueueIoError()
                .enqueue(Responses.binary(200, "image/png", new byte[]{1}, null));

        client(transport, 1).capture("https://e.com");

        assertEquals(2, transport.callCount());
    }

    @Test
    void exhaustsRetriesThenThrows() {
        StubTransport transport = new StubTransport()
                .enqueueIoError()
                .enqueueIoError();

        assertThrows(NetworkException.class, () -> client(transport, 1).capture("https://e.com"));
        assertEquals(2, transport.callCount());
    }

    @Test
    void doesNotRetryNonIdempotentSubmit() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(503, "{\"error\":\"INTERNAL_ERROR\"}"));

        assertThrows(ApiException.class,
                () -> client(transport, 3).screenshots().submit(ScreenshotRequest.url("https://e.com").build()));
        assertEquals(1, transport.callCount());
    }

    @Test
    void maxRetriesZeroMeansSingleAttempt() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(503, "{\"error\":\"INTERNAL_ERROR\"}"));

        assertThrows(ApiException.class, () -> client(transport, 0).capture("https://e.com"));
        assertEquals(1, transport.callCount());
    }
}
