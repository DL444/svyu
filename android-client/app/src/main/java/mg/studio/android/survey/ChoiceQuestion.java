package mg.studio.android.survey;

/**
 * Base class for choice-based questions.
 */
abstract class ChoiceQuestion implements ISurveyQuestion {
    /**
     * Constructs an instance of ChoiceQuestion.
     * @param question The question to be asked.
     * @param options An array of string containing all options.
     */
    public ChoiceQuestion(String question, String[] options) {
        this.question = question;
        this.options = options.clone();
    }

    /**
     * {@inheritDoc}
     */
    public abstract QuestionType getType();

    /**
     * {@inheritDoc}
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Gets a copy of an array containing all options.
     * @return A copy of an array containing all options.
     */
    public String[] getOptions() {
        return options.clone();
    }

    private String question;
    private String[] options;
}
