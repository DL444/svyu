package mg.studio.android.survey.serializers;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mg.studio.android.survey.models.ChoiceQuestionBase;
import mg.studio.android.survey.models.IQuestion;
import mg.studio.android.survey.models.MultiChoiceQuestion;
import mg.studio.android.survey.models.QuestionType;
import mg.studio.android.survey.models.SingleChoiceQuestion;

/**
 * Represents a serializer for questions that accepts choice-based response.
 */
class ChoiceQuestionSerializer implements IQuestionSerializer {
    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public JSONObject serialize(IQuestion model) throws JSONException {
        ChoiceQuestionBase question = (ChoiceQuestionBase) model;
        JSONObject jObject = new JSONObject();
        jObject.put("type", question.getType().toString());
        jObject.put("question", question.getQuestion());
        ArrayList<String> options = question.options();
        JSONArray optionsArray = new JSONArray();
        for (int i = 0; i < options.size(); i++) {
            JSONObject option = new JSONObject();
            option.put(String.valueOf(i + 1), options.get(i));
            optionsArray.put(option);
        }
        jObject.put("options", optionsArray);
        return jObject;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public IQuestion deserialize(JSONObject json) throws JSONException {
        String type = json.getString("type");
        ChoiceQuestionBase question;
        if (type.equals(QuestionType.Single.toString())) {
            question = new SingleChoiceQuestion();
        } else {
            question = new MultiChoiceQuestion();
        }
        question.setQuestion(json.getString("question"));
        JSONArray options = json.getJSONArray("options");
        for (int i = 0; i < options.length(); i++) {
            String opt = options.getJSONObject(i).getString(String.valueOf(i + 1));
            question.options().add(opt);
        }
        return question;
    }
}
