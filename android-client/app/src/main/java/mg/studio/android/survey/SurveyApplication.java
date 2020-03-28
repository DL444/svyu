package mg.studio.android.survey;

import android.app.Application;

import java.util.Random;

import mg.studio.android.survey.di.AppContextModule;
import mg.studio.android.survey.di.DaggerInjectionComponent;
import mg.studio.android.survey.di.InjectionComponent;
import mg.studio.android.survey.di.RandomModule;

/**
 * Represents the application class.
 */
public final class SurveyApplication extends Application {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerInjectionComponent.builder()
                .appContextModule(new AppContextModule(this))
                .randomModule(new RandomModule(new Random(System.currentTimeMillis())))
                .build();
    }

    /**
     * Gets the dagger component used for dependency injection.
     * @return The dagger component.
     */
    public InjectionComponent getComponent() {
        return component;
    }

    private InjectionComponent component;
}
