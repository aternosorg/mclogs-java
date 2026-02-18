package gs.mclo.api;

import gs.mclo.api.data.Metadata;
import gs.mclo.api.internal.filter.*;
import gs.mclo.api.reader.FileLogReader;
import gs.mclo.api.reader.LogReader;
import gs.mclo.api.reader.StringLogReader;
import gs.mclo.api.response.Limits;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class Log {
    /**
     * pattern for IPv4 addresses
     */
    @Deprecated
    public static final Pattern IPV4_PATTERN = Pattern.compile("(?<!([0-9]|-|\\w))(?:[1-2]?[0-9]{1,2}\\.){3}[1-2]?[0-9]{1,2}(?!([0-9]|-|\\w))");

    /**
     * whitelist patterns for IPv4 addresses
     */
    @Deprecated
    public static final Pattern[] IPV4_FILTER = new Pattern[]{
            Pattern.compile("127\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}"),
            Pattern.compile("0\\.0\\.0\\.0"),
            Pattern.compile("1\\.[01]\\.[01]\\.1"),
            Pattern.compile("8\\.8\\.[84]\\.[84]"),
    };

    /**
     * pattern for IPv6 addresses
     */
    @Deprecated
    public static final Pattern IPV6_PATTERN = Pattern.compile("(?<!([0-9]|-|\\w))(?:[0-9a-f]{0,4}:){7}[0-9a-f]{0,4}(?!([0-9]|-|\\w))", Pattern.CASE_INSENSITIVE);

    /**
     * whitelist patterns for IPv4 addresses
     */
    @Deprecated
    public static final Pattern[] IPV6_FILTER = new Pattern[]{
            Pattern.compile("[0:]+1?"),
    };

    private final LogReader reader;

    /**
     * Log content
     */
    private @Nullable String content;

    /**
     * Optional name of the log source, e.g. a domain or software name.
     */
    private @Nullable String source;

    /**
     * Optional metadata associated with the log.
     */
    private Set<Metadata<?>> metadata = new HashSet<>();

    /**
     * Create a new Log from a Path
     *
     * @param logFile Path to the log file, which must have a file extension of '.log' or '.txt'. The file extension may be suffixed by both `.0` and `.gz`
     */
    public Log(Path logFile) {
        this(new FileLogReader(logFile));
    }

    /**
     * Create a new Log
     *
     * @param content The whole content of the log.
     */
    public Log(String content) {
        this(new StringLogReader(content));
    }

    /**
     * Create a new Log from a log reader
     *
     * @param reader The log reader to read the log from
     */
    public Log(LogReader reader) {
        this.reader = reader;
    }

    /**
     * @return log content
     */
    @Deprecated
    public String getContent(Limits limits) throws IOException {
        return getContent(new FilterList(new Filter[]{
                new TrimFilter(),
                new LimitBytesFilter(limits.getMaxLength()),
                new LimitLinesFilter(limits.getMaxLines()),
                legacyRegex(IPV4_PATTERN, IPV4_FILTER, "**.**.**.**"),
                legacyRegex(IPV6_PATTERN, IPV6_FILTER, "****:****:****:****:****:****:****:****"),
                legacyRegex(IPV6_PATTERN, IPV6_FILTER, "****:****:****:****:****:****:****:****"),
        }));
    }

    /**
     * @return log content
     */
    @ApiStatus.Internal
    public String getContent(FilterList filters) throws IOException {
        if (content != null) {
            return content;
        }

        content = reader.readContents(filters);
        for (var filter : filters.getFilters()) {
            content = filter.apply(content);
        }

        return content;
    }

    /**
     * Get the name of the log source, e.g. a domain or software name.
     *
     * @return Name of the log source or null.
     */
    public @Nullable String getSource() {
        return source;
    }

    /**
     * Set the name of the log source, e.g. a domain or software name.
     *
     * @param source Name of the log source or null.
     * @return this Log instance.
     */
    public Log setSource(@Nullable String source) {
        this.source = source;
        return this;
    }

    /**
     * Get the metadata associated with the log.
     *
     * @return Set of metadata.
     */
    public Set<Metadata<?>> getMetadata() {
        return new HashSet<>(metadata);
    }

    /**
     * Set the metadata associated with the log.
     *
     * @param metadata Set of metadata.
     * @return this Log instance.
     */
    public Log setMetadata(Set<Metadata<?>> metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * Add a metadata entry to the log.
     *
     * @param data Metadata entry to add.
     * @return this Log instance.
     */
    public Log addMetadata(Metadata<?> data) {
        this.metadata.add(data);
        return this;
    }

    private RegexFilter legacyRegex(Pattern pattern, Pattern[] exemptions, String replacement) {
        return new RegexFilter(new ReplacingRegexPattern[]{
                new ReplacingRegexPattern(pattern.pattern(), new char[]{}, replacement),
        }, Arrays.stream(exemptions).map(p -> new RegexPattern(p.pattern(), new char[]{})).toArray(RegexPattern[]::new));
    }
}
