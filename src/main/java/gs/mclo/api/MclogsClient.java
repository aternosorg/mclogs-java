package gs.mclo.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gs.mclo.api.data.LogField;
import gs.mclo.api.internal.JsonBodyHandler;
import gs.mclo.api.internal.RequestBuilder;
import gs.mclo.api.internal.Util;
import gs.mclo.api.internal.gson.InstantTypeAdapter;
import gs.mclo.api.internal.request.UploadLogRequestBody;
import gs.mclo.api.reader.FileLogReader;
import gs.mclo.api.reader.StringLogReader;
import gs.mclo.api.response.GetLogResponse;
import gs.mclo.api.response.InsightsResponse;
import gs.mclo.api.response.Limits;
import gs.mclo.api.response.UploadLogResponse;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class MclogsClient {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
            .create();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private Instance instance = new Instance();
    private final RequestBuilder requestBuilder = new RequestBuilder();

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
        requestBuilder.setCustomUserAgent(customUserAgent);
        return this;
    }

    /**
     * Set your project name (used for the user agent)
     *
     * @param projectName the name of your project
     * @return this
     */
    public MclogsClient setProjectName(String projectName) {
        requestBuilder.setProjectName(projectName);
        return this;
    }

    /**
     * Set your project version (used for the user agent)
     *
     * @param projectVersion the version of your project
     * @return this
     */
    public MclogsClient setProjectVersion(String projectVersion) {
        requestBuilder.setProjectVersion(projectVersion);
        return this;
    }

    /**
     * Set the minecraft version (used for the user agent)
     *
     * @param minecraftVersion the version of minecraft
     * @return this
     */
    public MclogsClient setMinecraftVersion(@Nullable String minecraftVersion) {
        requestBuilder.setMinecraftVersion(minecraftVersion);
        return this;
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
        // Try new upload method
        HttpRequest request = requestBuilder.request(instance.getLogUploadUrl())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(new UploadLogRequestBody(log.getContent(), log.getSource(), log.getMetadata()))))
                .build();

        return asyncRequest(request, UploadLogResponse.class).handle((response, throwable) -> {
            // No idea why IntelliJ is complaining about this being always true. response is clearly nullable here.
            //noinspection ConstantValue
            if (response != null) {
                return CompletableFuture.completedFuture(response);
            }


            if (throwable != null && throwable.getCause() instanceof APIException) {
                APIException apiException = (APIException) throwable.getCause();
                if (apiException.getMessage().contains("Required POST argument 'content' not found")) {
                    // Fallback to old upload method
                    HttpRequest fallbackRequest = requestBuilder.legacyUpload(instance.getLogUploadUrl(), log);
                    return asyncRequest(fallbackRequest, UploadLogResponse.class);
                }
            }
            throw new CompletionException(throwable);
        }).thenCompose(x -> x);
    }

    /**
     * Upload a log to mclogs
     *
     * @param log content of the log to upload
     * @return the response
     */
    public CompletableFuture<UploadLogResponse> uploadLog(String log) {
        return this.getLog(log).thenCompose(this::uploadLog);
    }

    /**
     * Upload a log to mclogs
     *
     * @param log path to the log to upload
     * @return the response
     */
    public CompletableFuture<UploadLogResponse> uploadLog(Path log) {
        return this.getLog(log).thenCompose(this::uploadLog);
    }

    /**
     * Fetch the raw contents of a log from mclogs
     *
     * @param logId the id of the log
     * @return the raw contents of the log
     */
    public CompletableFuture<String> getRawLogContent(String logId) {
        HttpRequest request = requestBuilder.request(instance.getRawLogUrl(logId))
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
        HttpRequest request = requestBuilder.request(instance.getLogInsightsUrl(logId))
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
        HttpRequest request = requestBuilder.legacyUpload(instance.getLogAnalysisUrl(), log);
        return asyncRequest(request, InsightsResponse.class);
    }

    /**
     * Analyse a log with mclogs without saving it
     *
     * @param log content of the log to analyse
     * @return the insights of the log
     */
    public CompletableFuture<InsightsResponse> analyseLog(String log) {
        return this.getLog(log).thenCompose(this::analyseLog);
    }

    /**
     * Analyse a log with mclogs without saving it
     *
     * @param log path to the log to analyse
     * @return the insights of the log
     */
    public CompletableFuture<InsightsResponse> analyseLog(Path log) {
        return this.getLog(log).thenCompose(this::analyseLog);
    }

    /**
     * Get the storage limits of this mclogs instance
     *
     * @return the storage limits
     */
    public CompletableFuture<Limits> getLimits() {
        HttpRequest request = requestBuilder.request(instance.getStorageLimitUrl())
                .header("Accept", "application/json")
                .GET()
                .build();
        return asyncRequest(request, Limits.class);
    }

    /**
     * Fetch a log with optional fields
     *
     * @param id     id of the log to fetch
     * @param fields Which optional log fields to include
     * @return the log
     */
    public CompletableFuture<GetLogResponse> getLog(String id, LogField... fields) {
        var uri = instance.getLogUrl(id);
        if (fields.length > 0) {
            uri += "?" + String.join("&", Arrays.stream(fields).map(LogField::getName).toArray(String[]::new));
        }

        HttpRequest request = requestBuilder.request(uri)
                .header("Accept", "application/json")
                .GET()
                .build();
        return asyncRequest(request, GetLogResponse.class);
    }

    /**
     * Delete a log
     *
     * @param id    id of the log to delete
     * @param token deletion token of the log to delete
     * @return a future that completes when the log is deleted
     */
    public CompletableFuture<Void> deleteLog(String id, @Nullable String token) {
        HttpRequest request = requestBuilder.request(instance.getLogUrl(id))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();
        return asyncRequest(request, Void.class).thenAccept(x -> {
        });
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

    private CompletableFuture<Limits> getLimitsOrDefault() {
        return this.getLimits().exceptionally(t -> Limits.DEFAULT);
    }

    private CompletableFuture<Log> getLog(Path log) {
        return this.getLimitsOrDefault().thenApply(limits -> {
            try {
                return new Log(new FileLogReader(log, limits));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    private CompletableFuture<Log> getLog(String log) {
        return this.getLimitsOrDefault().thenApply(limits -> {
            try {
                return new Log(new StringLogReader(log, limits));
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        });
    }

    @ApiStatus.Internal
    public Gson gson() {
        return this.gson;
    }
}
