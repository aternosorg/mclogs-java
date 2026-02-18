package gs.mclo.api.internal.filter;

import org.jetbrains.annotations.ApiStatus;

import java.util.regex.Matcher;

public final class RegexFilter implements Filter {
    private final ReplacingRegexPattern[] patterns;
    private final RegexPattern[] exemptions;

    @ApiStatus.Internal
    public RegexFilter(ReplacingRegexPattern[] patterns, RegexPattern[] exemptions) {
        this.patterns = patterns;
        this.exemptions = exemptions;
    }

    @Override
    public String getType() {
        return "regex";
    }

    @Override
    public String apply(String input) {
        for (var pattern : patterns) {
            var compiled = pattern.tryParse();
            if (compiled.isEmpty()) {
                // Skip patterns that failed to compile.
                continue;
            }
            var matcher = compiled.get().matcher(input);
            var result = new StringBuilder();
            while (matcher.find()) {
                boolean isExempted = false;
                if (exemptions != null) {
                    for (var exemption : exemptions) {
                        var exemptionCompiled = exemption.tryParse();
                        if (exemptionCompiled.isEmpty()) {
                            continue;
                        }
                        var exemptionMatcher = exemptionCompiled.get().matcher(matcher.group());
                        if (exemptionMatcher.matches()) {
                            isExempted = true;
                            break;
                        }
                    }
                }

                String replacement = isExempted ? matcher.group() : pattern.getReplacement();
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(result);
            input = result.toString();
        }
        return input;
    }
}
