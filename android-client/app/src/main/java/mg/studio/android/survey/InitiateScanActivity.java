package mg.studio.android.survey;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;

public class InitiateScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void preliminaryCheckClick(View sender) {
        Button nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setEnabled(((CheckBox)sender).isChecked());
    }

    public void initScan(View sender) {
        CheckBox check = findViewById(R.id.welcome_check);
        if (!check.isChecked()) {
            return;
        }

        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setBarcodeImageEnabled(true);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setPrompt(getText(R.string.qrPrompt).toString());
        intentIntegrator.setCaptureActivity(CustomCaptureActivity.class);
        intentIntegrator.initiateScan();
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
        String id;
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            id = result.getContents();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        setProgress(true);
        JsonClient client = JsonClient.getInstance(getApplicationContext());
        client.getJson("https://svyu.azure-api.net/survey/" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Survey survey = Survey.parse(response);
                    new DataHelper(InitiateScanActivity.this).reset();
                    Intent navIntent = new Intent(InitiateScanActivity.this, MainActivity.class);
                    navIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    navIntent.putExtra(getPackageName() + ".survey", survey);
                    startActivity(navIntent);
                    InitiateScanActivity.this.finish();
                } catch (QuestionTypeNotSupportedException ex) {
                    Toast.makeText(InitiateScanActivity.this, R.string.unsupportedVersion, Toast.LENGTH_LONG).show();
                } catch (JSONException ex) {
                    Toast.makeText(InitiateScanActivity.this, R.string.unexpectedSurveyJson, Toast.LENGTH_LONG).show();
                } catch (NumberFormatException ex) {
                    Toast.makeText(InitiateScanActivity.this, R.string.unexpectedSurveyJson, Toast.LENGTH_LONG).show();
                } finally {
                    setProgress(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError || error instanceof TimeoutError) {
                    Toast.makeText(InitiateScanActivity.this, R.string.connectFail, Toast.LENGTH_SHORT).show();
                    setProgress(false);
                } else if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    Toast.makeText(InitiateScanActivity.this, R.string.surveyNotFound, Toast.LENGTH_SHORT).show();
                    setProgress(false);
                }
            }
        });
    }
}
