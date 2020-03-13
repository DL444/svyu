package mg.studio.android.survey;

/**
 * Representing a question that accepts multiple responses.
 */
final class SingleResponseQuestion extends ChoiceQuestion {
    /**
     * Constructs an instance of SingleResponseQuestion.
     * @param question The question to be asked.
     * @param options An array of string containing all options.
     */
    public SingleResponseQuestion(String question, String[] options) {
        super(question, options);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuestionType getType() {
        return QuestionType.Single;
    }
}
