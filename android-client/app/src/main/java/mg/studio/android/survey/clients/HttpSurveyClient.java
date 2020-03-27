package mg.studio.android.survey.clients;

import android.content.Context;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;

import java.util.Random;

import mg.studio.android.survey.dal.HttpClient;
import mg.studio.android.survey.models.SurveyModel;
import mg.studio.android.survey.serializers.QuestionTypeNotSupportedException;
import mg.studio.android.survey.serializers.SurveySerializer;

final class HttpSurveyClient implements ISurveyClient {

    public HttpSurveyClient(Context appContext) {
        context = appContext.getApplicationContext();
    }

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

    @Override
    public void postSurvey(final SurveyModel survey, final ISurveyClientCallback callback) {
        HttpClient client = HttpClient.getInstance(context);
        final ConflictRetryStatus conflictRetryStatus = new ConflictRetryStatus();
        do {
            conflictRetryStatus.conflict = false;
            int id = random.nextInt();
            survey.setId(id);
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

    @Override
    public void deleteSurvey(int id, ISurveyClientCallback callback) {
        callback.onError(ClientErrorType.NotSupported, null);
    }

    private boolean handleDefaultErrors(VolleyError error, ISurveyClientCallback callback) {
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
