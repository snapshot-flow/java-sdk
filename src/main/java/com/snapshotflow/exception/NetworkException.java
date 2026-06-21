package com.snapshotflow.exception;

/**
 * Thrown when a request could not be completed at the transport level — the
 * server was never reached, or no response came back before the configured
 * timeout. Examples: connection refused, DNS failure, TLS handshake error,
 * read timeout, or the calling thread being interrupted mid-request.
 *
 * <p>Unlike {@link ApiException}, there is no HTTP status code because no HTTP
 * response was received. The original cause ({@link java.io.IOException},
 * {@link java.lang.InterruptedException}, …) is always attached.
 */
public class NetworkException extends SnapshotFlowException {

    private static final long serialVersionUID = 1L;

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
