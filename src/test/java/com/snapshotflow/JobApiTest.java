package com.snapshotflow;

import com.snapshotflow.job.Job;
import com.snapshotflow.support.Responses;
import com.snapshotflow.support.StubTransport;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobApiTest {

    private static SnapshotFlowClient client(StubTransport transport) {
        return SnapshotFlowClient.builder().apiKey("k").transport(transport).maxRetries(0).build();
    }

    @Test
    void awaitPollsUntilTerminal() {
        StubTransport transport = new StubTransport()
                .enqueue(Responses.json(200, "{\"id\":\"j1\",\"status\":\"processing\",\"createdAt\":\"t0\"}"))
                .enqueue(Responses.json(200, "{\"id\":\"j1\",\"status\":\"done\",\"createdAt\":\"t0\","
                        + "\"result\":{\"url\":\"https://a\",\"format\":\"png\",\"storagePath\":\"https://signed\",\"sizeBytes\":1234}}"));

        Job job = client(transport).jobs().await("j1", Duration.ofSeconds(5), Duration.ofMillis(1));

        assertTrue(job.isDone());
        assertEquals("https://signed", job.result().orElseThrow(AssertionError::new).storagePath().orElse(null));
        assertEquals(1234L, job.result().get().sizeBytes().orElse(0L));
        assertEquals(2, transport.callCount());
        assertTrue(transport.last().url.endsWith("/jobs/j1"));
    }

    @Test
    void getParsesFailedJob() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(200,
                "{\"id\":\"j2\",\"status\":\"failed\",\"createdAt\":\"t0\",\"error\":\"boom\",\"errorCode\":\"TIMEOUT\"}"));

        Job job = client(transport).jobs().get("j2");

        assertTrue(job.isFailed());
        assertEquals("TIMEOUT", job.errorCode().orElse(null));
        assertEquals("boom", job.error().orElse(null));
    }
}
