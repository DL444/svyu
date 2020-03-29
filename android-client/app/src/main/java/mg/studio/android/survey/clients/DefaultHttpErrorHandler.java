package mg.studio.android.survey.clients;

import com.android.volley.NetworkError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import javax.inject.Inject;

/**
 * An HTTP error handler that handles network IO errors and status code 404 errors.
 */
final class DefaultHttpErrorHandler implements IHttpErrorHandler {

    @Inject
    public DefaultHttpErrorHandler() { }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handle(VolleyError error, IClientCallback callback) {
        if (error instanceof NetworkError || error instanceof TimeoutError) {
            callback.onError(ClientErrorType.IO, error);
            return true;
        } else if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
            callback.onError(ClientErrorType.NotFound, error);
            return true;
        }
        return false;
    }
}
