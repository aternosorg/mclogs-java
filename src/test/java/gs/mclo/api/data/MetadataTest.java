package gs.mclo.api.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataTest {
    @Test
    void testToString() {
        Metadata<String> metadata = new Metadata<>("key", "value", "label", true);
        String expected = "Metadata{key='key', value=value, label='label', visible=true}";
        assertEquals(expected, metadata.toString());

        Metadata<Integer> metadata2 = new Metadata<>("num", 42);
        String expected2 = "Metadata{key='num', value=42, label='null', visible=true}";
        assertEquals(expected2, metadata2.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        Metadata<String> m1 = new Metadata<>("key", "value", "label", true);
        Metadata<String> m2 = new Metadata<>("key", "value", "label", true);
        Metadata<String> m3 = new Metadata<>("key", "value", null, true);
        Metadata<String> m4 = new Metadata<>("key", "value", "label", false);
        Metadata<String> m5 = new Metadata<>("other", "value", "label", true);
        Metadata<Integer> m6 = new Metadata<>("key", 42, "label", true);

        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());
        assertNotEquals(m1, m3);
        assertNotEquals(m1, m4);
        assertNotEquals(m1, m5);
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(m1, m6);
        //noinspection SimplifiableAssertion,ConstantValue
        assertFalse(m1.equals(null));
        //noinspection EqualsBetweenInconvertibleTypes,SimplifiableAssertion
        assertFalse(m1.equals("not metadata"));
    }
}
