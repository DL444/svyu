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
import android.os.StrictMode;
import android.telephony.TelephonyManager;
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

import com.google.zxing.client.android.Intents;
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

    /**
     * when we click the button from beginscan.xml
     *it can scan QR code
     */
    private Button btnscan;
    private String str_postJson="";
    //The ID of a questionnaire
    private String questionnaireID;


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

    //database
    DataHelper dataHelper;
    private SQLiteDatabase db;

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

        /*
         *database set
         */
        dataHelper=new DataHelper(this);

    }


    // get scanning's result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "No QR code", Toast.LENGTH_SHORT).show();
            } else {

                questionnaireID=result.getContents();
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                Json jsonObject=new Json();
                json=jsonObject.getJson("https://svyu.azure-api.net/survey/"+questionnaireID);
                try {
                    survey = Survey.parse(json);
                    setContentView(R.layout.welcome);
                    current = -1;
                } catch (QuestionTypeNotSupportedException ex) {
                    Log.wtf("Initialize", "Unexpected question type: " + ex.getType());
                } catch (JSONException ex) {
                    Log.wtf("Initialize", "JSON format exception - General.");
                } catch (NumberFormatException ex) {
                    Log.wtf("Initialize", "JSON format exception - Invalid number format.");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        //输出问卷ID
        //System.out.println(questionnaireID);
    }

    /**
     * Event handler for the primary button of each page.
     * @param sender The view that triggered the handler.
     */
    public void next(View sender) {
        if (finalized) {
            return;
        }

        if (current == -1) {
            CheckBox prerequisiteCheck = findViewById(R.id.welcome_check);
            if (!prerequisiteCheck.isChecked()) {
                return;
            }
        } else if (current >= 0 && current < survey.getLength()) {
            ISurveyResponse response = getResponse(survey.getQuestions()[current].getType());
            if (response.hasResponse()) {
                responses.add(response);
            } else {
                return;
            }
        } else {
            finalized = true;
            writeResultCache();
            navigateToSummary();
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
            setContentView(R.layout.finish_survey);
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
        /***
         * Post json to server
         */
        try {
            checkIMEI();
            checkLocation();

            JSONObject jObject = new JSONObject();
            jObject.put("id", survey.getId());
            jObject.put("len", survey.getLength());
            jObject.put("longitude", longitude);
            jObject.put("latitude", latitude);
            jObject.put("time",System.currentTimeMillis());
            jObject.put("imei",imei_meaningful);
            JSONArray answers = new JSONArray();
            Toast.makeText(this,str_postJson, Toast.LENGTH_LONG).show();
            String[] postJson = str_postJson.split("/");
            for(String s : postJson) {
                String []pair=s.split(":");
                JSONObject detail_answer=new JSONObject();
                detail_answer.put("type",pair[0]);
                if(pair[1].indexOf(",")==-1) detail_answer.put("answer",pair[1]);
                else {
                    String []detail=pair[1].split(",");
                    String sum=null;
                    JSONArray sequence = new JSONArray();
                    for(String d: detail){
                        sequence.put(d);
                    }
                    detail_answer.put("answer",sequence);
                }
                answers.put(detail_answer);
            }
            jObject.put("answers",answers);
            Toast.makeText(this,jObject.toString(), Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            Json jsonObject=new Json();
            jsonObject.postJson("https://svyu.azure-api.net/response",jObject);
        } catch (JSONException ex) {
        }
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

                        db=dataHelper.getWritableDatabase();
                        values=new ContentValues();
                        values.put("type","single");
                        values.put("answer",i);
                        if(current==survey.getLength()-1)str_postJson=str_postJson+"single"+":"+i;
                        str_postJson=str_postJson+"single"+":"+i+"/";
                        db.insert("q_answer",null,values);
                        db.close();
                        break;
                    }
                }
                break;
            case Multiple:
                response = new MultipleResponse();
                ViewGroup checks = findViewById(R.id.opts);
                String  sequence="";
                int checksum=0;
                int count=0;
                for (int i = 0; i < checks.getChildCount(); i++) {
                    CheckBox check = (CheckBox)checks.getChildAt(i);
                    if(check.isChecked())checksum++;
                }
                for (int i = 0; i < checks.getChildCount(); i++) {
                    CheckBox check = (CheckBox)checks.getChildAt(i);
                    if (check.isChecked()) {
                        count++;
                        response.setResponse(check.getText().toString());
                        db=dataHelper.getWritableDatabase();
                        values=new ContentValues();
                        values.put("type","multiple");
                        values.put("answer",i);
                        if(count==checksum)sequence=sequence+i;
                        else sequence=sequence+i+",";
                        db.insert("q_answer",null,values);
                        db.close();
                    }
                }
                if(current==survey.getLength()-1)str_postJson=str_postJson+"multiple"+":"+sequence;
                str_postJson=str_postJson+"multiple"+":"+sequence+"/";
                break;
            case Text:
                response = new SingleResponse();
                EditText inputBox = findViewById(R.id.inputBox);
                response.setResponse(inputBox.getText().toString());
                db=dataHelper.getWritableDatabase();
                values=new ContentValues();
                values.put("type","text");
                values.put("answer",inputBox.getText().toString());
                if(current==survey.getLength()-1)str_postJson=str_postJson+"text"+":"+inputBox.getText().toString();
                else str_postJson=str_postJson+"text"+":"+inputBox.getText().toString()+"/";
                db.insert("q_answer",null,values);
                db.close();

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
    private String json;

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
