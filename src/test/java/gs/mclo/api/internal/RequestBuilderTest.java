package gs.mclo.api.internal;

import gs.mclo.api.MclogsClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RequestBuilderTest {

    @Test
    void fullUserAgent() {
        RequestBuilder builder = new RequestBuilder();
        builder.setProjectName("asd");
        builder.setMinecraftVersion("1.12.2");
        builder.setProjectVersion("1.0.0");
        var request = builder.request("https://example.com").build();
        assertEquals("asd/1.0.0 (Minecraft 1.12.2)", request.headers().firstValue("User-Agent").orElse(""));
    }

    @Test
    void shortUserAgent() {
        RequestBuilder builder = new RequestBuilder();
        builder.setProjectName("asd");
        builder.setProjectVersion("1.0.0");
        var request = builder.request("https://example.com").build();
        assertEquals("asd/1.0.0", request.headers().firstValue("User-Agent").orElse(""));
    }

    @Test
    void customUserAgent() {
        RequestBuilder builder = new RequestBuilder();
        builder.setCustomUserAgent("asd");
        var request = builder.request("https://example.com").build();
        assertEquals("asd", request.headers().firstValue("User-Agent").orElse(""));
    }
}
