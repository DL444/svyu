package mg.studio.android.survey;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import mg.studio.android.survey.models.IQuestion;
import mg.studio.android.survey.models.IResponse;
import mg.studio.android.survey.models.MultiChoiceQuestion;
import mg.studio.android.survey.models.MultiChoiceResponse;
import mg.studio.android.survey.models.ResultModel;
import mg.studio.android.survey.models.SingleChoiceQuestion;
import mg.studio.android.survey.models.SingleChoiceResponse;
import mg.studio.android.survey.models.SurveyModel;
import mg.studio.android.survey.models.TextQuestion;
import mg.studio.android.survey.models.TextResponse;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        survey = (SurveyModel) this.getIntent().getSerializableExtra(getPackageName() + ".survey");
        result = new ResultModel();
        result.setId(survey.getId());

        current = -1;
        next(null);
    }

    /**
     * Event handler for the primary button of each page.
     * @param sender The view that triggered the handler.
     */
   public void next(View sender) {
        if (current >= 0 && current < survey.getLength()) {
            IResponse response = getResponse(survey.questions().get(current).getType());
            if (!response.hasResponse()) {
                return;
            } else {
                ArrayList<IResponse> responses = result.responses();
                if (current < responses.size()) {
                    responses.set(current, response);
                } else {
                    responses.add(response);
                }
            }
        }

        current++;
        if (current < survey.getLength()) {
            IQuestion question = survey.questions().get(current);
            switch (survey.questions().get(current).getType()) {
                case Single:
                    bindLayout((SingleChoiceQuestion)question);
                    break;
                case Multiple:
                    bindLayout((MultiChoiceQuestion)question);
                    break;
                case Text:
                    bindLayout((mg.studio.android.survey.models.TextQuestion)question);
                    break;
            }
        } else {
            Intent finalizeNavIntent = new Intent(this, FinalizeActivity.class);
            finalizeNavIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            finalizeNavIntent.putExtra(getPackageName() + ".result", result);
            startActivity(finalizeNavIntent);
            this.finish();
        }
    }

    /**
     * Binds the app layout to a survey question.
     * @param question The question for the layout to bind to.
     */
    private void bindLayout(SingleChoiceQuestion question) {
        setContentView(R.layout.question_single);
        bindLayoutTitle(question);
        ViewGroup optGroup = findViewById(R.id.opts);
        ArrayList<String> options = question.options();
        for (String opt : options) {
            RadioButton optBtn = new RadioButton(this);
            optBtn.setOnClickListener(checkClicked);
            optBtn.setText(opt);
            optGroup.addView(optBtn);
        }
    }

    /**
     * Binds the app layout to a survey question.
     * @param question The question for the layout to bind to.
     */
    private void bindLayout(MultiChoiceQuestion question) {
        setContentView(R.layout.question_multiple);
        bindLayoutTitle(question);
        ViewGroup optGroup = findViewById(R.id.opts);
        ArrayList<String> options = question.options();
        for (String opt : options) {
            CheckBox optBtn = new CheckBox(this);
            optBtn.setOnClickListener(checkClicked);
            optBtn.setText(opt);
            optGroup.addView(optBtn);
        }
    }

    /**
     * Binds the app layout to a survey question.
     * @param question The question for the layout to bind to.
     */
    private void bindLayout(TextQuestion question) {
        setContentView(R.layout.question_text);
        EditText inputBox = findViewById(R.id.inputBox);
        inputBox.addTextChangedListener(textInputWatcher);
        bindLayoutTitle(question);
    }

    /**
     * A helper method for binding the title of the question.
     * @param question The question for the layout to bind to.
     */
    private void bindLayoutTitle(IQuestion question) {
        TextView textView = findViewById(R.id.header);
        textView.setText(getString(R.string.questionHeader, current + 1));
        textView = findViewById(R.id.title);
        textView.setText(question.getQuestion());
    }

    private View.OnClickListener checkClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button nextBtn = findViewById(R.id.nextBtn);
            ViewGroup opts = findViewById(R.id.opts);
            for (int i = 0; i < opts.getChildCount(); i++) {
                CompoundButton optBtn = (CompoundButton)opts.getChildAt(i);
                if (optBtn.isChecked()) {
                    nextBtn.setEnabled(true);
                    return;
                }
            }
            nextBtn.setEnabled(false);
        }
    };

    private TextWatcher textInputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void afterTextChanged(Editable s) {
            Button nextBtn = findViewById(R.id.nextBtn);
            if (s.toString().equals("")) {
                nextBtn.setEnabled(false);
            } else {
                nextBtn.setEnabled(true);
            }
        }
    };

    /**
     * Gets the user response of current page.
     * @param type The type of the current question.
     * @return An IResponse object representing the user response.
     */
    private IResponse getResponse(mg.studio.android.survey.models.QuestionType type) {
        // TODO: Switch to using binders.
        switch (type) {
            case Single:
                SingleChoiceResponse singleResponse = new SingleChoiceResponse();
                ViewGroup opts = findViewById(R.id.opts);
                for (int i = 0; i < opts.getChildCount(); i++) {
                    RadioButton optBtn = (RadioButton)opts.getChildAt(i);
                    if (optBtn.isChecked()) {
                        singleResponse.setResponse(i);
                        return singleResponse;
                    }
                }
                return singleResponse;
            case Multiple:
                MultiChoiceResponse multiResponse = new MultiChoiceResponse();
                ViewGroup checks = findViewById(R.id.opts);
                for (int i = 0; i < checks.getChildCount(); i++) {
                    CheckBox check = (CheckBox)checks.getChildAt(i);
                    if (check.isChecked()) {
                        multiResponse.setResponse(i);
                    }
                }
                return multiResponse;
            case Text:
                TextResponse textResponse = new TextResponse();
                EditText inputBox = findViewById(R.id.inputBox);
                textResponse.setResponse(inputBox.getText().toString());
                return textResponse;
            default:
                return null;
        }
    }

    private int current;

    private SurveyModel survey;
    private ResultModel result;
}
