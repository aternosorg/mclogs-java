package gs.mclo.api.response.insights;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("NotNullFieldNotInitialized")
public class Entry {
    /**
     * The level of the entry.
     * Use {@link gs.mclo.api.response.insights.Level} to get the level name.
     */
    protected int level;

    /**
     * UNIX timestamp of the entry.
     * This is null for most logs as most software doesn't log dates.
     */
    protected @Nullable Integer time;

    /**
     * The prefix of the entry.
     */
    protected @Nullable String prefix;

    /**
     * The lines that make up the entry.
     */
    protected Line[] lines;

    /**
     * The level of this entry.
     * @return The level of this entry or null if unknown
     */
    public @Nullable Level getLevel() {
        return Level.fromValue(level);
    }

    /**
     * Get the UNIX timestamp of the entry.
     * This is null for most logs as most software doesn't log dates.
     * @return UNIX timestamp of the entry
     */
    public @Nullable Integer getTime() {
        return time;
    }

    /**
     * Get the prefix of the entry.
     * @return the prefix of the entry
     */
    public @Nullable String getPrefix() {
        return prefix;
    }

    /**
     * Get the lines that make up the entry.
     * @return the lines that make up the entry
     */
    public Line[] getLines() {
        return lines;
    }
}
