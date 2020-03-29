package mg.studio.android.survey.clients;

import mg.studio.android.survey.models.ResultModel;

/**
 * Represents an abstract result client.
 */
public interface IResultClient {

    /**
     * Posts a survey result model.
     * @param result The result model to be posted.
     * @param callback The callback methods executed when the operation completes.
     */
    void postResult(ResultModel result, IResultClientCallback callback);
}
