package mg.studio.android.survey;

/**
 * Represents the type of a question.
 */
enum QuestionType {
    /**
     * Represents a question that accepts single choice-based response.
     */
    Single,
    /**
     * Represents a question that accepts multiple choice-based response.
     */
    Multiple,
    /**
     * Represents a question that accepts arbitrary text input response.
     */
    Text,
}
