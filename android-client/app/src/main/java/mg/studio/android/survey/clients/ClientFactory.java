package mg.studio.android.survey.clients;

import android.content.Context;

public final class ClientFactory {
    public ClientFactory(Context appContext) {
        context = appContext;
        // TODO: Set offline field based on preference.
    }

    public ISurveyClient getSurveyClient() {
        // TODO: Create instances based on online/offline preferences.
        if (offline) {
            throw new RuntimeException("Not implemented.");
        } else {
            return new HttpSurveyClient(context);
        }
    }

    private Context context;
    private boolean offline = false;
}
