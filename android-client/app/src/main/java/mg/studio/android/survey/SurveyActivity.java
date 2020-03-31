package mg.studio.android.survey;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import java.util.ArrayList;

import javax.inject.Inject;

import mg.studio.android.survey.clients.ClientErrorType;
import mg.studio.android.survey.clients.ISurveyProgressClient;
import mg.studio.android.survey.clients.ISurveyProgressClientCallback;
import mg.studio.android.survey.models.IQuestion;
import mg.studio.android.survey.models.IResponse;
import mg.studio.android.survey.models.ResultModel;
import mg.studio.android.survey.models.SurveyModel;
import mg.studio.android.survey.views.IResponseValidityChangedListener;
import mg.studio.android.survey.views.QuestionViewBase;
import mg.studio.android.survey.views.QuestionViewSelector;

public class SurveyActivity extends AppCompatActivity implements IResponseValidityChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((SurveyApplication)getApplication()).getComponent().inject(this);
        fragmentManager = getSupportFragmentManager();

        survey = (SurveyModel) this.getIntent().getSerializableExtra(getPackageName() + ".survey");
        result = (ResultModel) this.getIntent().getSerializableExtra(getPackageName() + ".result");
        if (result == null) {
            result = new ResultModel();
        }
        result.setId(survey.getId());

        setContentView(R.layout.question_frame);

        current = result.responses().size() - 1;
        next(null);
    }

    @Override
    protected void onStop() {
        if (!discardResult) {
            progressClient.saveProgress(survey, result, new ISurveyProgressClientCallback() {
                @Override
                public void onComplete(SurveyModel survey, ResultModel result) { }
                @Override
                public void onError(ClientErrorType errorType, Exception exception) { }
            });
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.survey_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_cancel_survey) {
            if (result.responses().size() > survey.getLength() / 2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SurveyActivity.this);
                builder.setTitle(R.string.cancelSurvey)
                        .setMessage(R.string.cancelSurveyConfirm)
                        .setPositiveButton(R.string.cancelSurveyOk,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        cancelSurvey();
                                    }
                                })
                        .setNegativeButton(R.string.cancelSurveyCancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dangerText));
            } else {
                cancelSurvey();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Event handler for the primary button of each page.
     * @param sender The view that triggered the handler.
     */
    public void next(View sender) {
        if (current >= result.responses().size() && current < survey.getLength()) {
            IResponse response = currentView.getResponse();
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
            ProgressBar progressBar = findViewById(R.id.surveyProgressBar);
            int progress = (int)Math.round((double)current / survey.getLength() * 100);
            ObjectAnimator progressAnimation = ObjectAnimator.ofInt(progressBar, "progress", progress).setDuration(300);
            progressAnimation.setInterpolator(new FastOutSlowInInterpolator());
            progressAnimation.start();
            TextView progressText = findViewById(R.id.surveyProgressText);
            progressText.setText(getString(R.string.surveyProgressText, current + 1, survey.getLength()));

            IQuestion question = survey.questions().get(current);

            currentView = viewSelector.getBoundView(question);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_left_in_anim, R.anim.slide_left_out_anim);
            transaction.replace(R.id.questionFrame, currentView);
            transaction.commit();
            onResponseValidityChanged(false);
        } else {
            Intent finalizeNavIntent = new Intent(this, FinalizeActivity.class);
            finalizeNavIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            finalizeNavIntent.putExtra(getPackageName() + ".result", result);
            startActivity(finalizeNavIntent);
            this.finish();
        }
    }

    @Override
    public void onResponseValidityChanged(boolean valid) {
        Button nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setEnabled(valid);
    }

    private void cancelSurvey() {
        progressClient.clearProgress(null);
        discardResult = true;
        Intent navIntent = new Intent(SurveyActivity.this, InitiateScanActivity.class);
        navIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(navIntent);
        SurveyActivity.this.finish();
    }

    private int current;
    private SurveyModel survey;
    private ResultModel result;
    private boolean discardResult;

    private QuestionViewBase currentView;
    private FragmentManager fragmentManager;
    @Inject QuestionViewSelector viewSelector;
    @Inject ISurveyProgressClient progressClient;
}
