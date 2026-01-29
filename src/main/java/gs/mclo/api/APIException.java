package gs.mclo.api;

import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;

public final class APIException extends IOException {
    @ApiStatus.Internal
    public APIException(String message) {
        super(message);
    }
}
