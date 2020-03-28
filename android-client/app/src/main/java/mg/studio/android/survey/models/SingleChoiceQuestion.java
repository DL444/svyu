package mg.studio.android.survey.models;

/**
 * Representing a question that accepts a single response.
 */
public final class SingleChoiceQuestion extends ChoiceQuestionBase {

    /**
     * {@inheritDoc}
     */
    @Override
    public QuestionType getType() {
        return QuestionType.Single;
    }
}
