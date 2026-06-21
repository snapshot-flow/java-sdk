package com.snapshotflow;

import com.snapshotflow.account.CreatedApiKey;
import com.snapshotflow.account.TokenPair;
import com.snapshotflow.account.UserProfile;
import com.snapshotflow.account.WebhookSecretMetadata;
import com.snapshotflow.health.HealthStatus;
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
}
