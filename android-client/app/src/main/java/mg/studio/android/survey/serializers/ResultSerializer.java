package mg.studio.android.survey.serializers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import mg.studio.android.survey.models.IResponse;
import mg.studio.android.survey.models.ResultModel;

/**
 * Represents a serializer that serializes and deserializes result models.
 */
public final class ResultSerializer {

    @Inject
    public ResultSerializer(ResponseSerializerSelector selector) {
        this.selector = selector;
    }

    /**
     * Deserializes JSON string into corresponding result model.
     * @param json The JSON string to deserialize.
     * @return The deserialized result model.
     * @throws JSONException Thrown when there is a problem deserializing JSON.
     * @throws QuestionTypeNotSupportedException Thrown when there is an unsupported question type.
     */
    public ResultModel getModel(String json)
            throws JSONException, QuestionTypeNotSupportedException {
        ResultModel result = new ResultModel();
        JSONObject jObject = new JSONObject(json);
        result.setId(jObject.getString("id"));
        result.setLongitude(jObject.getDouble("longitude"));
        result.setLatitude(jObject.getDouble("latitude"));
        result.setTime(jObject.getLong("time"));
        result.setImei(jObject.getString("imei"));
        JSONArray responseArray = jObject.getJSONArray("answers");
        for (int i = 0; i < responseArray.length(); i++) {
            result.responses().add(selector.deserialize(responseArray.getJSONObject(i)));
        }
        return result;
    }

    /**
     * Serializes a result model into corresponding JSON string.
     * @param model The model to serialize.
     * @return The serialized JSON string.
     * @throws JSONException Thrown when there is a problem serializing JSON.
     * @throws QuestionTypeNotSupportedException Thrown when there is an unsupported question type.
     */
    public String getJson(ResultModel model)
            throws JSONException, QuestionTypeNotSupportedException {
        JSONObject jObject = new JSONObject();
        jObject.put("id", model.getId());
        jObject.put("len", model.getLength());
        jObject.put("longitude", model.getLongitude());
        jObject.put("latitude", model.getLatitude());
        jObject.put("time", model.getTime());
        jObject.put("imei", model.getImei());
        JSONArray responseArray = new JSONArray();
        for (IResponse r : model.responses()) {
            responseArray.put(selector.serialize(r));
        }
        jObject.put("answers", responseArray);
        return jObject.toString();
    }

    private ResponseSerializerSelector selector;
}
