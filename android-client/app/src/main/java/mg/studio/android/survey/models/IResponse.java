package mg.studio.android.survey.models;

import java.io.Serializable;

/**
 * An interface representing a survey response.
 */
public interface IResponse extends Serializable {

    /**
     * Gets the type of the question the response is for.
     * @return A QuestionType object representing the type of the question.
     */
    QuestionType getType();

    /**
     * Gets if the response is not empty.
     * @return True if the response is not empty. False otherwise.
     */
    boolean hasResponse();
}
