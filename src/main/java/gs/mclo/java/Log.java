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
