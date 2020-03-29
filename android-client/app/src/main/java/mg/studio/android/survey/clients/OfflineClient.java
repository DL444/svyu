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
 * Represents a client that retrieves and stores data to a local database.
 */
class OfflineClient implements IClient, IDraftClient {

    /**
     * Creates an OfflineClient object.
     * @param dbClient The database client to use.
     * @param surveySerializer The survey serializer to use.
     * @param resultSerializer The result serializer to use.
     */
    @Inject
    public OfflineClient(DbClient dbClient,
                         SurveySerializer surveySerializer,
                         ResultSerializer resultSerializer,
                         IDraftClient draftClient) {
        this.dbClient = dbClient;
        this.surveySerializer = surveySerializer;
        this.resultSerializer = resultSerializer;
        this.draftClient = draftClient;

        dbClient.createCollection(resultCollection);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void postResult(ResultModel result, IResultClientCallback callback) {
        try {
            String key = UUID.randomUUID().toString();
            String value = resultSerializer.getJson(result);
            dbClient.upsert(resultCollection, key, value);
            callback.onComplete(result);
        } catch (JSONException ex) {
            callback.onError(ClientErrorType.Serialization, ex);
        } catch (QuestionTypeNotSupportedException ex) {
            callback.onError(ClientErrorType.Versioning, ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getSurveyDraft(ISurveyClientCallback callback) {
        draftClient.getSurveyDraft(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSurveyDraft(SurveyModel model, ISurveyClientCallback callback) {
        draftClient.saveSurveyDraft(model, callback);
    }

    private static final String resultCollection = "results";
    private static final String cachedSurveyCollection = "cachedSurvey";
    private static final String cachedSurveyKey = "latest";

    private final DbClient dbClient;
    private final SurveySerializer surveySerializer;
    private final ResultSerializer resultSerializer;
    private final IDraftClient draftClient;
}
