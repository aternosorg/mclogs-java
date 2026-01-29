package gs.mclo.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RemoveIPv6Test extends LogTest {

    private static final String REMOVED_IPv6 = "****:****:****:****:****:****:****:****";

    @Test
    void removeIPv6() {
        Log log = create("9557:c600:a213:4835:fdaa:9d04:e354:ed9e");
        assertEquals(REMOVED_IPv6, log.getContent());
    }

    @Test
    void removeIPv6s() {
        Log log = create("010b:0611:2138:c376:6c8f:a46e:af48:7014 53a6:8214:7341:e156:0c3c:ffcc:3474:e207");
        assertEquals(REMOVED_IPv6 + " " + REMOVED_IPv6, log.getContent());
    }

    @Test
    void similarToIPv6() {
        Log log = create("10000:8214:7341:e156:0c3c:ffcc:3474:e207");
        assertEquals("10000:8214:7341:e156:0c3c:ffcc:3474:e207", log.getContent());
    }

    @Test
    void whitelistedIpv6s() {
        String content = "::1 0:0:0:0:0:0:0:0:1 ::0 0:0:0:0:0:0:0:0:0";
        Log log = create(content);
        assertEquals(content, log.getContent());
    }
}
