package mg.studio.android.survey.di;

import dagger.Module;
import dagger.Provides;
import mg.studio.android.survey.clients.ISurveyProgressClient;
import mg.studio.android.survey.clients.LocalSurveyProgressClient;
import mg.studio.android.survey.dal.DbClient;
import mg.studio.android.survey.serializers.ResultSerializer;
import mg.studio.android.survey.serializers.SurveySerializer;

@Module
public class TempResultClientModule {

    @Provides
    public ISurveyProgressClient provideTempResultClient(
            DbClient dbClient, SurveySerializer surveySerializer, ResultSerializer resultSerializer) {
        return new LocalSurveyProgressClient(dbClient, surveySerializer, resultSerializer);
    }
}
