package gs.mclo.api.response;

import gs.mclo.api.MclogsClient;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings({"FieldMayBeFinal", "NotNullFieldNotInitialized", "unused"})
public class UploadLogResponse implements Initializable {
    private String id;
    private transient @Nullable MclogsClient client;

    @ApiStatus.Internal
    @Override
    public void setClient(MclogsClient client) {
        this.client = client;
    }

    /**
     * Get the log id
     * @return the log id (e.g. HpAwPry)
     */
    public @Nullable String getId() {
        return id;
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
