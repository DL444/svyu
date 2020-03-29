package mg.studio.android.survey.clients;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;

import javax.inject.Inject;

import mg.studio.android.survey.dal.HttpClient;
import mg.studio.android.survey.models.ResultModel;
import mg.studio.android.survey.serializers.QuestionTypeNotSupportedException;
import mg.studio.android.survey.serializers.ResultSerializer;

/**
 * Represents a client that retrieves and stores result data to a remote server through HTTP.
 */
final class OnlineResultClient implements IResultClient {

    /**
     * Creates an OnlineResultClientObject.
     * @param httpClient The HTTP client to use.
     * @param resultSerializer The result serializer to use.
     * @param defaultErrorHandler The default HTTP error handler to use.
     */
    @Inject
    public OnlineResultClient(HttpClient httpClient,
                              ResultSerializer resultSerializer,
                              DefaultHttpErrorHandler defaultErrorHandler) {
        this.httpClient = httpClient;
        this.resultSerializer = resultSerializer;
        this.defaultErrorHandler = defaultErrorHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postResult(final ResultModel result, final IResultClientCallback callback) {
        String json;
        try{
            json = resultSerializer.getJson(result);
        } catch (JSONException ex) {
            callback.onError(ClientErrorType.Serialization, ex);
            return;
        } catch (QuestionTypeNotSupportedException ex) {
            callback.onError(ClientErrorType.Versioning, ex);
            return;
        }
        httpClient.postJson("https://svyu.azure-api.net/response", json,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onComplete(result);
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

    private final HttpClient httpClient;
    private final ResultSerializer resultSerializer;
    private final DefaultHttpErrorHandler defaultErrorHandler;
}
