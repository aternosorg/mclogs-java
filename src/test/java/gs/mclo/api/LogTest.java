package gs.mclo.api;

import gs.mclo.api.response.Limits;

import java.io.IOException;
import java.io.UncheckedIOException;

public class LogTest {
    protected String create(String content) {
        try {
            return new Log(content, Limits.DEFAULT).getContent();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
