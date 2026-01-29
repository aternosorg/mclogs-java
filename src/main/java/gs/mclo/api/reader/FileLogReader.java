package gs.mclo.api.reader;

import gs.mclo.api.response.Limits;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public final class FileLogReader extends LogReader {
    /**
     * Only allow logs with the file extension `.log` or `.txt` which may be suffixed by:
     * <ul>
     *     <li> up to two dots with numbers as used by BungeeCord
     *     <li> `.gz` for gzip compressed files
     * </ul>
     */
    public static final Pattern ALLOWED_FILE_NAME_PATTERN = Pattern.compile(".*\\.(log|txt)(\\.\\d+){0,2}(\\.gz)?");

    private final Path path;

    public FileLogReader(Path path, Limits limits) {
        super(limits);
        this.path = Objects.requireNonNull(path, "Path must not be null");

        if (!path.getFileName().toString().matches(ALLOWED_FILE_NAME_PATTERN.pattern())) {
            throw new IllegalArgumentException("Forbidden log file name: " + path.getFileName());
        }
    }

    @Override
    protected Reader getReader() throws IOException {
        File file = path.toFile();
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("Log '" + path + "' does not exist");
        }

        InputStream in = Files.newInputStream(path);
        if (path.getFileName().toString().endsWith(".gz")) {
            in = new GZIPInputStream(in);
        }

        return new InputStreamReader(in);
    }
}
