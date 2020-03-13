package mg.studio.android.survey;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A factory class that creates instances of survey questions.
 */
class SurveyQuestionFactory {
    /**
     * Creates an instance of survey question.
     * @param jObject A JSONObject object containing the information of the question.
     * @return A ISurveyQuestion object corresponding to the question.
     * @throws JSONException Thrown when there is a problem getting information from JSONObject.
     * @throws QuestionTypeNotSupportedException Thrown when the argument contains a question whose type the app cannot recognize.
     */
    public static ISurveyQuestion getQuestion(JSONObject jObject) throws JSONException, QuestionTypeNotSupportedException {
        String type = jObject.getString("type");
        switch (type) {
            case "single":
                return ChoiceQuestionFactory.getQuestion(jObject, false);
            case "multiple":
                return ChoiceQuestionFactory.getQuestion(jObject, true);
            case "text":
                return new TextQuestion(jObject.getString("question"));
            default:
                throw new QuestionTypeNotSupportedException(type);
        }
    }
}

/**
 * Represents an exception thrown when the app does not recognize a specific type of survey question.
 */
class QuestionTypeNotSupportedException extends Exception {
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