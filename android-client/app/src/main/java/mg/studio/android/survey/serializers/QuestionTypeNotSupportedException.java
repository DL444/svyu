package mg.studio.android.survey.serializers;

/**
 * Represents an exception thrown when the app does not recognize a specific type of survey question.
 */
public final class QuestionTypeNotSupportedException extends Exception {
    /**
     * Constructs an instance of QuestionTypeNotSupportedException.
     * @param type The type of the question that results in the exception.
     */
    public QuestionTypeNotSupportedException(String type) {
        this.type = type;
    }

    /**
     * Gets the type of the question that results in the exception.
     * @return The type of the question that results in the exception.
     */
    public String getType() {
        return type;
    }

    private String type;
}
