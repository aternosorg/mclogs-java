package gs.mclo.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class Util {

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
     * @throws IOException error reading file
     */
    public static String getFileContents(String path) throws IOException {
        return Util.inputStreamToString(Files.newInputStream(Paths.get(path)));
    }

    /**
     * Get the contents of the GZIP compressed file located at this path
     * @param path path to file
     * @return file content
     * @throws IOException error reading file
     */
    public static String getGZIPFileContents(String path) throws IOException {
        return Util.inputStreamToString(new GZIPInputStream(Files.newInputStream(Paths.get(path))));
    }
}
