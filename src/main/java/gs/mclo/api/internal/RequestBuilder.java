package gs.mclo.api.internal;

import com.google.gson.Gson;
import gs.mclo.api.Log;
import gs.mclo.api.internal.request.UploadLogRequestBody;
import gs.mclo.api.internal.filter.FilterList;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;

public final class RequestBuilder {

    private final Gson gson;

    private @Nullable String projectName = null;
    private @Nullable String projectVersion = null;

    private @Nullable String minecraftVersion = null;

    private @Nullable String customUserAgent = null;

    public RequestBuilder(Gson gson) {
        this.gson = gson;
    }

    /**
     * Set a custom user agent. This will always be preferred over the project name and version.
     *
     * @param customUserAgent the user agent to use
     */
    public void setCustomUserAgent(String customUserAgent) {
        //noinspection ConstantValue
        if (customUserAgent == null || customUserAgent.isEmpty())
            throw new IllegalArgumentException("Custom user agent must not be null or empty");

        this.customUserAgent = customUserAgent;
    }

    /**
     * Set your project name (used for the user agent)
     *
     * @param projectName the name of your project
     */
    public void setProjectName(String projectName) {
        //noinspection ConstantValue
        if (projectName == null || projectName.isEmpty())
            throw new IllegalArgumentException("Project name must not be null or empty");
        this.projectName = projectName;
    }

    /**
     * Set your project version (used for the user agent)
     *
     * @param projectVersion the version of your project
     */
    public void setProjectVersion(String projectVersion) {
        //noinspection ConstantValue
        if (projectVersion == null || projectVersion.isEmpty())
            throw new IllegalArgumentException("Project version must not be null or empty");
        this.projectVersion = projectVersion;
    }

    /**
     * Set the minecraft version (used for the user agent)
     *
     * @param minecraftVersion the version of minecraft
     */
    public void setMinecraftVersion(@Nullable String minecraftVersion) {
        this.minecraftVersion = minecraftVersion;
    }

    /**
     * @return the complete user agent
     */
    private String getUserAgent() {
        if (this.customUserAgent != null) {
            return this.customUserAgent;
        }

        String userAgent = this.projectName + "/" + this.projectVersion;

        if (this.minecraftVersion != null) {
            userAgent += " (Minecraft " + this.minecraftVersion + ")";
        }

        return userAgent;
    }


    public HttpRequest.Builder request(String uri) {
        return HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("User-Agent", this.getUserAgent());
    }

    public HttpRequest uploadRequest(String url, Log log, FilterList filters) throws IOException {
        return request(url)
                .header("Content-Type", "application/json")
                .header("Content-Encoding", "gzip")
                .header("Accept", "application/json")
                .POST(CustomBodyPublishers.ofGzipString(gson.toJson(
                        new UploadLogRequestBody(log.getContent(filters),
                                log.getSource(),
                                log.getMetadata())))
                )
                .build();
    }

    public @Nullable String getProjectName() {
        return projectName;
    }
}
