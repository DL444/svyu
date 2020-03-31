package mg.studio.android.survey.clients;

import mg.studio.android.survey.models.ResultModel;
import mg.studio.android.survey.models.SurveyModel;

public interface ISurveyProgressClient {

    void saveProgress(SurveyModel survey, ResultModel result, ISurveyProgressClientCallback callback);

    void getProgress(ISurveyProgressClientCallback callback);

    void clearProgress(ISurveyProgressClientCallback callback);
}
