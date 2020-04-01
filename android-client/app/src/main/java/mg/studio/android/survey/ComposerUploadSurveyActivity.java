package mg.studio.android.survey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.EnumMap;
import java.util.Map;

import javax.inject.Inject;

import mg.studio.android.survey.clients.ClientErrorType;
import mg.studio.android.survey.clients.ClientFactory;
import mg.studio.android.survey.clients.IDraftClient;
import mg.studio.android.survey.clients.ISurveyClient;
import mg.studio.android.survey.clients.ISurveyClientCallback;
import mg.studio.android.survey.models.SurveyModel;

public class ComposerUploadSurveyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composer_upload_survey);
        ((SurveyApplication)getApplication()).getComponent().inject(this);

        survey = (SurveyModel) getIntent().getSerializableExtra(getPackageName() + ".survey");
        final ViewSwitcher switcher = findViewById(R.id.surveyuploadRootSwitcher);
        ISurveyClient client = clientFactory.getSurveyClient();

        client.postSurvey(survey, new ISurveyClientCallback() {
            @Override
            public void onComplete(SurveyModel survey) {
                draftClient.clearSurveyDraft(null);
                switcher.showNext();
                ImageView qrView = findViewById(R.id.qrView);
                String id = survey.getId();
                MultiFormatWriter writer = new MultiFormatWriter();
                try {
                    Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
                    hints.put(EncodeHintType.MARGIN, 1);
                    BitMatrix matrix = writer.encode(id, BarcodeFormat.QR_CODE, 400, 400, hints);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Bitmap image = encoder.createBitmap(matrix);
                    qrView.setImageBitmap(image);
                } catch (WriterException ex) {
                    Log.wtf("QR encode", ex.getMessage());
                }
                TextView idText = findViewById(R.id.surveyIdText);
                idText.setText(getString(R.string.surveyId, id));
            }

            @Override
            public void onError(ClientErrorType errorType, Exception exception) {
                switch (errorType) {
                    case IO:
                        Toast.makeText(ComposerUploadSurveyActivity.this, R.string.connectFail, Toast.LENGTH_LONG).show();
                        finish(null);
                        break;
                    default:
                        Toast.makeText(ComposerUploadSurveyActivity.this, R.string.unexpectedSurveyJson, Toast.LENGTH_LONG).show();
                        finish(null);
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent navIntent = new Intent(this, InitiateScanActivity.class);
        navIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(navIntent);
        this.finish();
    }

    public void finish(View sender) {
        onBackPressed();
    }

    private SurveyModel survey;
    @Inject ClientFactory clientFactory;
    @Inject IDraftClient draftClient;
}
