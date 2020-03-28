package mg.studio.android.survey.views;

import android.content.Context;

import androidx.fragment.app.Fragment;

import mg.studio.android.survey.models.IResponse;

public abstract class QuestionViewBase extends Fragment {

    public QuestionViewBase() { }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IResponseValidityChangedListener) {
            validityChangedCallback = (IResponseValidityChangedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IResponseValidityChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        validityChangedCallback = null;
    }

    public abstract IResponse getResponse();

    protected IResponseValidityChangedListener getValidityChangedCallback() {
        return validityChangedCallback;
    }

    private IResponseValidityChangedListener validityChangedCallback;
}
