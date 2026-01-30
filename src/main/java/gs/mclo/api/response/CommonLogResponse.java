package gs.mclo.api.response;

import gs.mclo.api.MclogsClient;
import gs.mclo.api.data.LogField;
import gs.mclo.api.data.Metadata;
import gs.mclo.api.internal.Initializable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings({"unused", "NotNullFieldNotInitialized", "FieldMayBeFinal"})
@ApiStatus.NonExtendable
public class CommonLogResponse implements Initializable {
    private transient @Nullable MclogsClient client;

    private String id;
    private @Nullable String source;
    private Instant created;
    private Instant expires;
    private long size;
    private int lines;
    private int errors;
    private String url;
    private String raw;
    private Set<Metadata<?>> metadata = new HashSet<>();

    /**
     * Get the log id
     *
     * @return the log id (e.g. HpAwPry)
     */
    public String getId() {
        return id;
    }

    /**
     * Get the source of the log
     *
     * @return source of the log (e.g. a domain or software name) or null
     */
    public @Nullable String getSource() {
        return source;
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
     * Get the url to view this log
     *
     * @return the url to view this log (e.g. <a href="https://mclo.gs/HpAwPry">https://mclo.gs/HpAwPry</a>)
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get the url to view this log raw
     *
     * @return the url to view this log raw (e.g. <a href="https://mclo.gs/raw/HpAwPry">https://mclo.gs/raw/HpAwPry</a>)
     */
    public String getRawUrl() {
        return raw;
    }

    /**
     * Get the metadata associated with this log
     *
     * @return the metadata associated with this log
     */
    public Set<Metadata<?>> getMetadata() {
        return metadata;
    }

    /**
     * Fetch the raw content of this log
     *
     * @return the raw content of this log
     */
    public CompletableFuture<String> getRawContent() {
        return client().getRawLogContent(id);
    }

    /**
     * Fetch the log with optional fields
     *
     * @param fields Which optional log fields to include
     * @return the raw content of this log
     */
    public CompletableFuture<GetLogResponse> get(LogField... fields) {
        return client().getLog(id, fields);
    }

    /**
     * Fetch the insights for this log
     *
     * @return the insights for this log
     */
    public CompletableFuture<InsightsResponse> getInsights() {
        return client().getInsights(id);
    }


    @ApiStatus.Internal
    @Override
    public void setClient(MclogsClient client) {
        this.client = client;
    }

    protected MclogsClient client() {
        return Objects.requireNonNull(client);
    }
}
