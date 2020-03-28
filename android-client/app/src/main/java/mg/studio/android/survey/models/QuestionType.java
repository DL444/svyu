package mg.studio.android.survey.models;

import androidx.annotation.NonNull;

/**
 * Represents the type of a question.
 */
public enum QuestionType {
    /**
     * Represents a question that accepts single choice-based response.
     */
    Single("single"),
    /**
     * Represents a question that accepts multiple choice-based response.
     */
    Multiple("multiple"),
    /**
     * Represents a question that accepts arbitrary text input response.
     */
    Text("text");

    private final String value;
    QuestionType(final String value) {
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return value;
    }
}
