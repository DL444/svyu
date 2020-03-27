package mg.studio.android.survey.models;

/**
 * Representing a question that accepts multiple responses.
 */
public class MultiChoiceQuestion extends ChoiceQuestionBase {

    /**
     * {@inheritDoc}
     */
    @Override
    public QuestionType getType() {
        return QuestionType.Multiple;
    }
}
