package mg.studio.android.survey.clients;

import mg.studio.android.survey.models.ResultModel;

/**
 * Represents a set of callback methods executed when a result client operation completes.
 */
public interface IResultClientCallback extends IClientCallback {

    /**
     * A callback method executed when an operation successfully completes.
     * @param result The result model related to the operation.
     */
    void onComplete(ResultModel result);

    /**
     * {@inheritDoc}
     */
    void onError(ClientErrorType errorType, Exception exception);
}
