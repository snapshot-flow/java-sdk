package com.snapshotflow;

import com.snapshotflow.support.StubTransport;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientBuilderTest {

    private static SnapshotFlowClient.Builder builder() {
        return SnapshotFlowClient.builder().transport(new StubTransport());
    }

    @Test
    void rejectsNullConnectTimeout() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> builder().connectTimeout(null).build());
        assertTrue(e.getMessage().contains("connectTimeout"));
    }

    @Test
    void rejectsNonPositiveRequestTimeout() {
        assertThrows(IllegalArgumentException.class, () -> builder().requestTimeout(Duration.ZERO).build());
        assertThrows(IllegalArgumentException.class, () -> builder().requestTimeout(Duration.ofSeconds(-1)).build());
    }

    @Test
    void rejectsNegativeRetryBackoffButAllowsZero() {
        assertThrows(IllegalArgumentException.class, () -> builder().retryBackoff(Duration.ofMillis(-1)).build());
        builder().retryBackoff(Duration.ZERO).build();
    }
}
