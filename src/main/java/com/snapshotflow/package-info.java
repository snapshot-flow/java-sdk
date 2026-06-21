/**
 * SnapshotFlow Java SDK.
 *
 * <p>Start with {@link com.snapshotflow.SnapshotFlowClient}:
 *
 * <pre>{@code
 * SnapshotFlowClient client = SnapshotFlowClient.create("e2e_your_api_key");
 * client.capture("https://example.com").writeTo(java.nio.file.Path.of("out.png"));
 * }</pre>
 *
 * <p>Operations are grouped into sub-APIs reached from the client:
 * {@code screenshots()}, {@code batch()}, {@code diff()}, {@code jobs()},
 * {@code history()}, {@code auth()}, {@code apiKeys()}, {@code webhookSecrets()}
 * and {@code health()}. To verify inbound webhooks on a receiver, use
 * {@link com.snapshotflow.webhook.WebhookVerifier}.
 *
 * <p>All failures are subclasses of
 * {@link com.snapshotflow.exception.SnapshotFlowException}.
 */
package com.snapshotflow;
