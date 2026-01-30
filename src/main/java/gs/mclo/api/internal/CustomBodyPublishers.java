package gs.mclo.api.internal;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class CustomBodyPublishers {
    public static HttpRequest.BodyPublisher ofGzipString(String body) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {
            gzipStream.write(body.getBytes(StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofByteArray(byteStream.toByteArray());
    }
}
