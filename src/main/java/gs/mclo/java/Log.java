package gs.mclo.java;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class Log {

    /**
     * log content
     */
    private final String content;

    /**
     * create a new log
     * @param path path to a log file
     * @throws IOException errors loading log
     */
    public Log(String path) throws IOException {
        InputStream in = new FileInputStream(path);

        if (path.endsWith(".gz")) {
            in = new GZIPInputStream(in);
        }

        this.content = Util.inputStreamToString(in);
    }

    /**
     * @return log content
     */
    public String getContent() {
        return content;
    }
}
