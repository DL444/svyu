package mg.studio.android.survey.clients;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

import static android.content.Context.MODE_PRIVATE;

/**
 * Represents a factory class that creates client instances.
 */
public final class ClientFactory {
    /**
     * Creates an instance of ClientFactory class.
     * @param appContext The application context to create clients with.
     * @param onlineClient The online version of client to provide.
     * @param offlineClient The offline version of client to provide.
     */
    @Inject
    public ClientFactory(Context appContext, OnlineClient onlineClient, OfflineClient offlineClient) {
        Context context = appContext.getApplicationContext();
        this.onlineClient = onlineClient;
        this.offlineClient = offlineClient;
        prefs = context.getSharedPreferences(context.getPackageName() + ".pref", MODE_PRIVATE);
    }

    /**
     * Creates a client instance.
     * @return Created client instance.
     */
    public IClient getClient() {
        boolean offline = prefs.getBoolean(workOfflineKey, false);
        if (offline) {
            return offlineClient;
        } else {
            return onlineClient;
        }
    }

    private final SharedPreferences prefs;
    private final OnlineClient onlineClient;
    private final OfflineClient offlineClient;
    private static final String workOfflineKey = "workOffline";
}
