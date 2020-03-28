package mg.studio.android.survey.serializers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import mg.studio.android.survey.models.IResponse;
import mg.studio.android.survey.models.QuestionType;
import mg.studio.android.survey.models.ResultModel;

/**
 * Represents a serializer that serializes and deserializes result models.
 */
public final class ResultSerializer {

    /**
     * Gets the singleton instance of ResultSerializer class.
     * @return The singleton instance.
     */
    public static ResultSerializer getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ResultSerializer();
                }
            }
        }
        return instance;
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
            result.responses().add(dispatchForDeserialize(responseArray.getJSONObject(i)));
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
            responseArray.put(dispatchForSerialize(r));
        }
        jObject.put("answers", responseArray);
        return jObject.toString();
    }

    private JSONObject dispatchForSerialize(IResponse model)
            throws JSONException, QuestionTypeNotSupportedException {
        String type = model.getType().toString();
        if (dispatchTable.containsKey(type)) {
            return dispatchTable.get(type).serialize(model);
        } else {
            throw new QuestionTypeNotSupportedException(type);
        }
    }

    private IResponse dispatchForDeserialize(JSONObject json)
            throws JSONException, QuestionTypeNotSupportedException {
        String type = json.getString("type").toLowerCase();
        if (dispatchTable.containsKey(type)) {
            return dispatchTable.get(type).deserialize(json);
        } else {
            throw new QuestionTypeNotSupportedException(type);
        }
    }

    private ResultSerializer() {
        // Register all response serializers here.
        dispatchTable.put(QuestionType.Single.toString(), new SingleChoiceResponseSerializer());
        dispatchTable.put(QuestionType.Multiple.toString(), new MultiChoiceResponseSerializer());
        dispatchTable.put(QuestionType.Text.toString(), new TextResponseSerializer());
    }

    private Hashtable<String, IResponseSerializer> dispatchTable = new Hashtable<>();

    private static volatile ResultSerializer instance;
    private static final Object lock = new Object();
}
