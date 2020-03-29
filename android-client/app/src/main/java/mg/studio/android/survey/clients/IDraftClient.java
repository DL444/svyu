package mg.studio.android.survey.clients;

import mg.studio.android.survey.models.SurveyModel;

/**
 * Represents an abstract client that handles draft storage.
 */
public interface IDraftClient {

    /**
     * Gets the draft survey model.
     * @param callback The callback methods executed when the operation completes.
     */
    void getSurveyDraft(ISurveyClientCallback callback);

    /**
     * Saves a survey model as draft.
     * @param model The survey to save.
     * @param callback The callback methods executed when the operation completes.
     */
    void saveSurveyDraft(SurveyModel model, ISurveyClientCallback callback);
}
