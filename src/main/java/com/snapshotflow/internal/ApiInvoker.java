package com.snapshotflow.internal;

/**
 * Sends a fully-described {@link ApiRequest} and returns a successful
 * {@link ApiResponse}, applying auth, default headers and retry policy, and
 * translating any non-2xx response into the appropriate
 * {@link com.snapshotflow.exception.SnapshotFlowException}.
 *
 * <p>The domain API classes depend only on this interface, so they can be unit
 * tested against a stub invoker with no network involved.
 */
public interface ApiInvoker {

    ApiResponse invoke(ApiRequest request);
}
