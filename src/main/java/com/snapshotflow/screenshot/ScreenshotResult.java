package com.snapshotflow.screenshot;

import com.snapshotflow.internal.ApiResponse;
import com.snapshotflow.internal.Json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;

/**
 * Result of a synchronous capture. Always carries the rendered {@link #bytes()}
 * (decoded from base64 when the server replied in JSON), plus whatever metadata
 * the response exposed: cache status, screenshot id, storage path, page
 * metadata and extracted content.
 *
 * <p>When the server runs in {@code SAVE_TO_DISK} mode the response is JSON with
 * a {@link #storagePath()} and no inline image — {@link #hasBytes()} is then
 * {@code false}.
 */
public final class ScreenshotResult {

    private final byte[] bytes;
    private final OutputFormat format;
    private final boolean cached;
    private final String screenshotId;
    private final String storagePath;
    private final Integer width;
    private final Integer height;
    private final String takenAt;
    private final PageMetadata metadata;
    private final String content;
    private final String etag;
    private final String contentType;

    private ScreenshotResult(byte[] bytes, OutputFormat format, boolean cached, String screenshotId,
                             String storagePath, Integer width, Integer height, String takenAt,
                             PageMetadata metadata, String content, String etag, String contentType) {
        this.bytes = bytes == null ? new byte[0] : bytes;
        this.format = format;
        this.cached = cached;
        this.screenshotId = screenshotId;
        this.storagePath = storagePath;
        this.width = width;
        this.height = height;
        this.takenAt = takenAt;
        this.metadata = metadata;
        this.content = content;
        this.etag = etag;
        this.contentType = contentType;
    }

    static ScreenshotResult fromResponse(ApiResponse response, OutputFormat requestedFormat) {
        if (response.isJson()) {
            ScreenshotPayload p = Json.read(response.body(), ScreenshotPayload.class);
            OutputFormat fmt = p.format != null ? p.format : requestedFormat;
            return new ScreenshotResult(decodeImage(p.image), fmt, Boolean.TRUE.equals(p.cached),
                    p.screenshotId, p.storagePath, p.width, p.height, p.takenAt, p.metadata, p.content,
                    null, fmt == null ? null : fmt.mimeType());
        }
        boolean cached = response.header("x-cache").map("HIT"::equalsIgnoreCase).orElse(false);
        return new ScreenshotResult(response.body(), requestedFormat, cached,
                response.header("x-screenshot-id").orElse(null), null, null, null, null, null, null,
                response.header("etag").orElse(null), response.header("content-type").orElse(null));
    }

    private static byte[] decodeImage(String dataUrl) {
        if (dataUrl == null || dataUrl.isEmpty()) {
            return new byte[0];
        }
        int comma = dataUrl.indexOf(',');
        String base64 = comma >= 0 ? dataUrl.substring(comma + 1) : dataUrl;
        return Base64.getDecoder().decode(base64);
    }

    /** The rendered image or PDF bytes. Empty in {@code SAVE_TO_DISK} mode (see {@link #storagePath()}). */
    public byte[] bytes() {
        return bytes;
    }

    public boolean hasBytes() {
        return bytes.length > 0;
    }

    public OutputFormat format() {
        return format;
    }

    /** Whether the capture was served from cache ({@code X-Cache: HIT} / {@code "cached": true}). */
    public boolean cached() {
        return cached;
    }

    /** Persistent history id (when the account has screenshot history enabled). */
    public Optional<String> screenshotId() {
        return Optional.ofNullable(screenshotId);
    }

    /** Signed download URL (when the server saved the file to storage). */
    public Optional<String> storagePath() {
        return Optional.ofNullable(storagePath);
    }

    public Optional<Integer> width() {
        return Optional.ofNullable(width);
    }

    public Optional<Integer> height() {
        return Optional.ofNullable(height);
    }

    /** ISO-8601 capture timestamp, when the server returned one. */
    public Optional<String> takenAt() {
        return Optional.ofNullable(takenAt);
    }

    /** Page metadata, present when {@code metadata(true)} was requested. */
    public Optional<PageMetadata> metadata() {
        return Optional.ofNullable(metadata);
    }

    /** Extracted page content, present when {@code extractContent(true)} was requested. */
    public Optional<String> content() {
        return Optional.ofNullable(content);
    }

    public Optional<String> etag() {
        return Optional.ofNullable(etag);
    }

    public Optional<String> contentType() {
        return Optional.ofNullable(contentType);
    }

    /** Base64 of {@link #bytes()} (no {@code data:} prefix). */
    public String base64() {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /** Write {@link #bytes()} to a file, returning the path for chaining. */
    public Path writeTo(Path path) throws IOException {
        Files.write(path, bytes);
        return path;
    }

    @Override
    public String toString() {
        return "ScreenshotResult{format=" + format + ", bytes=" + bytes.length
                + ", cached=" + cached + ", screenshotId=" + screenshotId + '}';
    }
}
