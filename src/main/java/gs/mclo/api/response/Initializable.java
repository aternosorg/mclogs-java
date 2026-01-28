package gs.mclo.api.response;

import gs.mclo.api.MclogsClient;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface Initializable {
    @ApiStatus.Internal
    void setClient(MclogsClient client);
}
