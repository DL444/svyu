package mg.studio.android.survey.serializers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import javax.inject.Inject;

import mg.studio.android.survey.models.IResponse;
import mg.studio.android.survey.models.QuestionType;

/**
 * Represents a serialization selector that select serializers for different types of responses.
 */
public final class ResponseSerializerSelector {

    /**
     * Constructs an instance of ResponseSerializerSelector class.
     * Register all types of response serializers here.
     */
    @Inject
    public ResponseSerializerSelector() {
        dispatchTable.put(QuestionType.Single.toString(), new SingleChoiceResponseSerializer());
        dispatchTable.put(QuestionType.Multiple.toString(), new MultiChoiceResponseSerializer());
        dispatchTable.put(QuestionType.Text.toString(), new TextResponseSerializer());
    }

    JSONObject serialize(IResponse model)
            throws JSONException, QuestionTypeNotSupportedException {
        String type = model.getType().toString();
        if (dispatchTable.containsKey(type)) {
            return dispatchTable.get(type).serialize(model);
        } else {
            throw new QuestionTypeNotSupportedException(type);
        }
    }

    IResponse deserialize(JSONObject json)
            throws JSONException, QuestionTypeNotSupportedException {
        String type = json.getString("type").toLowerCase();
        if (dispatchTable.containsKey(type)) {
            return dispatchTable.get(type).deserialize(json);
        } else {
            throw new QuestionTypeNotSupportedException(type);
        }
    }

    private Hashtable<String, IResponseSerializer> dispatchTable = new Hashtable<>();
}
