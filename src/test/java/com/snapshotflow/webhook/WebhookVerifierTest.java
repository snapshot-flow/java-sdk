package com.snapshotflow.webhook;

import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebhookVerifierTest {

    private static final String SECRET = "whsec_test_secret_value_12345";

    private static String sign(String secret, long t, String body) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] digest = mac.doFinal((t + "." + body).getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : digest) {
            hex.append(String.format("%02x", b));
        }
        return "t=" + t + ",sha256=" + hex;
    }

    @Test
    void verifiesValidSignatureAndParsesEvent() throws Exception {
        long now = Instant.now().getEpochSecond();
        String body = "{\"event\":\"screenshot.completed\",\"job_id\":\"job-1\",\"status\":\"done\","
                + "\"result\":{\"url\":\"https://example.com\",\"format\":\"png\",\"storagePath\":\"https://signed\"}}";
        String header = sign(SECRET, now, body);

        WebhookEvent event = new WebhookVerifier(SECRET)
                .verifyAndParse(body.getBytes(StandardCharsets.UTF_8), header);

        assertTrue(event.isCompleted());
        assertEquals("job-1", event.jobId());
        assertEquals("https://signed", event.result().orElseThrow(AssertionError::new).storagePath());
    }

    @Test
    void rejectsTamperedBody() throws Exception {
        long now = Instant.now().getEpochSecond();
        String body = "{\"job_id\":\"job-1\"}";
        String header = sign(SECRET, now, body);
        WebhookVerifier verifier = new WebhookVerifier(SECRET);

        byte[] tampered = "{\"job_id\":\"job-2\"}".getBytes(StandardCharsets.UTF_8);
        assertThrows(WebhookVerificationException.class, () -> verifier.verify(tampered, header));
    }

    @Test
    void rejectsWrongSecret() throws Exception {
        long now = Instant.now().getEpochSecond();
        String body = "{\"job_id\":\"job-1\"}";
        String header = sign(SECRET, now, body);
        assertThrows(WebhookVerificationException.class,
                () -> new WebhookVerifier("whsec_a_different_secret").verify(body.getBytes(StandardCharsets.UTF_8), header));
    }

    @Test
    void rejectsStaleTimestamp() throws Exception {
        long signedAt = 1_700_000_000L;
        String body = "{\"job_id\":\"job-1\"}";
        String header = sign(SECRET, signedAt, body);
        WebhookVerifier verifier = new WebhookVerifier(SECRET);
        // "now" is 10 minutes after the signature, tolerance is 5 minutes.
        Instant now = Instant.ofEpochSecond(signedAt + 600);
        assertThrows(WebhookVerificationException.class,
                () -> verifier.verify(body.getBytes(StandardCharsets.UTF_8), header, Duration.ofMinutes(5), now));
    }

    @Test
    void rejectsMalformedAndMissingHeader() {
        WebhookVerifier verifier = new WebhookVerifier(SECRET);
        byte[] body = "{}".getBytes(StandardCharsets.UTF_8);
        assertThrows(WebhookVerificationException.class, () -> verifier.verify(body, "garbage"));
        assertThrows(WebhookVerificationException.class, () -> verifier.verify(body, null));
        assertThrows(WebhookVerificationException.class, () -> verifier.verify(body, "t=123"));
    }
}
