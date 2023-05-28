package gs.mclo.api;

import com.google.gson.Gson;

import java.io.*;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class Util {

    public static String[] listFilesInDirectory(File file) {
        if(!file.exists())
            return new String[0];
        String[] files = file.list();
        if (files == null)
            files = new String[0];

        return Arrays.stream(files)
                .filter(f -> f.matches(Log.ALLOWED_FILE_NAME_PATTERN.pattern()))
                .sorted()
                .toArray(String[]::new);
    }

    public static <T> HttpResponse.BodyHandler<T> parseResponse(Class<T> clazz, Gson gson) {
        return responseInfo -> HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8),
                body -> gson.fromJson(body, clazz));
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
