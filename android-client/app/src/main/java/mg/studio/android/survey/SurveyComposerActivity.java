package mg.studio.android.survey;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import javax.inject.Inject;

import mg.studio.android.survey.models.QuestionType;
import mg.studio.android.survey.viewmodels.IQuestionViewModel;
import mg.studio.android.survey.views.ComposerListFragment;
import mg.studio.android.survey.views.ComposerQuestionViewBase;
import mg.studio.android.survey.views.ComposerQuestionViewSelector;
import mg.studio.android.survey.views.IQuestionOperationCompleteListener;
import mg.studio.android.survey.views.IQuestionOperationRequestListener;

public class SurveyComposerActivity extends AppCompatActivity
        implements IQuestionOperationRequestListener, IQuestionOperationCompleteListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_composer);
        ((SurveyApplication)getApplication()).getComponent().inject(this);

        listView = ComposerListFragment.newInstance();
        fragmentMgr = getSupportFragmentManager();
        fragmentMgr.beginTransaction()
                .add(R.id.composerFrame, listView)
                .commit();
    }

    @Override
    public void onAddQuestionRequested(QuestionType type) {
        ComposerQuestionViewBase fragment = selector.getView(type);
        fragmentMgr.beginTransaction()
                .setCustomAnimations(R.anim.slide_left_in_anim, R.anim.slide_left_out_anim,
                        R.anim.slide_right_in_anim, R.anim.slide_right_out_anim)
                .replace(R.id.composerFrame, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onUpdateQuestionRequested(IQuestionViewModel model, int index) {
        ComposerQuestionViewBase fragment = selector.getView(model, index);
        fragmentMgr.beginTransaction()
                .setCustomAnimations(R.anim.slide_left_in_anim, R.anim.slide_left_out_anim,
                        R.anim.slide_right_in_anim, R.anim.slide_right_out_anim)
                .replace(R.id.composerFrame, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onQuestionOperationComplete(IQuestionViewModel question, boolean isUpdate, int index) {
        if (isUpdate) {
            listView.updateQuestion(index);
        } else {
            listView.addQuestion(question);
        }
        fragmentMgr.popBackStack();
    }

    private ComposerListFragment listView;
    private FragmentManager fragmentMgr;
    @Inject ComposerQuestionViewSelector selector;
}
