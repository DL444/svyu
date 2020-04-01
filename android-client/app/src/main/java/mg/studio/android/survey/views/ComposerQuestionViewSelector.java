package mg.studio.android.survey.views;

import android.content.Context;

import javax.inject.Inject;

import mg.studio.android.survey.models.QuestionType;
import mg.studio.android.survey.viewmodels.ChoiceQuestionViewModelBase;
import mg.studio.android.survey.viewmodels.IQuestionViewModel;
import mg.studio.android.survey.viewmodels.MultiChoiceQuestionViewModel;
import mg.studio.android.survey.viewmodels.SingleChoiceQuestionViewModel;
import mg.studio.android.survey.viewmodels.StarRateQuestionViewModel;
import mg.studio.android.survey.viewmodels.TextQuestionViewModel;

public final class ComposerQuestionViewSelector {

    @Inject
    public ComposerQuestionViewSelector(Context context) {
        this.context = context;
    }

    public ComposerQuestionViewBase getView(QuestionType type) {
        IQuestionViewModel vm = null;
        switch (type) {
            case Single:
                vm = new SingleChoiceQuestionViewModel(context);
                break;
            case Multiple:
                vm = new MultiChoiceQuestionViewModel(context);
                break;
            case Text:
                vm = new TextQuestionViewModel(context);
                break;
            case StarRate:
                vm = new StarRateQuestionViewModel(context);
        }
        return getView(vm, false, -1);
    }

    public ComposerQuestionViewBase getView(IQuestionViewModel question, int index) {
        return getView(question, true, index);
    }

    private ComposerQuestionViewBase getView(IQuestionViewModel question, boolean isUpdate, int index) {
        switch (question.getModel().getType()) {
            case Single:
            case Multiple:
                return ComposerChoiceQuestionView.createInstance((ChoiceQuestionViewModelBase) question, isUpdate, index);
            case Text:
                return ComposerTextQuestionView.createInstance(question, isUpdate, index);
            case StarRate:
                return ComposerStarRateQuestionView.createInstance(question, isUpdate, index);
        }
        return null;
    }

    private final Context context;
}
