package mg.studio.android.survey.di;

import javax.inject.Singleton;

import dagger.Component;
import mg.studio.android.survey.FinalizeActivity;
import mg.studio.android.survey.InitiateScanActivity;
import mg.studio.android.survey.MainActivity;
import mg.studio.android.survey.SettingsActivity;

/**
 * An abstraction of dagger components that performs dependency injection.
 */
@Singleton
@Component(modules = { AppContextModule.class, RandomModule.class, DraftClientModule.class})
public interface InjectionComponent {
    void inject(InitiateScanActivity activity);
    void inject(FinalizeActivity activity);
    void inject(MainActivity activity);
    void inject(SettingsActivity activity);
}
