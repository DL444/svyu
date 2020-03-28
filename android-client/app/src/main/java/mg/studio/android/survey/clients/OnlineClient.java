package mg.studio.android.survey.clients;

import android.content.Context;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;

import java.util.Random;

import mg.studio.android.survey.dal.HttpClient;
import mg.studio.android.survey.models.ResultModel;
import mg.studio.android.survey.models.SurveyModel;
import mg.studio.android.survey.serializers.QuestionTypeNotSupportedException;
import mg.studio.android.survey.serializers.ResultSerializer;
import mg.studio.android.survey.serializers.SurveySerializer;

/**
 * Represents a client that retrieves and stores data to a remote server through HTTP.
 */
final class OnlineClient implements IClient {

    /**
     * Creates a OnlineClient object.
     * @param appContext The application context to create the client in.
     */
    public OnlineClient(Context appContext) {
        context = appContext.getApplicationContext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getSurvey(String id, final ISurveyClientCallback callback) {
        HttpClient client = HttpClient.getInstance(context);
        client.get("https://svyu.azure-api.net/survey/" + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            SurveyModel survey = SurveySerializer.getInstance().getModel(response);
                            callback.onComplete(survey);
                        } catch (JSONException ex) {
                            callback.onError(ClientErrorType.Serialization, ex);
                        } catch (QuestionTypeNotSupportedException ex) {
                            callback.onError(ClientErrorType.Versioning, ex);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (!handleDefaultErrors(error, callback)) {
                            callback.onError(ClientErrorType.Unknown, error);
                        }
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postSurvey(final SurveyModel survey, final ISurveyClientCallback callback) {
        HttpClient client = HttpClient.getInstance(context);
        final ConflictRetryStatus conflictRetryStatus = new ConflictRetryStatus();
        do {
            conflictRetryStatus.conflict = false;
            int id = random.nextInt();
            survey.setId(String.valueOf(id));
            try {
                client.postJson("https://svyu.azure-api.net/survey/" + id,
                        SurveySerializer.getInstance().getJson(survey),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                callback.onComplete(survey);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (handleDefaultErrors(error, callback)) {
                                    return;
                                } else if (error.networkResponse != null && error.networkResponse.statusCode == 409) {
                                    conflictRetryStatus.conflict = true;
                                } else {
                                    callback.onError(ClientErrorType.Unknown, error);
                                }
                            }
                        });
            } catch (JSONException ex) {
                callback.onError(ClientErrorType.Serialization, ex);
            } catch (QuestionTypeNotSupportedException ex) {
                callback.onError(ClientErrorType.Versioning, ex);
            }
        } while (conflictRetryStatus.conflict);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteSurvey(int id, ISurveyClientCallback callback) {
        callback.onError(ClientErrorType.NotSupported, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postResult(final ResultModel result, final IResultClientCallback callback) {
        HttpClient client = HttpClient.getInstance(context);
        String json;
        try{
            json = ResultSerializer.getInstance().getJson(result);
        } catch (JSONException ex) {
            callback.onError(ClientErrorType.Serialization, ex);
            return;
        } catch (QuestionTypeNotSupportedException ex) {
            callback.onError(ClientErrorType.Versioning, ex);
            return;
        }
        client.postJson("https://svyu.azure-api.net/response", json,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onComplete(result);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (!handleDefaultErrors(error, callback)) {
                            callback.onError(ClientErrorType.Unknown, error);
                        }
                    }
                });
    }


    private boolean handleDefaultErrors(VolleyError error, IClientCallback callback) {
        if (error instanceof NetworkError || error instanceof TimeoutError) {
            callback.onError(ClientErrorType.IO, error);
            return true;
        } else if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
            callback.onError(ClientErrorType.NotFound, error);
            return true;
        }
        return false;
    }
    private Context context;

    private Random random = new Random(System.currentTimeMillis());

    private class ConflictRetryStatus {
        public boolean conflict = false;
    }
}
