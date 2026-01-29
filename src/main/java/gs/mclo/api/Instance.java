package gs.mclo.api;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("NotNullFieldNotInitialized")
public final class Instance {
    private String apiBaseUrl;
    private String viewLogUrl;

    /**
     * Create a new Instance with the default API base URL and view log URL
     */
    public Instance() {
        this(null, null);
    }

    /**
     * Create a new Instance with a custom API base URL and view log URL
     * @param apiBaseUrl the base URL for the API (e.g. <a href="https://api.mclo.gs/">https://api.mclo.gs/</a>)
     * @param viewLogUrl the base URL for viewing logs (e.g. <a href="https://mclo.gs/">https://mclo.gs/</a>)
     */
    public Instance(@Nullable String apiBaseUrl, @Nullable String viewLogUrl) {
        this.setApiBaseUrl(apiBaseUrl)
                .setViewLogUrl(viewLogUrl);
    }

    private String ensureEndsWithSlash(String url) {
        if (!url.endsWith("/"))
            url += "/";
        return url;
    }

    /**
     * Get the base URL for the API
     * @return the base URL for the API
     */
    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    /**
     * Set the base URL for the API
     * @param apiBaseUrl the base URL for the API (e.g. <a href="https://api.mclo.gs/">https://api.mclo.gs/</a>)
     * @return this
     */
    public Instance setApiBaseUrl(@Nullable String apiBaseUrl) {
        if (apiBaseUrl == null || apiBaseUrl.isEmpty())
            apiBaseUrl = "https://api.mclo.gs/";

        this.apiBaseUrl = ensureEndsWithSlash(apiBaseUrl);
        return this;
    }

    /**
     * Get the base URL for viewing logs
     * @return the base URL for viewing logs
     */
    public String getViewLogUrl() {
        return viewLogUrl;
    }

    /**
     * Get the URL for viewing a log
     * @param id the ID of the log (e.g. HpAwPry)
     * @return the base URL for viewing logs (e.g. <a href="https://mclo.gs/HpAwPry">https://mclo.gs/HpAwPry</a>)
     */
    public String getViewLogUrl(String id) {
        return viewLogUrl + id;
    }

    /**
     * Set the base URL for viewing logs
     * @param viewLogUrl the base URL for viewing logs (e.g. <a href="https://mclo.gs/">https://mclo.gs/</a>)
     * @return this
     */
    public Instance setViewLogUrl(@Nullable String viewLogUrl) {
        if (viewLogUrl == null || viewLogUrl.isEmpty())
            viewLogUrl = "https://mclo.gs/";
        this.viewLogUrl = ensureEndsWithSlash(viewLogUrl);
        return this;
    }

    /**
     * Get the URL for uploading a log
     * @return the URL for uploading a log (e.g. <a href="https://api.mclo.gs/1/log">https://api.mclo.gs/1/log</a>)
     */
    public String getLogUploadUrl() {
        return apiBaseUrl + "1/log";
    }

    /**
     * Get the URL for viewing a raw log
     * @param id the ID of the log (e.g. HpAwPry)
     * @return the URL for fetching the raw log (e.g. <a href="https://api.mclo.gs/1/raw/HpAwPry">https://api.mclo.gs/1/raw/HpAwPry</a>)
     */
    public String getRawLogUrl(String id) {
        return apiBaseUrl + "1/raw/" + id;
    }

    /**
     * Get the URL for fetching log insights
     * @param id the ID of the log (e.g. HpAwPry)
     * @return the URL for fetching log insights (e.g. <a href="https://api.mclo.gs/1/insights/HpAwPry">https://api.mclo.gs/1/insights/HpAwPry</a>)
     */
    public String getLogInsightsUrl(String id) {
        return apiBaseUrl + "1/insights/" + id;
    }

    /**
     * Get the URL for analysing a log without saving it
     * @return the URL for analysing a log without saving it (e.g. <a href="https://api.mclo.gs/1/analyse">https://api.mclo.gs/1/analyse</a>)
     */
    public String getLogAnalysisUrl() {
        return apiBaseUrl + "1/analyse";
    }

    /**
     * Get the URL for fetching storage limits
     * @return the URL for fetching storage limits (e.g. <a href="https://api.mclo.gs/1/limit">https://api.mclo.gs/1/limit</a>)
     */
    public String getStorageLimitUrl() {
        return apiBaseUrl + "1/limits";
    }

    /**
     * Get the URL for a log
     * @param id the ID of the log (e.g. HpAwPry)
     * @return the URL for the log (e.g. <a href="https://api.mclo.gs/1/log/HpAwPry">https://api.mclo.gs/1/log/HpAwPry</a>)
     */
    public String getLogUrl(String id) {
        return apiBaseUrl + "1/log/" + id;
    }
}
