package gs.mclo.api;

import gs.mclo.api.response.Limits;

import java.io.IOException;
import java.io.UncheckedIOException;

public class LogTest {
    protected Log create(String content) {
        try {
            return new Log(content, Limits.DEFAULT);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
