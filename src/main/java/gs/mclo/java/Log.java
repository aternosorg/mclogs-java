package gs.mclo.java;

import java.io.*;
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
     * whitelist patterns for IPv4 addresses
     */
    private static final Pattern[] IPV4_FILTER = new Pattern[]{
            Pattern.compile("127\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}"),
            Pattern.compile("0\\.0\\.0\\.0"),
            Pattern.compile("1\\.[01]\\.[01]\\.1"),
            Pattern.compile("8\\.8\\.[84]\\.[84]"),
    };

    /**
     * whitelist patterns for IPv4 addresses
     */
    private static final Pattern[] IPV6_FILTER = new Pattern[]{
            Pattern.compile("[0:]+1?"),
    };

    /**
     * pattern for IPv6 addresses
     */
    private static final Pattern IPV6_PATTERN = Pattern.compile("(?:[0-9a-f]{0,4}:){7}[0-9a-f]{0,4}%", Pattern.CASE_INSENSITIVE);

    /**
     * create a new log
     * @param dir logs directory
     * @param file log file name
     * @throws IOException errors loading log
     */
    public Log(String dir, String file) throws IOException {
        File logs = new File(dir);
        File log = new File(logs, file);

        if (!log.getParentFile().equals(logs) || !file.matches(".*\\.log(\\.gz)?")) throw new FileNotFoundException();

        InputStream in = new FileInputStream(log);
        if (file.endsWith(".gz")) {
            in = new GZIPInputStream(in);
        }

        this.content = Util.inputStreamToString(in);
        this.filter();
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
        StringBuffer sb = new StringBuffer();
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
