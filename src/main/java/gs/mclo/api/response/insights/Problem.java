package gs.mclo.api.response.insights;

@SuppressWarnings("NotNullFieldNotInitialized")
public class Problem extends Insight {
    protected Solution[] solutions;

    /**
     * Get the solutions for this problem
     * @return possible solutions for this problem
     */
    public Solution[] getSolutions() {
        return solutions;
    }
}
