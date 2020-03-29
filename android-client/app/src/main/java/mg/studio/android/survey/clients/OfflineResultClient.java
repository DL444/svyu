package mg.studio.android.survey.clients;

import org.json.JSONException;

import java.util.UUID;

import javax.inject.Inject;

import mg.studio.android.survey.dal.DbClient;
import mg.studio.android.survey.models.ResultModel;
import mg.studio.android.survey.serializers.QuestionTypeNotSupportedException;
import mg.studio.android.survey.serializers.ResultSerializer;

/**
 * Represents a client that retrieves and stores result data to a local database.
 */
final class OfflineResultClient implements IResultClient {

    /**
     * Creates an OfflineResultClient object.
     * @param dbClient The database client to use.
     * @param resultSerializer The result serializer to use.
     */
    @Inject
    public OfflineResultClient(DbClient dbClient, ResultSerializer resultSerializer) {
        this.dbClient = dbClient;
        this.resultSerializer = resultSerializer;

        dbClient.createCollection(resultCollection);
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

    private static final String resultCollection = "results";

    private final DbClient dbClient;
    private final ResultSerializer resultSerializer;
}
