package gs.mclo.api.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class TestUtil {
    /**
     * parse an input stream to a string
     * @param is input stream
     * @return stream content
     */
    public static String inputStreamToString (InputStream is) {
        return new BufferedReader(new InputStreamReader(is))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    /**
     * Get the contents of the file located at this path
     * @param path path to file
     * @return file content
     * @throws UncheckedIOException error reading file
     */
    public static String getFileContents(String path) {
        try {
            return inputStreamToString(Files.newInputStream(Paths.get(path)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Get the contents of the GZIP compressed file located at this path
     * @param path path to file
     * @return file content
     * @throws UncheckedIOException error reading file
     */
    public static String getGZIPFileContents(String path) {
        try {
            return inputStreamToString(new GZIPInputStream(Files.newInputStream(Paths.get(path))));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
