package com.snapshotflow.job;

import com.snapshotflow.batch.BatchItemResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Status and result of an async capture job, as returned by {@code GET /jobs/:id}. */
public final class Job {

    private String id;
    private JobKind kind;
    private JobStatus status;
    private String createdAt;
    private String startedAt;
    private String completedAt;
    private JobProgress progress;
    private JobResult result;
    private List<BatchItemResult> batchResults;
    private String error;
    private String errorCode;
    private String externalIdentifier;

    public String id() {
        return id;
    }

    /** Whether this is a single-page or batch capture job (empty on older servers). */
    public Optional<JobKind> kind() {
        return Optional.ofNullable(kind);
    }

    public JobStatus status() {
        return status;
    }

    public String createdAt() {
        return createdAt;
    }

    public Optional<String> startedAt() {
        return Optional.ofNullable(startedAt);
    }

    public Optional<String> completedAt() {
        return Optional.ofNullable(completedAt);
    }

    /** Progress counters for batch jobs ({@code completed} / {@code total}). */
    public Optional<JobProgress> progress() {
        return Optional.ofNullable(progress);
    }

    /** Present once a single capture job is {@link JobStatus#DONE}. */
    public Optional<JobResult> result() {
        return Optional.ofNullable(result);
    }

    /** Completed item results for batch jobs (populated incrementally while processing). */
    public List<BatchItemResult> batchResults() {
        return batchResults == null ? Collections.emptyList() : Collections.unmodifiableList(batchResults);
    }

    /** Human-readable error message when the job {@link JobStatus#FAILED}. */
    public Optional<String> error() {
        return Optional.ofNullable(error);
    }

    /** Machine-readable error code when the job failed (e.g. {@code TIMEOUT}, {@code QUOTA_EXCEEDED}). */
    public Optional<String> errorCode() {
        return Optional.ofNullable(errorCode);
    }

    public Optional<String> externalIdentifier() {
        return Optional.ofNullable(externalIdentifier);
    }

    public boolean isDone() {
        return status == JobStatus.DONE;
    }

    public boolean isFailed() {
        return status == JobStatus.FAILED;
    }

    public boolean isTerminal() {
        return status != null && status.isTerminal();
    }

    @Override
    public String toString() {
        return "Job{id=" + id + ", status=" + status + ", errorCode=" + errorCode + '}';
    }
}
