package com.snapshotflow.job;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

/** The {@code 202 Accepted} acknowledgement returned when an async job is submitted. */
public final class JobSubmission {

    @JsonProperty("job_id")
    @JsonAlias("jobId")
    private String jobId;
    private JobStatus status;
    @JsonProperty("external_identifier")
    private String externalIdentifier;
    private Integer total;

    public String jobId() {
        return jobId;
    }

    public JobStatus status() {
        return status;
    }

    public Optional<String> externalIdentifier() {
        return Optional.ofNullable(externalIdentifier);
    }

    /** Total number of URLs queued (present on async batch submissions). */
    public Optional<Integer> total() {
        return Optional.ofNullable(total);
    }

    @Override
    public String toString() {
        return "JobSubmission{jobId=" + jobId + ", status=" + status + '}';
    }
}
