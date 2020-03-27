package mg.studio.android.survey.serializers;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import mg.studio.android.survey.models.IQuestion;

/**
 * An interface representing a serializer for a type of question.
 */
interface IQuestionSerializer {

    /**
     * Serialize a question model into JSON object.
     * @param model The model to serialize.
     * @return The serialized JSON object.
     */
    @NonNull
    JSONObject serialize(IQuestion model) throws JSONException;

    /**
     * Deserialize a JSON object into a question model.
     * @param json The JSON object to deserialize.
     * @return The deserialized model.
     */
    @NonNull
    IQuestion deserialize(JSONObject json) throws JSONException;
}
