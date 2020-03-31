package mg.studio.android.survey.viewmodels;

import android.content.Context;

import mg.studio.android.survey.R;
import mg.studio.android.survey.models.MultiChoiceQuestion;

public final class MultiChoiceQuestionViewModel extends ChoiceQuestionViewModelBase {


    public MultiChoiceQuestionViewModel(Context context) {
        this(new MultiChoiceQuestion(), context);
    }

    public MultiChoiceQuestionViewModel(MultiChoiceQuestion model, Context context) {
        super(model, context);
    }

    @Override
    public String getDescription() {
        if (model.options().size() > 0) {
            return context.getString(R.string.multiChoiceQuestionDescription, model.options().size());
        } else {
            return context.getString(R.string.multiChoiceQuestionEmptyDescription);
        }
    }
}
