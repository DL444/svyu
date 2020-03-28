package mg.studio.android.survey.clients;

import android.content.Context;

/**
 * Represents a factory class that creates client instances.
 */
public final class ClientFactory {
    /**
     * Creates an instance of ClientFactory class.
     * @param appContext The application context to create clients with.
     */
    public ClientFactory(Context appContext) {
        context = appContext;
        // TODO: Set offline field based on preference.
    }

    /**
     * Creates a client instance.
     * @return Created client instance.
     */
    public IClient getClient() {
        // TODO: Create instances based on online/offline preferences.
        if (offline) {
            throw new RuntimeException("Not implemented.");
        } else {
            return new OnlineClient(context);
        }
    }

    private Context context;
    private boolean offline = false;
}
