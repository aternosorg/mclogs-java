import gs.mclo.java.APIResponse;
import gs.mclo.java.MclogsAPI;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class APITest {

    @Test
    void listLogs() {
        assertDoesNotThrow(() -> {
            assertArrayEquals(
                    Stream.of("one.log", "three.log.gz", "two.log").sorted().toArray(String[]::new),
                    Arrays.stream(MclogsAPI.listLogs("src/test/resources")).sorted().toArray(String[]::new)
            );
        });
    }

    @Test
    void shareLog() {
        assertDoesNotThrow(() -> {
            APIResponse response = MclogsAPI.share("src/test/resources/logs/one.log");
            assertTrue(response.success);
            assertNotNull(response.id);
            assertNotNull(response.url);
            assertNull(response.error);

            System.out.println("Test log has been shared at " + response.url);
        });
    }
}
