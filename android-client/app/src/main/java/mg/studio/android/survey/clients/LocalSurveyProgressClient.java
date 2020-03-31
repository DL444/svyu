package mg.studio.android.survey.clients;

import org.json.JSONException;

import javax.inject.Inject;

import mg.studio.android.survey.dal.DbClient;
import mg.studio.android.survey.models.ResultModel;
import mg.studio.android.survey.models.SurveyModel;
import mg.studio.android.survey.serializers.QuestionTypeNotSupportedException;
import mg.studio.android.survey.serializers.ResultSerializer;
import mg.studio.android.survey.serializers.SurveySerializer;

public class LocalSurveyProgressClient implements ISurveyProgressClient {

    @Inject
    public LocalSurveyProgressClient(DbClient dbClient, SurveySerializer surveySerializer, ResultSerializer resultSerializer) {
        this.dbClient = dbClient;
        this.surveySerializer = surveySerializer;
        this.resultSerializer = resultSerializer;

        dbClient.createCollection(progressCollection);
    }

    @Override
    public void saveProgress(SurveyModel survey, ResultModel result, ISurveyProgressClientCallback callback) {
        try{
            String json = surveySerializer.getJson(survey);
            dbClient.upsert(progressCollection, tmpSurveyKey, json);
            json = resultSerializer.getJson(result);
            dbClient.upsert(progressCollection, tmpResultKey, json);
            callback.onComplete(survey, result);
        } catch (JSONException ex) {
            callback.onError(ClientErrorType.Serialization, ex);
        } catch (QuestionTypeNotSupportedException ex) {
            callback.onError(ClientErrorType.Versioning, ex);
        }
    }

    @Override
    public void getProgress(ISurveyProgressClientCallback callback) {
        String surveyJson = dbClient.getOne(progressCollection, tmpSurveyKey);
        String resultJson = dbClient.getOne(progressCollection, tmpResultKey);
        if (surveyJson == null || resultJson == null) {
            callback.onComplete(null, null);
        } else {
            try {
                SurveyModel survey = surveySerializer.getModel(surveyJson);
                ResultModel result = resultSerializer.getModel(resultJson);
                callback.onComplete(survey, result);
            } catch (JSONException ex) {
                callback.onError(ClientErrorType.Serialization, ex);
            } catch (QuestionTypeNotSupportedException ex) {
                callback.onError(ClientErrorType.Versioning, ex);
            }
        }
    }

    @Override
    public void clearProgress(ISurveyProgressClientCallback callback) {
        dbClient.clear(progressCollection);
        if (callback != null) {
            callback.onComplete(null, null);
        }
    }

    private static final String progressCollection = "progress";
    private static final String tmpSurveyKey = "tmpSurvey";
    private static final String tmpResultKey = "tmpResult";

    private final DbClient dbClient;
    private final SurveySerializer surveySerializer;
    private final ResultSerializer resultSerializer;
}
