package mg.studio.android.survey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A factory class that creates instances of choice-based questions.
 */
class ChoiceQuestionFactory {
    /**
     * Creates an instance of choice-based question.
     * @param jObject A JSONObject object containing the information of the question.
     * @param multipleResponse Determines if the resulting question allows multiple responses.
     * @return A ChoiceQuestion object corresponding to the question.
     * @throws JSONException Thrown when there is a problem getting information from JSONObject.
     */
    public static ChoiceQuestion getQuestion(JSONObject jObject, boolean multipleResponse) throws JSONException {
        String question = jObject.getString("question");
        JSONArray choiceArray = jObject.getJSONArray("options");
        String[] options = new String[choiceArray.length()];
        for (int i = 0; i < options.length; i++) {
            String opt = choiceArray.getJSONObject(i).getString(String.valueOf(i + 1));
            options[i] = opt;
        }

        ChoiceQuestion q;
        if (multipleResponse) {
            q = new MultipleResponseQuestion(question, options);
        } else {
            q = new SingleResponseQuestion(question, options);
        }
        return q;
    }
}
