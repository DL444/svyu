package mg.studio.android.survey.viewmodels;

import java.io.Serializable;

import mg.studio.android.survey.models.IQuestion;

/**
 * An interface representing question view models.
 */
public interface IQuestionViewModel extends Serializable {

    /**
     * Gets the text of the question.
     * @return The text of the question.
     */
    String getQuestion();

    /**
     * Sets the text of the question.
     * @param question The text of the question.
     */
    void setQuestion(String question);

    /**
     * Gets a description of the question.
     * @return The description.
     */
    String getDescription();

    /**
     * Gets the corresponding question model.
     * @return The question model.
     */
    IQuestion getModel();

    /**
     * Gets if the question is currently set.
     * @return True if the question is set, false otherwise.
     */
    boolean hasQuestion();
}
