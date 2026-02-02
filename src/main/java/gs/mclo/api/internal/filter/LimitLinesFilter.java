package gs.mclo.api.internal.filter;

import org.jetbrains.annotations.ApiStatus;

public final class LimitLinesFilter implements Filter {
    private final int limit;

    @ApiStatus.Internal
    public LimitLinesFilter(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    @Override
    public String getType() {
        return "limit-lines";
    }

    @Override
    public String apply(String input) {
        String[] lines = input.split("\n", -1);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < Math.min(lines.length, limit); i++) {
            result.append(lines[i]);
            if (i < Math.min(lines.length, limit) - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }
}
