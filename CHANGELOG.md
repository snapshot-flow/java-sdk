# Changelog

All notable changes to the SnapshotFlow Java SDK are documented here. The format
is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this
project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial release. Full coverage of the SnapshotFlow public API:
  - **Capture** — `GET /screenshot`, synchronous (`capture`) and async (`submit`),
    with the complete set of capture, emulation, blocking, PDF and content-extraction options.
  - **Batch** — `POST /batch`, up to 10 URLs.
  - **Diff** — `GET /diff`, visual diff with stats and rendered image.
  - **Jobs** — `GET /jobs/:id`, with a polling helper (`jobs().await`).
  - **History** — `GET/DELETE /screenshots`, list / fetch / delete.
  - **Account** — register, login, refresh, email verification, profile (`/api/auth/*`).
  - **API keys** — create / list / revoke / rotate.
  - **Webhook secrets** — create / get / rotate / delete.
  - **Health** — `GET /health`.
  - **Webhook verification** — `WebhookVerifier` for HMAC-SHA256 signature checking
    and payload parsing on receivers.
- Built on the JDK `HttpClient` (Java 11+); Jackson is the only runtime dependency.
- Typed exception hierarchy and automatic retry of idempotent requests on
  transient failures.
