package gs.mclo.api;

import com.google.gson.Gson;
import gs.mclo.api.response.*;
import gs.mclo.api.util.JsonBodyHandler;
import gs.mclo.api.util.Util;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class MclogsClient {

    private final Gson gson = new Gson();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private @Nullable String projectName = null;
    private @Nullable String projectVersion = null;

    private @Nullable String minecraftVersion = null;

    private @Nullable String customUserAgent = null;

    private Instance instance = new Instance();

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
    public MclogsClient(String projectName, String projectVersion, @Nullable String minecraftVersion) {
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
        //noinspection ConstantValue
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
        //noinspection ConstantValue
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
        //noinspection ConstantValue
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
    public MclogsClient setMinecraftVersion(@Nullable String minecraftVersion) {
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
     * Upload a log to mclogs
     *
     * @param log the log to upload
     * @return the response
     */
    public CompletableFuture<UploadLogResponse> uploadLog(Log log) {
        HttpRequest request = this.uploadRequest(instance.getLogUploadUrl(), log);
        return asyncRequest(request, UploadLogResponse.class);
    }

    /**
     * Upload a log to mclogs
     *
     * @param log content of the log to upload
     * @return the response
     */
    public CompletableFuture<UploadLogResponse> uploadLog(String log) {
        return this.uploadLog(new Log(log));
    }

    /**
     * Upload a log to mclogs
     *
     * @param log path to the log to upload
     * @return the response
     */
    public CompletableFuture<UploadLogResponse> uploadLog(Path log) {
        return this.getLimits()
                .handleAsync((limits, e) -> {
                    if (e != null) {
                        return new LogReader(log);
                    }
                    return new LogReader(log, limits);
                })
                .thenCompose(reader -> {
                    try {
                        return this.uploadLog(reader.readLog());
                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }
                });
    }

    /**
     * Fetch the raw contents of a log from mclogs
     *
     * @param logId the id of the log
     * @return the raw contents of the log
     */
    public CompletableFuture<String> getRawLogContent(String logId) {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(instance.getRawLogUrl(logId)))
                .header("User-Agent", this.getUserAgent())
                .GET()
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);
    }

    /**
     * Fetch the insights for a log from mclogs
     *
     * @param logId the id of the log
     * @return the insights for the log
     */
    public CompletableFuture<InsightsResponse> getInsights(String logId) {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(instance.getLogInsightsUrl(logId)))
                .header("User-Agent", this.getUserAgent())
                .header("Accept", "application/json")
                .GET()
                .build();
        return asyncRequest(request, InsightsResponse.class);
    }

    /**
     * Analyse a log with mclogs without saving it
     *
     * @param log the log to analyse
     * @return the insights of the log
     */
    public CompletableFuture<InsightsResponse> analyseLog(Log log) {
        HttpRequest request = uploadRequest(instance.getLogAnalysisUrl(), log);
        return asyncRequest(request, InsightsResponse.class);
    }

    /**
     * Analyse a log with mclogs without saving it
     *
     * @param log content of the log to analyse
     * @return the insights of the log
     */
    public CompletableFuture<InsightsResponse> analyseLog(String log) {
        return this.analyseLog(new Log(log));
    }

    /**
     * Analyse a log with mclogs without saving it
     *
     * @param log path to the log to analyse
     * @return the insights of the log
     */
    public CompletableFuture<InsightsResponse> analyseLog(Path log) {
        return this.getLimits()
                .handleAsync((limits, e) -> {
                    if (e != null) {
                        return new LogReader(log);
                    }
                    return new LogReader(log, limits);
                })
                .thenCompose(reader -> {
                    try {
                        return this.analyseLog(reader.readLog());
                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }
                });
    }


    /**
     * Get the storage limits of this mclogs instance
     *
     * @return the storage limits
     */
    public CompletableFuture<Limits> getLimits() {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(instance.getStorageLimitUrl()))
                .header("User-Agent", this.getUserAgent())
                .header("Accept", "application/json")
                .GET()
                .build();
        return asyncRequest(request, Limits.class);
    }

    /**
     * List logs in the {@code logs} subdirectory of a path
     *
     * @param directory server/client directory
     * @return log file names
     */
    public String[] listLogsInDirectory(Path directory) {
        return Util.listFilesInDirectory(directory.resolve("logs"));
    }

    /**
     * List logs in the {@code logs} subdirectory of a path
     *
     * @param directory server/client directory
     * @return log file names
     */
    public String[] listLogsInDirectory(String directory) {
        return listLogsInDirectory(Path.of(directory));
    }

    /**
     * List logs in the {@code crash-reports} subdirectory of a path
     *
     * @param directory server/client directory
     * @return log file names
     */
    public String[] listCrashReportsInDirectory(Path directory) {
        return Util.listFilesInDirectory(directory.resolve("crash-reports"));
    }

    /**
     * List logs in the {@code crash-reports} subdirectory of a path
     *
     * @param directory server/client directory
     * @return log file names
     */
    public String[] listCrashReportsInDirectory(String directory) {
        return listCrashReportsInDirectory(Path.of(directory));
    }

    private <T> CompletableFuture<T> asyncRequest(HttpRequest request, Class<T> responseClass) {
        return httpClient.sendAsync(request, new JsonBodyHandler<>(this, responseClass))
                .thenApply(HttpResponse::body);
    }

    private HttpRequest uploadRequest(String url, Log log) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Accept", "application/json")
                .header("User-Agent", this.getUserAgent())
                .POST(HttpRequest.BodyPublishers.ofString("content=" + URLEncoder.encode(log.getContent(), StandardCharsets.UTF_8)))
                .build();
    }

    @ApiStatus.Internal
    public Gson gson() {
        return this.gson;
    }
}
