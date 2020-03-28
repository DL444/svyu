package mg.studio.android.survey.serializers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import javax.inject.Inject;

import mg.studio.android.survey.models.IQuestion;
import mg.studio.android.survey.models.QuestionType;

/**
 * Represents a serialization selector that select serializers for different types of questions.
 */
public final class QuestionSerializerSelector {

    /**
     * Constructs an instance of QuestionSerializerSelector class.
     * Register all types of question serializers here.
     */
    @Inject
    public QuestionSerializerSelector() {
        ChoiceQuestionSerializer choiceSerializer = new ChoiceQuestionSerializer();
        dispatchTable.put(QuestionType.Single.toString(), choiceSerializer);
        dispatchTable.put(QuestionType.Multiple.toString(), choiceSerializer);
        dispatchTable.put(QuestionType.Text.toString(), new TextQuestionSerializer());
    }

    JSONObject serialize(IQuestion model)
            throws JSONException, QuestionTypeNotSupportedException {
        String type = model.getType().toString();
        if (dispatchTable.containsKey(type)) {
            return dispatchTable.get(type).serialize(model);
        } else {
            throw new QuestionTypeNotSupportedException(type);
        }
    }

    IQuestion deserialize(JSONObject questionJson)
            throws JSONException, QuestionTypeNotSupportedException {
        String type = questionJson.getString("type").toLowerCase();
        if (dispatchTable.containsKey(type)) {
            return dispatchTable.get(type).deserialize(questionJson);
        } else {
            throw new QuestionTypeNotSupportedException(type);
        }
    }

    private Hashtable<String, IQuestionSerializer> dispatchTable = new Hashtable<>();
}
