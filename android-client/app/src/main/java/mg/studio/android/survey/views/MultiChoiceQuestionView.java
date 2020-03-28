package mg.studio.android.survey.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import mg.studio.android.survey.R;
import mg.studio.android.survey.models.IQuestion;
import mg.studio.android.survey.models.IResponse;
import mg.studio.android.survey.models.MultiChoiceQuestion;
import mg.studio.android.survey.models.MultiChoiceResponse;

public final class MultiChoiceQuestionView extends QuestionViewBase {

    public static MultiChoiceQuestionView createInstance(IQuestion question) {
        MultiChoiceQuestionView fragment = new MultiChoiceQuestionView();
        Bundle args = new Bundle();
        args.putSerializable("question", question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (MultiChoiceQuestion) getArguments().getSerializable("question");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.question_multiple_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView title = getView().findViewById(R.id.title);
        title.setText(question.getQuestion());

        ViewGroup container = getView().findViewById(R.id.opts);
        for (String opt : question.options()) {
            CheckBox optBtn = new CheckBox(getContext());
            optBtn.setOnClickListener(checkClicked);
            optBtn.setText(opt);
            container.addView(optBtn);
        }
    }

    @Override
    public IResponse getResponse() {
        MultiChoiceResponse response = new MultiChoiceResponse();
        ViewGroup container = getView().findViewById(R.id.opts);
        for (int i = 0; i < container.getChildCount(); i++) {
            CheckBox optBtn = (CheckBox) container.getChildAt(i);
            if (optBtn.isChecked()) {
                response.setResponse(i);
            }
        }
        return response;
    }

    private View.OnClickListener checkClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewGroup opts = MultiChoiceQuestionView.this.getView().findViewById(R.id.opts);
            for (int i = 0; i < opts.getChildCount(); i++) {
                CompoundButton optBtn = (CompoundButton)opts.getChildAt(i);
                if (optBtn.isChecked()) {
                    getValidityChangedCallback().onResponseValidityChanged(true);
                    return;
                }
            }
            getValidityChangedCallback().onResponseValidityChanged(false);
        }
    };

    private MultiChoiceQuestion question;
}
