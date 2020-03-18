package mg.studio.android.survey;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;

public class FinalizeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        surveyId = String.valueOf(getIntent().getIntExtra(getPackageName() + ".surveyId", -1));
        setContentView(R.layout.finish_survey);
    }

    public void upload(View sender) {
        // TODO: Obtain ancillary info.
        double latitude = 0.0;
        double longitude = 0.0;
        String imei = null;

        final DataHelper dataHelper = new DataHelper(this);
        SurveyReport report = new SurveyReport(surveyId, dataHelper.getResponses(), System.currentTimeMillis(),
                latitude, longitude, imei);
        String json;
        try {
            json = report.getJson();
        } catch (JSONException ex) {
            Log.wtf("Upload.JSON", "Json serialization failed!");
            Toast.makeText(this, R.string.unexpectedResponseError, Toast.LENGTH_SHORT).show();
            return;
        }

        JsonClient.getInstance(getApplicationContext()).postJson("https://svyu.azure-api.net/response", json,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO: Bring up screen lock.
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NetworkError || error instanceof TimeoutError) {
                            Toast.makeText(FinalizeActivity.this, R.string.connectFail, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FinalizeActivity.this, R.string.unexpectedResponseError, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String surveyId;
}
