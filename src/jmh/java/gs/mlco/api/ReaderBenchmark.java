package gs.mlco.api;

import org.openjdk.jmh.annotations.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

@State(Scope.Benchmark)
public abstract class ReaderBenchmark {

    protected final int lineCount;

    protected Path filePath;

    protected ReaderBenchmark(int lineCount) {
        this.lineCount = lineCount;
    }

    @Setup
    public void setup() throws IOException {
        var random = new Random();
        filePath = Path.of(System.getProperty("java.io.tmpdir"))
                .resolve("test-file-" + random.nextInt(1_000_000) + ".log");

        // Create a large file that the reader may need to cut off
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (int i = 0; i < lineCount; i++) {
                writer.write("This is a test log line " + i + "\n");
            }
        }
    }

    @TearDown
    public void tearDown() throws IOException {
        Files.deleteIfExists(filePath);
    }
}
