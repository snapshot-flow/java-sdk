# Changelog

All notable changes to the SnapshotFlow Java SDK are documented here. The format
is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this
project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.2] - 2026-11-17

### Added
- **Content extraction** — `client.extract()` (`GET /extract`) for fast Markdown, HTML, or plain-text extraction without rendering an image (`ExtractRequest`, `ExtractResult`).
- **Async batch jobs** — submit batches asynchronously via `batch().submit(...)` and track completion progress and per-URL results in `jobs().get(...)` / `jobs().await(...)`.
- **Batch & Diff options** — added ad blocking (`blockAds`), cookie-banner blocking (`blockCookieBanners`), and navigation wait strategy (`waitUntil`) to `BatchRequest` and `DiffRequest`, plus signed `downloadUrl()` support on `BatchItemResult` for `BatchResponseType.IMAGE_URLS`.
- **History URLs & lite mode** — `history().list(...)` now exposes pre-signed download links on list items (`downloadUrl()`) and accepts optional `ttlSeconds` and `lite` parameters to skip link signing when only metadata is needed.

### Changed
- Raised default `requestTimeout` from 60s to 90s so heavy-site `/batch` and `/diff` requests do not time out on the client before the server finishes.
- `diff().compare(...)` no longer auto-retries on transient HTTP errors to avoid duplicate renders and double quota charges.

## [1.0.1] - 2026-10-23

### Fixed
- `User-Agent` now reports the real SDK version; 1.0.0 sent `snapshotflow-java/1.0.0-SNAPSHOT`.
  The version is stamped from the build, so it can no longer drift from the release.
- `SnapshotFlowClient.Builder.build()` rejects a null, zero or negative timeout and a
  negative retry backoff with a clear `IllegalArgumentException`, instead of failing later.
- `jobs().await(...)` no longer sleeps past its timeout when the poll interval is longer
  than the time remaining.

### Changed
- Internal cleanup with no public API changes: shared `Retry-After` parsing and URL encoding,
  constant sets for error codes.
- Build: Maven Wrapper, CI on JDK 11/17/21 and Windows for every pull request.

## [1.0.0] - 2026-06-24

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
- Built on the JDK `HttpURLConnection` (Java 8+); Jackson is the only runtime dependency.
- Typed exception hierarchy and automatic retry of idempotent requests on
  transient failures.
