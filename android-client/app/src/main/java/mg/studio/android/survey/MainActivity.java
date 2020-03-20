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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        survey = (Survey) this.getIntent().getSerializableExtra(getPackageName() + ".survey");
        dataHelper = new DataHelper(this);

        current = -1;
        next(null);
    }

    /**
     * Event handler for the primary button of each page.
     * @param sender The view that triggered the handler.
     */
   public void next(View sender) {
        if (current >= 0 && current < survey.getLength()) {
            ISurveyResponse response = getResponse(survey.getQuestions()[current].getType());
            if (!response.hasResponse()) {
                return;
            }
        }

        current++;
        if (current < survey.getLength()) {
            switch (survey.getQuestions()[current].getType()) {
                case Single:
                    bindLayout((SingleResponseQuestion) survey.getQuestions()[current]);
                    break;
                case Multiple:
                    bindLayout((MultipleResponseQuestion) survey.getQuestions()[current]);
                    break;
                case Text:
                    bindLayout((TextQuestion) survey.getQuestions()[current]);
                    break;
            }
        } else {
            Intent finalizeNavIntent = new Intent(this, FinalizeActivity.class);
            finalizeNavIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            finalizeNavIntent.putExtra(getPackageName() + ".surveyId", survey.getId());
            startActivity(finalizeNavIntent);
            this.finish();
        }
    }

    /**
     * Binds the app layout to a survey question.
     * @param question The question for the layout to bind to.
     */
    private void bindLayout(SingleResponseQuestion question) {
        setContentView(R.layout.question_single);
        bindLayoutTitle(question);
        ViewGroup optGroup = findViewById(R.id.opts);
        String[] options = question.getOptions();
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
    private void bindLayout(MultipleResponseQuestion question) {
        setContentView(R.layout.question_multiple);
        bindLayoutTitle(question);
        ViewGroup optGroup = findViewById(R.id.opts);
        String[] options = question.getOptions();
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
    private void bindLayoutTitle(ISurveyQuestion question) {
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
     * @return An ISurveyResponse object representing the user response.
     */
    private ISurveyResponse getResponse(QuestionType type) {
        ISurveyResponse response;
        switch (type) {
            case Single:
                response = new SingleResponse();
                ViewGroup opts = findViewById(R.id.opts);
                for (int i = 0; i < opts.getChildCount(); i++) {
                    RadioButton optBtn = (RadioButton)opts.getChildAt(i);
                    if (optBtn.isChecked()) {
                        response.setResponse(optBtn.getText().toString());
                        dataHelper.addResponse(current, QuestionType.Single, String.valueOf(i));
                        break;
                    }
                }
                break;
            case Multiple:
                response = new MultipleResponse();
                ViewGroup checks = findViewById(R.id.opts);
                StringBuilder responseBuilder = new StringBuilder();
                for (int i = 0; i < checks.getChildCount(); i++) {
                    CheckBox check = (CheckBox)checks.getChildAt(i);
                    if (check.isChecked()) {
                        response.setResponse(check.getText().toString());
                        responseBuilder.append(i).append(" ");
                    }
                }
                dataHelper.addResponse(current, QuestionType.Multiple, responseBuilder.toString().trim());
                break;
            case Text:
                response = new SingleResponse();
                EditText inputBox = findViewById(R.id.inputBox);
                response.setResponse(inputBox.getText().toString());
                dataHelper.addResponse(current, QuestionType.Text, inputBox.getText().toString());
                break;
            default:
                return null;
        }
        return response;
    }

    private int current;

    private Survey survey;
    private DataHelper dataHelper;
}
