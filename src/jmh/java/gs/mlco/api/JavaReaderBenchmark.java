package gs.mlco.api;

import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Threads(Threads.MAX)
public class JavaReaderBenchmark extends ReaderBenchmark {
    public JavaReaderBenchmark() {
        super(1000);
    }

    @Benchmark
    public void testBufferedReader() throws IOException {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            while (reader.read() != -1) {
                // Read the file
            }
        }
    }

    @Benchmark
    public void testInputStreamReader() throws IOException {
        try (Reader reader = new InputStreamReader(Files.newInputStream(filePath))) {
            while (reader.read() != -1) {
                // Read the file
            }
        }
    }
}
