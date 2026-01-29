package gs.mclo.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RemoveIPv4Test extends LogTest {
    @Test
    void removeIPv4() {
        Log log = create("123.45.67.89");
        assertEquals("**.**.**.**", log.getContent());
    }

    @Test
    void removeIPv4s() {
        Log log = create("123.45.67.89 189.123.42.34");
        assertEquals("**.**.**.** **.**.**.**", log.getContent());
    }

    @Test
    void similarToIPv4() {
        Log log = create("1000.45.67.89 189.123.42.34");
        assertEquals("1000.45.67.89 **.**.**.**", log.getContent());
    }

    @Test
    void whitelistedIpv4s() {
        String content = "127.0.0.1 127.75.75.18 0.0.0.0 1.1.1.1 1.0.0.1 8.8.8.8 8.8.8.4";
        Log log = create(content);
        assertEquals(content, log.getContent());
    }
}
