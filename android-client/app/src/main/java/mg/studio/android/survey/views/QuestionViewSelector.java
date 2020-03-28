package mg.studio.android.survey.views;

import javax.inject.Inject;

import mg.studio.android.survey.models.IQuestion;

public final class QuestionViewSelector {

    @Inject
    public QuestionViewSelector() { }

    public QuestionViewBase getBoundView(IQuestion question) {
        switch (question.getType()) {
            case Single:
                return SingleChoiceQuestionView.createInstance(question);
            case Multiple:
                return MultiChoiceQuestionView.createInstance(question);
            case Text:
                return TextQuestionView.createInstance(question);
            default:
                return null;
        }
    }
}
