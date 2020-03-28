package mg.studio.android.survey.serializers;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import mg.studio.android.survey.models.IResponse;

/**
 * An interface representing a serializer for a type of response.
 */
interface IResponseSerializer {

    /**
     * Serialize a response model into JSON object.
     * @param model The model to serialize.
     * @return The serialized JSON object.
     * @throws JSONException Thrown when there is a problem serializing JSON.
     */
    @NonNull
    JSONObject serialize(IResponse model) throws JSONException;

    /**
     * Deserialize a JSON object into a response model.
     * @param json The JSON object to deserialize.
     * @return The deserialized model.
     * @throws JSONException Thrown when there is a problem deserializing JSON.
     */
    @NonNull
    IResponse deserialize(JSONObject json) throws JSONException;
}
