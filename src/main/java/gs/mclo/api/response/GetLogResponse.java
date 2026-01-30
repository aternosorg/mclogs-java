package gs.mclo.api.response;

import gs.mclo.api.response.log.LogContent;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused"})
public final class GetLogResponse extends CommonLogResponse {

    private @Nullable LogContent content;

    private GetLogResponse() {

    }

    /**
     * Get the content of the log
     *
     * @return the content of the log or null if no content was requested
     */
    public @Nullable LogContent getContent() {
        return content;
    }
}
