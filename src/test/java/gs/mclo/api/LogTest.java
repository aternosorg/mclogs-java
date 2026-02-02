package gs.mclo.api;

import gs.mclo.api.data.Metadata;
import gs.mclo.api.reader.LogReader;
import gs.mclo.api.reader.StringLogReader;
import gs.mclo.api.response.Limits;
import gs.mclo.api.internal.filter.Filter;
import gs.mclo.api.internal.filter.FilterList;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LogTest extends ApiTest {
    private static final String REMOVED_IPv6 = "****:****:****:****:****:****:****:****";
    private static final String REMOVED_IPv4 = "**.**.**.**";

    private final FilterList filters = new FilterList(new Filter[]{});

    @Test
    void removeIPv4() {
        assertFilter(REMOVED_IPv4, "123.45.67.89");
    }

    @Test
    void removeIPv4s() {
        assertFilter(REMOVED_IPv4 + " " + REMOVED_IPv4, "123.45.67.89 189.123.42.34");
    }

    @Test
    void similarToIPv4() {
        assertFilter("1000.45.67.89 " + REMOVED_IPv4, "1000.45.67.89 189.123.42.34");
    }

    @Test
    void whitelistedIpv4s() {
        String content = "127.0.0.1 127.75.75.18 0.0.0.0 1.1.1.1 1.0.0.1 8.8.8.8 8.8.8.4";
        assertFilter(content, content);
    }

    @Test
    void removeIPv6() {
        assertFilter(REMOVED_IPv6, "9557:c600:a213:4835:fdaa:9d04:e354:ed9e");
    }

    @Test
    void removeIPv6s() {
        assertFilter(REMOVED_IPv6 + " " + REMOVED_IPv6, "010b:0611:2138:c376:6c8f:a46e:af48:7014 53a6:8214:7341:e156:0c3c:ffcc:3474:e207");
    }

    @Test
    void similarToIPv6() {
        assertFilter("10000:8214:7341:e156:0c3c:ffcc:3474:e207", "10000:8214:7341:e156:0c3c:ffcc:3474:e207");
    }

    @Test
    void whitelistedIpv6s() {
        String content = "::1 0:0:0:0:0:0:0:0:1 ::0 0:0:0:0:0:0:0:0:0";
        assertFilter(content, content);
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
            String firstCall = log.getContent(filters);
            String secondCall = log.getContent(filters);
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

    protected void assertFilter(String expected, String content) {
        assertEquals(expected, legacyFilter(content));
        assertEquals(expected, filter(content));
    }

    protected String legacyFilter(String content) {
        try {
            return new Log(new StringLogReader(content)).getContent(Limits.DEFAULT);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected String filter(String content) {
        try {
            return new Log(new StringLogReader(content)).getContent(client.getFilters().join());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
