package mg.studio.android.survey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        surveyId = String.valueOf(getIntent().getIntExtra(getPackageName() + ".surveyId", -1));
        setContentView(R.layout.finish_survey);
        getIMEI();
        getLocation();
    }

    public void upload(View sender) {
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

    private final LocationListener locationListener = new LocationListener() {
        private int count = 0;

        @Override
        public void onLocationChanged(Location location) {
            if (count < 5) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                count++;
            } else {
                locationManager.removeUpdates(this);
                count = 0;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) { }
        @Override
        public void onProviderDisabled(String provider) { }
    };

    private void getIMEI() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            try{
                TelephonyManager tmg = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    imei = tmg.getImei();
                }else{
                    imei = null;
                }
            }catch (SecurityException e){
                imei = null;
            }
        }else {
            requestPhoneStatusPermission();
            imei = null;
        }
    }

    public void getLocation() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            try {
                // Use network provider for better indoor reception.
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            requestLocationPermissions();
            requestInternetPermission();
        }
    }

    public void requestPhoneStatusPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permRequired)
                    .setMessage(R.string.phoneIdPermReason)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(FinalizeActivity.this, new String[] { Manifest.permission.READ_PHONE_STATE }, PERMISSION_READPHONESTATE_CODE);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            imei = null;
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.READ_PHONE_STATE }, PERMISSION_READPHONESTATE_CODE);
        }
    }

    public void requestLocationPermissions(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permRequired)
                    .setMessage(R.string.locationPermReason)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(FinalizeActivity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, PERMISSION_LOCATION_CODE);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            latitude = 0.0;
                            longitude = 0.0;
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, PERMISSION_LOCATION_CODE);
        }
    }

    public void requestInternetPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.INTERNET)){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permRequired)
                    .setMessage(R.string.internetPermReason)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(FinalizeActivity.this, new String[] { Manifest.permission.INTERNET }, PERMISSION_INTERNET_CODE);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(FinalizeActivity.this, R.string.internetPermRequired, Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.INTERNET }, PERMISSION_INTERNET_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_LOCATION_CODE:
                if (grantResults.length == 2
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    latitude = 0.0;
                    longitude = 0.0;
                }
                return;
            case PERMISSION_INTERNET_CODE:
                if (grantResults.length == 0
                        || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this,R.string.internetPermRequired, Toast.LENGTH_LONG).show();
                }
                return;
            case 4:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getIMEI();
                } else {
                    imei = null;
                }
                return;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    private String surveyId;
    private String imei;
    private double latitude;
    private double longitude;

    private LocationManager locationManager;

    //PERMISSION CODE
    private static final int PERMISSION_LOCATION_CODE = 1;
    private static final int PERMISSION_INTERNET_CODE = 3;
    private static final int PERMISSION_READPHONESTATE_CODE = 4;
}
