package gs.mclo.java;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class MclogsAPI {
    public static String mcversion = "unknown";
    public static String userAgent = "unknown";
    public static String version = "unknown";
    private static String apiHost = "api.mclo.gs";
    private static String protocol = "https";

    /**
     * share a log to the mclogs API
     * @param log The {@link Log} to share
     * @return mclogs {@link APIResponse}
     * @throws IOException error sharing the log content
     */
    public static APIResponse share(Log log) throws IOException {
        //connect to api
        URL url = new URL(protocol + "://" + apiHost + "/1/log");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        //convert log to application/x-www-form-urlencoded
        String content = "content=" + URLEncoder.encode(log.getContent(), StandardCharsets.UTF_8.toString());
        byte[] out = content.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        //send log to api
        connection.setFixedLengthStreamingMode(length);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("User-Agent", userAgent + "/" + version + "/" + mcversion);
        connection.connect();
        try (OutputStream os = connection.getOutputStream()) {
            os.write(out);
        }

        //handle response
        return APIResponse.parse(Util.inputStreamToString(connection.getInputStream()));
    }

    /**
     * share a log to the mclogs API
     * @param logFile The log file, which must have a file extension of '.log'. The file extension may be suffixed by both `.0` and `.gz`
     * @return mclogs {@link APIResponse}
     * @throws IOException error reading/sharing file
     */
    public static APIResponse share(Path logFile) throws IOException {
        return share(new Log(logFile));
    }

    /**
     * share a log to the mclogs API
     * @param dir The parent directory of the log file
     * @param file The log file, which must have a file extension of '.log'. The file extension may be suffixed by both `.0` and `.gz`
     * @return mclogs {@link APIResponse}
     * @throws IOException error reading/sharing file
     * @deprecated Use {@link MclogsAPI#share(Log)} or {@link MclogsAPI#share(Path)} instead
     */
    @Deprecated
    public static APIResponse share(String dir, String file) throws IOException {
        return share(Paths.get(dir).resolve(file));
    }

    /**
     * get the raw log contents
     * @param id log id
     * @return log content
     * @throws IOException connection error
     */
    public static String get(String id) throws IOException {
        if (id == null) {
            throw new IllegalArgumentException("No ID provided!");
        }
        URL url = new URL(protocol + "://" + apiHost + "/1/raw/" + id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        connection.connect();
        return Util.inputStreamToString(connection.getInputStream());
    }

    /**
     * list logs in a directory
     * @param directory server/client directory
     * @return log file names
     */
    public static String[] listLogs(String directory){
        File logsDirectory = new File(directory, "logs");

        if (!logsDirectory.exists()) {
            return new String[0];
        }

        String[] files = logsDirectory.list();
        if (files == null)
            files = new String[0];

        return Arrays.stream(files)
                .filter(file -> file.endsWith(".log") || file.endsWith(".log.gz"))
                .sorted()
                .toArray(String[]::new);
    }

    /**
     * list crash reports in a directory
     * @param directory server/client directory
     * @return log file names
     */
    public static String[] listCrashReports(String directory){
        File crashReportDirectory = new File(directory, "crash-reports");

        if (!crashReportDirectory.exists()) {
            return new String[0];
        }

        String[] files = crashReportDirectory.list();
        if (files == null)
            files = new String[0];

        return Arrays.stream(files)
                .sorted()
                .toArray(String[]::new);
    }

    /**
     * @return api host URL
     */
    public static String getApiHost() {
        return apiHost;
    }

    /**
     * @param apiHost api host url
     */
    public static void setApiHost(String apiHost) {
        if (apiHost != null && apiHost.length() > 0) MclogsAPI.apiHost = apiHost;
    }

    /**
     * @return protocol
     */
    public static String getProtocol() {
        return protocol;
    }

    /**
     * @param protocol protocol
     */
    public static void setProtocol(String protocol) {
        if (protocol == null) return;
        switch (protocol) {
            case "http":
            case "https":
                MclogsAPI.protocol = protocol;
                break;
        }
    }
}