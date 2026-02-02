package gs.mclo.api.internal;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.Objects;

/**
 * A Helper class that limits the number of bytes and/or lines read from a Reader.
 */
@SuppressWarnings("SynchronizeOnNonFinalField")
public final class LimitedReader extends Reader {
    /**
     * The underlying reader
     */
    @Nullable
    private Reader in;
    /**
     * Whether to trim whitespace at the start of the log
     */
    private boolean trim;
    /**
     * The remaining byte limit. If null, no limit is enforced.
     */
    @Nullable
    private Integer remainingByteLimit;
    /**
     * The remaining line limit. If null, no limit is enforced.
     */
    @Nullable
    private Integer remainingLineLimit;

    /**
     * Creates a new LimitedReader with the given limits.
     *
     * @param in        the underlying reader
     * @param byteLimit the maximum number of bytes to read, or null for no limit
     * @param lineLimit the maximum number of lines to read, or null for no limit
     * @param trim      whether to trim whitespace at the start of the log
     */
    public LimitedReader(Reader in, @Nullable Integer byteLimit, @Nullable Integer lineLimit, boolean trim) {
        this.in = Objects.requireNonNull(in);
        this.remainingByteLimit = byteLimit;
        this.remainingLineLimit = lineLimit;
        this.trim = trim;
    }

    @Override
    public int read(char[] chars, int offset, int length) throws IOException {
        synchronized (this.lock) {
            if (this.in == null) {
                throw new IOException("Stream closed");
            }

            char[] readChars = new char[length];
            int read = this.in.read(readChars, 0, length);
            if (read == -1) {
                return -1;
            }

            int i = 0;
            for (int b = 0; i < read; i++) {
                char c = readChars[b];
                if (this.remainingByteLimit != null) {
                    if (this.remainingByteLimit <= 0) {
                        return i == 0 ? -1 : i;
                    }

                    var remainingByteLimit = this.remainingByteLimit;
                    int byteCount = getPartialUTF8Size(c);
                    if (this.getRemainingUTF8Size(c) > remainingByteLimit) {
                        return i == 0 ? -1 : i;
                    }
                    this.remainingByteLimit -= byteCount;
                }

                if (c == '\n' && this.remainingLineLimit != null) {
                    this.remainingLineLimit--;
                    if (this.remainingLineLimit <= 0) {
                        return i == 0 ? -1 : i;
                    }
                }

                if (!trim || !Character.isWhitespace(c)) {
                    chars[offset + i] = c;
                    b++;
                }

                if (trim && !Character.isWhitespace(c)) {
                    this.trim = false;
                }
            }

            return i;
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (this.lock) {
            if (this.in != null) {
                try {
                    this.in.close();
                } finally {
                    this.in = null;
                }
            }
        }
    }

    /**
     * Get the number of bytes it would require to encode this full char in UTF-8.
     * High surrogates are considered 4 bytes since the low surrogate is still needed.
     *
     * @param character the UTF-16 input character
     * @return size required to encode this character in UTF-8
     */
    private int getRemainingUTF8Size(char character) {
        if (character < 0x80) {
            return 1;
        }

        if (Character.isHighSurrogate(character)) {
            return 4;
        }

        return 2;
    }

    /**
     * Get the number of bytes it would require to encode this (part of a) char in UTF-8.
     * Surrogates are considered to be 2 bytes by this method.
     *
     * @param character the UTF-16 input character
     * @return size this char would take in UTF-8
     */
    private int getPartialUTF8Size(char character) {
        if (character < 0x80) {
            return 1;
        }

        return 2;
    }
}
