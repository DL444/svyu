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
     * @param onlineSurveyClient The online survey client to provide.
     * @param offlineSurveyClient The offline survey of client to provide.
     * @param onlineResultClient The online result client to provide.
     * @param offlineResultClient The offline result client to provide.
     */
    @Inject
    public ClientFactory(Context appContext,
                         OnlineSurveyClient onlineSurveyClient,
                         OfflineSurveyClient offlineSurveyClient,
                         OnlineResultClient onlineResultClient,
                         OfflineResultClient offlineResultClient) {
        Context context = appContext.getApplicationContext();
        this.onlineSurveyClient = onlineSurveyClient;
        this.offlineSurveyClient = offlineSurveyClient;
        this.onlineResultClient = onlineResultClient;
        this.offlineResultClient = offlineResultClient;
        prefs = context.getSharedPreferences(context.getPackageName() + ".pref", MODE_PRIVATE);
    }

    /**
     * Creates a survey client instance.
     * @return Created survey client instance.
     */
    public ISurveyClient getSurveyClient() {
        boolean offline = prefs.getBoolean(workOfflineKey, false);
        if (offline) {
            return offlineSurveyClient;
        } else {
            return onlineSurveyClient;
        }
    }

    /**
     * Creates a result client instance.
     * @return Created result client instance.
     */
    public IResultClient getResultClient() {
        boolean offline = prefs.getBoolean(workOfflineKey, false);
        if (offline) {
            return offlineResultClient;
        } else {
            return onlineResultClient;
        }
    }

    private final SharedPreferences prefs;
    private final OnlineSurveyClient onlineSurveyClient;
    private final OfflineSurveyClient offlineSurveyClient;
    private final OnlineResultClient onlineResultClient;
    private final OfflineResultClient offlineResultClient;
    private static final String workOfflineKey = "workOffline";
}
