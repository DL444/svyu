package mg.studio.android.survey.clients;

import com.android.volley.VolleyError;

/**
 * An HTTP error handler that handles errors of a specific status code.
 */
final class StatusCodeHttpErrorHandler implements IHttpErrorHandler {

    /**
     * Creates an instance of StatusCodeHttpErrorHandler class.
     * @param statusCode The status code to match.
     * @param errorType The error type to provide in callback.
     */
    public StatusCodeHttpErrorHandler(int statusCode, ClientErrorType errorType) {
        this.statusCode = statusCode;
        this.errorType = errorType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handle(VolleyError error, IClientCallback callback) {
        if (error.networkResponse != null && error.networkResponse.statusCode == statusCode) {
            callback.onError(errorType, error);
            return true;
        } else {
            return false;
        }
    }

    private final int statusCode;
    private final ClientErrorType errorType;
}
