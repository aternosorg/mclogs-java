package gs.mclo.api;

import gs.mclo.api.reader.StringLogReader;
import gs.mclo.api.response.Limits;

import java.io.IOException;
import java.io.UncheckedIOException;

public class LogTest {
    protected String create(String content) {
        try {
            return new Log(new StringLogReader(content)).getContent(Limits.DEFAULT);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
