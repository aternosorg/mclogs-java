package gs.mclo.api.response;

import gs.mclo.api.response.log.LogContent;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

@SuppressWarnings({"unused", "NotNullFieldNotInitialized"})
public final class GetLogResponse extends CommonLogResponse {
    private Instant created;
    private Instant expires;
    private long size;
    private int lines;
    private int errors;

    private @Nullable LogContent content;

    private GetLogResponse() {

    }

    /**
     * Get the creation time of the log
     *
     * @return the creation time of the log
     */
    public Instant getCreated() {
        return created;
    }

    /**
     * Get the expiration time of the log
     *
     * @return the expiration time of the log
     */
    public Instant getExpires() {
        return expires;
    }

    /**
     * Get the size of the log in bytes
     *
     * @return the size of the log in bytes
     */
    public long getSize() {
        return size;
    }

    /**
     * Get the number of lines in the log
     *
     * @return the number of lines in the log
     */
    public int getLines() {
        return lines;
    }

    /**
     * Get the number of error lines detected in the log
     *
     * @return the number of error lines detected in the log
     */
    public int getErrors() {
        return errors;
    }

    /**
     * Get the content of the log
     *
     * @return the content of the log or null if no content was requested
     */
    public @Nullable LogContent getContent() {
        return content;
    }
}
