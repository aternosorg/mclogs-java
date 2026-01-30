package gs.mclo.api;

import org.junit.jupiter.api.BeforeEach;

public class ApiTest {
    protected MclogsClient client;

    @BeforeEach
    protected void setUp() throws InterruptedException {
        client = new MclogsClient("aternos/mclogs-java-tests");

        var apiUrl = System.getenv("MCLOGS_API_URL");

        if ((apiUrl != null && !apiUrl.isBlank())) {
            client.setInstance(new Instance(apiUrl));
        }

        Thread.sleep(50); // Avoid rate limiting
    }
}
