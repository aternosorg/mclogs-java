package gs.mclo.api.response;

import java.time.Duration;

public class LimitsResponse {
    /**
     * The duration in seconds that a log is stored for after the last view.
     */
    private int storageTime;

    /**
     * Maximum file length in bytes. Logs over this limit will be truncated to this length.
     */
    private int maxLength;

    /**
     * Maximum number of lines. Additional lines will be removed.
     */
    private int maxLines;

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
