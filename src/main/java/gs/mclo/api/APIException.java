package gs.mclo.api;

import java.io.IOException;

public class APIException extends IOException {
    public APIException(String message) {
        super(message);
    }
}
