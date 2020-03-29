package mg.studio.android.survey.di;

import dagger.Module;
import dagger.Provides;
import mg.studio.android.survey.clients.IDraftClient;
import mg.studio.android.survey.clients.LocalDraftClient;
import mg.studio.android.survey.dal.DbClient;
import mg.studio.android.survey.serializers.SurveySerializer;

/**
 * Dagger module that provides local draft client for dependency injection.
 */
@Module
class DraftClientModule {

    @Provides
    IDraftClient provideLocalDraftClient(DbClient dbClient, SurveySerializer surveySerializer) {
        return new LocalDraftClient(dbClient, surveySerializer);
    }
}
