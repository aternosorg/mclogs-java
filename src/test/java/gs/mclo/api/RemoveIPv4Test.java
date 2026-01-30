package gs.mclo.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RemoveIPv4Test extends LogTest {
    @Test
    void removeIPv4() {
        assertEquals("**.**.**.**", create("123.45.67.89"));
    }

    @Test
    void removeIPv4s() {
        assertEquals("**.**.**.** **.**.**.**", create("123.45.67.89 189.123.42.34"));
    }

    @Test
    void similarToIPv4() {
        assertEquals("1000.45.67.89 **.**.**.**", create("1000.45.67.89 189.123.42.34"));
    }

    @Test
    void whitelistedIpv4s() {
        String content = "127.0.0.1 127.75.75.18 0.0.0.0 1.1.1.1 1.0.0.1 8.8.8.8 8.8.8.4";
        assertEquals(content, create(content));
    }
}
