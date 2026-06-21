package com.snapshotflow.batch;

import com.snapshotflow.screenshot.OutputFormat;

import java.util.Base64;
import java.util.Optional;

/** Outcome of a single URL within a {@link BatchResult}. Either a capture or an {@link #error()}. */
public final class BatchItemResult {

    private String url;
    private OutputFormat format;
    private Boolean cached;
    private String image;
    private String storagePath;
    private String screenshotId;
    private String error;

    public String url() {
        return url;
    }

    public Optional<OutputFormat> format() {
        return Optional.ofNullable(format);
    }

    public boolean cached() {
        return Boolean.TRUE.equals(cached);
    }

    public boolean hasError() {
        return error != null;
    }

    /** The error message when this item failed (e.g. {@code QUOTA_EXCEEDED}), else empty. */
    public Optional<String> error() {
        return Optional.ofNullable(error);
    }

    /** The {@code data:} URL when the batch used {@link BatchResponseType#BASE64}. */
    public Optional<String> image() {
        return Optional.ofNullable(image);
    }

    public Optional<String> storagePath() {
        return Optional.ofNullable(storagePath);
    }

    public Optional<String> screenshotId() {
        return Optional.ofNullable(screenshotId);
    }

    /** Decoded image bytes when an inline base64 {@link #image()} is present. */
    public Optional<byte[]> bytes() {
        if (image == null || image.isEmpty()) {
            return Optional.empty();
        }
        int comma = image.indexOf(',');
        String base64 = comma >= 0 ? image.substring(comma + 1) : image;
        return Optional.of(Base64.getDecoder().decode(base64));
    }

    @Override
    public String toString() {
        return "BatchItemResult{url=" + url + (hasError() ? ", error=" + error : ", cached=" + cached()) + '}';
    }
}
