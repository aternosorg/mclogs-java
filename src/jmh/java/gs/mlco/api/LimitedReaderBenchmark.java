package gs.mlco.api;

import gs.mclo.api.internal.LimitedReader;
import gs.mclo.api.response.Limits;
import org.openjdk.jmh.annotations.*;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Threads(Threads.MAX)
public class LimitedReaderBenchmark extends ReaderBenchmark {
    protected final int lineLimit = 1000;
    protected final int byteLimit = 10_000;

    public LimitedReaderBenchmark() {
        super(2500);
    }

    @Benchmark
    public int testWithBufferedReaderByteLimit() throws IOException {
        return test(bufferedReader(), byteLimit, null);
    }

    @Benchmark
    public int testWithBufferedReaderLineLimit() throws IOException {
        return test(bufferedReader(), null, lineLimit);
    }

    @Benchmark
    public int testWithInputStreamByteLimit() throws IOException {
        return test(inputStreamReader(), byteLimit, null);
    }

    @Benchmark
    public int testWithInputStreamLineLimit() throws IOException {
        return test(inputStreamReader(), null, lineLimit);
    }

    @Benchmark
    public int testWithBufferedInputStreamByteLimit() throws IOException {
        return test(bufferedInputStreamReader(), byteLimit, null);
    }

    @Benchmark
    public int testWithBufferedInputStreamLineLimit() throws IOException {
        return test(bufferedInputStreamReader(), null, lineLimit);
    }

    private int test(Reader in, Integer byteLimit, Integer lineLimit) throws IOException {
        try (Reader reader = new LimitedReader(in, new Limits(1, byteLimit, lineLimit))) {
            StringWriter writer = new StringWriter();
            reader.transferTo(writer);
            return writer.toString().length();
        }
    }

    private Reader bufferedReader() throws IOException {
        return Files.newBufferedReader(filePath);
    }

    private Reader inputStreamReader() throws IOException {
        return new InputStreamReader(Files.newInputStream(filePath));
    }

    private Reader bufferedInputStreamReader() throws IOException {
        return new BufferedReader(inputStreamReader());
    }
}
