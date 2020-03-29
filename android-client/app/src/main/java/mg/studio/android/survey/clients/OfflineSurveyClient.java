package mg.studio.android.survey.clients;

import org.json.JSONException;

import java.util.UUID;

import javax.inject.Inject;

import mg.studio.android.survey.dal.DbClient;
import mg.studio.android.survey.models.ResultModel;
import mg.studio.android.survey.models.SurveyModel;
import mg.studio.android.survey.serializers.QuestionTypeNotSupportedException;
import mg.studio.android.survey.serializers.ResultSerializer;
import mg.studio.android.survey.serializers.SurveySerializer;

/**
 * Represents a client that retrieves and stores survey data to a local database.
 */
final class OfflineSurveyClient implements ISurveyClient {

    /**
     * Creates an OfflineSurveyClient object.
     * @param dbClient The database client to use.
     * @param surveySerializer The survey serializer to use.
     */
    @Inject
    public OfflineSurveyClient(DbClient dbClient, SurveySerializer surveySerializer) {
        this.dbClient = dbClient;
        this.surveySerializer = surveySerializer;

        dbClient.createCollection(cachedSurveyCollection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getSurvey(String id, ISurveyClientCallback callback) {
        String value = dbClient.getOne(cachedSurveyCollection, cachedSurveyKey);
        if (value == null) {
            callback.onError(ClientErrorType.CacheMiss, null);
        } else {
            try {
                SurveyModel survey = surveySerializer.getModel(value);
                callback.onComplete(survey);
            } catch (JSONException ex) {
                callback.onError(ClientErrorType.Serialization, ex);
            } catch (QuestionTypeNotSupportedException ex) {
                callback.onError(ClientErrorType.Versioning, ex);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postSurvey(SurveyModel survey, ISurveyClientCallback callback) {
        callback.onError(ClientErrorType.NotSupported, null);
    }

    private static final String resultCollection = "results";
    private static final String cachedSurveyCollection = "cachedSurvey";
    private static final String cachedSurveyKey = "latest";

    private final DbClient dbClient;
    private final SurveySerializer surveySerializer;
}
