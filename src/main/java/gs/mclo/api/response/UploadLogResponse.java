package gs.mclo.api.response;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings({"unused", "NotNullFieldNotInitialized"})
public final class UploadLogResponse extends CommonLogResponse {
    private String token;

    private UploadLogResponse() {

    }

    /**
     * Get the token for deleting the log
     *
     * @return the token for deleting the log or null if this instance is outdated
     */
    public String getToken() {
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
