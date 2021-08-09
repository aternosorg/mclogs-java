package gs.mclo.java;

import java.io.*;
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
    private static final Pattern IPV4_PATTERN = Pattern.compile("(?:[1-2]?[0-9]{1,2}\\.){3}[1-2]?[0-9]{1,2}");

    /**
     * pattern for IPv6 addresses
     */
    private static final Pattern IPV6_PATTERN = Pattern.compile("(?:[0-9a-f]{0,4}:){7}[0-9a-f]{0,4}%", Pattern.CASE_INSENSITIVE);

    /**
     * Create a new Log
     * @param logFile The log file, which must have a file extension of '.log'. The file extension may be suffixed by both `.0` and `.gz`
     * @throws IOException If an exception occurs while reading the logFile
     */
    public Log(Path logFile) throws IOException {
        if (!Files.exists(logFile)) {
            throw new FileNotFoundException("Log file does not exist!");
        }
        if (Files.isDirectory(logFile)) {
            throw new IllegalArgumentException("Path '" + logFile + "' is a directory, not a file!");
        }
        if (!logFile.getFileName().toString().matches(".*\\.log(\\.0)?(\\.gz)?")) {
            throw new IllegalArgumentException("Log file must have a `.log` file extension. It may be suffixed by both `.0` and `.gz`");
        }

        InputStream in = Files.newInputStream(logFile);
        if (logFile.endsWith(".gz")) {
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
     * @param file The log file, which must have a file extension of '.log'. The file extension may be suffixed by both `.0` and `.gz`
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
    }

    /**
     * remove IPv4 addresses
     */
    private void filterIPv4() {
        Matcher matcher = IPV4_PATTERN.matcher(this.content);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group().replaceAll("[0-9]", "*"));
        }
        matcher.appendTail(sb);
        this.content = sb.toString();
    }

    /**
     * remove IPv6 addresses
     */
    private void filterIPv6() {
        Matcher matcher = IPV6_PATTERN.matcher(this.content);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group().replaceAll("[0-9a-fA-F]", "*"));
        }
        matcher.appendTail(sb);
        this.content = sb.toString();
    }

    /**
     * @return log content
     */
    public String getContent() {
        return content;
    }
}
