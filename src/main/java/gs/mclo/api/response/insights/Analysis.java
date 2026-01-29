package gs.mclo.api.response.insights;

@SuppressWarnings("NotNullFieldNotInitialized")
public final class Analysis {
    /**
     * A list of detected problems
     */
    private Problem[] problems;

    /**
     * A list of detected information
     */
    private Information[] information;

    private Analysis() {

    }

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
