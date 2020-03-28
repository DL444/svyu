package mg.studio.android.survey.clients;

import mg.studio.android.survey.models.SurveyModel;

/**
 * Represents a set of callback methods executed when a survey client operation completes.
 */
public interface ISurveyClientCallback extends IClientCallback {
    /**
     * A callback method executed when an operation successfully completes.
     * @param survey The survey model related to the operation.
     */
    void onComplete(SurveyModel survey);

    /**
     * {@inheritDoc}
     */
    void onError(ClientErrorType errorType, Exception exception);
}
