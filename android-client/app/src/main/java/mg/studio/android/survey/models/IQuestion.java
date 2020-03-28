package mg.studio.android.survey.models;

import java.io.Serializable;

/**
 * An interface representing a survey question.
 */
public interface IQuestion extends Serializable {
    /**
     * Gets the type of the question.
     * @return A QuestionType object representing the type of the question.
     */
    QuestionType getType();

    /**
     * Gets the text of the question.
     * @return The text of the question.
     */
    String getQuestion();

    /**
     * Sets the text of the question.
     */
    void setQuestion(String question);
}
