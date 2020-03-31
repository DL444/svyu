package mg.studio.android.survey.clients;

import org.json.JSONException;

import javax.inject.Inject;

import mg.studio.android.survey.dal.DbClient;
import mg.studio.android.survey.models.SurveyModel;
import mg.studio.android.survey.serializers.QuestionTypeNotSupportedException;
import mg.studio.android.survey.serializers.SurveySerializer;

/**
 * Represents a client that saves and retrieves draft surveys from a local database.
 */
public final class LocalDraftClient implements IDraftClient {

    /**
     * Creates an instance of LocalDraftClient.
     * @param dbClient The database client to use.
     */
    @Inject
    public LocalDraftClient(DbClient dbClient,
                            SurveySerializer serializer) {
        this.dbClient = dbClient;
        this.serializer = serializer;

        dbClient.createCollection(draftSurveyCollection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getSurveyDraft(ISurveyClientCallback callback) {
        String value = dbClient.getOne(draftSurveyCollection, draftSurveyKey);
        if (value == null) {
            callback.onComplete(null);
        } else {
            try {
                SurveyModel model = serializer.getModel(value);
                callback.onComplete(model);
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
    public void saveSurveyDraft(SurveyModel model, ISurveyClientCallback callback) {
        try{
            String json = serializer.getJson(model);
            dbClient.upsert(draftSurveyCollection, draftSurveyKey, json);
            callback.onComplete(model);
        } catch (JSONException ex) {
            callback.onError(ClientErrorType.Serialization, ex);
        } catch (QuestionTypeNotSupportedException ex) {
            callback.onError(ClientErrorType.Versioning, ex);
        }
    }

    private static final String draftSurveyCollection = "draftSurvey";
    private static final String draftSurveyKey = "draft";

    private final DbClient dbClient;
    private final SurveySerializer serializer;
}
