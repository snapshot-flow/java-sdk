package com.snapshotflow.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.snapshotflow.job.JobStatus;
import com.snapshotflow.screenshot.OutputFormat;

import java.util.Optional;

/** Parsed webhook payload delivered when an async job finishes. */
public final class WebhookEvent {

    private String event;
    @JsonProperty("job_id")
    private String jobId;
    private JobStatus status;
    @JsonProperty("external_identifier")
    private String externalIdentifier;
    private Result result;
    private ErrorInfo error;
    private Meta meta;

    /** Raw event name: {@code "screenshot.completed"} or {@code "screenshot.failed"}. */
    public String event() {
        return event;
    }

    public boolean isCompleted() {
        return "screenshot.completed".equals(event);
    }

    public boolean isFailed() {
        return "screenshot.failed".equals(event);
    }

    public String jobId() {
        return jobId;
    }

    public JobStatus status() {
        return status;
    }

    public Optional<String> externalIdentifier() {
        return Optional.ofNullable(externalIdentifier);
    }

    public Optional<Result> result() {
        return Optional.ofNullable(result);
    }

    public Optional<ErrorInfo> error() {
        return Optional.ofNullable(error);
    }

    public Optional<Meta> meta() {
        return Optional.ofNullable(meta);
    }

    @Override
    public String toString() {
        return "WebhookEvent{event=" + event + ", jobId=" + jobId + ", status=" + status + '}';
    }

    /** The {@code result} block of a successful delivery. */
    public static final class Result {
        private String storagePath;
        private String url;
        private OutputFormat format;
        private Integer width;
        private Integer height;
        private String screenshotId;
        private Long sizeBytes;

        public String storagePath() {
            return storagePath;
        }

        public String url() {
            return url;
        }

        public OutputFormat format() {
            return format;
        }

        public Integer width() {
            return width;
        }

        public Integer height() {
            return height;
        }

        public Optional<String> screenshotId() {
            return Optional.ofNullable(screenshotId);
        }

        public Optional<Long> sizeBytes() {
            return Optional.ofNullable(sizeBytes);
        }
    }

    /** The {@code error} block of a failed delivery (absent when {@code webhook_errors=false}). */
    public static final class ErrorInfo {
        private String code;
        private String message;

        public String code() {
            return code;
        }

        public String message() {
            return message;
        }
    }

    /** The {@code meta} block present on every delivery. */
    public static final class Meta {
        @JsonProperty("rendering_ms")
        private Long renderingMs;
        @JsonProperty("size_bytes")
        private Long sizeBytes;
        @JsonProperty("completed_at")
        private String completedAt;

        public Optional<Long> renderingMs() {
            return Optional.ofNullable(renderingMs);
        }

        public Optional<Long> sizeBytes() {
            return Optional.ofNullable(sizeBytes);
        }

        public String completedAt() {
            return completedAt;
        }
    }
}
