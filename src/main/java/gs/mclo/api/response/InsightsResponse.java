package gs.mclo.api.response;

import gs.mclo.api.response.insights.Analysis;

@SuppressWarnings({"FieldMayBeFinal", "unused", "NotNullFieldNotInitialized"})
public final class InsightsResponse {

    /**
     * ID of detected log (name/type) e.g. "vanilla/server"
     */
    private String id;

    /**
     * Software name, e.g. "Vanilla"
     */
    private String name;

    /**
     * Software type, e.g. "server"
     */
    private String type;

    /**
     * Combined title, e.g. "Vanilla 1.12.2 Server Log"
     */
    private String title;

    /**
     * Information obtained from the analysis of the log
     */
    private Analysis analysis;

    private InsightsResponse() {

    }

    /**
     * Get the ID of detected log (name/type) e.g. "vanilla/server"
     * @return the ID of detected log (name/type) e.g. "vanilla/server"
     */
    public String getId() {
        return id;
    }

    /**
     * Get the software name, e.g. "Vanilla"
     * @return the software name, e.g. "Vanilla"
     */
    public String getName() {
        return name;
    }

    /**
     * Get the software type, e.g. "server"
     * @return the software type, e.g. "server"
     */
    public String getType() {
        return type;
    }

    /**
     * Get the combined title, e.g. "Vanilla 1.12.2 Server Log"
     * @return the combined title, e.g. "Vanilla 1.12.2 Server Log"
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the information obtained from the analysis of the log
     * @return the information obtained from the analysis of the log
     */
    public Analysis getAnalysis() {
        return analysis;
    }
}
