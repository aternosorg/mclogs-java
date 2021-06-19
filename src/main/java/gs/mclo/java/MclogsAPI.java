package gs.mclo.java;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

public class MclogsAPI {
    public static String mcversion = "unknown";
    public static String userAgent = "unknown";
    public static String version = "unknown";

    /**
     * share a log to the mclogs API
     * @param path file path
     * @return mclogs response
     * @throws IOException error reading/sharing file
     */
    public static APIResponse share(String path) throws IOException {
        Log log = new Log(path);

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
        File logdir = new File(rundir + "/logs");

        String[] logs = logdir.list();
        if (logs == null)
            logs = new String[0];

        ArrayList<String> logsList = new ArrayList<>();
        for (String log:logs) {
            if (log.endsWith(".log")||log.endsWith(".log.gz"))
                logsList.add(log);
        }

        Collections.sort(logsList);
        return logsList.toArray(new String[0]);
    }
}