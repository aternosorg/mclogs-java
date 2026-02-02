package gs.mclo.api.internal.filter;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface Filter {
    String getType();
    String apply(String input);
}
