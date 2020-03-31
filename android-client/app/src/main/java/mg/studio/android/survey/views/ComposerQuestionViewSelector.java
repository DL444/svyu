package mg.studio.android.survey.views;

import android.content.Context;

import javax.inject.Inject;

import mg.studio.android.survey.models.QuestionType;
import mg.studio.android.survey.viewmodels.ChoiceQuestionViewModelBase;
import mg.studio.android.survey.viewmodels.IQuestionViewModel;
import mg.studio.android.survey.viewmodels.MultiChoiceQuestionViewModel;
import mg.studio.android.survey.viewmodels.SingleChoiceQuestionViewModel;
import mg.studio.android.survey.viewmodels.TextQuestionViewModel;

public final class ComposerQuestionViewSelector {

    @Inject
    public ComposerQuestionViewSelector(Context context) {
        this.context = context;
    }

    public ComposerQuestionViewBase getView(QuestionType type) {
        switch (type) {
            case Single:
                return getView(new SingleChoiceQuestionViewModel(context));
            case Multiple:
                return getView(new MultiChoiceQuestionViewModel(context));
            case Text:
                return getView(new TextQuestionViewModel(context));
        }
        return null;
    }

    public ComposerQuestionViewBase getView(IQuestionViewModel question) {
        switch (question.getModel().getType()) {
            case Single:
            case Multiple:
                return ComposerChoiceQuestionView.createInstance((ChoiceQuestionViewModelBase) question);
            case Text:
                return ComposerTextQuestionView.createInstance(question);
        }
        return null;
    }

    private final Context context;
}
