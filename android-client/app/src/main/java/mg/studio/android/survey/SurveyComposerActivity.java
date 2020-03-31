package mg.studio.android.survey;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import javax.inject.Inject;

import mg.studio.android.survey.clients.ClientErrorType;
import mg.studio.android.survey.clients.IDraftClient;
import mg.studio.android.survey.clients.ISurveyClientCallback;
import mg.studio.android.survey.models.QuestionType;
import mg.studio.android.survey.models.SurveyModel;
import mg.studio.android.survey.viewmodels.IQuestionViewModel;
import mg.studio.android.survey.viewmodels.ViewModelConverter;
import mg.studio.android.survey.views.ComposerListFragment;
import mg.studio.android.survey.views.ComposerQuestionViewBase;
import mg.studio.android.survey.views.ComposerQuestionViewSelector;
import mg.studio.android.survey.views.IQuestionOperationCompleteListener;
import mg.studio.android.survey.views.IQuestionOperationRequestListener;

public class SurveyComposerActivity extends AppCompatActivity
        implements IQuestionOperationRequestListener, IQuestionOperationCompleteListener {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_composer);
        ((SurveyApplication)getApplication()).getComponent().inject(this);
        fragmentMgr = getSupportFragmentManager();
        fragmentMgr.addOnBackStackChangedListener(backStackChangedListener);

        draftClient.getSurveyDraft(new ISurveyClientCallback() {
            @Override
            public void onComplete(SurveyModel survey) {
                if (survey == null) {
                    listView = ComposerListFragment.newInstance();
                    navigateToQuestionList();
                } else {
                    listView = ComposerListFragment.newInstance(converter.getSurveyViewModel(survey));
                    navigateToQuestionList();
                }
            }
            @Override
            public void onError(ClientErrorType errorType, Exception exception) {
                listView = ComposerListFragment.newInstance();
                navigateToQuestionList();
            }
        });
    }

    @Override
    protected void onStop() {
        fragmentMgr.removeOnBackStackChangedListener(backStackChangedListener);
        fragmentMgr.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        SurveyModel model = converter.getSurveyModel(listView.getQuestions());
        draftClient.saveSurveyDraft(model, new ISurveyClientCallback() {
            @Override
            public void onComplete(SurveyModel survey) { }
            @Override
            public void onError(ClientErrorType errorType, Exception exception) { }
        });
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.composer_menu, menu);
        uploadAction = menu.findItem(R.id.action_upload_survey);
        return true;
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

    private void navigateToQuestionList() {
        fragmentMgr.beginTransaction()
                .replace(R.id.composerFrame, listView)
                .commit();
    }

    private FragmentManager.OnBackStackChangedListener backStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            uploadAction.setVisible(fragmentMgr.getBackStackEntryCount() == 0);
        }
    };

    private ComposerListFragment listView;
    private FragmentManager fragmentMgr;
    private MenuItem uploadAction;
    @Inject ComposerQuestionViewSelector selector;
    @Inject IDraftClient draftClient;
    @Inject ViewModelConverter converter;
}
