package mg.studio.android.survey.clients;

import mg.studio.android.survey.models.ResultModel;
import mg.studio.android.survey.models.SurveyModel;

public interface ISurveyProgressClientCallback extends IClientCallback {
    void onComplete(SurveyModel survey, ResultModel result);
}
