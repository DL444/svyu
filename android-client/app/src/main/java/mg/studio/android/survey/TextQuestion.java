package mg.studio.android.survey;

/**
 * Representing a question that accepts text input responses.
 */
final class TextQuestion implements ISurveyQuestion {
    /**
     * Constructs an instance of TextQuestion.
     * @param question The question to be asked.
     */
    public TextQuestion(String question) {
        this.question = question;
    }

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

    private String question;
}
