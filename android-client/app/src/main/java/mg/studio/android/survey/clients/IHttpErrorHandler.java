package mg.studio.android.survey.clients;

import com.android.volley.VolleyError;

/**
 * An interface of handler that handles HTTP errors.
 */
interface IHttpErrorHandler {

    /**
     * Handles an HTTP error.
     * @param error The error to handle.
     * @param callback The callback method to call when handing error.
     * @return True if the error was handled. False otherwise.
     */
    boolean handle(VolleyError error, IClientCallback callback);
}
