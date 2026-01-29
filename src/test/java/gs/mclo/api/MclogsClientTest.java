package gs.mclo.api;

import gs.mclo.api.data.Metadata;
import gs.mclo.api.internal.request.UploadLogRequestBody;
import gs.mclo.api.response.InsightsResponse;
import gs.mclo.api.response.Limits;
import gs.mclo.api.response.UploadLogResponse;
import gs.mclo.api.util.TestUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class MclogsClientTest extends ApiTest {
    @Test
    void listLogs() {
        assertDoesNotThrow(() -> assertArrayEquals(
                Stream.of("one.log", "three.log.gz", "two.log").sorted().toArray(String[]::new),
                Arrays.stream(client.listLogsInDirectory("src/test/resources")).sorted().toArray(String[]::new)
        ));
    }

    @Test
    void listLogsNoDir() {
        assertDoesNotThrow(() -> assertArrayEquals(
                new String[0],
                Arrays.stream(client.listLogsInDirectory("src/test/resources/directory")).sorted().toArray(String[]::new)
        ));
    }

    @Test
    void listLogsEmptyDir() {
        assertDoesNotThrow(() -> assertArrayEquals(
                new String[0],
                Arrays.stream(client.listLogsInDirectory("src/test/resources/empty")).sorted().toArray(String[]::new)
        ));
    }

    @Test
    void listCrashReports() {
        assertDoesNotThrow(() -> assertArrayEquals(
                Stream.of("eight.txt", "seven.log.gz", "five.log").sorted().toArray(String[]::new),
                Arrays.stream(client.listCrashReportsInDirectory("src/test/resources")).sorted().toArray(String[]::new)
        ));
    }

    @Test
    void listCrashReportsNoDir() {
        assertDoesNotThrow(() -> assertArrayEquals(
                new String[0],
                Arrays.stream(client.listCrashReportsInDirectory("src/test/resources/dir")).sorted().toArray(String[]::new)
        ));
    }

    @Test
    void listCrashReportsEmptyDir() {
        assertDoesNotThrow(() -> assertArrayEquals(
                new String[0],
                Arrays.stream(client.listCrashReportsInDirectory("src/test/resources/empty")).sorted().toArray(String[]::new)
        ));
    }

    @Test
    void shareLog() {
        assertDoesNotThrow(() -> {
            CompletableFuture<UploadLogResponse> response = client.uploadLog(Paths.get("src/test/resources/logs/one.log"));
            UploadLogResponse res = response.get();
            assertNotNull(res.getId());
            assertNotNull(res.getUrl());
            CompletableFuture<String> rawLog = res.getRawContent();
            assertEquals(TestUtil.getFileContents("src/test/resources/logs/one.log"), rawLog.get());

            System.out.println("Test log has been shared at " + res.getUrl());
        });
    }

    @Test
    void shareLogWithMetadata() {
        assertDoesNotThrow(() -> {
            var request = new Log(Paths.get("src/test/resources/logs/one.log"));
            request.setSource("mclogs-java-tests");
            request.addMetadata(new Metadata<>("key-a", "value-a", "Label A", true));
            CompletableFuture<UploadLogResponse> response = client.uploadLog(request);
            UploadLogResponse res = response.get();
            assertNotNull(res.getId());
            assertNotNull(res.getUrl());
            CompletableFuture<String> rawLog = res.getRawContent();
            assertEquals(TestUtil.getFileContents("src/test/resources/logs/one.log"), rawLog.get());

            System.out.println("Test log has been shared at " + res.getUrl());
        });
    }

    @Test
    void shareGzipLog() {
        assertDoesNotThrow(() -> {
            CompletableFuture<UploadLogResponse> response = client.uploadLog(Paths.get("src/test/resources/logs/three.log.gz"));
            UploadLogResponse res = response.get();
            assertNotNull(res.getId());
            assertNotNull(res.getUrl());
            CompletableFuture<String> rawLog = res.getRawContent();
            assertEquals(TestUtil.getGZIPFileContents("src/test/resources/logs/three.log.gz"), rawLog.get());

            System.out.println("Gzip test log has been shared at " + res.getUrl());
        });
    }

    @Test
    void shareSecretFile() {
        CompletableFuture<?> future = client.uploadLog(Paths.get("src/test/resources/logs/secret.secret"));
        assertThrows(ExecutionException.class, future::get);
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
        assertDoesNotThrow(() -> {
            MclogsClient client = new MclogsClient("project", "1.0.0")
                    .setProjectName("asd")
                    .setMinecraftVersion("1.12.2")
                    .setProjectVersion("1.0.0");
            assertEquals("asd/1.0.0 (Minecraft 1.12.2)", client.getUserAgent());
        });
    }

    @Test
    void customInstance() {
        String content = TestUtil.getFileContents("src/test/resources/logs/one.log");

        Instance instance = new Instance("https://api.mclo.gs", "https://mclo.gs")
                .setApiBaseUrl("https://api.mclo.gs")
                .setViewLogUrl("https://mclo.gs");

        MclogsClient client = new MclogsClient("aternos/mclogs-java-tests")
                .setInstance(instance);
        client.uploadLog(content)
                .thenAccept(res -> {
                    assertNotNull(res.getId());
                    assertNotNull(res.getUrl());
                    res.getRawContent().thenAccept(raw -> assertEquals(content, raw))
                            .exceptionally(t -> {
                                fail(t);
                                return null;
                            });
                    System.out.println("Test log has been shared at " + res.getUrl());
                })
                .exceptionally(t -> {
                    fail(t);
                    return null;
                });
    }

    @Test
    void getLogInsights() {
        assertDoesNotThrow(() -> {
            CompletableFuture<UploadLogResponse> response = client.uploadLog(Paths.get("src/test/resources/logs/three.log.gz"));
            UploadLogResponse res = response.get();
            assertNotNull(res.getId());
            assertNotNull(res.getUrl());
            CompletableFuture<InsightsResponse> insightsResponse = res.getInsights();
            InsightsResponse insights = insightsResponse.get();
            assertNotNull(insights);
            assertNotNull(insights.getAnalysis());
            assertNotNull(insights.getAnalysis().getInformation());
            assertNotNull(insights.getAnalysis().getProblems());
        });
    }

    @Test
    void analyseLog() {
        assertDoesNotThrow(() -> {
            CompletableFuture<InsightsResponse> insightsResponse = client.analyseLog(Paths.get("src/test/resources/logs/three.log.gz"));
            InsightsResponse insights = insightsResponse.get();
            assertNotNull(insights);
            assertNotNull(insights.getAnalysis());
            assertNotNull(insights.getAnalysis().getInformation());
            assertNotNull(insights.getAnalysis().getProblems());
        });
    }

    @Test
    void getStorageLimits() {
        assertDoesNotThrow(() -> {
            CompletableFuture<Limits> limitsResponse = client.getLimits();
            Limits limits = limitsResponse.get();
            assertNotNull(limits);
            assertTrue(limits.getStorageTime() > 0);
            assertTrue(limits.getMaxLength() > 0);
            assertTrue(limits.getMaxLines() > 0);
        });
    }
}
