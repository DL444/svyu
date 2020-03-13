package mg.studio.android.survey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

class Survey implements Serializable {
    private Survey() { }

    public int getId() {
        return id;
    }

    public int getLength() {
        return length;
    }

    public ISurveyQuestion[] getQuestions() {
        return questions;
    }

    public static Survey parse(String json) throws JSONException, QuestionTypeNotSupportedException {
        JSONObject jObject = new JSONObject(json).getJSONObject("survey");
        Survey survey = new Survey();
        survey.id = jObject.getInt("id");
        survey.length = jObject.getInt("len");
        survey.questions = new ISurveyQuestion[survey.length];
        JSONArray questionArray = jObject.getJSONArray("questions");
        for (int i = 0; i < survey.length; i++) {
            survey.questions[i] = SurveyQuestionFactory.getQuestion(questionArray.getJSONObject(i));
        }
        return survey;
    }

    private int id;
    private int length;
    private ISurveyQuestion[] questions;
}
