package com.snapshotflow;

import com.snapshotflow.job.JobStatus;
import com.snapshotflow.job.JobSubmission;
import com.snapshotflow.screenshot.OutputFormat;
import com.snapshotflow.screenshot.ScreenshotRequest;
import com.snapshotflow.screenshot.ScreenshotResult;
import com.snapshotflow.support.Responses;
import com.snapshotflow.support.StubTransport;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScreenshotApiTest {

    private static SnapshotFlowClient client(StubTransport transport) {
        return SnapshotFlowClient.builder().apiKey("e2e_test").transport(transport).maxRetries(0).build();
    }

    @Test
    void captureReturnsBinaryBytesAndHeaderMetadata() {
        byte[] png = {(byte) 0x89, 'P', 'N', 'G'};
        StubTransport transport = new StubTransport().enqueue(Responses.binary(200, "image/png", png,
                Responses.headers("X-Cache", "MISS", "X-Screenshot-Id", "ss-1", "ETag", "etag-1")));

        ScreenshotResult result = client(transport).capture("https://example.com");

        assertArrayEquals(png, result.bytes());
        assertFalse(result.cached());
        assertEquals("ss-1", result.screenshotId().orElse(null));
        assertEquals("etag-1", result.etag().orElse(null));

        StubTransport.Recorded request = transport.last();
        assertEquals("GET", request.method);
        assertTrue(request.url.contains("/screenshot?"));
        assertTrue(request.url.contains("url=https%3A%2F%2Fexample.com"));
        assertTrue(request.url.contains("response_type=image"));
        assertTrue(request.url.contains("async=false"));
        assertEquals("e2e_test", request.headers.get("X-Api-Key"));
    }

    @Test
    void captureWithMetadataUsesBase64AndDecodes() {
        String image64 = Base64.getEncoder().encodeToString(new byte[]{1, 2, 3});
        String body = "{\"image\":\"data:image/png;base64," + image64 + "\",\"format\":\"png\","
                + "\"width\":800,\"height\":600,\"cached\":false,"
                + "\"metadata\":{\"title\":\"Hi\",\"http_status\":200}}";
        StubTransport transport = new StubTransport().enqueue(Responses.json(200, body));

        ScreenshotRequest request = ScreenshotRequest.url("https://e.com").metadata(true).build();
        ScreenshotResult result = client(transport).capture(request);

        assertArrayEquals(new byte[]{1, 2, 3}, result.bytes());
        assertEquals(OutputFormat.PNG, result.format());
        assertEquals("Hi", result.metadata().orElseThrow(AssertionError::new).title());
        assertEquals(200, result.metadata().get().httpStatus());
        assertTrue(transport.last().url.contains("response_type=base64"));
        assertTrue(transport.last().url.contains("metadata=true"));
    }

    @Test
    void submitReturnsJobAndMarksAsync() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(202,
                "{\"job_id\":\"job-9\",\"status\":\"processing\",\"external_identifier\":\"order_42\"}"));

        JobSubmission submission = client(transport).screenshots()
                .submit(ScreenshotRequest.url("https://e.com").webhookUrl("https://hook.test/x").build());

        assertEquals("job-9", submission.jobId());
        assertEquals(JobStatus.PROCESSING, submission.status());
        assertEquals("order_42", submission.externalIdentifier().orElse(null));
        assertTrue(transport.last().url.contains("async=true"));
        assertTrue(transport.last().url.contains("webhook_url=https%3A%2F%2Fhook.test%2Fx"));
    }

    @Test
    void htmlIsBase64EncodedOnTheWire() {
        StubTransport transport = new StubTransport().enqueue(Responses.binary(200, "image/png", new byte[]{9}, null));

        client(transport).capture(ScreenshotRequest.html("<h1>Hi</h1>").build());

        String expected = Base64.getEncoder().encodeToString("<h1>Hi</h1>".getBytes(StandardCharsets.UTF_8));
        assertTrue(transport.last().url.contains("html=" + URLEncoder.encode(expected, StandardCharsets.UTF_8)));
    }
}
