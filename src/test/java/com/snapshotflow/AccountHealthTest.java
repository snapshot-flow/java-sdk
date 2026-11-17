package com.snapshotflow;

import com.snapshotflow.account.CreatedApiKey;
import com.snapshotflow.account.TokenPair;
import com.snapshotflow.account.UserProfile;
import com.snapshotflow.account.WebhookSecretMetadata;
import com.snapshotflow.extract.ExtractRequest;
import com.snapshotflow.extract.ExtractResult;
import com.snapshotflow.health.HealthStatus;
import com.snapshotflow.history.ScreenshotPage;
import com.snapshotflow.history.ScreenshotSummary;
import com.snapshotflow.screenshot.ContentFormat;
import com.snapshotflow.support.Responses;
import com.snapshotflow.support.StubTransport;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountHealthTest {

    @Test
    void loginParsesTokensAndSendsCredentials() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(200,
                "{\"token\":\"jwt-abc\",\"refreshToken\":\"ref-xyz\"}"));

        TokenPair tokens = SnapshotFlowClient.builder().transport(transport).build()
                .auth().login("a@b.com", "pw");

        assertEquals("jwt-abc", tokens.token());
        assertEquals("ref-xyz", tokens.refreshToken());
        assertTrue(transport.last().url.endsWith("/api/auth/login"));
        assertTrue(transport.last().bodyAsString().contains("\"email\":\"a@b.com\""));
    }

    @Test
    void meParsesProfileAndQuotaWithBearerAuth() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(200,
                "{\"userId\":\"u1\",\"email\":\"a@b.com\",\"role\":\"USER\",\"emailVerified\":true,"
                        + "\"quota\":{\"used\":3,\"limit\":100,\"remaining\":97}}"));

        UserProfile me = SnapshotFlowClient.builder().bearerToken("jwt-tok").transport(transport).build()
                .auth().me();

        assertEquals("u1", me.userId());
        assertTrue(me.emailVerified());
        assertEquals(97, me.quota().remaining());
        assertEquals("Bearer jwt-tok", transport.last().headers.get("Authorization"));
    }

    @Test
    void apiKeyCreateReturnsRawKeyOnce() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(201,
                "{\"apiKeyId\":\"k1\",\"key\":\"e2e_raw_secret\",\"keyPrefix\":\"e2e_abc\",\"label\":\"ci\",\"createdAt\":\"t0\"}"));

        CreatedApiKey created = SnapshotFlowClient.builder().bearerToken("jwt").transport(transport).build()
                .apiKeys().create("ci");

        assertEquals("e2e_raw_secret", created.key());
        assertEquals("ci", created.label().orElse(null));
        assertTrue(transport.last().bodyAsString().contains("\"label\":\"ci\""));
    }

    @Test
    void webhookSecretGetReturnsEmptyWhenNotConfigured() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(404,
                "{\"error\":\"Not Found\",\"message\":\"No webhook secret configured\"}"));

        Optional<WebhookSecretMetadata> meta = SnapshotFlowClient.builder().bearerToken("jwt").transport(transport).build()
                .webhookSecrets().get();

        assertFalse(meta.isPresent());
    }

    @Test
    void healthParsesPoolAndCache() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(200,
                "{\"status\":\"ok\",\"browserPool\":{\"size\":3,\"available\":2},\"cache\":\"redis\"}"));

        HealthStatus health = SnapshotFlowClient.builder().transport(transport).build().health().check();

        assertTrue(health.isOk());
        assertEquals(3, health.browserPool().size());
        assertEquals(2, health.browserPool().available());
        assertEquals("redis", health.cache());
    }

    @Test
    void historyListWithTtlAndLiteParsesDownloadUrls() {
        StubTransport transport = new StubTransport()
                .enqueue(Responses.json(200,
                        "{\"total\":1,\"limit\":10,\"offset\":0,\"items\":[{"
                                + "\"screenshotId\":\"ss-1\",\"sourceUrl\":\"https://a\",\"format\":\"png\","
                                + "\"width\":1280,\"height\":800,\"createdAt\":\"t0\",\"expiresAt\":\"t1\","
                                + "\"downloadUrl\":\"https://signed\",\"downloadUrlExpiresAt\":\"t2\"}]}"))
                .enqueue(Responses.json(200,
                        "{\"total\":1,\"limit\":10,\"offset\":0,\"items\":[{"
                                + "\"screenshotId\":\"ss-1\",\"sourceUrl\":\"https://a\",\"format\":\"png\","
                                + "\"width\":1280,\"height\":800,\"createdAt\":\"t0\",\"expiresAt\":\"t1\","
                                + "\"downloadUrl\":null,\"downloadUrlExpiresAt\":null}]}"));

        SnapshotFlowClient client = SnapshotFlowClient.builder().bearerToken("jwt").transport(transport).build();
        ScreenshotPage page = client.history().list(10, 0, 3600, false);

        ScreenshotSummary item = page.items().get(0);
        assertEquals("https://signed", item.downloadUrl().orElse(null));
        assertEquals("t2", item.downloadUrlExpiresAt().orElse(null));
        String url1 = transport.last().url;
        assertTrue(url1.contains("limit=10"));
        assertTrue(url1.contains("offset=0"));
        assertTrue(url1.contains("ttl=3600"));
        assertTrue(url1.contains("lite=false"));

        ScreenshotPage litePage = client.history().list(10, 0, null, true);
        assertFalse(litePage.items().get(0).downloadUrl().isPresent());
        assertFalse(litePage.items().get(0).downloadUrlExpiresAt().isPresent());
        assertTrue(transport.last().url.contains("lite=true"));
    }

    @Test
    void extractSendsQueryParamsAndParsesResult() {
        StubTransport transport = new StubTransport().enqueue(Responses.json(200,
                "{\"url\":\"https://example.com\",\"format\":\"markdown\",\"content\":\"# Hi\","
                        + "\"chars\":4,\"truncated\":false,\"took_ms\":42}"));

        ExtractResult result = SnapshotFlowClient.builder().apiKey("k").transport(transport).build()
                .extract().extract(ExtractRequest.url("https://example.com")
                        .format(ContentFormat.MARKDOWN)
                        .maxChars(5000)
                        .build());

        assertEquals("https://example.com", result.url());
        assertEquals(ContentFormat.MARKDOWN, result.format());
        assertEquals("# Hi", result.content());
        assertEquals(4, result.chars());
        assertFalse(result.truncated());
        assertEquals(42L, result.tookMs());

        String url = transport.last().url;
        assertTrue(url.contains("/extract?"));
        assertTrue(url.contains("url=https%3A%2F%2Fexample.com"));
        assertTrue(url.contains("format=markdown"));
        assertTrue(url.contains("max_chars=5000"));
    }
}
