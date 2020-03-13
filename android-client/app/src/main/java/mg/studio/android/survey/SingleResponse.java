package mg.studio.android.survey;

/**
 * Represents a response to a single response question.
 */
final class SingleResponse implements ISurveyResponse {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponse() {
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResponse(String response) {
        this.response = response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean hasResponse() {
        return !response.trim().isEmpty();
    }

    private String response = "";
}
