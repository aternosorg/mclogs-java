package gs.mclo.api.internal;

import com.google.gson.JsonParser;
import gs.mclo.api.APIException;
import gs.mclo.api.ApiTest;
import gs.mclo.api.response.UploadLogResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletionException;

public class JsonBodyHandlerTest extends ApiTest {
    private final HttpResponse.ResponseInfo responseInfo = new HttpResponse.ResponseInfo() {
        @Override
        public int statusCode() {
            return 200;
        }

        @Override
        public HttpHeaders headers() {
            return HttpHeaders.of(Map.of(), (s1, s2) -> true);
        }

        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_1_1;
        }
    };

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"success\": false}",
            "{\"success\": false, \"error\": null}",
            "{\"success\": false, \"error\": 123}",
    })
    public void testUnknownError(String body) {
        var handler = new JsonBodyHandler<>(client, UploadLogResponse.class);
        var json = JsonParser.parseString(body);

        APIException exception = null;
        try {
            handler.map(json, responseInfo);
        } catch (CompletionException e) {
            assert e.getCause() != null;
            assert e.getCause() instanceof APIException;
            exception = (APIException) e.getCause();
        }
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("Unknown API error", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"error\": \"test\"}",
            "{\"success\": false, \"error\": \"test\"}",
    })
    public void testError(String body) {
        var handler = new JsonBodyHandler<>(client, UploadLogResponse.class);
        var json = JsonParser.parseString(body);

        APIException exception = null;
        try {
            handler.map(json, responseInfo);
        } catch (CompletionException e) {
            assert e.getCause() != null;
            assert e.getCause() instanceof APIException;
            exception = (APIException) e.getCause();
        }
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("test", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"success\": true}",
            "{\"success\": 1}",
            "{\"success\": {}}",
            "{\"error\": null}",
    })
    public void testSuccess(String body) {
        var handler = new JsonBodyHandler<>(client, UploadLogResponse.class);
        var json = JsonParser.parseString(body);
        handler.map(json, responseInfo);
    }

    @Test
    public void testNotJsonObject() {
        var handler = new JsonBodyHandler<>(client, UploadLogResponse.class);
        var json = JsonParser.parseString("1");

        APIException exception = null;
        try {
            handler.map(json, responseInfo);
        } catch (CompletionException e) {
            assert e.getCause() != null;
            assert e.getCause() instanceof APIException;
            exception = (APIException) e.getCause();
        }
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("Invalid API response (Status code: 200)", exception.getMessage());
        Assertions.assertEquals(200, exception.getHttpStatusCode());
    }
}
