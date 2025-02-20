package gs.mclo.api;

import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class LimitedReaderTest {
    @Test
    void testReadSimpleString() {
        assertDoesNotThrow(() -> {
            String input = "Hello, world!";
            try (LimitedReader reader = new LimitedReader(new StringReader(input), 100, 100)) {
                char[] buffer = new char[100];

                int offset = 0;
                int lastRead;
                int totalRead = 0;

                do {
                    lastRead = reader.read(buffer, offset, 5);

                    if (lastRead > 0) {
                        totalRead += lastRead;
                        offset += lastRead;
                    }

                    assertTrue(lastRead <= 5, "Read more characters than requested");
                    assertTrue(totalRead <= input.length(), "Read past end of stream");

                } while (lastRead != -1);

                assertEquals(input.length(), totalRead);
                assertEquals(input, new String(buffer, 0, totalRead));
            }
        });
    }

    @Test
    void testReadByteLimit() {
        assertDoesNotThrow(() -> {
            String input = "Hello, world!";
            try (LimitedReader reader = new LimitedReader(new StringReader(input), 10, 100)) {
                char[] buffer = new char[100];

                int offset = 0;
                int lastRead;
                int totalRead = 0;

                do {
                    lastRead = reader.read(buffer, offset, 5);

                    if (lastRead > 0) {
                        totalRead += lastRead;
                        offset += lastRead;
                    }

                    assertTrue(lastRead <= 5, "Read more characters than requested");
                    assertTrue(totalRead <= input.length(), "Read past end of stream");

                } while (lastRead != -1);

                assertEquals(10, totalRead);
                assertEquals("Hello, wor", new String(buffer, 0, totalRead));
            }
        });
    }

    @Test
    void testReadByteLimitUnicode() {
        assertDoesNotThrow(() -> {
            String input = "Hello, world 🌍!";
            try (LimitedReader reader = new LimitedReader(new StringReader(input), 17, 100)) {
                char[] buffer = new char[100];

                int offset = 0;
                int lastRead;
                int totalRead = 0;

                do {
                    lastRead = reader.read(buffer, offset, 5);

                    if (lastRead > 0) {
                        totalRead += lastRead;
                        offset += lastRead;
                    }

                    assertTrue(lastRead <= 5, "Read more characters than requested");
                    assertTrue(totalRead <= input.length(), "Read past end of stream");

                } while (lastRead != -1);

                assertEquals(15, totalRead);
                assertEquals("Hello, world 🌍", new String(buffer, 0, totalRead));
            }
        });
    }

    @Test
    void testReadByteLimitUnicodeCutoff() {
        assertDoesNotThrow(() -> {
            String input = "Hello, world 🌍";
            try (LimitedReader reader = new LimitedReader(new StringReader(input), 14, 100)) {
                char[] buffer = new char[100];

                int offset = 0;
                int lastRead;
                int totalRead = 0;

                do {
                    lastRead = reader.read(buffer, offset, 5);

                    if (lastRead > 0) {
                        totalRead += lastRead;
                        offset += lastRead;
                    }

                    assertTrue(lastRead <= 5, "Read more characters than requested");
                    assertTrue(totalRead <= input.length(), "Read past end of stream");

                } while (lastRead != -1);

                assertEquals(13, totalRead);
                assertEquals("Hello, world ", new String(buffer, 0, totalRead));
            }
        });
    }

    @Test
    void testReadLineLimit() {
        assertDoesNotThrow(() -> {
            String input = "H\ne\nl\nl\no, world!";
            try (LimitedReader reader = new LimitedReader(new StringReader(input), 100, 3)) {
                char[] buffer = new char[100];

                int offset = 0;
                int lastRead;
                int totalRead = 0;

                do {
                    lastRead = reader.read(buffer, offset, 5);

                    if (lastRead > 0) {
                        totalRead += lastRead;
                        offset += lastRead;
                    }

                    assertTrue(lastRead <= 5, "Read more characters than requested");
                    assertTrue(totalRead <= input.length(), "Read past end of stream");

                } while (lastRead != -1);

                assertEquals(5, totalRead);
                assertEquals("H\ne\nl", new String(buffer, 0, totalRead));
            }
        });
    }
}
