package gs.mclo.api.internal.filter;

import org.jetbrains.annotations.ApiStatus;

public final class LimitBytesFilter implements Filter {
    private final int limit;

    @ApiStatus.Internal
    public LimitBytesFilter(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    @Override
    public String getType() {
        return "limit-bytes";
    }

    @Override
    public String apply(String input) {
        return input.substring(0, Math.min(input.length(), limit));
    }
}
