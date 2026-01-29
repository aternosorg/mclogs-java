package gs.mclo.api.response.insights;

import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("NotNullFieldNotInitialized")
@ApiStatus.NonExtendable
public class Insight {
    /**
     * Problem: The problem message
     * Information: The label: value pair
     */
    protected String message;
    /**
     * The number of times this insight was detected in this log
     */
    protected int counter;

    protected Entry entry;

    protected Insight() {

    }

    /**
     * Get the entry
     * @return the entry
     */
    public Entry getEntry() {
        return entry;
    }

    /**
     * Get the message
     * Problem: The problem message
     * Information: The label: value pair
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * The number of times this insight was detected in this log
     * @return The number of times this insight was detected in this log
     */
    public int getCounter() {
        return counter;
    }
}
