package mg.studio.android.survey.serializers;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import mg.studio.android.survey.models.IResponse;
import mg.studio.android.survey.models.SingleChoiceResponse;

/**
 * Represents a serializer for responses that consists of a single choice.
 */
final class SingleChoiceResponseSerializer implements IResponseSerializer {
    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public JSONObject serialize(IResponse model) throws JSONException {
        SingleChoiceResponse response = (SingleChoiceResponse)model;
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
        SingleChoiceResponse response = new SingleChoiceResponse();
        response.setResponse(json.getInt("answer"));
        return response;
    }
}
