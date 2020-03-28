package mg.studio.android.survey.clients;

/**
 * Represents an abstraction of a set of callback methods executed when a client operation completes.
 */
interface IClientCallback {

    /**
     * A callback method executed when an operation completed with error.
     * @param errorType The type of error occurred.
     * @param exception The exception, if any, that caused the error.
     */
    void onError(ClientErrorType errorType, Exception exception);

}
