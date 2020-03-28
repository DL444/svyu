package mg.studio.android.survey.models;

/**
 * Represents a response that consists of a single choice.
 */
public final class SingleChoiceResponse implements IResponse {

    /**
     * {@inheritDoc}
     */
    @Override
    public QuestionType getType() {
        return QuestionType.Single;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasResponse() {
        return response >= 0;
    }

    /**
     * Gets the choice for this response.
     * @return The index of the choice.
     */
    public int getResponse() {
        return response;
    }

    /**
     * Sets the choice for this response.
     * @param response The index of the choice to set to.
     */
    public void setResponse(int response) {
        this.response = response;
    }

    private int qid;
    private int response = -1;
}
