package gs.mclo.api.internal.request;

import gs.mclo.api.data.Metadata;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
@ApiStatus.Internal
public final class UploadLogRequestBody {
    private final String content;
    private final @Nullable String source;
    private final Set<Metadata<?>> metadata;

    public UploadLogRequestBody(String content, @Nullable String source, Set<Metadata<?>> metadata) {
        this.content = content;
        this.source = source;
        this.metadata = metadata;
    }
}
