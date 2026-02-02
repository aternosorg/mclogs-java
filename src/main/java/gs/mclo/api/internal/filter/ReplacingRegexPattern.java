package gs.mclo.api.internal.filter;

public class ReplacingRegexPattern extends RegexPattern {
    private final String replacement;

    public ReplacingRegexPattern(String pattern, char[] modifiers, String replacement) {
        super(pattern, modifiers);
        this.replacement = replacement;
    }

    public String getReplacement() {
        return replacement;
    }
}
