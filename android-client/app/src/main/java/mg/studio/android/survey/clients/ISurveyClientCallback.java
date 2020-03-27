package mg.studio.android.survey.clients;

import mg.studio.android.survey.models.SurveyModel;

/**
 * Represents a set of callback methods executed when a survey client operation completes.
 */
public interface ISurveyClientCallback {
    /**
     * A callback method executed when an operation successfully completes.
     * @param survey The survey model related to the operation.
     */
    void onComplete(SurveyModel survey);

    /**
     * A callback method executed when an operation completed with error.
     * @param errorType The type of error occurred.
     * @param exception The exception, if any, that caused the error.
     */
    void onError(ClientErrorType errorType, Exception exception);
}
