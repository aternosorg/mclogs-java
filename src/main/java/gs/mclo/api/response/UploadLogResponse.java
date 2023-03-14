package gs.mclo.api.response;

import gs.mclo.api.MclogsClient;

import java.io.IOException;

public class UploadLogResponse extends JsonResponse {
    protected String id = null;
    protected MclogsClient client;

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
    public String getId() {
        return id;
    }

    /**
     * Get the url to view this log
     * @return the url to view this log (e.g. <a href="https://mclo.gs/HpAwPry">https://mclo.gs/HpAwPry</a>)
     */
    public String getUrl() {
        return client.getInstance().getViewLogUrl(this.id);
    }

    /**
     * Get the url to view this log raw
     * @return the url to view this log raw (e.g. <a href="https://mclo.gs/raw/HpAwPry">https://mclo.gs/raw/HpAwPry</a>)
     */
    public String getRawUrl() {
        return client.getInstance().getRawLogUrl(this.id);
    }

    /**
     * Fetch the raw content of this log
     * @return the raw content of this log
     * @throws IOException if an error occurs while fetching the content
     */
    public String getRawContent() throws IOException {
        return this.client.getRawLogContent(this.id);
    }

    /**
     * Fetch the insights for this log
     * @return the insights for this log
     * @throws IOException if an error occurs while fetching the insights
     */
    public InsightsResponse getInsights() throws IOException {
        return this.client.getInsights(this.id);
    }
}
