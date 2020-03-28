package mg.studio.android.survey.serializers;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import mg.studio.android.survey.models.IQuestion;
import mg.studio.android.survey.models.TextQuestion;

/**
 * Represents a serializer for questions that accepts arbitrary text response.
 */
final class TextQuestionSerializer implements IQuestionSerializer {
    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public JSONObject serialize(IQuestion model) throws JSONException {
        JSONObject question = new JSONObject();
        question.put("type", model.getType().toString());
        question.put("question", model.getQuestion());
        return question;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public IQuestion deserialize(JSONObject json) throws JSONException {
        TextQuestion question = new TextQuestion();
        question.setQuestion(json.getString("question"));
        return question;
    }
}
