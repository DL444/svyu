package mg.studio.android.survey.models;

/**
 * Representing a question that accepts star rating responses.
 */
public final class StarRateQuestion implements IQuestion {
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
