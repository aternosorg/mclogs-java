package gs.mclo.api.response.insights;

@SuppressWarnings("NotNullFieldNotInitialized")
public final class Problem extends Insight {
    private Solution[] solutions;

    private Problem() {
    }

    /**
     * Get the solutions for this problem
     *
     * @return possible solutions for this problem
     */
    public Solution[] getSolutions() {
        return solutions;
    }
}
