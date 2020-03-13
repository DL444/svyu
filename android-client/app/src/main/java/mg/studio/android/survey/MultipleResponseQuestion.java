package mg.studio.android.survey;

/**
 * Representing a question that accepts multiple responses.
 */
final class MultipleResponseQuestion extends ChoiceQuestion {
    /**
     * Constructs an instance of MultipleResponseQuestion.
     * @param question The question to be asked.
     * @param options An array of string containing all options.
     */
    public MultipleResponseQuestion(String question, String[] options) {
        super(question, options);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuestionType getType() {
        return QuestionType.Multiple;
    }
}
