package gs.mclo.api;

import gs.mclo.api.data.LogField;
import gs.mclo.api.data.Metadata;
import gs.mclo.api.reader.FileLogReader;
import gs.mclo.api.util.TestUtil;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class MclogsClientTest extends ApiTest {
    @Test
    void listLogs() {
        assertArrayEquals(
                Stream.of("one.log", "three.log.gz", "two.log").sorted().toArray(String[]::new),
                Arrays.stream(client.listLogsInDirectory("src/test/resources")).sorted().toArray(String[]::new)
        );
    }

    @Test
    void listLogsNoDir() {
        assertArrayEquals(
                new String[0],
                Arrays.stream(client.listLogsInDirectory("src/test/resources/directory")).sorted().toArray(String[]::new)
        );
    }

    @Test
    void listLogsEmptyDir() {
        assertArrayEquals(
                new String[0],
                Arrays.stream(client.listLogsInDirectory("src/test/resources/empty")).sorted().toArray(String[]::new)
        );
    }

    @Test
    void listCrashReports() {
        assertArrayEquals(
                Stream.of("eight.txt", "seven.log.gz", "five.log").sorted().toArray(String[]::new),
                Arrays.stream(client.listCrashReportsInDirectory("src/test/resources")).sorted().toArray(String[]::new)
        );
    }

    @Test
    void listCrashReportsNoDir() {
        assertArrayEquals(
                new String[0],
                Arrays.stream(client.listCrashReportsInDirectory("src/test/resources/dir")).sorted().toArray(String[]::new)
        );
    }

    @Test
    void listCrashReportsEmptyDir() {
        assertArrayEquals(
                new String[0],
                Arrays.stream(client.listCrashReportsInDirectory("src/test/resources/empty")).sorted().toArray(String[]::new)
        );
    }

    @Test
    void shareLog() {
        client.uploadLog(Paths.get("src/test/resources/logs/one.log"))
                .thenCompose(res -> {
                    assertNotNull(res.getId());
                    assertNotNull(res.getUrl());
                    return res.getRawContent()
                            .thenCompose(content -> {
                                assertEquals(TestUtil.getFileContents("src/test/resources/logs/one.log"), content);
                                return res.delete();
                            });
                })
                .orTimeout(10, TimeUnit.SECONDS)
                .join();
    }

    @Test
    void shareLogWithMetadata() {
        Log request;
        request = new Log(new FileLogReader(Paths.get("src/test/resources/logs/one.log")));
        request.setSource("mclogs-java-tests");
        request.addMetadata(new Metadata<>("key-a", "value-a", "Label A", true));
        client.uploadLog(request)
                .thenCompose(res -> {
                    assertNotNull(res.getId());
                    assertNotNull(res.getUrl());
                    assertNotNull(res.getRawUrl());
                    assertEquals("mclogs-java-tests", res.getSource());

                    assertNotNull(res.getCreated());
                    assertTrue(res.getCreated().isAfter(Instant.ofEpochSecond(1769690721)), "Expected log to be created after this test was written");
                    assertTrue(res.getCreated().isBefore(Instant.ofEpochSecond(1769690721000L)), "Expected log to be created within the next 50k years");

                    assertNotNull(res.getExpires());
                    assertTrue(res.getExpires().isAfter(Instant.ofEpochSecond(1769690721)), "Expected log to expire after this test was written");
                    assertTrue(res.getExpires().isBefore(Instant.ofEpochSecond(1769690721000L)), "Expected log expire within the next 50k years");

                    assertNotNull(res.getSize());
                    assertTrue(res.getSize() > 10, "Expected log size to be greater than 10 bytes");

                    assertNotNull(res.getLines());
                    assertEquals(1, res.getLines());
                    assertEquals(0, res.getErrors());

                    assertNotNull(res.getToken());
                    assertEquals(1, res.getMetadata().size());

                    Metadata<?> metadata = res.getMetadata().iterator().next();
                    assertEquals("key-a", metadata.getKey());
                    assertEquals("value-a", metadata.getValue());
                    assertEquals("Label A", metadata.getLabel());
                    assertTrue(metadata.isVisible());

                    return res.getRawContent()
                            .thenCompose(content -> {
                                assertEquals(TestUtil.getFileContents("src/test/resources/logs/one.log"), content);
                                return res.delete();
                            });
                })
                .orTimeout(10, TimeUnit.SECONDS)
                .join();
    }

    @Test
    void shareGzipLog() {
        client.uploadLog(Paths.get("src/test/resources/logs/three.log.gz"))
                .thenCompose(res -> {
                    assertNotNull(res.getId());
                    assertNotNull(res.getUrl());
                    return res.getRawContent()
                            .thenCompose(content -> {
                                assertEquals(TestUtil.getGZIPFileContents("src/test/resources/logs/three.log.gz"), content);
                                return res.delete();
                            });
                })
                .orTimeout(10, TimeUnit.SECONDS)
                .join();
    }

    @Test
    void shareSecretFile() {
        CompletableFuture<?> future = client.uploadLog(Paths.get("src/test/resources/logs/secret.secret"));
        assertThrows(Exception.class, future::join);
    }

    @Test
    void userAgents() {
        //noinspection DataFlowIssue
        assertThrows(IllegalArgumentException.class, () -> new MclogsClient(null));
        assertThrows(IllegalArgumentException.class, () -> new MclogsClient(""));
        //noinspection DataFlowIssue
        assertThrows(IllegalArgumentException.class, () -> new MclogsClient("aternos/mclogs-java-tests", null));
        //noinspection DataFlowIssue
        assertThrows(IllegalArgumentException.class, () -> new MclogsClient(null, "1.0.0"));
        assertThrows(IllegalArgumentException.class, () -> new MclogsClient("", "1.0.0"));
        assertThrows(IllegalArgumentException.class, () -> new MclogsClient("a", ""));
        assertDoesNotThrow(() -> new MclogsClient("project", "1.0.0"));
        assertDoesNotThrow(() -> new MclogsClient("project", "1.0.0", "1.12.2"));
    }

    @Test
    void customInstance() {
        String content = TestUtil.getFileContents("src/test/resources/logs/one.log");

        Instance instance = new Instance("https://api.mclo.gs");

        MclogsClient client = new MclogsClient("aternos/mclogs-java-tests")
                .setInstance(instance);
        client.uploadLog(content)
                .thenCompose(res -> {
                    assertNotNull(res.getId());
                    assertNotNull(res.getUrl());
                    System.out.println("Test log has been shared at " + res.getUrl());
                    return res.getRawContent()
                            .thenCompose(raw -> {
                                assertEquals(content, raw);
                                return res.delete();
                            });
                })
                .orTimeout(10, TimeUnit.SECONDS)
                .join();
    }

    @Test
    void getLogNone() {
        client.uploadLog(Paths.get("src/test/resources/logs/three.log.gz"))
                .thenCompose(res -> {
                    assertNotNull(res.getId());
                    assertNotNull(res.getUrl());
                    return res.get().thenAccept(log -> {
                        assertNotNull(log);
                        assertEquals(res.getId(), log.getId());
                        assertEquals(res.getSize(), log.getSize());
                        assertNull(log.getContent());
                    }).thenCompose(v -> res.delete());
                })
                .orTimeout(10, TimeUnit.SECONDS)
                .join();
    }

    @Test
    void getLogAll() {
        var raw = "Test Log";
        client.uploadLog(raw)
                .thenCompose(res -> {
                    assertNotNull(res.getId());
                    assertNotNull(res.getUrl());
                    return res.get(LogField.RAW, LogField.PARSED, LogField.INSIGHTS).thenAccept(log -> {
                        assertNotNull(log);
                        assertEquals(res.getId(), log.getId());
                        assertEquals(res.getSize(), log.getSize());
                        assertNotNull(log.getContent());
                        assertNotNull(log.getContent().getRaw());
                        assertEquals(raw, log.getContent().getRaw());
                        assertNotNull(log.getContent().getParsed());
                        assertEquals(1, log.getContent().getParsed().length);
                        assertNotNull(log.getContent().getInsights());
                        assertEquals("unknown/unknown", log.getContent().getInsights().getId());
                    }).thenCompose(v -> res.delete());
                })
                .orTimeout(10, TimeUnit.SECONDS)
                .join();
    }

    @Test
    void getLogInsights() {
        client.uploadLog(Paths.get("src/test/resources/logs/three.log.gz"))
                .thenCompose(res -> {
                    assertNotNull(res.getId());
                    assertNotNull(res.getUrl());
                    return res.getInsights().thenAccept(insights -> {
                        assertNotNull(insights);
                        assertNotNull(insights.getAnalysis());
                        assertNotNull(insights.getAnalysis().getInformation());
                        assertNotNull(insights.getAnalysis().getProblems());
                    }).thenCompose(v -> res.delete());
                })
                .orTimeout(10, TimeUnit.SECONDS)
                .join();
    }

    @Test
    void analyseLog() {
        client.analyseLog(Paths.get("src/test/resources/logs/three.log.gz"))
                .thenAccept(insights -> {
                    assertNotNull(insights);
                    assertNotNull(insights.getAnalysis());
                    assertNotNull(insights.getAnalysis().getInformation());
                    assertNotNull(insights.getAnalysis().getProblems());
                })
                .orTimeout(10, TimeUnit.SECONDS)
                .join();
    }

    @Test
    void getStorageLimits() {
        client.getLimits()
                .thenAccept(limits -> {
                    assertNotNull(limits);
                    assertTrue(limits.getStorageTime() > 0);
                    assertTrue(limits.getMaxLength() > 0);
                    assertTrue(limits.getMaxLines() > 0);
                })
                .orTimeout(10, TimeUnit.SECONDS)
                .join();
    }
}
