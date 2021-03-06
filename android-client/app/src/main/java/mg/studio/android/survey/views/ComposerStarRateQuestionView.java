package mg.studio.android.survey.views;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import mg.studio.android.survey.R;
import mg.studio.android.survey.viewmodels.IQuestionViewModel;
import mg.studio.android.survey.viewmodels.StarRateQuestionViewModel;

public final class ComposerStarRateQuestionView extends ComposerQuestionViewBase {

    public static ComposerStarRateQuestionView createInstance(IQuestionViewModel question, boolean isUpdate, int index) {
        ComposerStarRateQuestionView fragment = new ComposerStarRateQuestionView();
        Bundle args = new Bundle();
        args.putSerializable("question", question);
        args.putBoolean("isUpdate", isUpdate);
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (StarRateQuestionViewModel) getArguments().getSerializable("question");
            isUpdate = getArguments().getBoolean("isUpdate");
            index = getArguments().getInt("index");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.composer_star_rate_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText title = getView().findViewById(R.id.composerQuestionInput);
        title.addTextChangedListener(textWatcher);

        FloatingActionButton confirmBtn = getView().findViewById(R.id.composerConfirmBtn);
        confirmBtn.setOnClickListener(confirmListener);
        boolean hasQuestion = question.hasQuestion();
        confirmBtn.setEnabled(hasQuestion);
        confirmBtn.setTranslationY(hasQuestion ? 0.0f : 500.0f);
        if (hasQuestion) {
            title.setText(question.getQuestion());
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().isEmpty()) {
                setConfirmButtonEnabled(false);
            } else {
                setConfirmButtonEnabled(true);
            }
        }
    };

    private Button.OnClickListener confirmListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText title = ComposerStarRateQuestionView.this.getView().findViewById(R.id.composerQuestionInput);
            question.setQuestion(title.getText().toString());
            ComposerStarRateQuestionView.this.getQuestionOperationCompleteCallback().onQuestionOperationComplete(question, isUpdate, index);
        }
    };

    private void setConfirmButtonEnabled(boolean enabled) {
        FloatingActionButton btn = getView().findViewById(R.id.composerConfirmBtn);
        if (btn.isEnabled() == enabled) {
            return;
        }
        if (enabled) {
            btn.setEnabled(true);
        } else {
            btn.setEnabled(false);
        }
        float value = enabled ? 0.0f : 500.0f;
        ObjectAnimator animation = ObjectAnimator.ofFloat(btn, "translationY", value).setDuration(200);
        if (enabled) {
            animation.setInterpolator(new OvershootInterpolator());
        } else {
            animation.setInterpolator(new AnticipateInterpolator());
        }
        animation.start();
    }

    private boolean isUpdate;
    private int index;
    private StarRateQuestionViewModel question;
}
