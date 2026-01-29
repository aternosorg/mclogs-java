package gs.mclo.api.response;

import gs.mclo.api.MclogsClient;
import gs.mclo.api.data.Metadata;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings({"FieldMayBeFinal", "NotNullFieldNotInitialized", "unused"})
public final class UploadLogResponse implements Initializable {
    private transient @Nullable MclogsClient client;

    private String id;
    private @Nullable String source;
    private @Nullable Instant created;
    private @Nullable Instant expires;
    private @Nullable Long size;
    private @Nullable Integer lines;
    private @Nullable Integer errors;
    private @Nullable String token;
    private Set<Metadata<?>> metadata = new HashSet<>();

    private UploadLogResponse() {

    }

    @ApiStatus.Internal
    @Override
    public void setClient(MclogsClient client) {
        this.client = client;
    }

    /**
     * Get the log id
     * @return the log id (e.g. HpAwPry)
     */
    public String getId() {
        return id;
    }

    /**
     * Get the source of the log
     * @return source of the log (e.g. a domain or software name) or null
     */
    public @Nullable String getSource() {
        return source;
    }

    /**
     * Get the creation time of the log
     * @return the creation time of the log
     */
    public @Nullable Instant getCreated() {
        return created;
    }

    /**
     * Get the expiration time of the log
     * @return the expiration time of the log
     */
    public @Nullable Instant getExpires() {
        return expires;
    }

    /**
     * Get the size of the log in bytes
     * @return the size of the log in bytes
     */
    public @Nullable Long getSize() {
        return size;
    }

    /**
     * Get the number of lines in the log
     * @return the number of lines in the log or null if this instance is outdated
     */
    public @Nullable Integer getLines() {
        return lines;
    }

    /**
     * Get the number of error lines detected in the log
     * @return the number of error lines detected in the log or null if this instance is outdated
     */
    public @Nullable Integer getErrors() {
        return errors;
    }

    /**
     * Get the token for deleting the log
     * @return the token for deleting the log or null if this instance is outdated
     */
    public @Nullable String getToken() {
        return token;
    }

    /**
     * Get the metadata associated with this log
     * @return the metadata associated with this log
     */
    public Set<Metadata<?>> getMetadata() {
        return metadata;
    }

    /**
     * Get the url to view this log
     * @return the url to view this log (e.g. <a href="https://mclo.gs/HpAwPry">https://mclo.gs/HpAwPry</a>)
     */
    public String getUrl() {
        return client().getInstance().getViewLogUrl(id);
    }

    /**
     * Get the url to view this log raw
     * @return the url to view this log raw (e.g. <a href="https://mclo.gs/raw/HpAwPry">https://mclo.gs/raw/HpAwPry</a>)
     */
    public String getRawUrl() {
        return client().getInstance().getRawLogUrl(id);
    }

    /**
     * Fetch the raw content of this log
     * @return the raw content of this log
     */
    public CompletableFuture<String> getRawContent() {
        return client().getRawLogContent(id);
    }

    /**
     * Fetch the insights for this log
     * @return the insights for this log
     */
    public CompletableFuture<InsightsResponse> getInsights() {
        return client().getInsights(id);
    }

    private MclogsClient client() {
        return Objects.requireNonNull(client);
    }
}
