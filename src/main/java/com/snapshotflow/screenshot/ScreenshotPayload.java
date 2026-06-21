package com.snapshotflow.screenshot;

/**
 * Internal DTO mirroring the JSON body the {@code /screenshot} endpoint returns
 * for {@code response_type=json|base64} and for {@code SAVE_TO_DISK} mode.
 * Mapped into the public {@link ScreenshotResult}.
 */
final class ScreenshotPayload {

    String image;
    String url;
    OutputFormat format;
    Integer width;
    Integer height;
    Boolean cached;
    String takenAt;
    PageMetadata metadata;
    String content;
    String screenshotId;
    String storagePath;
}
