package mg.studio.android.survey.clients;

/**
 * Represents a set of callback methods executed when a result synchronize operation completes.
 */
public interface IResultSynchronizeCallback extends IClientCallback {

    /**
     * A callback method executed when an operation successfully completes.
     * @param count The number of items synced.
     */
    void onComplete(int count);

    /**
     * {@inheritDoc}
     */
    void onError(ClientErrorType errorType, Exception exception);
}
