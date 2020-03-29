package mg.studio.android.survey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import javax.inject.Inject;

import mg.studio.android.survey.clients.ClientErrorType;
import mg.studio.android.survey.clients.ClientFactory;
import mg.studio.android.survey.clients.IClient;
import mg.studio.android.survey.clients.ISurveyClientCallback;
import mg.studio.android.survey.models.SurveyModel;

public class InitiateScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((SurveyApplication)getApplication()).getComponent().inject(this);
        prefs= getSharedPreferences(getPackageName() + ".pref", MODE_PRIVATE);
        setContentView(R.layout.init_scan);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initScan(View sender) {
        boolean workOffline = prefs.getBoolean(workOfflineKey, false);
        if (workOffline) {
            getSurveyAndProceed(null);
        } else {
            IntentIntegrator intentIntegrator = new IntentIntegrator(this);
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.setBarcodeImageEnabled(true);
            intentIntegrator.setBeepEnabled(true);
            intentIntegrator.setPrompt(getText(R.string.qrPrompt).toString());
            intentIntegrator.setCaptureActivity(CustomCaptureActivity.class);
            intentIntegrator.initiateScan();
        }
    }

    private void setProgress(boolean active) {
        ProgressBar progressBar = findViewById(R.id.receiveProgress);
        Button nextBtn = findViewById(R.id.nextBtn);
        if (active) {
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            nextBtn.setEnabled(true);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            String id = result.getContents();
            getSurveyAndProceed(id);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getSurveyAndProceed(String id) {
        setProgress(true);
        IClient client = clientFactory.getClient();
        client.getSurvey(id, new ISurveyClientCallback() {
            @Override
            public void onComplete(SurveyModel survey) {
                Intent navIntent = new Intent(InitiateScanActivity.this, MainActivity.class);
                navIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                navIntent.putExtra(getPackageName() + ".survey", survey);
                startActivity(navIntent);
                setProgress(false);
                InitiateScanActivity.this.finish();
            }

            @Override
            public void onError(ClientErrorType errorType, Exception exception) {
                setProgress(false);
                switch (errorType) {
                    case IO:
                    case CacheMiss:
                        Toast.makeText(InitiateScanActivity.this, R.string.connectFail, Toast.LENGTH_SHORT).show();
                        break;
                    case NotFound:
                        Toast.makeText(InitiateScanActivity.this, R.string.surveyNotFound, Toast.LENGTH_SHORT).show();
                        break;
                    case Versioning:
                        Toast.makeText(InitiateScanActivity.this, R.string.unsupportedVersion, Toast.LENGTH_LONG).show();
                        break;
                    case Serialization:
                    default:
                        Toast.makeText(InitiateScanActivity.this, R.string.unexpectedSurveyJson, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    @Inject ClientFactory clientFactory;
    private SharedPreferences prefs;
    private static final String workOfflineKey = "workOffline";
}
