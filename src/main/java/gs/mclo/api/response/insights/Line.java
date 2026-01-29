package gs.mclo.api.response.insights;

@SuppressWarnings("NotNullFieldNotInitialized")
public final class Line {
    private int number;

    private String content;

    private Line() {
    }

    /**
     * @return Line number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @return Line content
     */
    public String getContent() {
        return content;
    }
}
