package mg.studio.android.survey.serializers;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import mg.studio.android.survey.models.IResponse;
import mg.studio.android.survey.models.StarRateResponse;

/**
 * Represents a serializer for star rating response.
 */
final class StarRateResponseSerializer implements IResponseSerializer {
    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public JSONObject serialize(IResponse model) throws JSONException {
        StarRateResponse response = (StarRateResponse) model;
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
        StarRateResponse response = new StarRateResponse();
        response.setResponse(json.getInt("answer"));
        return response;
    }
}
