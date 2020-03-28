package mg.studio.android.survey.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import mg.studio.android.survey.R;
import mg.studio.android.survey.models.IQuestion;
import mg.studio.android.survey.models.IResponse;
import mg.studio.android.survey.models.TextQuestion;
import mg.studio.android.survey.models.TextResponse;

public final class TextQuestionView extends QuestionViewBase {

    public static TextQuestionView createInstance(IQuestion question) {
        TextQuestionView fragment = new TextQuestionView();
        Bundle args = new Bundle();
        args.putSerializable("question", question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (TextQuestion) getArguments().getSerializable("question");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.question_text_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView title = getView().findViewById(R.id.title);
        title.setText(question.getQuestion());

        EditText inputBox = getView().findViewById(R.id.inputBox);
        inputBox.addTextChangedListener(textWatcher);
    }

    @Override
    public IResponse getResponse() {
        TextResponse response = new TextResponse();
        EditText input = getView().findViewById(R.id.inputBox);
        response.setResponse(input.getText().toString());
        return response;
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().isEmpty()) {
                getValidityChangedCallback().onResponseValidityChanged(false);
            } else {
                getValidityChangedCallback().onResponseValidityChanged(true);
            }
        }
    };

    private TextQuestion question;
}
