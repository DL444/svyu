package mg.studio.android.survey.clients;

import mg.studio.android.survey.models.SurveyModel;

/**
 * Represents an abstract survey client.
 */
public interface ISurveyClient {
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
     * Deletes a survey model.
     * @param id The ID of the survey to delete.
     * @param callback The callback methods executed when the operation completes.
     */
    void deleteSurvey(int id, ISurveyClientCallback callback);
}
