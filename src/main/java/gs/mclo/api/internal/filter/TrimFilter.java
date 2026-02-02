package gs.mclo.api.internal.filter;

public final class TrimFilter implements Filter {
    @Override
    public String getType() {
        return "trim";
    }

    @Override
    public String apply(String input) {
        return input.trim();
    }
}
