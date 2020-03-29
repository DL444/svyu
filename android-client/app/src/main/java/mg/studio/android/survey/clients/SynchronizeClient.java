package mg.studio.android.survey.clients;

import android.util.Pair;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

import javax.inject.Inject;

import mg.studio.android.survey.dal.DbClient;
import mg.studio.android.survey.dal.HttpClient;

/**
 * Represents a client that handles synchronization of local and remote data.
 */
public class SynchronizeClient {

    /**
     * Constructs a new instance of SynchronizeClient.
     * @param dbClient The database client to use.
     * @param httpClient The HTTP client to use.
     */
    @Inject
    public SynchronizeClient(DbClient dbClient,
                             HttpClient httpClient,
                             DefaultHttpErrorHandler defaultErrorHandler) {
        this.dbClient = dbClient;
        this.httpClient = httpClient;
        this.defaultErrorHandler = defaultErrorHandler;
    }

    /**
     * Synchronizes offline survey results with server.
     * @param callback The callback methods executed when the operation completes.
     */
    public void synchronizeResults(final IResultSynchronizeCallback callback) {
        final ArrayList<Pair<String, String>> results = dbClient.getAll(resultCollection);
        if (results.size() == 0) {
            callback.onComplete(0);
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < results.size() - 1; i++) {
            builder.append(results.get(i).second).append(",");
        }
        builder.append(results.get(results.size() - 1).second).append("]");
        String json = builder.toString();
        httpClient.postJson("https://svyu.azure-api.net/batchResponse", json,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onComplete(results.size());
                        dbClient.clear(resultCollection);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (!defaultErrorHandler.handle(error, callback)) {
                            callback.onError(ClientErrorType.Unknown, error);
                        }
                    }
                });
    }

    private static final String resultCollection = "results";

    private final DbClient dbClient;
    private final HttpClient httpClient;
    private final DefaultHttpErrorHandler defaultErrorHandler;
}
