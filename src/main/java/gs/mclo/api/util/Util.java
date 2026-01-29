package gs.mclo.api.util;

import gs.mclo.api.LogReader;
import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@ApiStatus.Internal
public class Util {

    /**
     * List all files in a directory that match the allowed file name pattern
     * @param directory directory to list files from
     * @return array of file names that can be uploaded
     */
    public static String[] listFilesInDirectory(File directory) {
        if (!directory.exists()) {
            return new String[0];
        }

        String[] files = directory.list();
        if (files == null)
            files = new String[0];

        return Arrays.stream(files)
                .filter(f -> f.matches(LogReader.ALLOWED_FILE_NAME_PATTERN.pattern()))
                .sorted()
                .toArray(String[]::new);
    }

    /**
     * List all files in a directory that match the allowed file name pattern
     * @param directory directory to list files from
     * @return array of file names that can be uploaded
     */
    public static String[] listFilesInDirectory(Path directory) {
        return listFilesInDirectory(directory.toFile());
    }

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
