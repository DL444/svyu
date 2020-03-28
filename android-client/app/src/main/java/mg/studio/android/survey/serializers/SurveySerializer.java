package mg.studio.android.survey.serializers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import mg.studio.android.survey.models.IQuestion;
import mg.studio.android.survey.models.SurveyModel;

/**
 * Represents a serializer that serializes and deserializes survey models.
 */
public final class SurveySerializer {

    /**
     * Constructs an instance of SurveySerializer class.
     * @param selector The question serialization selector to use.
     */
    @Inject
    public SurveySerializer(QuestionSerializerSelector selector) {
        this.selector = selector;
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
            survey.questions().add(selector.deserialize(questionsArray.getJSONObject(i)));
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
            questionsArray.put(selector.serialize(q));
        }
        jObject.put("questions", questionsArray);
        JSONObject root = new JSONObject();
        root.put("survey", jObject);
        return root.toString();
    }

    private QuestionSerializerSelector selector;
}
