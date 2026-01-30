package gs.mclo.api.internal;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestBuilderTest {
    private RequestBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new RequestBuilder(new Gson());
    }

    @Test
    void fullUserAgent() {
        builder.setProjectName("asd");
        builder.setMinecraftVersion("1.12.2");
        builder.setProjectVersion("1.0.0");
        var request = builder.request("https://example.com").build();
        assertEquals("asd/1.0.0 (Minecraft 1.12.2)", request.headers().firstValue("User-Agent").orElse(""));
    }

    @Test
    void shortUserAgent() {
        builder.setProjectName("asd");
        builder.setProjectVersion("1.0.0");
        var request = builder.request("https://example.com").build();
        assertEquals("asd/1.0.0", request.headers().firstValue("User-Agent").orElse(""));
    }

    @Test
    void customUserAgent() {
        builder.setCustomUserAgent("asd");
        var request = builder.request("https://example.com").build();
        assertEquals("asd", request.headers().firstValue("User-Agent").orElse(""));
    }
}
