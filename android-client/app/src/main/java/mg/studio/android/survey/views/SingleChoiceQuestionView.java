package mg.studio.android.survey.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import mg.studio.android.survey.R;
import mg.studio.android.survey.models.IQuestion;
import mg.studio.android.survey.models.IResponse;
import mg.studio.android.survey.models.SingleChoiceQuestion;
import mg.studio.android.survey.models.SingleChoiceResponse;

public final class SingleChoiceQuestionView extends QuestionViewBase {

    public static SingleChoiceQuestionView createInstance(IQuestion question) {
        SingleChoiceQuestionView fragment = new SingleChoiceQuestionView();
        Bundle args = new Bundle();
        args.putSerializable("question", question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (SingleChoiceQuestion) getArguments().getSerializable("question");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       return inflater.inflate(R.layout.question_single_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView title = getView().findViewById(R.id.title);
        title.setText(question.getQuestion());

        ViewGroup container = getView().findViewById(R.id.opts);
        for (String opt : question.options()) {
            RadioButton optBtn = new RadioButton(getContext());
            optBtn.setOnClickListener(checkClicked);
            optBtn.setText(opt);
            container.addView(optBtn);
        }
    }

    @Override
    public IResponse getResponse() {
        SingleChoiceResponse response = new SingleChoiceResponse();
        ViewGroup container = getView().findViewById(R.id.opts);
        for (int i = 0; i < container.getChildCount(); i++) {
            RadioButton optBtn = (RadioButton)container.getChildAt(i);
            if (optBtn.isChecked()) {
                response.setResponse(i);
                return response;
            }
        }
        return response;
    }

    private View.OnClickListener checkClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getValidityChangedCallback().onResponseValidityChanged(true);
        }
    };

    private SingleChoiceQuestion question;
}
