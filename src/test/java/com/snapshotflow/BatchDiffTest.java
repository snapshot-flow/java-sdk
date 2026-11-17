package com.snapshotflow;

import com.snapshotflow.batch.BatchRequest;
import com.snapshotflow.batch.BatchResponseType;
import com.snapshotflow.batch.BatchResult;
import com.snapshotflow.diff.DiffRequest;
import com.snapshotflow.diff.DiffResult;
import com.snapshotflow.exception.ApiException;
import com.snapshotflow.job.JobStatus;
import com.snapshotflow.job.JobSubmission;
import com.snapshotflow.screenshot.ResponseType;
import com.snapshotflow.screenshot.WaitUntil;
import com.snapshotflow.support.Responses;
import com.snapshotflow.support.StubTransport;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BatchDiffTest {

    private static SnapshotFlowClient client(StubTransport transport) {
        return SnapshotFlowClient.builder().apiKey("k").transport(transport).maxRetries(0).build();
    }

    @Test
    void batchPostsJsonBodyAndSplitsResults() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(200,
                "{\"total\":2,\"results\":["
                        + "{\"url\":\"https://a\",\"format\":\"png\",\"cached\":false,\"image\":\"data:image/png;base64,AQ==\"},"
                        + "{\"url\":\"https://b\",\"error\":\"QUOTA_EXCEEDED\"}]}"));

        BatchResult result = client(transport).batch().capture("https://a", "https://b");

        assertEquals(2, result.total());
        assertEquals(1, result.successes().size());
        assertEquals(1, result.failures().size());
        assertEquals("QUOTA_EXCEEDED", result.failures().get(0).error().orElse(null));
        assertArrayEquals(new byte[]{1}, result.successes().get(0).bytes().orElse(null));

        StubTransport.Recorded request = transport.last();
        assertEquals("POST", request.method);
        assertTrue(request.url.endsWith("/batch"));
        assertTrue(request.bodyAsString().contains("\"urls\""));
        assertEquals("application/json", request.headers.get("Content-Type"));
    }

    @Test
    void batchSerializesNewOptionsAndParsesDownloadUrls() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(200,
                "{\"total\":1,\"results\":["
                        + "{\"url\":\"https://a\",\"format\":\"png\",\"cached\":true,"
                        + "\"downloadUrl\":\"https://cdn.test/a.png\",\"screenshotId\":\"ss-1\"}]}"));

        BatchResult result = client(transport).batch().capture(
                BatchRequest.urls("https://a")
                        .blockAds(true)
                        .blockCookieBanners(true)
                        .waitUntil(WaitUntil.DOM_CONTENT_LOADED)
                        .responseType(BatchResponseType.IMAGE_URLS)
                        .build());

        assertEquals(1, result.total());
        assertEquals("https://cdn.test/a.png", result.successes().get(0).downloadUrl().orElse(null));
        assertEquals("ss-1", result.successes().get(0).screenshotId().orElse(null));

        String body = transport.last().bodyAsString();
        assertTrue(body.contains("\"block_ads\":true"));
        assertTrue(body.contains("\"block_cookie_banners\":true"));
        assertTrue(body.contains("\"wait_until\":\"domcontentloaded\""));
        assertTrue(body.contains("\"response_type\":\"image_urls\""));
    }

    @Test
    void batchSubmitPostsAsyncAndParsesJobSubmission() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(202,
                "{\"jobId\":\"batch-job-1\",\"status\":\"pending\",\"total\":2}"));

        JobSubmission submission = client(transport).batch().submit("https://a", "https://b");

        assertEquals("batch-job-1", submission.jobId());
        assertEquals(JobStatus.PENDING, submission.status());
        assertEquals(2, submission.total().orElse(0));

        StubTransport.Recorded request = transport.last();
        assertEquals("POST", request.method);
        assertTrue(request.url.endsWith("/batch"));
        assertTrue(request.bodyAsString().contains("\"async\":true"));
    }

    @Test
    void batchSubmitDoesNotRetryOn5xx() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(503, "{\"error\":\"INTERNAL_ERROR\"}"));
        SnapshotFlowClient retryingClient = SnapshotFlowClient.builder()
                .apiKey("k")
                .transport(transport)
                .maxRetries(3)
                .retryBackoff(Duration.ofMillis(1))
                .build();

        assertThrows(ApiException.class, () -> retryingClient.batch().submit("https://a"));
        assertEquals(1, transport.callCount());
    }

    @Test
    void diffBase64ReturnsStatsAndImage() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(200,
                "{\"before\":\"https://a\",\"after\":\"https://b\",\"diff_percent\":12.5,"
                        + "\"changed_pixels\":100,\"total_pixels\":800,\"has_changes\":true,"
                        + "\"image\":\"data:image/png;base64,AQ==\"}"));

        DiffResult result = client(transport).diff().compare("https://a", "https://b");

        assertEquals(12.5, result.diffPercent().orElse(0.0));
        assertEquals(100L, result.changedPixels().orElse(0L));
        assertTrue(result.hasChanges().orElse(false));
        assertArrayEquals(new byte[]{1}, result.bytes());
        assertTrue(transport.last().url.contains("response_type=base64"));
    }

    @Test
    void diffImageResponseReturnsBytesOnly() {
        StubTransport transport = new StubTransport().enqueue(Responses.binary(200, "image/png", new byte[]{5, 6}, null));

        DiffResult result = client(transport).diff()
                .compare(DiffRequest.of("https://a", "https://b").responseType(ResponseType.IMAGE).build());

        assertArrayEquals(new byte[]{5, 6}, result.bytes());
        assertFalse(result.diffPercent().isPresent());
        assertTrue(transport.last().url.contains("response_type=image"));
    }

    @Test
    void diffSerializesBlockingAndWaitOptionsAndDoesNotRetry() {
        StubTransport transport = new StubTransport()
                .enqueue(Responses.json(503, "{\"error\":\"INTERNAL_ERROR\"}"))
                .enqueue(Responses.json(200,
                        "{\"before\":\"https://a\",\"after\":\"https://b\",\"diff_percent\":0.0,"
                                + "\"changed_pixels\":0,\"has_changes\":false,\"image\":\"data:image/png;base64,AQ==\"}"));

        SnapshotFlowClient retrying = SnapshotFlowClient.builder()
                .apiKey("k")
                .transport(transport)
                .maxRetries(2)
                .retryBackoff(Duration.ofMillis(1))
                .build();

        assertThrows(ApiException.class, () -> retrying.diff().compare("https://a", "https://b"));
        assertEquals(1, transport.callCount());

        DiffResult result = retrying.diff().compare(
                DiffRequest.of("https://a", "https://b")
                        .blockAds(true)
                        .blockCookieBanners(true)
                        .waitUntil(WaitUntil.DOM_CONTENT_LOADED)
                        .build());

        assertFalse(result.hasChanges().orElse(true));
        String url = transport.last().url;
        assertTrue(url.contains("block_ads=true"));
        assertTrue(url.contains("block_cookie_banners=true"));
        assertTrue(url.contains("wait_until=domcontentloaded"));
    }
}
