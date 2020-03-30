package mg.studio.android.survey.viewmodels;

import android.content.Context;

import mg.studio.android.survey.R;
import mg.studio.android.survey.models.IQuestion;
import mg.studio.android.survey.models.SingleChoiceQuestion;

public final class SingleChoiceQuestionViewModel implements IQuestionViewModel {

    public SingleChoiceQuestionViewModel(Context context) {
        this(new SingleChoiceQuestion(), context);
    }

    public SingleChoiceQuestionViewModel(SingleChoiceQuestion model, Context context) {
        this.model = model;
        this.context = context;
    }

    @Override
    public String getQuestion() {
        String q = model.getQuestion();
        if (q == null || q.trim().isEmpty()) {
            return context.getString(R.string.questionPlaceholder);
        } else {
            return model.getQuestion();
        }
    }

    @Override
    public void setQuestion(String question) {
        model.setQuestion(question);
    }

    @Override
    public String getDescription() {
        if (model.options().size() > 0) {
            return context.getString(R.string.singleChoiceQuestionDescription, model.options().size());
        } else {
            return context.getString(R.string.singleChoiceQuestionEmptyDescription);
        }
    }

    @Override
    public IQuestion getModel() {
        return model;
    }

    @Override
    public boolean hasQuestion() {
        String q = model.getQuestion();
        return q != null && !q.trim().isEmpty();
    }

    private final SingleChoiceQuestion model;
    private final Context context;
}
