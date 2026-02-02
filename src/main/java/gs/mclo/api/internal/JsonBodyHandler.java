package gs.mclo.api.internal;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import gs.mclo.api.APIException;
import gs.mclo.api.MclogsClient;
import org.jetbrains.annotations.Nullable;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletionException;

public final class JsonBodyHandler<T> implements HttpResponse.BodyHandler<T> {
    private final MclogsClient client;
    private final Class<T> clazz;

    public JsonBodyHandler(MclogsClient client, Class<T> clazz) {
        this.client = client;
        this.clazz = clazz;
    }

    @Override
    public HttpResponse.BodySubscriber<@Nullable T> apply(HttpResponse.ResponseInfo responseInfo) {
        return HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8), x -> map(x, responseInfo));
    }

    @Nullable T map(String json, HttpResponse.ResponseInfo responseInfo) {
        var element = JsonParser.parseString(json);
        if (element.isJsonObject()) {
            checkError(element, responseInfo);
        } else if (!element.isJsonArray()) {
            throw new CompletionException(new APIException("Invalid API response (Status code: " + responseInfo.statusCode() + ")", responseInfo.statusCode()));
        }


        if (clazz == Void.class) {
            return null;
        }

        var body = client.gson().fromJson(element, clazz);

        if (body instanceof Initializable) {
            ((Initializable) body).setClient(client);
        }

        return body;
    }

    /**
     * Check if the response contains an error
     * @param element response JSON element
     */
    private static void checkError(JsonElement element, HttpResponse.ResponseInfo responseInfo) {
        var json = element.getAsJsonObject();

        var success = json.get("success");
        var error = json.get("error");
        if (
                (success != null && success.isJsonPrimitive() && success.getAsJsonPrimitive().isBoolean() && !success.getAsBoolean())
                || (error != null && !error.isJsonNull())
        ) {
            var message = "Unknown API error";
            if (error != null && error.isJsonPrimitive()) {
                if (error.getAsJsonPrimitive().isString()) {
                    message = error.getAsJsonPrimitive().getAsString();
                }
            }
            throw new CompletionException(new APIException(message, responseInfo.statusCode()));
        }
    }
}
