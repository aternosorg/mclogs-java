package gs.mclo.api.internal.filter;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexPattern {
    private final String pattern;
    private final char[] modifiers;

    public RegexPattern(String pattern, char[] modifiers) {
        this.pattern = pattern;
        this.modifiers = modifiers;
    }

    /**
     * Parses the pattern with the given modifiers.
     * @return The compiled pattern or an empty optional if the pattern is invalid.
     */
    public Optional<Pattern> tryParse() {
        int flags = 0;
        if (modifiers != null) {
            for (char modifier : modifiers) {
                switch (modifier) {
                    case 'i':
                        flags |= Pattern.CASE_INSENSITIVE;
                        break;
                    case 'm':
                        flags |= Pattern.MULTILINE;
                        break;
                    case 's':
                        flags |= Pattern.DOTALL;
                        break;
                    case 'u':
                        flags |= Pattern.UNICODE_CASE;
                        break;
                    default:
                        return Optional.empty();
                }
            }
        }
        try {
            return Optional.of(Pattern.compile(pattern, flags));
        } catch (PatternSyntaxException e) {
            return Optional.empty();
        }
    }
}
