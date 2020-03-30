package mg.studio.android.survey.views;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public abstract class ComposerQuestionViewBase extends Fragment {

    public ComposerQuestionViewBase() { }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IAddQuestionCompleteListener) {
            addQuestionCompleteCallback = (IAddQuestionCompleteListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IAddQuestionCompleteListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        addQuestionCompleteCallback = null;
    }

    protected IAddQuestionCompleteListener getAddQuestionCompleteCallback() {
        return addQuestionCompleteCallback;
    }

    private IAddQuestionCompleteListener addQuestionCompleteCallback;
}
