package gs.mclo.api;

import gs.mclo.api.response.Limits;
import gs.mclo.api.util.LimitedReader;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public final class LogReader {
    /**
     * Only allow logs with the file extension `.log` or `.txt` which may be suffixed by:
     * <ul>
     *     <li> up to two dots with numbers as used by BungeeCord
     *     <li> `.gz` for gzip compressed files
     * </ul>
     */
    public static final Pattern ALLOWED_FILE_NAME_PATTERN = Pattern.compile(".*\\.(log|txt)(\\.\\d+){0,2}(\\.gz)?");

    /**
     * The path to the log file
     */
    private final Path path;

    /**
     * The limits used to filter this log
     */
    @Nullable
    private Limits limits;

    /**
     * Creates a new log reader with default limits
     *
     * @param path the path to the log file
     */
    public LogReader(Path path) {
        this(path, Limits.DEFAULT);
    }

    /**
     * Creates a new log reader with custom limits
     *
     * @param path   the path to the log file
     * @param limits the limits to use
     */
    public LogReader(Path path, @Nullable Limits limits) {
        this.path = Objects.requireNonNull(path, "Path must not be null");

        if (!path.getFileName().toString().matches(ALLOWED_FILE_NAME_PATTERN.pattern())) {
            throw new IllegalArgumentException("Forbidden log file name: " + path.getFileName());
        }

        this.limits = limits;
    }

    /**
     * Sets the limits for this log
     *
     * @param limits the limits to use
     * @return this log reader
     */
    public LogReader setLimits(Limits limits) {
        this.limits = limits;
        return this;
    }

    /**
     * Reads the contents of the log file
     *
     * @return the log
     * @throws FileNotFoundException if the log file does not exist
     * @throws IOException           if an I/O error occurs
     */
    public String readContents() throws IOException {
        File file = path.toFile();
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("Log '" + path + "' does not exist");
        }

        InputStream in = Files.newInputStream(path);
        if (path.getFileName().toString().endsWith(".gz")) {
            in = new GZIPInputStream(in);
        }

        var maxLength = limits != null ? limits.getMaxLength() : null;
        var maxLines = limits != null ? limits.getMaxLines() : null;
        try (var reader = new LimitedReader(new InputStreamReader(in), maxLength, maxLines)) {
            return read(reader);
        }
    }

    /**
     * Reads the log file and returns a log object
     *
     * @return a new log object
     * @throws IOException if an I/O error occurs
     */
    public Log readLog() throws IOException {
        return new Log(readContents());
    }

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
