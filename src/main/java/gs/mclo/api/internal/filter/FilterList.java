package gs.mclo.api.internal.filter;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public final class FilterList {
    private final Filter[] filters;

    @ApiStatus.Internal
    public FilterList(Filter[] filters) {
        this.filters = filters;
    }

    public @Nullable Integer getMaxBytes() {
        return getFilters(LimitBytesFilter.class)
                .map(LimitBytesFilter::getLimit)
                .min(Integer::compareTo)
                .orElse(null);
    }

    public @Nullable Integer getMaxLines() {
        return getFilters(LimitLinesFilter.class)
                .map(LimitLinesFilter::getLimit)
                .min(Integer::compareTo)
                .orElse(null);
    }

    public Filter[] getFilters() {
        return filters;
    }

    public <T> Stream<T> getFilters(Class<T> filterClass) {
        return Arrays.stream(filters)
                .filter(filterClass::isInstance)
                .map(filterClass::cast);
    }

    public <T> Optional<T> getFilter(Class<T> filterClass) {
        return getFilters(filterClass).findFirst();
    }
}
