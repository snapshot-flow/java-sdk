package com.snapshotflow;

import com.snapshotflow.exception.SnapshotFlowException;
import com.snapshotflow.job.Job;
import com.snapshotflow.job.JobKind;
import com.snapshotflow.job.JobProgress;
import com.snapshotflow.screenshot.OutputFormat;
import com.snapshotflow.support.Responses;
import com.snapshotflow.support.StubTransport;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void awaitDoesNotSleepPastTheDeadline() {
        // Answer every poll with "processing", however many polls fit before the deadline.
        StubTransport transport = new StubTransport()
                .handler(request -> Responses.json(200, "{\"id\":\"j3\",\"status\":\"processing\",\"createdAt\":\"t0\"}"));

        long start = System.nanoTime();
        assertThrows(SnapshotFlowException.class,
                () -> client(transport).jobs().await("j3", Duration.ofMillis(100), Duration.ofSeconds(30)));
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

        assertTrue(elapsedMillis < 5_000, "await overshot its timeout: " + elapsedMillis + "ms");
    }

    @Test
    void getParsesBatchJobProgressAndResults() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(200,
                "{\"id\":\"bj-1\",\"kind\":\"batch\",\"status\":\"processing\",\"createdAt\":\"t0\","
                        + "\"startedAt\":\"t1\",\"progress\":{\"completed\":1,\"total\":2},"
                        + "\"batchResults\":[{\"url\":\"https://a\",\"format\":\"png\",\"cached\":false,"
                        + "\"downloadUrl\":\"https://signed-a\",\"screenshotId\":\"ss-a\"}]}"));

        Job job = client(transport).jobs().get("bj-1");

        assertEquals(JobKind.BATCH, job.kind().orElse(null));
        JobProgress progress = job.progress().orElseThrow(AssertionError::new);
        assertEquals(1, progress.completed());
        assertEquals(2, progress.total());
        assertEquals(1, job.batchResults().size());
        assertEquals("https://a", job.batchResults().get(0).url());
        assertEquals(OutputFormat.PNG, job.batchResults().get(0).format().orElse(null));
        assertEquals("https://signed-a", job.batchResults().get(0).downloadUrl().orElse(null));
        assertEquals("ss-a", job.batchResults().get(0).screenshotId().orElse(null));
    }
}
