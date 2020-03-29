package mg.studio.android.survey.clients;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;

import java.util.Random;

import javax.inject.Inject;

import mg.studio.android.survey.dal.DbClient;
import mg.studio.android.survey.dal.HttpClient;
import mg.studio.android.survey.models.ResultModel;
import mg.studio.android.survey.models.SurveyModel;
import mg.studio.android.survey.serializers.QuestionTypeNotSupportedException;
import mg.studio.android.survey.serializers.ResultSerializer;
import mg.studio.android.survey.serializers.SurveySerializer;

/**
 * Represents a client that retrieves and stores data to a remote server through HTTP.
 */
final class OnlineClient implements IClient, IDraftClient {

    /**
     * Creates an OnlineClient object.
     * @param httpClient The HTTP client to use.
     * @param dbClient The database client to use.
     * @param surveySerializer The survey serializer to use.
     * @param resultSerializer The result serializer to use.
     * @param draftClient The draft client to use.
     * @param random The random number generator to use.
     */
    @Inject
    public OnlineClient(HttpClient httpClient,
                        DbClient dbClient,
                        SurveySerializer surveySerializer,
                        ResultSerializer resultSerializer,
                        IDraftClient draftClient,
                        Random random) {
        this.httpClient = httpClient;
        this.dbClient = dbClient;
        this.surveySerializer = surveySerializer;
        this.resultSerializer = resultSerializer;
        this.draftClient = draftClient;
        this.random = random;

        dbClient.createCollection(cachedSurveyCollection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getSurvey(String id, final ISurveyClientCallback callback) {
        httpClient.get("https://svyu.azure-api.net/survey/" + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            SurveyModel survey = surveySerializer.getModel(response);
                            dbClient.upsert(cachedSurveyCollection, cachedSurveyKey, response);
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
        final ConflictRetryStatus conflictRetryStatus = new ConflictRetryStatus();
        do {
            conflictRetryStatus.conflict = false;
            int id = random.nextInt();
            survey.setId(String.valueOf(id));
            try {
                httpClient.postJson("https://svyu.azure-api.net/survey/" + id,
                        surveySerializer.getJson(survey),
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

    private static final String cachedSurveyCollection = "cachedSurvey";
    private static final String cachedSurveyKey = "latest";

    private final HttpClient httpClient;
    private final DbClient dbClient;
    private final SurveySerializer surveySerializer;
    private final ResultSerializer resultSerializer;
    private final IDraftClient draftClient;
    private final Random random;

    private class ConflictRetryStatus {
        public boolean conflict = false;
    }
}
