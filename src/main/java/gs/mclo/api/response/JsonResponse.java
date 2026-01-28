package gs.mclo.api.response;

import gs.mclo.api.APIException;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class JsonResponse {
    private boolean success = true;
    private @Nullable String error = null;

    /**
     * was the upload successful?
     * @return true if the upload was successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Get the error message
     * @return the error message
     */
    public @Nullable String getError() {
        return error;
    }

    /**
     * Throw an APIException if the api returned an error
     * @throws APIException if success is false
     */
    public JsonResponse throwIfError() throws APIException {
        if (!isSuccess())
            throw new APIException(this);
        return this;
    }
}
