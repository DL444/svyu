package mg.studio.android.survey.viewmodels;

import android.content.Context;

import java.util.ArrayList;

import mg.studio.android.survey.R;
import mg.studio.android.survey.models.ChoiceQuestionBase;
import mg.studio.android.survey.models.IQuestion;

public abstract class ChoiceQuestionViewModelBase implements IQuestionViewModel {

    public ChoiceQuestionViewModelBase(ChoiceQuestionBase model, Context context) {
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
    public IQuestion getModel() {
        return model;
    }

    @Override
    public boolean hasQuestion() {
        String q = model.getQuestion();
        return q != null && !q.trim().isEmpty();
    }

    public ArrayList<String> options() {
        return model.options();
    }

    protected final ChoiceQuestionBase model;
    protected final Context context;
}
