package gs.mclo.api;

import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;

public final class APIException extends IOException {
    private final int httpStatusCode;

    @ApiStatus.Internal
    public APIException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
