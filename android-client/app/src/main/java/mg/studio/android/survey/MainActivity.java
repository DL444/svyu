package mg.studio.android.survey;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DataHelper dataHelper;


    //The Location Cache
    private double latitude;
    private double longitude;

    //IMEI strings
    private String imei_null="";
    private String imei_meaningful="";

    //For use of getting IMEI
    private TelephonyManager tmg;

    //PERMISSION CODE
    private int PERMISSION_FINELOC_CODE = 1;
    private int PERMISSION_COARSELOC_CODE = 2;
    private int PERMISSION_INTERNET_CODE = 3;
    private int PERMISSION_READPHONESTATE_CODE = 4;

    //Location Manager for get Loc info
    protected LocationManager locationManager;
    protected Location locationinfo;
    protected LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /**add Clicklistener for button "Scan" from welcome.xml*/
        setContentView(R.layout.beginscan);

        /**
         * Location Listener for getting Location info.
         *
         */
        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude=location.getLatitude();
                longitude=location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        btnscan=findViewById(R.id.btn_scan);
        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create IntentIntegrator's object
                IntentIntegrator intentIntegrator=new IntentIntegrator(MainActivity.this);
                //10s
                intentIntegrator.setTimeout(10000);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setPrompt("Please scan a QR code");
                //设置自定义扫描activity
                intentIntegrator.setCaptureActivity(CustomCaptureActivity.class);
                //scan
                intentIntegrator.initiateScan();
            }
        });

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
            if (response.hasResponse()) {
                responses.add(response);
            } else {
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
     * Reads previously stored response data.
     * @return A JSONArray object containing all the previously stored response data.
     */
    private JSONArray readResultCache() {
        File file = new File(this.getExternalFilesDir(null), "results.json");
        try {
            FileInputStream input = new FileInputStream(file);
            byte[] encoded = new byte[(int)file.length()];
            input.read(encoded);
            input.close();
            String json = new String(encoded);
            JSONArray cache = new JSONArray(json);
            return cache;
        } catch (FileNotFoundException ex) {
            Log.i("Result Load", "Result load failed because the file was not found.", ex);
            return null;
        } catch (IOException ex) {
            Log.e("Result Load", "Result load failed because IO operation failed.", ex);
            return null;
        } catch (JSONException ex) {
            Log.e("Result Load", "Result load failed because deserialization failed.", ex);
            return null;
        }
    }

    /**
     * Appends the responses of current session to the response store.
     * @return A boolean value signaling if the operation was successful.
     */
    private boolean writeResultCache() {
        String json = jsonSerializeResults(readResultCache());
        byte[] content = json.getBytes(Charset.forName("utf-8"));
        File file = new File(this.getExternalFilesDir(null), "results.json");
        return writeFile(file, content);
    }

    /**
     * Helper method for writing encoded file content.
     * @param file A File object describing the file to write to.
     * @param content A byte array containing encoded data to write.
     * @return A boolean value signaling if the operation was successful.
     */
    private boolean writeFile(File file, byte[] content) {
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream output = new FileOutputStream(file);
            output.write(content);
            output.flush();
            output.close();
            return true;
        } catch (IOException ex) {
            Log.e("Internal Save", "Result save failed because IO operation failed.", ex);
            return false;
        }
    }

    /**
     * Serialize he responses of current session and append the result to previously stored data.
     * @param existingRoot The root JSONArray for existing data.
     * @return Serialized JSON string.
     */
    private String jsonSerializeResults(JSONArray existingRoot) {
        try {
            if (existingRoot == null) {
                existingRoot = new JSONArray();
            }
            JSONObject jObject = new JSONObject();
            jObject.put("id", survey.getId());
            JSONArray responses = new JSONArray();
            for (ISurveyResponse r : this.responses) {
                responses.put(r.getResponse());
            }
            jObject.put("responses", responses);
            existingRoot.put(jObject);
            return existingRoot.toString();
        } catch (JSONException ex) {
            return existingRoot.toString();
        }
    }

    /**
     * A helper method for navigate to summary activity.
     */
    private void navigateToSummary() {
        Intent navIntent = new Intent(this, SummaryActivity.class);
        navIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        navIntent.putExtra(getPackageName() + ".survey", survey);
        navIntent.putExtra(getPackageName() + ".responses", responses);
        startActivity(navIntent);
        this.finish();
    }

    private void checkIMEI() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(MainActivity.this, "You have already granted READ_PHONE_STATE permission!",Toast.LENGTH_SHORT).show();
            try{
                tmg = (TelephonyManager) getSystemService(MainActivity.this.TELEPHONY_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    imei_meaningful=tmg.getImei();
                }else{
                    imei_meaningful=imei_null;
                }
            }catch (SecurityException e){
                imei_meaningful=imei_null;
            }
        }else {
            requestPhoneStatusPermission();
        }
    }

    public void checkLocation() {
        if (    ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.INTERNET)==PackageManager.PERMISSION_GRANTED){

            try{
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            requestFineLocPermission();
            requestCoarseLocPermission();
            requestInternetPermission();
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
                ContentValues values;
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
    private boolean finalized = false;

    private ArrayList<ISurveyResponse> responses = new ArrayList<>();
    private Survey survey;

    /**
     * These are permission requesting services.
     * Incl. Phone Status, Fine/Coarse and Internet Request
     * As well as Override onRequestPermissionsResult
     */
    public void requestPhoneStatusPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)){

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to let us differentiate devices")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},PERMISSION_READPHONESTATE_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();


        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},PERMISSION_READPHONESTATE_CODE);
        }
    }

    public void requestFineLocPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to let us know the survey position and differentiate these positions, no privacy info is gathered")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_FINELOC_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();


        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_FINELOC_CODE);
        }
    }

    public void requestCoarseLocPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)){

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to let us know the survey position and differentiate these positions, no privacy info is gathered")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_COARSELOC_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();


        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_COARSELOC_CODE);
        }
    }

    public void requestInternetPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.INTERNET)){

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to let us know the survey position and differentiate these positions, no privacy info is gathered")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.INTERNET},PERMISSION_INTERNET_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();


        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},PERMISSION_INTERNET_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==PERMISSION_READPHONESTATE_CODE){
            if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"READ PHONE STATEGranted",Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(this,"READ PHONE STATE Not Granted",Toast.LENGTH_SHORT).show();
            }
        }

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"FINE LOCATION Granted",Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this,"FINE LOCATION Not Granted",Toast.LENGTH_SHORT).show();

                }
                return;
            }
            case 2:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"COARSE LOCATION Granted",Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this,"COARSE LOCATION Not Granted",Toast.LENGTH_SHORT).show();

                }
                return;

            }
            case 3:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"INTERNET LOCATION Granted",Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this,"INTERNET LOCATION Not Granted",Toast.LENGTH_SHORT).show();

                }
                return;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }
    /**PERMISSION SERVICE ENDS HERE---*/

}
