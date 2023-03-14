package gs.mclo.api.response.insights;

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
    protected Integer time;

    /**
     * The prefix of the entry.
     */
    protected String prefix;

    /**
     * The lines that make up the entry.
     */
    protected Line[] lines;

    /**
     * The problems that make up the entry.
     */
    public Level getLevel() {
        return Level.fromValue(level);
    }

    /**
     * Get the UNIX timestamp of the entry.
     * This is null for most logs as most software doesn't log dates.
     * @return UNIX timestamp of the entry
     */
    public Integer getTime() {
        return time;
    }

    /**
     * Get the prefix of the entry.
     * @return the prefix of the entry
     */
    public String getPrefix() {
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
