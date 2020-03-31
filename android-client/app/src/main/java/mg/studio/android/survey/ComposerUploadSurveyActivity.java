package mg.studio.android.survey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import mg.studio.android.survey.models.SurveyModel;

public class ComposerUploadSurveyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composer_upload_survey);

        survey = (SurveyModel) getIntent().getSerializableExtra(getPackageName() + ".survey");

        // TODO: Clear survey draft.
    }

    @Override
    public void onBackPressed() {
        Intent navIntent = new Intent(this, InitiateScanActivity.class);
        navIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(navIntent);
        this.finish();
    }

    private SurveyModel survey;
}
