package gs.mclo.api.response;

import gs.mclo.api.response.insights.Analysis;

public class InsightsResponse extends JsonResponse {

    /**
     * ID of detected log (name/type) e.g. "vanilla/server"
     */
    protected String id = null;

    /**
     * Software name, e.g. "Vanilla"
     */
    protected String name = null;

    /**
     * Software type, e.g. "server"
     */
    protected String type = null;

    /**
     * Combined title, e.g. "Vanilla 1.12.2 Server Log"
     */
    protected String title = null;

    /**
     * Information obtained from the analysis of the log
     */
    protected Analysis analysis = null;

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
