package mg.studio.android.survey.models;

import java.util.ArrayList;

/**
 * Base class for choice-based questions.
 */
public abstract class ChoiceQuestionBase implements IQuestion {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getQuestion() {
        return question;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * Gets the ArrayList containing all options.
     * @return The list containing all options.
     */
    public ArrayList<String> options() {
        return _options;
    }

    private String question;
    private ArrayList<String> _options = new ArrayList<>();
}
