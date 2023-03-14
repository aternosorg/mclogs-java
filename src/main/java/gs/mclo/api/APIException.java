package gs.mclo.api;

import gs.mclo.api.response.JsonResponse;

import java.io.IOException;

public class APIException extends IOException {
    protected JsonResponse response;

    public APIException(JsonResponse response) {
        super("The API returned an error");
        this.response = response;
    }

    /**
     * Get the parsed response
     * @return the parsed response
     */
    public JsonResponse getResponse() {
        return response;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " (" + this.response.getError() + ")";
    }
}
