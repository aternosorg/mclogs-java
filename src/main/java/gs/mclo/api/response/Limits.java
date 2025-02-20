package gs.mclo.api.response;

import java.time.Duration;

/**
 * Storage limits of a mclogs instance. This represents the response of the `/1/limits` endpoint.
 */
public class Limits {
    /**
     * The duration in seconds that a log is stored for after the last view.
     */
    private final int storageTime;

    /**
     * Maximum file length in bytes. Logs over this limit will be truncated to this length.
     */
    private final int maxLength;

    /**
     * Maximum number of lines. Additional lines will be removed.
     */
    private final int maxLines;

    /**
     * Create a new instance of the
     * @param storageTime the duration in seconds that a log is stored for after the last view
     * @param maxLength the maximum file length in bytes
     * @param maxLines the maximum number of lines
     */
    public Limits(int storageTime, int maxLength, int maxLines) {
        this.storageTime = storageTime;
        this.maxLength = maxLength;
        this.maxLines = maxLines;
    }

    /**
     * Get the duration in seconds that a log is stored for after the last view.
     * @return the duration in seconds that a log is stored for after the last view
     */
    public int getStorageTime() {
        return storageTime;
    }

    /**
     * Get the duration that a log is stored for after the last view.
     * @return the duration that a log is stored for after the last view
     */
    public Duration getStorageDuration() {
        return Duration.ofSeconds(storageTime);
    }

    /**
     * Get the maximum file length in bytes. Logs over this limit will be truncated to this length.
     * @return the maximum file length in bytes
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Get the maximum number of lines. Additional lines will be removed.
     * @return the maximum number of lines
     */
    public int getMaxLines() {
        return maxLines;
    }
}
