package com.snapshotflow.job;

import com.snapshotflow.exception.SnapshotFlowException;
import com.snapshotflow.internal.ApiInvoker;
import com.snapshotflow.internal.ApiRequest;
import com.snapshotflow.internal.ApiResponse;
import com.snapshotflow.internal.Encoding;
import com.snapshotflow.internal.Json;
import com.snapshotflow.internal.Validate;

import java.time.Duration;

/** Operations on async capture jobs ({@code GET /jobs/:id}). */
public final class JobApi {

    private static final Duration DEFAULT_POLL_INTERVAL = Duration.ofSeconds(1);

    private final ApiInvoker invoker;

    public JobApi(ApiInvoker invoker) {
        this.invoker = invoker;
    }

    /** Fetch the current status (and result, once finished) of a job. */
    public Job get(String jobId) {
        Validate.notBlank(jobId, "jobId");
        ApiResponse response = invoker.invoke(ApiRequest.get("/jobs/" + Encoding.pathSegment(jobId)).build());
        return Json.read(response.body(), Job.class);
    }

    /** Poll until the job reaches a terminal state or {@code timeout} elapses (1s poll interval). */
    public Job await(String jobId, Duration timeout) {
        return await(jobId, timeout, DEFAULT_POLL_INTERVAL);
    }

    /**
     * Poll until the job is {@link JobStatus#DONE} or {@link JobStatus#FAILED}, or until
     * {@code timeout} elapses (in which case a {@link SnapshotFlowException} is thrown). Note a
     * terminal {@code FAILED} job is returned normally — inspect {@link Job#isFailed()}.
     */
    public Job await(String jobId, Duration timeout, Duration pollInterval) {
        Validate.notBlank(jobId, "jobId");
        Validate.notNull(timeout, "timeout");
        Validate.notNull(pollInterval, "pollInterval");
        long deadlineNanos = System.nanoTime() + timeout.toNanos();
        while (true) {
            Job job = get(jobId);
            if (job.isTerminal()) {
                return job;
            }
            if (System.nanoTime() >= deadlineNanos) {
                throw new SnapshotFlowException("Timed out after " + timeout + " waiting for job " + jobId);
            }
            try {
                Thread.sleep(pollInterval.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SnapshotFlowException("Interrupted while waiting for job " + jobId, e);
            }
        }
    }
}
