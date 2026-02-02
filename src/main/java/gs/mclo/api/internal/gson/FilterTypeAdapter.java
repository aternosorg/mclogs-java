package gs.mclo.api.internal.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gs.mclo.api.internal.filter.*;

import java.io.IOException;
import java.util.Map;

public class FilterTypeAdapter extends TypeAdapter<Filter> {
    private final Map<String, Class<? extends Filter>> filterClasses = Map.of(
            "trim", TrimFilter.class,
            "limit-bytes", LimitBytesFilter.class,
            "limit-lines", LimitLinesFilter.class,
            "regex", RegexFilter.class
    );

    private final Gson gson;

    public FilterTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, Filter value) throws IOException {
        out.beginObject();
        out.value("type");
        out.value(value.getType());
        out.value("data");
        JsonElement dataElement = gson.toJsonTree(value);
        gson.toJson(dataElement, out);
        out.endObject();
    }

    @Override
    public Filter read(JsonReader in) throws IOException {
        in.beginObject();
        String type = null;
        JsonElement dataElement = null;

        while (in.hasNext()) {
            String name = in.nextName();
            if (name.equals("type")) {
                type = in.nextString();
            } else if (name.equals("data")) {
                dataElement = gson.fromJson(in, JsonElement.class);
            }
        }
        in.endObject();

        if (type == null || dataElement == null) {
            throw new IOException("Invalid Filter JSON: missing type or data");
        }

        Class<? extends Filter> filterClass = filterClasses.get(type);
        if (filterClass == null) {
            throw new IOException("Unknown filter type: " + type);
        }

        return gson.fromJson(dataElement, filterClass);
    }
}
