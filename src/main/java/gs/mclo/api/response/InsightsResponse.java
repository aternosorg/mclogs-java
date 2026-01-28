package gs.mclo.api.response;

import gs.mclo.api.response.insights.Analysis;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("FieldMayBeFinal")
public class InsightsResponse extends JsonResponse {

    /**
     * ID of detected log (name/type) e.g. "vanilla/server"
     */
    private @Nullable String id = null;

    /**
     * Software name, e.g. "Vanilla"
     */
    private @Nullable String name = null;

    /**
     * Software type, e.g. "server"
     */
    private @Nullable String type = null;

    /**
     * Combined title, e.g. "Vanilla 1.12.2 Server Log"
     */
    private @Nullable String title = null;

    /**
     * Information obtained from the analysis of the log
     */
    private @Nullable Analysis analysis = null;

    /**
     * Get the ID of detected log (name/type) e.g. "vanilla/server"
     * @return the ID of detected log (name/type) e.g. "vanilla/server"
     */
    public @Nullable String getId() {
        return id;
    }

    /**
     * Get the software name, e.g. "Vanilla"
     * @return the software name, e.g. "Vanilla"
     */
    public @Nullable String getName() {
        return name;
    }

    /**
     * Get the software type, e.g. "server"
     * @return the software type, e.g. "server"
     */
    public @Nullable String getType() {
        return type;
    }

    /**
     * Get the combined title, e.g. "Vanilla 1.12.2 Server Log"
     * @return the combined title, e.g. "Vanilla 1.12.2 Server Log"
     */
    public @Nullable String getTitle() {
        return title;
    }

    /**
     * Get the information obtained from the analysis of the log
     * @return the information obtained from the analysis of the log
     */
    public @Nullable Analysis getAnalysis() {
        return analysis;
    }
}
