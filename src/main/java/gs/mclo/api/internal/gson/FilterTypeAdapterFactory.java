package gs.mclo.api.internal.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import gs.mclo.api.internal.filter.Filter;
import org.jetbrains.annotations.Nullable;

public class FilterTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> @Nullable TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        // Only use the type adapter for the Filter interface, not for its implementations
        if (!Filter.class.equals(type.getRawType())) {
            return null;
        }
        //noinspection unchecked
        return (TypeAdapter<T>) new FilterTypeAdapter(gson);
    }
}
