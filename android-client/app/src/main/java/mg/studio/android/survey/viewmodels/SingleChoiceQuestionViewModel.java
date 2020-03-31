package mg.studio.android.survey.viewmodels;

import android.content.Context;

import mg.studio.android.survey.R;
import mg.studio.android.survey.models.SingleChoiceQuestion;

public final class SingleChoiceQuestionViewModel extends ChoiceQuestionViewModelBase {

    public SingleChoiceQuestionViewModel(Context context) {
        this(new SingleChoiceQuestion(), context);
    }

    public SingleChoiceQuestionViewModel(SingleChoiceQuestion model, Context context) {
        super(model, context);
    }

    @Override
    public String getDescription() {
        if (model.options().size() > 0) {
            return context.getString(R.string.singleChoiceQuestionDescription, model.options().size());
        } else {
            return context.getString(R.string.singleChoiceQuestionEmptyDescription);
        }
    }
}
