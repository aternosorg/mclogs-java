package gs.mclo.api.reader;

import java.io.Reader;
import java.io.StringReader;
import java.util.Objects;

public final class StringLogReader extends LogReader {
    private final String input;

    public StringLogReader(String input) {
        super();
        this.input = Objects.requireNonNull(input, "Input must not be null");
    }

    @Override
    protected Reader getReader() {
        return new StringReader(input);
    }
}
