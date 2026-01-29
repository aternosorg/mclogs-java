package gs.mclo.api.internal;

import gs.mclo.api.LogReader;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

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
}
