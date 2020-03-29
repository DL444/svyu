package mg.studio.android.survey.clients;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;

import java.util.Random;

import javax.inject.Inject;

import mg.studio.android.survey.dal.DbClient;
import mg.studio.android.survey.dal.HttpClient;
import mg.studio.android.survey.models.SurveyModel;
import mg.studio.android.survey.serializers.QuestionTypeNotSupportedException;
import mg.studio.android.survey.serializers.SurveySerializer;

/**
 * Represents a client that retrieves and stores survey data to a remote server through HTTP.
 */
final class OnlineSurveyClient implements ISurveyClient {

    /**
     * Creates an OnlineSurveyClient object.
     * @param httpClient The HTTP client to use.
     * @param dbClient The database client to use.
     * @param surveySerializer The survey serializer to use.
     * @param random The random number generator to use.
     * @param defaultErrorHandler The default HTTP error handler to use.
     */
    @Inject
    public OnlineSurveyClient(HttpClient httpClient,
                              DbClient dbClient,
                              SurveySerializer surveySerializer,
                              Random random,
                              DefaultHttpErrorHandler defaultErrorHandler) {
        this.httpClient = httpClient;
        this.dbClient = dbClient;
        this.surveySerializer = surveySerializer;
        this.random = random;
        this.defaultErrorHandler = defaultErrorHandler;

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
                        if (!defaultErrorHandler.handle(error, callback)) {
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
                                if (defaultErrorHandler.handle(error, callback)) {
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

    private static final String cachedSurveyCollection = "cachedSurvey";
    private static final String cachedSurveyKey = "latest";

    private final HttpClient httpClient;
    private final DbClient dbClient;
    private final SurveySerializer surveySerializer;
    private final Random random;
    private final DefaultHttpErrorHandler defaultErrorHandler;

    private class ConflictRetryStatus {
        public boolean conflict = false;
    }
}
