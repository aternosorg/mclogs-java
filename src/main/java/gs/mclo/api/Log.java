package gs.mclo.api;

import gs.mclo.api.response.Limits;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class Log {
    /**
     * pattern for IPv4 addresses
     */
    public static final Pattern IPV4_PATTERN = Pattern.compile("(?<!([0-9]|-|\\w))(?:[1-2]?[0-9]{1,2}\\.){3}[1-2]?[0-9]{1,2}(?!([0-9]|-|\\w))");

    /**
     * whitelist patterns for IPv4 addresses
     */
    public static final Pattern[] IPV4_FILTER = new Pattern[]{
            Pattern.compile("127\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}"),
            Pattern.compile("0\\.0\\.0\\.0"),
            Pattern.compile("1\\.[01]\\.[01]\\.1"),
            Pattern.compile("8\\.8\\.[84]\\.[84]"),
    };

    /**
     * pattern for IPv6 addresses
     */
    public static final Pattern IPV6_PATTERN = Pattern.compile("(?<!([0-9]|-|\\w))(?:[0-9a-f]{0,4}:){7}[0-9a-f]{0,4}(?!([0-9]|-|\\w))", Pattern.CASE_INSENSITIVE);

    /**
     * whitelist patterns for IPv4 addresses
     */
    public static final Pattern[] IPV6_FILTER = new Pattern[]{
            Pattern.compile("[0:]+1?"),
    };

    /**
     * Only allow logs with the file extension `.log` or `.txt` which may be suffixed by:
     * <ul>
     *     <li> up to two dots with numbers as used by BungeeCord
     *     <li> `.gz` for gzip compressed files
     * </ul>
     * @deprecated Use {@link LogReader#ALLOWED_FILE_NAME_PATTERN} instead
     */
    @Deprecated(since = "4.2", forRemoval = true)
    public static final Pattern ALLOWED_FILE_NAME_PATTERN = LogReader.ALLOWED_FILE_NAME_PATTERN;

    /**
     * Maximum length of log files that can be uploaded in bytes
     * @deprecated Since these limits configurable use {@link MclogsClient#getLimits()} to fetch them
     */
    @Deprecated(since = "4.2", forRemoval = true)
    public static final int MAX_LOG_LENGTH = 10 * 1024 * 1024;

    /**
     * Log content
     */
    private String content;

    /**
     * Create a new Log from a Path
     * @param logFile Path to the log file, which must have a file extension of '.log' or '.txt'. The file extension may be suffixed by both `.0` and `.gz`
     * @throws IOException If an exception occurs while reading the logFile
     */
    public Log(Path logFile) throws IOException {
        this(new LogReader(logFile));
    }

    /**
     * Create a new Log from a log reader
     * @param reader The log reader to read the log from
     * @throws IOException If an exception occurs while reading the logFile
     */
    public Log(LogReader reader) throws IOException {
        this(reader.readContents());
    }

    /**
     * Create a new Log
     * @param content The whole content of the log to share.
     */
    public Log(String content) {
        this.content = content;
        this.filter();
    }

    /**
     * Create a new Log
     * @param dir The parent directory of the log file
     * @param file The log file, which must have a file extension of '.log' or '.txt'. The file extension may be suffixed by both `.0` and `.gz`
     * @throws IOException If an exception occurs while reading the logFile
     * @deprecated Use {@link Log#Log(Path)} instead
     */
    @Deprecated
    public Log(String dir, String file) throws IOException {
        this(Paths.get(dir).resolve(file));
    }

    /**
     * remove IP addresses
     */
    private void filter() {
        this.filterIPv4();
        this.filterIPv6();
    }

    /**
     * remove IPv4 addresses
     */
    private void filterIPv4() {
        Matcher matcher = IPV4_PATTERN.matcher(this.content);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            if (isWhitelistedIPv4(matcher.group())) {
                continue;
            }
            matcher.appendReplacement(sb, "**.**.**.**");
        }
        matcher.appendTail(sb);
        this.content = sb.toString();
    }

    /**
     * does this IPv4 address match any whitelist filters
     * @param s string to test
     * @return matches
     */
    private boolean isWhitelistedIPv4(String s) {
        for (Pattern filter: IPV4_FILTER) {
            if (s.matches(filter.pattern())) {
                return true;
            }
        }
        return false;
    }

    /**
     * remove IPv6 addresses
     */
    private void filterIPv6() {
        Matcher matcher = IPV6_PATTERN.matcher(this.content);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            if (isWhitelistedIPv6(matcher.group())) {
                continue;
            }
            matcher.appendReplacement(sb, "****:****:****:****:****:****:****:****");
        }
        matcher.appendTail(sb);
        this.content = sb.toString();
    }

    /**
     * does this IPv6 address match any whitelist filters
     * @param s string to test
     * @return matches
     */
    private boolean isWhitelistedIPv6(String s) {
        for (Pattern filter: IPV6_FILTER) {
            if (s.matches(filter.pattern())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return log content
     */
    public String getContent() {
        return content;
    }
}
