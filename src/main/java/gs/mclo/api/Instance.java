package gs.mclo.api;

import java.util.Objects;

public final class Instance {
    private String apiBaseUrl;

    /**
     * Create a new Instance with the default API base URL and view log URL
     */
    public Instance() {
        this("https://api.mclo.gs/");
    }

    /**
     * Create a new Instance with a custom API base URL and view log URL
     * @param apiBaseUrl the base URL for the API (e.g. <a href="https://api.mclo.gs/">https://api.mclo.gs/</a>)
     */
    public Instance(String apiBaseUrl) {
        this.setApiBaseUrl(apiBaseUrl);
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
    public Instance setApiBaseUrl(String apiBaseUrl) {
        if (Objects.requireNonNull(apiBaseUrl).isBlank()) {
            throw new IllegalArgumentException("API base URL cannot be null or blank");
        }

        this.apiBaseUrl = ensureEndsWithSlash(apiBaseUrl);
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

    private String ensureEndsWithSlash(String url) {
        if (!url.endsWith("/"))
            url += "/";
        return url;
    }
}
