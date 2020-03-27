package mg.studio.android.survey.models;

/**
 * Representing a question that accepts text input responses.
 */
public class TextQuestion implements IQuestion {
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

    private String question;
}
