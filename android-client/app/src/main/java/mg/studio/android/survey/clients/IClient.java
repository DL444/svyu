package mg.studio.android.survey.clients;

import mg.studio.android.survey.models.ResultModel;
import mg.studio.android.survey.models.SurveyModel;

/**
 * Represents an abstract survey client.
 */
public interface IClient {
    /**
     * Gets a survey model.
     * @param id The ID of the survey to get.
     * @param callback The callback methods executed when the operation completes.
     */
    void getSurvey(String id, ISurveyClientCallback callback);

    /**
     * Posts a survey model.
     * @param survey The survey to post.
     * @param callback The callback methods executed when the operation completes.
     */
    void postSurvey(SurveyModel survey, ISurveyClientCallback callback);

    /**
     * Posts a survey result model.
     * @param result The result model to be posted.
     * @param callback The callback methods executed when the operation completes.
     */
    void postResult(ResultModel result, IResultClientCallback callback);
}
