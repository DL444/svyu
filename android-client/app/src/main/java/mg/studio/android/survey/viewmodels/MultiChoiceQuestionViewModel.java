package mg.studio.android.survey.viewmodels;

import android.content.Context;

import mg.studio.android.survey.R;
import mg.studio.android.survey.models.IQuestion;
import mg.studio.android.survey.models.MultiChoiceQuestion;

public final class MultiChoiceQuestionViewModel implements IQuestionViewModel {


    public MultiChoiceQuestionViewModel(Context context) {
        this(new MultiChoiceQuestion(), context);
    }

    public MultiChoiceQuestionViewModel(MultiChoiceQuestion model, Context context) {
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
            return context.getString(R.string.multiChoiceQuestionDescription, model.options().size());
        } else {
            return context.getString(R.string.multiChoiceQuestionEmptyDescription);
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

    private final MultiChoiceQuestion model;
    private final Context context;

}
