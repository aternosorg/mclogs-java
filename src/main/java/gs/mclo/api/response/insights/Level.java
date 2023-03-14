package gs.mclo.api.response.insights;

public enum Level {
    Emergency(0, "Emergency"),
    Alert(1, "Alert"),
    Critical(2, "Critical"),
    Error(3, "Error"),
    Warning(4, "Warning"),
    Notice(5, "Notice"),
    Info(6, "Info"),
    Debug(7, "Debug");

    public final int value;
    public final String name;

    Level(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static Level fromValue(int value) {
        for (Level level : Level.values()) {
            if (level.getValue() == value) {
                return level;
            }
        }
        return null;
    }

    /**
     * @return numeric value of the level
     */
    public int getValue() {
        return value;
    }

    /**
     * @return name of the level
     */
    public String getName() {
        return name;
    }
}
