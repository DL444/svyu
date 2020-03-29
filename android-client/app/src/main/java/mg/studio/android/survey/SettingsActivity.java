package mg.studio.android.survey;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import mg.studio.android.survey.clients.ClientErrorType;
import mg.studio.android.survey.clients.IResultSynchronizeCallback;
import mg.studio.android.survey.clients.SynchronizeClient;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(getPackageName() + ".pref", MODE_PRIVATE);
        policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        deviceAdminComponentName = new ComponentName(this, DeviceAdminListener.class);
        ((SurveyApplication)getApplication()).getComponent().inject(this);
        setContentView(R.layout.activity_settings);
        updateSwitchState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DEVICE_ADMIN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                updateSwitchState();
            }
        }
    }

    public void enabledAdmin(View sender) {
        Intent deviceAdminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        deviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminComponentName);
        deviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.deviceAdminReason));
        startActivityForResult(deviceAdminIntent, DEVICE_ADMIN_REQUEST_CODE);
    }

    public void syncData(View sender) {
        setSyncProgress(true);
        syncClient.synchronizeResults(new IResultSynchronizeCallback() {
            @Override
            public void onComplete(int count) {
                setSyncProgress(false);
                if (count == 0) {
                    Toast.makeText(SettingsActivity.this, R.string.noResultSynced, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, getString(R.string.syncSuccess, count), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ClientErrorType errorType, Exception exception) {
                setSyncProgress(false);
                if (errorType == ClientErrorType.IO) {
                    Toast.makeText(SettingsActivity.this, R.string.connectFail, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, R.string.unexpectedSyncError, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateSwitchState() {
        Switch check = findViewById(R.id.lockDeviceCheck);
        check.setOnCheckedChangeListener(switchChangedListener);
        if (prefs.getBoolean(lockDeviceKey, false)) {
            check.setChecked(true);
        } else {
            check.setChecked(false);
        }

        TextView tip = findViewById(R.id.deviceAdminTip);
        Button btn = findViewById(R.id.enableDeviceAdminBtn);
        if (policyManager.isAdminActive(deviceAdminComponentName)) {
            check.setEnabled(true);
            tip.setVisibility(View.GONE);
            btn.setVisibility(View.GONE);
        } else {
            check.setEnabled(false);
            tip.setVisibility(View.VISIBLE);
            btn.setVisibility(View.VISIBLE);
        }

        check = findViewById(R.id.workOfflineCheck);
        check.setOnCheckedChangeListener(switchChangedListener);
        tip = findViewById(R.id.workOfflineHint);
        btn = findViewById(R.id.syncBtn);
        if (prefs.getBoolean(workOfflineKey, false)) {
            check.setChecked(true);
            tip.setText(R.string.workOfflineHintOffline);
            btn.setVisibility(View.GONE);
        } else {
            check.setChecked(false);
            tip.setText(R.string.workOfflineHintOnline);
            btn.setVisibility(View.VISIBLE);
        }
    }

    private void setSyncProgress(boolean active) {
        Button btn = findViewById(R.id.syncBtn);
        btn.setEnabled(!active);
        ProgressBar progressBar = findViewById(R.id.syncProgress);
        progressBar.setVisibility(active ? View.VISIBLE : View.GONE);
    }

    private Switch.OnCheckedChangeListener switchChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.lockDeviceCheck) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(lockDeviceKey, isChecked);
                editor.apply();
            } else if (buttonView.getId() == R.id.workOfflineCheck) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(workOfflineKey, isChecked);
                editor.apply();
                updateSwitchState();
            }
        }
    };

    @Inject SynchronizeClient syncClient;

    private SharedPreferences prefs;
    private DevicePolicyManager policyManager;
    private ComponentName deviceAdminComponentName;
    
    private static final int DEVICE_ADMIN_REQUEST_CODE = 42;
    private static final String lockDeviceKey = "lockDevice";
    private static final String workOfflineKey = "workOffline";
}
