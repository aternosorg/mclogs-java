package gs.mclo.api.reader;

import gs.mclo.api.internal.LimitedReader;
import gs.mclo.api.response.Limits;
import org.jetbrains.annotations.ApiStatus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

@ApiStatus.NonExtendable
public abstract class LogReader {

    protected LogReader() {
    }

    /**
     * Reads the contents of the log file
     *
     * @return the log
     * @throws FileNotFoundException if the log file does not exist
     * @throws IOException           if an I/O error occurs
     */
    public String readContents(Limits limits) throws IOException {
        try (var reader = new LimitedReader(this.getReader(), limits)) {
            return read(reader);
        }
    }

    /**
     * Get the upstream reader
     * @return a reader
     */
    protected abstract Reader getReader() throws IOException;

    /**
     * Read the outputs of an entire reader into a string
     *
     * @param reader the reader to read from
     * @return the string
     * @throws IOException if an I/O error occurs
     */
    private String read(Reader reader) throws IOException {
        StringWriter writer = new StringWriter();
        reader.transferTo(writer);
        return writer.toString();
    }
}
