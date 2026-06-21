package com.snapshotflow.health;

/** Service health snapshot from {@code GET /health}. */
public final class HealthStatus {

    private String status;
    private BrowserPool browserPool;
    private String cache;

    public String status() {
        return status;
    }

    public BrowserPool browserPool() {
        return browserPool;
    }

    /** Cache backend in use: {@code "redis"} or {@code "memory"}. */
    public String cache() {
        return cache;
    }

    public boolean isOk() {
        return "ok".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "HealthStatus{status=" + status + ", cache=" + cache + ", browserPool=" + browserPool + '}';
    }

    /** Browser pool capacity and current availability. */
    public static final class BrowserPool {
        private int size;
        private int available;

        public int size() {
            return size;
        }

        public int available() {
            return available;
        }

        @Override
        public String toString() {
            return "BrowserPool{size=" + size + ", available=" + available + '}';
        }
    }
}
