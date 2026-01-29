package gs.mclo.api.response.log;

import gs.mclo.api.response.InsightsResponse;
import gs.mclo.api.response.entry.Entry;
import gs.mclo.api.response.insights.Analysis;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public final class LogContent {
    private @Nullable String raw;
    private Entry @Nullable [] parsed;
    private @Nullable InsightsResponse insights;

    /**
     * The raw log content as a single string
     *
     * @return the raw log content or null if not requested
     */
    public @Nullable String getRaw() {
        return raw;
    }

    /**
     * The parsed log content as an array of entries
     *
     * @return the parsed log content or null if not requested
     */
    public Entry @Nullable [] getParsed() {
        return parsed;
    }

    /**
     * The insights generated from the log content
     *
     * @return the insights or null if not requested
     */
    public @Nullable InsightsResponse getInsights() {
        return insights;
    }
}
