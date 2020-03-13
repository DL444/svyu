package mg.studio.android.survey;

import java.util.ArrayList;

/**
 * Represents a response to a multiple response question.
 */
final class MultipleResponse implements ISurveyResponse {

    /**
     * Gets all the responses as text, with one line containing a single response.
     * @return A string representing all responses.
     */
    @Override
    public String getResponse() {
        StringBuilder builder = new StringBuilder();
        for (String r : responses) {
            builder.append(r).append("\n");
        }
        return builder.toString().trim();
    }

    /**
     * Appends a response if it does not currently exist. Otherwise, remove it.
     * @param response The text of the response.
     */
    @Override
    public void setResponse(String response) {
        if (responses.contains(response)) {
            responses.remove(response);
        } else {
            responses.add(response);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean hasResponse() {
        return responses.size() != 0;
    }

    private ArrayList<String> responses = new ArrayList<>();
}
