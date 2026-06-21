package com.snapshotflow.webhook;

import com.snapshotflow.internal.Json;
import com.snapshotflow.internal.Validate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;

/**
 * Verifies the HMAC-SHA256 signature SnapshotFlow attaches to webhook
 * deliveries, so a receiver can trust the payload really came from us.
 *
 * <p>The {@code X-SnapshotFlow-Signature} header has the form
 * {@code t=<unix-seconds>,sha256=<hex>}, where the hex is
 * {@code HMAC-SHA256(secret, "<t>.<raw-body>")}. Always verify against the
 * <strong>raw request bytes</strong> — parsing and re-serializing JSON changes
 * the bytes and the HMAC will not match.
 *
 * <pre>{@code
 * WebhookVerifier verifier = new WebhookVerifier(System.getenv("SNAPSHOTFLOW_WEBHOOK_SECRET"));
 *
 * // In your HTTP handler, with the raw body bytes and the signature header:
 * WebhookEvent event = verifier.verifyAndParse(rawBody, signatureHeader);
 * if (event.isCompleted()) {
 *     String url = event.result().get().storagePath();
 *     // ... dedupe by event.jobId() and process ...
 * }
 * }</pre>
 *
 * Both the freshness check (default 5-minute tolerance) and the constant-time
 * HMAC comparison are applied; any failure throws {@link WebhookVerificationException}.
 */
public final class WebhookVerifier {

    /** The signature header name the API sends. */
    public static final String SIGNATURE_HEADER = "X-SnapshotFlow-Signature";

    private static final Duration DEFAULT_TOLERANCE = Duration.ofMinutes(5);
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final byte[] secret;

    public WebhookVerifier(String signingSecret) {
        this.secret = Validate.notBlank(signingSecret, "signingSecret").getBytes(StandardCharsets.UTF_8);
    }

    /** Verify the signature and deserialize the payload. Throws if verification fails. */
    public WebhookEvent verifyAndParse(byte[] rawBody, String signatureHeader) {
        verify(rawBody, signatureHeader, DEFAULT_TOLERANCE, Instant.now());
        return Json.read(rawBody, WebhookEvent.class);
    }

    /** Convenience overload taking the body as a UTF-8 string. Prefer the {@code byte[]} form. */
    public WebhookEvent verifyAndParse(String rawBody, String signatureHeader) {
        return verifyAndParse(rawBody.getBytes(StandardCharsets.UTF_8), signatureHeader);
    }

    /** Verify only (no deserialization), with the default 5-minute tolerance. */
    public void verify(byte[] rawBody, String signatureHeader) {
        verify(rawBody, signatureHeader, DEFAULT_TOLERANCE, Instant.now());
    }

    /** Verify with a custom freshness tolerance. */
    public void verify(byte[] rawBody, String signatureHeader, Duration tolerance) {
        verify(rawBody, signatureHeader, tolerance, Instant.now());
    }

    // Visible for testing — lets tests pin "now" against a fixed signature timestamp.
    void verify(byte[] rawBody, String signatureHeader, Duration tolerance, Instant now) {
        Validate.notNull(rawBody, "rawBody");
        if (signatureHeader == null || signatureHeader.trim().isEmpty()) {
            throw new WebhookVerificationException("Missing " + SIGNATURE_HEADER + " header");
        }
        ParsedSignature parsed = parseHeader(signatureHeader);
        long skewSeconds = Math.abs(now.getEpochSecond() - parsed.timestamp);
        if (skewSeconds > tolerance.getSeconds()) {
            throw new WebhookVerificationException("Signature timestamp is " + skewSeconds
                    + "s away — outside the " + tolerance.getSeconds() + "s tolerance (replay or clock skew)");
        }
        byte[] expected = hmac(parsed.timestamp, rawBody);
        byte[] received = hexDecode(parsed.sha256);
        if (received == null || !MessageDigest.isEqual(expected, received)) {
            throw new WebhookVerificationException("Signature does not match");
        }
    }

    private byte[] hmac(long timestamp, byte[] rawBody) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            mac.update((timestamp + ".").getBytes(StandardCharsets.UTF_8));
            mac.update(rawBody);
            return mac.doFinal();
        } catch (GeneralSecurityException e) {
            throw new WebhookVerificationException("Failed to compute HMAC: " + e.getMessage());
        }
    }

    private static ParsedSignature parseHeader(String header) {
        Long timestamp = null;
        String sha256 = null;
        for (String part : header.split(",")) {
            int eq = part.indexOf('=');
            if (eq <= 0) {
                continue;
            }
            String key = part.substring(0, eq).trim();
            String value = part.substring(eq + 1).trim();
            if ("t".equals(key)) {
                try {
                    timestamp = Long.parseLong(value);
                } catch (NumberFormatException ignored) {
                    // leave null → malformed
                }
            } else if ("sha256".equals(key)) {
                sha256 = value;
            }
        }
        if (timestamp == null || sha256 == null || sha256.isEmpty()) {
            throw new WebhookVerificationException("Malformed signature header: " + header);
        }
        return new ParsedSignature(timestamp, sha256);
    }

    private static byte[] hexDecode(String hex) {
        int len = hex.length();
        if (len % 2 != 0) {
            return null;
        }
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int hi = Character.digit(hex.charAt(i), 16);
            int lo = Character.digit(hex.charAt(i + 1), 16);
            if (hi < 0 || lo < 0) {
                return null;
            }
            out[i / 2] = (byte) ((hi << 4) | lo);
        }
        return out;
    }

    private static final class ParsedSignature {
        final long timestamp;
        final String sha256;

        ParsedSignature(long timestamp, String sha256) {
            this.timestamp = timestamp;
            this.sha256 = sha256;
        }
    }
}
