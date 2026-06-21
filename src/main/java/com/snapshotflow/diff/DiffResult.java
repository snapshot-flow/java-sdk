package com.snapshotflow.diff;

import com.snapshotflow.internal.ApiResponse;
import com.snapshotflow.internal.Json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;

/**
 * Result of a visual diff between two pages. Carries the diff statistics (when
 * the response was JSON) and/or the rendered diff PNG bytes (when the response
 * was an image or base64). With the SDK default of base64 you get both.
 */
public final class DiffResult {

    private final String before;
    private final String after;
    private final Integer width;
    private final Integer height;
    private final Long changedPixels;
    private final Long totalPixels;
    private final Double diffPercent;
    private final Boolean hasChanges;
    private final byte[] bytes;

    private DiffResult(String before, String after, Integer width, Integer height, Long changedPixels,
                       Long totalPixels, Double diffPercent, Boolean hasChanges, byte[] bytes) {
        this.before = before;
        this.after = after;
        this.width = width;
        this.height = height;
        this.changedPixels = changedPixels;
        this.totalPixels = totalPixels;
        this.diffPercent = diffPercent;
        this.hasChanges = hasChanges;
        this.bytes = bytes == null ? new byte[0] : bytes;
    }

    static DiffResult fromResponse(ApiResponse response) {
        if (response.isJson()) {
            DiffPayload p = Json.read(response.body(), DiffPayload.class);
            byte[] bytes = new byte[0];
            if (p.image != null && !p.image.isEmpty()) {
                int comma = p.image.indexOf(',');
                bytes = Base64.getDecoder().decode(comma >= 0 ? p.image.substring(comma + 1) : p.image);
            }
            return new DiffResult(p.before, p.after, p.width, p.height, p.changedPixels, p.totalPixels,
                    p.diffPercent, p.hasChanges, bytes);
        }
        return new DiffResult(null, null, null, null, null, null, null, null, response.body());
    }

    public Optional<String> before() {
        return Optional.ofNullable(before);
    }

    public Optional<String> after() {
        return Optional.ofNullable(after);
    }

    public Optional<Integer> width() {
        return Optional.ofNullable(width);
    }

    public Optional<Integer> height() {
        return Optional.ofNullable(height);
    }

    public Optional<Long> changedPixels() {
        return Optional.ofNullable(changedPixels);
    }

    public Optional<Long> totalPixels() {
        return Optional.ofNullable(totalPixels);
    }

    /** Percentage of pixels that differ (0–100), when stats were returned. */
    public Optional<Double> diffPercent() {
        return Optional.ofNullable(diffPercent);
    }

    /** Whether any pixels changed, when stats were returned. */
    public Optional<Boolean> hasChanges() {
        return Optional.ofNullable(hasChanges);
    }

    /** The rendered diff PNG, when the response carried an image (image or base64 response type). */
    public byte[] bytes() {
        return bytes;
    }

    public boolean hasBytes() {
        return bytes.length > 0;
    }

    public Path writeTo(Path path) throws IOException {
        Files.write(path, bytes);
        return path;
    }

    @Override
    public String toString() {
        return "DiffResult{diffPercent=" + diffPercent + ", changedPixels=" + changedPixels
                + ", bytes=" + bytes.length + '}';
    }
}
