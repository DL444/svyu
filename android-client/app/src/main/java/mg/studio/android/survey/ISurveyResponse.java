package mg.studio.android.survey;

import java.io.Serializable;

/**
 * An interface representing a response to a survey question.
 */
interface ISurveyResponse extends Serializable {
    /**
     * Gets the text of the response.
     * @return The text of the response;
     */
    String getResponse();

    /**
     * Sets the text of the response.
     * @param response The text of the response.
     */
    void setResponse(String response);

    /**
     * Gets if this response is not empty.
     * @return A boolean value signaling if the response is not empty.
     */
    Boolean hasResponse();
}
