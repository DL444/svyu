package mg.studio.android.survey.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import mg.studio.android.survey.SurveyApplication;

/**
 * Dagger module that provides application context for dependency injection.
 */
@Module
public class AppContextModule {

    /**
     * Constructs a new instance of AppContextModule.
     * @param application The application context to provide for injection.
     */
    public AppContextModule(SurveyApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideAppContext() {
        return application;
    }

    private final SurveyApplication application;
}