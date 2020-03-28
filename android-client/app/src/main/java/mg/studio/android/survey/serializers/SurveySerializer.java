package mg.studio.android.survey.serializers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import mg.studio.android.survey.models.IQuestion;
import mg.studio.android.survey.models.QuestionType;
import mg.studio.android.survey.models.SurveyModel;

/**
 * Represents a serializer that serializes and deserializes survey models.
 */
public final class SurveySerializer {

    /**
     * Gets the singleton instance of SurveySerializer class.
     * @return The singleton instance.
     */
    public static SurveySerializer getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new SurveySerializer();
                }
            }
        }
        return instance;
    }

    /**
     * Deserializes JSON string into corresponding survey model.
     * @param json The JSON string to deserialize.
     * @return The deserialized survey model.
     * @throws JSONException Thrown when there is a problem deserializing JSON.
     * @throws QuestionTypeNotSupportedException Thrown when there is an unsupported question type.
     */
    public SurveyModel getModel(String json)
            throws JSONException, QuestionTypeNotSupportedException {
        JSONObject jObject = new JSONObject(json).getJSONObject("survey");
        SurveyModel survey = new SurveyModel();
        survey.setId(jObject.getString("id"));
        JSONArray questionsArray = jObject.getJSONArray("questions");
        for (int i = 0; i < questionsArray.length(); i++) {
            survey.questions().add(dispatchForDeserialize(questionsArray.getJSONObject(i)));
        }
        return survey;
    }

    /**
     * Serializes a survey model into corresponding JSON string.
     * @param model The model to serialize.
     * @return The serialized JSON string.
     * @throws JSONException Thrown when there is a problem serializing JSON.
     * @throws QuestionTypeNotSupportedException Thrown when there is an unsupported question type.
     */
    public String getJson(SurveyModel model)
            throws JSONException, QuestionTypeNotSupportedException {
        JSONObject jObject = new JSONObject();
        jObject.put("id", model.getId());
        jObject.put("len", model.getLength());
        JSONArray questionsArray = new JSONArray();
        for (IQuestion q : model.questions()) {
            questionsArray.put(dispatchForSerialize(q));
        }
        jObject.put("questions", questionsArray);
        JSONObject root = new JSONObject();
        root.put("survey", jObject);
        return root.toString();
    }

    private JSONObject dispatchForSerialize(IQuestion model)
            throws JSONException, QuestionTypeNotSupportedException {
        String type = model.getType().toString();
        if (dispatchTable.containsKey(type)) {
            return dispatchTable.get(type).serialize(model);
        } else {
            throw new QuestionTypeNotSupportedException(type);
        }
    }

    private IQuestion dispatchForDeserialize(JSONObject questionJson)
            throws JSONException, QuestionTypeNotSupportedException {
        String type = questionJson.getString("type").toLowerCase();
        if (dispatchTable.containsKey(type)) {
            return dispatchTable.get(type).deserialize(questionJson);
        } else {
            throw new QuestionTypeNotSupportedException(type);
        }
    }

    private SurveySerializer() {
        // Register all question serializers here.
        ChoiceQuestionSerializer choiceSerializer = new ChoiceQuestionSerializer();
        dispatchTable.put(QuestionType.Single.toString(), choiceSerializer);
        dispatchTable.put(QuestionType.Multiple.toString(), choiceSerializer);
        dispatchTable.put(QuestionType.Text.toString(), new TextQuestionSerializer());
    }

    private Hashtable<String, IQuestionSerializer> dispatchTable = new Hashtable<>();

    private static volatile SurveySerializer instance;
    private static final Object lock = new Object();
}
