package mg.studio.android.survey.serializers;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mg.studio.android.survey.models.IResponse;
import mg.studio.android.survey.models.MultiChoiceResponse;

/**
 * Represents a serializer for responses that consists of one or more choices.
 */
final class MultiChoiceResponseSerializer implements IResponseSerializer {

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public JSONObject serialize(IResponse model) throws JSONException {
        MultiChoiceResponse response = (MultiChoiceResponse)model;
        JSONObject jObject = new JSONObject();
        jObject.put("type", response.getType().toString());
        JSONArray choices = new JSONArray();
        for (int c : response.getResponse()) {
            choices.put(c);
        }
        jObject.put("answer", choices);
        return jObject;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public IResponse deserialize(JSONObject json) throws JSONException {
        MultiChoiceResponse response = new MultiChoiceResponse();
        JSONArray choices = json.getJSONArray("answer");
        for (int i = 0; i < choices.length(); i++) {
            response.setResponse(choices.getInt(i));
        }
        return response;
    }
}
