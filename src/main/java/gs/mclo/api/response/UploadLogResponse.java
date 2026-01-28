package gs.mclo.api.response;

import gs.mclo.api.MclogsClient;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("FieldMayBeFinal")
public class UploadLogResponse extends JsonResponse {
    private @Nullable String id = null;
    private transient @Nullable MclogsClient client;

    /**
     * set the client used to upload this log
     * @param client the client used to upload this log
     */
    public UploadLogResponse setClient(MclogsClient client) {
        this.client = client;
        return this;
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
        if (!isSuccess()) {
            throw new IllegalStateException("Cannot get view url of log upload that failed");
        }
        return client().getInstance().getViewLogUrl(Objects.requireNonNull(id));
    }

    /**
     * Get the url to view this log raw
     * @return the url to view this log raw (e.g. <a href="https://mclo.gs/raw/HpAwPry">https://mclo.gs/raw/HpAwPry</a>)
     */
    public String getRawUrl() {
        if (!isSuccess()) {
            throw new IllegalStateException("Cannot get view url of log upload that failed");
        }
        return client().getInstance().getRawLogUrl(Objects.requireNonNull(id));
    }

    /**
     * Fetch the raw content of this log
     * @return the raw content of this log
     */
    public CompletableFuture<String> getRawContent() {
        if (!isSuccess()) {
            throw new IllegalStateException("Cannot get view url of log upload that failed");
        }
        return client().getRawLogContent(Objects.requireNonNull(id));
    }

    /**
     * Fetch the insights for this log
     * @return the insights for this log
     */
    public CompletableFuture<InsightsResponse> getInsights() {
        if (!isSuccess()) {
            throw new IllegalStateException("Cannot get view url of log upload that failed");
        }
        return client().getInsights(Objects.requireNonNull(id));
    }

    private MclogsClient client() {
        return Objects.requireNonNull(client);
    }
}
