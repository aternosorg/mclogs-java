package gs.mclo.api.response;

import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings({"unused"})
public final class UploadLogResponse extends CommonLogResponse {
    private @Nullable Instant created;
    private @Nullable Instant expires;
    private @Nullable Long size;
    private @Nullable Integer lines;
    private @Nullable Integer errors;
    private @Nullable String token;

    private UploadLogResponse() {

    }

    /**
     * Get the creation time of the log
     *
     * @return the creation time of the log or null if this instance is outdated
     */
    public @Nullable Instant getCreated() {
        return created;
    }

    /**
     * Get the expiration time of the log
     *
     * @return the expiration time of the log or null if this instance is outdated
     */
    public @Nullable Instant getExpires() {
        return expires;
    }

    /**
     * Get the size of the log in bytes
     *
     * @return the size of the log in bytes or null if this instance is outdated
     */
    public @Nullable Long getSize() {
        return size;
    }

    /**
     * Get the number of lines in the log
     *
     * @return the number of lines in the log or null if this instance is outdated
     */
    public @Nullable Integer getLines() {
        return lines;
    }

    /**
     * Get the number of error lines detected in the log
     *
     * @return the number of error lines detected in the log or null if this instance is outdated
     */
    public @Nullable Integer getErrors() {
        return errors;
    }

    /**
     * Get the token for deleting the log
     *
     * @return the token for deleting the log or null if this instance is outdated
     */
    public @Nullable String getToken() {
        return token;
    }

    /**
     * Delete this log from the instance
     *
     * @return a future that completes when the log has been deleted
     */
    public CompletableFuture<Void> delete() {
        return client().deleteLog(getId(), token);
    }
}
