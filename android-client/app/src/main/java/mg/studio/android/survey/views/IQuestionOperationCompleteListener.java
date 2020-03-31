package mg.studio.android.survey.views;

import mg.studio.android.survey.viewmodels.IQuestionViewModel;

public interface IQuestionOperationCompleteListener {

    void onQuestionOperationComplete(IQuestionViewModel question, boolean isUpdate, int index);
}
