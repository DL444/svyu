package mg.studio.android.survey.views;

import mg.studio.android.survey.models.QuestionType;
import mg.studio.android.survey.viewmodels.IQuestionViewModel;

public interface IQuestionOperationRequestListener {
    void onAddQuestionRequested(QuestionType type);
    void onUpdateQuestionRequested(IQuestionViewModel model, int index);
}
