package gs.mclo.api.data;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class Metadata<T> {
    private final String key;
    private final T value;
    private final @Nullable String label;
    private final boolean visible;

    public Metadata(String key, T value) {
        this(key, value, null, true);
    }

    public Metadata(String key, T value, @Nullable String label, boolean visible) {
        this.key = key;
        this.value = value;
        this.label = label;
        this.visible = visible;
    }

    public String getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    public @Nullable String getLabel() {
        return label;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Metadata<?> metadata = (Metadata<?>) o;
        return visible == metadata.visible && Objects.equals(key, metadata.key) && Objects.equals(value, metadata.value) && Objects.equals(label, metadata.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value, label, visible);
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "key='" + key + '\'' +
                ", value=" + value +
                ", label='" + label + '\'' +
                ", visible=" + visible +
                '}';
    }
}
