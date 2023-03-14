package gs.mclo.api.response.insights;

public class Analysis {
    /**
     * A list of detected problems
     */
    protected Problem[] problems;

    /**
     * A list of detected information
     */
    protected Information[] information;

    /**
     * A list of detected problems
     * @return an array of detected problems
     */
    public Problem[] getProblems() {
        return problems;
    }

    /**
     * A list of detected information
     * @return an array of detected information
     */
    public Information[] getInformation() {
        return information;
    }
}
