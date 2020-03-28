package mg.studio.android.survey.serializers;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import mg.studio.android.survey.models.IResponse;
import mg.studio.android.survey.models.TextResponse;

/**
 * Represents a serializer for arbitrary text response.
 */
final class TextResponseSerializer implements IResponseSerializer {
    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public JSONObject serialize(IResponse model) throws JSONException {
        TextResponse response = (TextResponse)model;
        JSONObject jObject = new JSONObject();
        jObject.put("type", response.getType().toString());
        jObject.put("answer", response.getResponse());
        return jObject;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public IResponse deserialize(JSONObject json) throws JSONException {
        TextResponse response = new TextResponse();
        response.setResponse(json.getString("answer"));
        return response;
    }
}
