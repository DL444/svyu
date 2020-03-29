package mg.studio.android.survey.clients;

import javax.inject.Inject;

/**
 * A factory class for creating StatusCodeHttpErrorHandler instances.
 */
final class StatusCodeHttpErrorHandlerFactory {

    @Inject
    public StatusCodeHttpErrorHandlerFactory() { }

    /**
     * Creates an instance of StatusCodeHttpErrorHandler class.
     * @param statusCode The status code to match.
     * @param errorType The error type to provide in callback.
     * @return The created instance.
     */
    public IHttpErrorHandler create(int statusCode, ClientErrorType errorType) {
        return new StatusCodeHttpErrorHandler(statusCode, errorType);
    }
}
