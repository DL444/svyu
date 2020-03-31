package mg.studio.android.survey.viewmodels;

import android.content.Context;

import mg.studio.android.survey.R;
import mg.studio.android.survey.models.IQuestion;
import mg.studio.android.survey.models.TextQuestion;

public final class TextQuestionViewModel implements IQuestionViewModel {

    public TextQuestionViewModel(Context context) {
        this(new TextQuestion(), context);
    }

    public TextQuestionViewModel(TextQuestion model, Context context) {
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
        return context.getString(R.string.textQuestionDescription);
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

    @Override
    public boolean isValid() {
        return hasQuestion();
    }

    private final TextQuestion model;
    private transient final Context context;
}
