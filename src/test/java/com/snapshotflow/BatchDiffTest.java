package com.snapshotflow;

import com.snapshotflow.batch.BatchResult;
import com.snapshotflow.diff.DiffRequest;
import com.snapshotflow.diff.DiffResult;
import com.snapshotflow.screenshot.ResponseType;
import com.snapshotflow.support.Responses;
import com.snapshotflow.support.StubTransport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
}
