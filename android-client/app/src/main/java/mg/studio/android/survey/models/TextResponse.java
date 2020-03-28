package mg.studio.android.survey.models;

/**
 * Represents a response that consists of arbitrary text.
 */
public final class TextResponse implements IResponse {

    /**
     * {@inheritDoc}
     */
    @Override
    public QuestionType getType() {
        return QuestionType.Text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasResponse() {
        return response != null && !response.isEmpty();
    }

    /**
     * Gets the response text.
     * @return The response text.
     */
    public String getResponse() {
        return response;
    }

    /**
     * Sets the response text.
     * @param response The response text to set to.
     */
    public void setResponse(String response) {
        this.response = response;
    }

    private int qid;
    private String response;
}
