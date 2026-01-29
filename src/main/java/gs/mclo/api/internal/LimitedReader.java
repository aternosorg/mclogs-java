package gs.mclo.api.internal;

import gs.mclo.api.response.Limits;
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
     * The remaining byte limit. If null, no limit is enforced.
     */
    private Integer remainingByteLimit;
    /**
     * The remaining line limit. If null, no limit is enforced.
     */
    private Integer remainingLineLimit;

    /**
     * Creates a new LimitedReader with the given limits.
     *
     * @param in     the underlying reader
     * @param limits the limits to enforce. If null, no limits are enforced.
     */
    public LimitedReader(Reader in, Limits limits) {
        this.in = Objects.requireNonNull(in);
        this.remainingByteLimit = limits.getMaxLength();
        this.remainingLineLimit = limits.getMaxLines();
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
            for (; i < read; i++) {
                if (this.remainingByteLimit <= 0) {
                    return i == 0 ? -1 : i;
                }

                char c = readChars[i];
                var remainingByteLimit = this.remainingByteLimit;
                int byteCount = getPartialUTF8Size(c);
                if (this.getRemainingUTF8Size(c) > remainingByteLimit) {
                    return i == 0 ? -1 : i;
                }
                this.remainingByteLimit -= byteCount;

                if (c == '\n') {
                    this.remainingLineLimit--;
                    if (this.remainingLineLimit <= 0) {
                        return i == 0 ? -1 : i;
                    }
                }

                chars[offset + i] = c;
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
