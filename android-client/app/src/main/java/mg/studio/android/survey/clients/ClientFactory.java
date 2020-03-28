package mg.studio.android.survey.clients;

import android.content.Context;

import javax.inject.Inject;

/**
 * Represents a factory class that creates client instances.
 */
public final class ClientFactory {
    /**
     * Creates an instance of ClientFactory class.
     * @param appContext The application context to create clients with.
     */
    @Inject
    public ClientFactory(Context appContext, OnlineClient onlineClient) {
        context = appContext.getApplicationContext();
        this.onlineClient = onlineClient;
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
            return onlineClient;
        }
    }

    private Context context;
    private OnlineClient onlineClient;
    private boolean offline = false;
}
