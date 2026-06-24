# SnapshotFlow Java SDK

Official Java client for the [SnapshotFlow](https://snapshotflow.com) screenshot API — capture website screenshots and PDFs, run batch captures and visual diffs, submit async jobs with webhooks, browse your screenshot history, manage API keys, and verify inbound webhook signatures.

- **Zero HTTP dependencies** — built on the JDK's `HttpURLConnection` (Java 8+). The only runtime dependency is Jackson for JSON.
- **Typed, fluent, immutable** — builders for requests, typed results, a precise exception hierarchy.
- **Thread-safe** — build one client and share it across your app.

> Requires **Java 8 or newer**.

---

## Installation

**Maven**

```xml
<dependency>
  <groupId>com.snapshotflow</groupId>
  <artifactId>snapshotflow-java</artifactId>
  <version>1.0.0</version>
</dependency>
```

**Gradle**

```kotlin
implementation("com.snapshotflow:snapshotflow-java:1.0.0")
```

---

## Quick start

```java
import com.snapshotflow.SnapshotFlowClient;
import com.snapshotflow.screenshot.ScreenshotResult;
import java.nio.file.Path;

SnapshotFlowClient client = SnapshotFlowClient.create("e2e_your_api_key");

ScreenshotResult shot = client.capture("https://example.com");
shot.writeTo(Path.of("example.png"));

System.out.println("cached? " + shot.cached() + ", bytes=" + shot.bytes().length);
```

The client is immutable and thread-safe — create it once (e.g. a singleton / Spring bean) and reuse it.

---

## Authentication

SnapshotFlow accepts two credential types:

- **API key** (`e2e_...`) — for capture operations. `X-Api-Key` header.
- **Bearer JWT** — for account management (`auth().me()`, `apiKeys()`, `webhookSecrets()`), obtained via `auth().login(...)`.

```java
// API key
SnapshotFlowClient client = SnapshotFlowClient.builder()
        .apiKey("e2e_your_api_key")
        .build();

// Bearer token
SnapshotFlowClient jwtClient = SnapshotFlowClient.builder()
        .bearerToken(accessToken)
        .build();

// Derive a differently-authenticated view sharing the same transport (cheap):
SnapshotFlowClient asJwt = client.withBearerToken(accessToken);
```

If the server runs with auth disabled, build with no credential.

---

## Capturing screenshots

All ~50 capture parameters are available through `ScreenshotRequest`. Only the
values you set are sent; the server applies its own defaults for the rest.

```java
import com.snapshotflow.screenshot.*;

ScreenshotRequest request = ScreenshotRequest.url("https://example.com")
        .viewport(1440, 900)
        .fullPage(true)
        .format(OutputFormat.JPEG)
        .quality(90)
        .darkMode(true)
        .blockAds(true)
        .blockCookieBanners(true)
        .waitUntil(WaitUntil.NETWORK_IDLE_2)
        .delay(500)
        .build();

ScreenshotResult shot = client.capture(request);
```

**Render raw HTML** (base64-encoded for you on the wire):

```java
ScreenshotResult shot = client.capture(
        ScreenshotRequest.html("<h1>Hello</h1>").viewport(600, 400).build());
```

**Capture a PDF:**

```java
ScreenshotResult pdf = client.capture(
        ScreenshotRequest.url("https://example.com")
                .format(OutputFormat.PDF)
                .pdfPaperFormat(PdfPaperFormat.A4)
                .pdfPrintBackground(true)
                .build());
pdf.writeTo(Path.of("example.pdf"));
```

**Metadata and content extraction.** When you request metadata or extracted
content, the SDK transparently fetches them alongside the image bytes:

```java
ScreenshotResult shot = client.capture(
        ScreenshotRequest.url("https://example.com")
                .metadata(true)
                .extractContent(true)
                .contentFormat(ContentFormat.MARKDOWN)
                .build());

shot.metadata().ifPresent(m -> System.out.println(m.title() + " / " + m.httpStatus()));
shot.content().ifPresent(System.out::println);
```

---

## Async jobs and webhooks

Submit a job, then either poll or receive a webhook:

```java
import com.snapshotflow.job.*;
import java.time.Duration;

JobSubmission submission = client.screenshots().submit(
        ScreenshotRequest.url("https://example.com")
                .webhookUrl("https://your.app/webhooks/snapshotflow")  // optional
                .externalIdentifier("order_42")                         // optional correlation id
                .build());

// Poll until done (or supply a webhook and skip this):
Job job = client.jobs().await(submission.jobId(), Duration.ofMinutes(2));
if (job.isDone()) {
    String url = job.result().get().storagePath().orElseThrow();
    // download the signed URL...
} else {
    System.err.println("failed: " + job.errorCode().orElse("unknown"));
}
```

---

## Verifying incoming webhooks

If you receive SnapshotFlow webhooks in a Java service, `WebhookVerifier` checks
the HMAC-SHA256 signature and timestamp freshness, then parses the payload.
**Always verify against the raw request bytes** — re-serialized JSON will not match.

```java
import com.snapshotflow.webhook.*;

WebhookVerifier verifier = new WebhookVerifier(System.getenv("SNAPSHOTFLOW_WEBHOOK_SECRET"));

// In your HTTP handler (raw body bytes + the X-SnapshotFlow-Signature header):
try {
    WebhookEvent event = verifier.verifyAndParse(rawBody, signatureHeader);
    if (event.isCompleted()) {
        // Dedupe by event.jobId() — retries reuse the same id.
        event.result().ifPresent(r -> process(r.storagePath()));
    }
    respond(200);
} catch (WebhookVerificationException e) {
    respond(401);   // reject: bad signature, stale timestamp, or tampering
}
```

Generate/rotate the signing secret with `client.webhookSecrets().create()` (requires JWT).

---

## Batch capture

Up to 10 URLs in one request, sharing options:

```java
import com.snapshotflow.batch.*;

BatchResult result = client.batch().capture(
        BatchRequest.urls("https://a.com", "https://b.com", "https://c.com")
                .format(OutputFormat.PNG)
                .fullPage(true)
                .responseType(BatchResponseType.BASE64)
                .build());

System.out.println(result.successes().size() + " ok, " + result.failures().size() + " failed");
result.successes().forEach(item -> item.bytes().ifPresent(bytes -> { /* save */ }));
```

---

## Visual diff

```java
import com.snapshotflow.diff.*;

DiffResult diff = client.diff().compare("https://example.com", "https://staging.example.com");
diff.diffPercent().ifPresent(p -> System.out.println("changed: " + p + "%"));
diff.writeTo(Path.of("diff.png"));   // the rendered diff image
```

---

## Screenshot history

For authenticated users with history enabled:

```java
import com.snapshotflow.history.*;

ScreenshotPage page = client.history().list(50, 0);
page.items().forEach(s -> System.out.println(s.screenshotId() + " " + s.sourceUrl().orElse("(html)")));

ScreenshotDetail detail = client.history().get(page.items().get(0).screenshotId());
System.out.println(detail.downloadUrl());     // fresh signed URL

client.history().delete(detail.screenshotId());
```

---

## Account, API keys & webhook secrets

```java
import com.snapshotflow.account.*;

// Log in to get tokens
TokenPair tokens = client.auth().login("you@example.com", "password");
SnapshotFlowClient jwt = client.withBearerToken(tokens.token());

// Profile + quota
UserProfile me = jwt.auth().me();
System.out.println("quota remaining: " + me.quota().remaining());

// API keys (JWT only)
CreatedApiKey key = jwt.apiKeys().create("ci-server");
System.out.println("save this once: " + key.key());
jwt.apiKeys().list();
jwt.apiKeys().rotate(key.apiKeyId());

// Webhook signing secret (JWT only)
CreatedWebhookSecret secret = jwt.webhookSecrets().create();
System.out.println("save this once: " + secret.secret());
```

---

## Error handling

Every failure is an unchecked `SnapshotFlowException`. Catch the base type, or a
specific subtype:

| Exception | When |
|---|---|
| `ValidationException` | 400 — invalid parameters / URL |
| `AuthenticationException` | 401 / 403 — missing, invalid or insufficient credentials |
| `NotFoundException` | 404 — unknown job / screenshot / key id |
| `RateLimitException` | 429 — rate limited (exposes `retryAfter()`) |
| `QuotaExceededException` | quota exhausted |
| `ApiException` | any other non-2xx (`statusCode()`, `errorCode()`, `details()`) |
| `NetworkException` | no response (timeout, connection error, interrupt) |

```java
try {
    client.capture("https://example.com");
} catch (RateLimitException e) {
    e.retryAfter().ifPresent(d -> sleep(d));
} catch (ApiException e) {
    System.err.println(e.statusCode() + " " + e.errorCode() + ": " + e.getMessage());
} catch (NetworkException e) {
    // transport-level failure
}
```

---

## Configuration

```java
SnapshotFlowClient client = SnapshotFlowClient.builder()
        .apiKey("e2e_your_api_key")
        .baseUrl("https://api.snapshotflow.com")   // override for self-hosting
        .connectTimeout(Duration.ofSeconds(10))
        .requestTimeout(Duration.ofSeconds(60))
        .maxRetries(2)                              // idempotent GETs only; 0 disables
        .retryBackoff(Duration.ofMillis(500))
        .build();
```

Idempotent requests (GETs) are retried on network errors and `408/429/5xx`,
honoring `Retry-After`. Async job submission is never auto-retried (a retry
would create a duplicate job).

---

## Scope

This SDK covers the full public API: `/screenshot` (sync + async), `/batch`,
`/diff`, `/jobs/:id`, `/screenshots` history, `/api/auth/*` (sessions, API keys,
webhook secrets), `/health`, and client-side webhook signature verification.

The server's `/.well-known/*`, `/oauth/*` and `/mcp` endpoints are internal
infrastructure for the Claude.ai MCP integration and are intentionally not
exposed here.

---

## License

[MIT](LICENSE)
