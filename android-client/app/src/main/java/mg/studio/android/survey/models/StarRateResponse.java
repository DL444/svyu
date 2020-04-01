package mg.studio.android.survey.models;

/**
 * Represents a response that consists a rating of stars.
 */
public final class StarRateResponse implements IResponse {

    /**
     * {@inheritDoc}
     */
    @Override
    public QuestionType getType() {
        return QuestionType.StarRate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasResponse() {
        return response != -1;
    }

    /**
     * Gets the response rating.
     * @return The response rating.
     */
    public float getResponse() {
        return response;
    }

    /**
     * Sets the response rating.
     * @param response The response rating to set to.
     */
    public void setResponse(float response) {
        this.response = response;
    }

    private int qid;
    private float response = -1;
}
