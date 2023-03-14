package gs.mclo.api;

import com.google.gson.Gson;
import gs.mclo.api.response.InsightsResponse;
import gs.mclo.api.response.UploadLogResponse;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

public class MclogsClient {
    protected String projectName = null;
    protected String projectVersion = null;

    protected String minecraftVersion = null;

    protected String customUserAgent = null;

    protected Instance instance = new Instance();

    protected Gson gson = new Gson();

    /**
     * Create a new Mclogs instance with a custom user agent
     * If you just want to pass along your name and version check out {@link #MclogsClient(String, String)}
     * If you're running alongside minecraft, please use {@link #MclogsClient(String, String, String)}
     *
     * @param customUserAgent the user agent to use
     */
    public MclogsClient(String customUserAgent) {
        this.setCustomUserAgent(customUserAgent);
    }

    /**
     * Create a new Mclogs instance
     * If you're running alongside minecraft, please use {@link #MclogsClient(String, String, String)}
     *
     * @param projectName    the name of your project (used for the user agent)
     * @param projectVersion the version of your project (used for the user agent)
     */
    public MclogsClient(String projectName, String projectVersion) {
        this(projectName, projectVersion, null);
    }

    /**
     * Create a new Mclogs instance for a project running alongside minecraft (e.g. a mod or plugin)
     * If you're not running alongside minecraft, use {@link #MclogsClient(String, String)}
     *
     * @param projectName      the name of your project (used for the user agent)
     * @param projectVersion   the version of your project (used for the user agent)
     * @param minecraftVersion the version of minecraft (used for the user agent)
     */
    public MclogsClient(String projectName, String projectVersion, String minecraftVersion) {
        this.setProjectName(projectName);
        this.setProjectVersion(projectVersion);
        this.setMinecraftVersion(minecraftVersion);
    }

    /**
     * Set a custom user agent. This will always be preferred over the project name and version.
     *
     * @param customUserAgent the user agent to use
     * @return this
     */
    public MclogsClient setCustomUserAgent(String customUserAgent) {
        if (customUserAgent == null || customUserAgent.isEmpty())
            throw new IllegalArgumentException("Custom user agent must not be null or empty");

        this.customUserAgent = customUserAgent;
        return this;
    }

    /**
     * Set your project name (used for the user agent)
     *
     * @param projectName the name of your project
     * @return this
     */
    public MclogsClient setProjectName(String projectName) {
        if (projectName == null || projectName.isEmpty())
            throw new IllegalArgumentException("Project name must not be null or empty");
        this.projectName = projectName;
        return this;
    }

    /**
     * Set your project version (used for the user agent)
     *
     * @param projectVersion the version of your project
     * @return this
     */
    public MclogsClient setProjectVersion(String projectVersion) {
        if (projectVersion == null || projectVersion.isEmpty())
            throw new IllegalArgumentException("Project version must not be null or empty");
        this.projectVersion = projectVersion;
        return this;
    }

    /**
     * Set the minecraft version (used for the user agent)
     *
     * @param minecraftVersion the version of minecraft
     * @return this
     */
    public MclogsClient setMinecraftVersion(String minecraftVersion) {
        this.minecraftVersion = minecraftVersion;
        return this;
    }

    /**
     * @return the complete user agent
     */
    protected String getUserAgent() {
        if (this.customUserAgent != null) {
            return this.customUserAgent;
        }

        String userAgent = this.projectName + "/" + this.projectVersion;

        if (this.minecraftVersion != null) {
            userAgent += " (Minecraft " + this.minecraftVersion + ")";
        }

        return userAgent;
    }

    /**
     * @return the instance to upload logs to
     */
    public Instance getInstance() {
        return instance;
    }

    /**
     * Set the instance to upload logs to.
     *
     * @param instance the instance to upload logs to
     * @return this
     */
    public MclogsClient setInstance(Instance instance) {
        this.instance = instance;
        return this;
    }

    /**
     * Upload a log to mclo.gs
     *
     * @param log the log to upload
     * @return the response
     */
    public UploadLogResponse uploadLog(Log log) throws IOException {
        //connect to api
        URL url = new URL(instance.getLogUploadUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            //convert log to application/x-www-form-urlencoded
            String content = "content=" + URLEncoder.encode(log.getContent(), StandardCharsets.UTF_8.toString());
            byte[] out = content.getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            //send log to api
            connection.setFixedLengthStreamingMode(length);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setRequestProperty("Accepts", "application/json");
            connection.setRequestProperty("User-Agent", this.getUserAgent());
            connection.connect();
            try (OutputStream os = connection.getOutputStream()) {
                os.write(out);
            }

            //handle response
            UploadLogResponse response = gson.fromJson(
                    Util.inputStreamToString(connection.getInputStream()),
                    UploadLogResponse.class
            );
            response.setClient(this).throwIfError();
            connection.disconnect();
            return response;
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Upload a log to mclo.gs
     *
     * @param log the log to upload
     * @return the response
     */
    public UploadLogResponse uploadLog(String log) throws IOException {
        return this.uploadLog(new Log(log));
    }

    /**
     * Upload a log to mclo.gs
     *
     * @param log the log to upload
     * @return the response
     */
    public UploadLogResponse uploadLog(Path log) throws IOException {
        return this.uploadLog(new Log(log));
    }

    /**
     * Fetch the raw contents of a log from mclo.gs
     * @param logId the id of the log
     * @return the raw contents of the log
     * @throws IOException if an error occurs while fetching the log
     */
    public String getRawLogContent(String logId) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(instance.getRawLogUrl(logId)).openConnection();
        try {
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", this.getUserAgent());
            connection.connect();
            String response = Util.inputStreamToString(connection.getInputStream());
            connection.disconnect();
            return response;
        }
        finally {
            connection.disconnect();
        }
    }

    /**
     * Fetch the insights for a log from mclo.gs
     * @param logId the id of the log
     * @return the insights for the log
     * @throws IOException if an error occurs while fetching the insights
     */
    public InsightsResponse getInsights(String logId) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(instance.getLogInsightsUrl(logId)).openConnection();
        try {
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", this.getUserAgent());
            connection.setRequestProperty("Accepts", "application/json");
            connection.connect();
            InsightsResponse response = gson.fromJson(
                    Util.inputStreamToString(connection.getInputStream()),
                    InsightsResponse.class
            );
            response.throwIfError();
            connection.disconnect();
            return response;
        }
        finally {
            connection.disconnect();
        }
    }

    /**
     * list logs in a directory
     * @param directory server/client directory
     * @return log file names
     */
    public String[] listLogsInDirectory(String directory){
        File logsDirectory = new File(directory, "logs");

        if (!logsDirectory.exists()) {
            return new String[0];
        }

        String[] files = logsDirectory.list();
        if (files == null)
            files = new String[0];

        return Arrays.stream(files)
                .filter(file -> file.matches(Log.ALLOWED_FILE_NAME_PATTERN.pattern()))
                .sorted()
                .toArray(String[]::new);
    }

    /**
     * list crash reports in a directory
     * @param directory server/client directory
     * @return log file names
     */
    public String[] listCrashReportsInDirectory(String directory){
        File crashReportDirectory = new File(directory, "crash-reports");

        if (!crashReportDirectory.exists()) {
            return new String[0];
        }

        String[] files = crashReportDirectory.list();
        if (files == null)
            files = new String[0];

        return Arrays.stream(files)
                .filter(file -> file.matches(Log.ALLOWED_FILE_NAME_PATTERN.pattern()))
                .sorted()
                .toArray(String[]::new);
    }
}
