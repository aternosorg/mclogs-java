package gs.mclo.api;

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
     * log content
     */
    private String content;

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
     */
    public static final Pattern ALLOWED_FILE_NAME_PATTERN = Pattern.compile(".*\\.(log|txt)(\\.\\d+){0,2}(\\.gz)?");

    /**
     * Maximum length of log files that can be uploaded in bytes
     */
    public static final int MAX_LOG_LENGTH = 10 * 1024 * 1024;

    /**
     * Create a new Log
     * @param logFile The log file, which must have a file extension of '.log' or '.txt'. The file extension may be suffixed by both `.0` and `.gz`
     * @throws IOException If an exception occurs while reading the logFile
     */
    public Log(Path logFile) throws IOException {
        if (!Files.exists(logFile)) {
            throw new FileNotFoundException("Log file does not exist!");
        }
        if (Files.isDirectory(logFile)) {
            throw new IllegalArgumentException("Path '" + logFile + "' is a directory, not a file!");
        }
        if (!logFile.getFileName().toString().matches(ALLOWED_FILE_NAME_PATTERN.pattern())) {
            throw new IllegalArgumentException("Log file must have a `.log` or `.txt` file extension. It may be suffixed by both `.0` and `.gz`");
        }

        InputStream in = Files.newInputStream(logFile);
        if (logFile.getFileName().toString().endsWith(".gz")) {
            in = new GZIPInputStream(in);
        }

        this.content = Util.inputStreamToString(in);
        in.close();
        this.filter();
    }

    /**
     * Create a new Log
     * @param content The whole content of the log to share.
     */
    @SuppressWarnings("unused") // API usage
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
        Log log = new Log(Paths.get(dir).resolve(file));
        this.content = log.content;
    }

    /**
     * remove IP addresses
     */
    private void filter() {
        filterIPv4();
        filterIPv6();
        filterLength();
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
     * limit the log length to 10 MB
     */
    private void filterLength() {
        this.content = this.content.trim();
        this.content = this.content.substring(0, Math.min(this.content.length(), MAX_LOG_LENGTH));
    }

    /**
     * @return log content
     */
    public String getContent() {
        return content;
    }
}
