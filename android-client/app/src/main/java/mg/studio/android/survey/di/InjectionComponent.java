package mg.studio.android.survey.di;

import javax.inject.Singleton;

import dagger.Component;
import mg.studio.android.survey.ComposerUploadSurveyActivity;
import mg.studio.android.survey.FinalizeActivity;
import mg.studio.android.survey.InitiateScanActivity;
import mg.studio.android.survey.SurveyActivity;
import mg.studio.android.survey.SettingsActivity;
import mg.studio.android.survey.SurveyComposerActivity;

/**
 * An abstraction of dagger components that performs dependency injection.
 */
@Singleton
@Component(modules = { AppContextModule.class, RandomModule.class, DraftClientModule.class, TempResultClientModule.class})
public interface InjectionComponent {
    void inject(InitiateScanActivity activity);
    void inject(FinalizeActivity activity);
    void inject(SurveyActivity activity);
    void inject(SettingsActivity activity);
    void inject(SurveyComposerActivity activity);
    void inject(ComposerUploadSurveyActivity activity);
}
