package gs.mclo.api;

import gs.mclo.api.data.Metadata;
import gs.mclo.api.reader.LogReader;
import gs.mclo.api.reader.StringLogReader;
import gs.mclo.api.response.Limits;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LogTest {
    private static final String REMOVED_IPv6 = "****:****:****:****:****:****:****:****";
    private static final String REMOVED_IPv4 = "**.**.**.**";

    @Test
    void removeIPv4() {
        assertEquals(REMOVED_IPv4, filter("123.45.67.89"));
    }

    @Test
    void removeIPv4s() {
        assertEquals(REMOVED_IPv4 + " " + REMOVED_IPv4, filter("123.45.67.89 189.123.42.34"));
    }

    @Test
    void similarToIPv4() {
        assertEquals("1000.45.67.89 " + REMOVED_IPv4, filter("1000.45.67.89 189.123.42.34"));
    }

    @Test
    void whitelistedIpv4s() {
        String content = "127.0.0.1 127.75.75.18 0.0.0.0 1.1.1.1 1.0.0.1 8.8.8.8 8.8.8.4";
        assertEquals(content, filter(content));
    }

    @Test
    void removeIPv6() {
        assertEquals(REMOVED_IPv6, filter("9557:c600:a213:4835:fdaa:9d04:e354:ed9e"));
    }

    @Test
    void removeIPv6s() {
        assertEquals(REMOVED_IPv6 + " " + REMOVED_IPv6, filter("010b:0611:2138:c376:6c8f:a46e:af48:7014 53a6:8214:7341:e156:0c3c:ffcc:3474:e207"));
    }

    @Test
    void similarToIPv6() {
        assertEquals("10000:8214:7341:e156:0c3c:ffcc:3474:e207", filter("10000:8214:7341:e156:0c3c:ffcc:3474:e207"));
    }

    @Test
    void whitelistedIpv6s() {
        String content = "::1 0:0:0:0:0:0:0:0:1 ::0 0:0:0:0:0:0:0:0:0";
        assertEquals(content, filter(content));
    }

    @Test
    void getContentCaches() {
        var testReader = new LogReader() {
            private boolean called = false;

            @Override
            protected @NotNull Reader getReader() throws IOException {
                if (called) {
                    throw new IOException("getReader called multiple times");
                }

                called = true;
                return new StringReader("Test content");
            }
        };

        var log = new Log(testReader);

        try {
            String firstCall = log.getContent(Limits.DEFAULT);
            String secondCall = log.getContent(Limits.DEFAULT);
            assertEquals("Test content", firstCall);
            assertEquals("Test content", secondCall);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Test
    void getSetAddMetadata() {
        var log = new Log(new StringLogReader(""));

        assertEquals(0, log.getMetadata().size());

        var meta1 = new Metadata<>("key1", "value1");
        assertSame(log, log.addMetadata(meta1));
        assertEquals(1, log.getMetadata().size());

        var meta2 = new Metadata<>("key2", "value2");
        assertSame(log, log.addMetadata(meta2));
        assertEquals(2, log.getMetadata().size());

        var newMetaSet = Set.<Metadata<?>>of(new Metadata<>("key3", "value3"));
        assertSame(log, log.setMetadata(newMetaSet));
        assertEquals(1, log.getMetadata().size());
    }

    @Test
    void getSetSource() {
        var log = new Log(new StringLogReader(""));

        assertNull(log.getSource());

        assertSame(log, log.setSource("MySource"));
        assertEquals("MySource", log.getSource());

        assertSame(log, log.setSource(null));
        assertNull(log.getSource());
    }

    protected String filter(String content) {
        try {
            return new Log(new StringLogReader(content)).getContent(Limits.DEFAULT);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
