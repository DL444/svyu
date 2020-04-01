package mg.studio.android.survey.viewmodels;

import android.content.Context;

import javax.inject.Inject;

import mg.studio.android.survey.models.IQuestion;
import mg.studio.android.survey.models.MultiChoiceQuestion;
import mg.studio.android.survey.models.SingleChoiceQuestion;
import mg.studio.android.survey.models.StarRateQuestion;
import mg.studio.android.survey.models.TextQuestion;

public final class QuestionViewModelSelector {

    @Inject
    public QuestionViewModelSelector(Context context) {
        this.context = context;
    }

    public IQuestionViewModel getViewModel(IQuestion model) {
        switch (model.getType()) {
            case Single:
                return new SingleChoiceQuestionViewModel((SingleChoiceQuestion) model, context);
            case Multiple:
                return new MultiChoiceQuestionViewModel((MultiChoiceQuestion) model, context);
            case Text:
                return new TextQuestionViewModel((TextQuestion) model, context);
            case StarRate:
                return new StarRateQuestionViewModel((StarRateQuestion) model, context);
        }
        return null;
    }

    private Context context;
}
