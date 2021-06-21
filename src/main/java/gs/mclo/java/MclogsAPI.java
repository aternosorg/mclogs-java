package gs.mclo.java;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MclogsAPI {
    public static String mcversion = "unknown";
    public static String userAgent = "unknown";
    public static String version = "unknown";

    /**
     * share a log to the mclogs API
     * @param dir logs directory
     * @param file log file name
     * @return mclogs response
     * @throws IOException error reading/sharing file
     */
    public static APIResponse share(String dir, String file) throws IOException {
        Log log = new Log(dir, file);

        //connect to api
        URL url = new URL("https://api.mclo.gs/1/log");
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        //convert log to application/x-www-form-urlencoded
        String content = "content=" + URLEncoder.encode(log.getContent(), StandardCharsets.UTF_8.toString());
        byte[] out = content.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        //send log to api
        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.setRequestProperty("User-Agent", userAgent + "/" + version + "/" + mcversion);
        http.connect();
        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        }

        //handle response
        return APIResponse.parse(Util.inputStreamToString(http.getInputStream()));
    }

    /**
     * list logs in a directory
     * @param rundir server/client directory
     * @return log file names
     */
    public static String[] listLogs(String rundir){
        File logdir = new File(rundir, "logs");

        String[] files = logdir.list();
        if (files == null)
            files = new String[0];

        return Arrays.stream(files)
                .filter(file -> file.endsWith(".log") || file.endsWith(".log.gz"))
                .toArray(String[]::new);
    }
}