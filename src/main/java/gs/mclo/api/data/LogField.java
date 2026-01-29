package gs.mclo.api.data;

/**
 * Optional log fields that can be requested from the API.
 */
public enum LogField {
    RAW("raw"),
    PARSED("parsed"),
    INSIGHTS("insights");

    private final String name;

    LogField(String name) {
        this.name = name;
    }

    /**
     * Get the field name as used in the API.
     *
     * @return The field name.
     */
    public String getName() {
        return name;
    }
}
