package mg.studio.android.survey;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(getPackageName() + ".pref", MODE_PRIVATE);
        policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        deviceAdminComponentName = new ComponentName(this, DeviceAdminListener.class);

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

    private void updateSwitchState() {
        Switch lockCheck = findViewById(R.id.lockDeviceCheck);
        lockCheck.setOnClickListener(switchClickListener);
        if (prefs.getBoolean("lockDevice", false)) {
            lockCheck.setChecked(true);
        } else {
            lockCheck.setChecked(false);
        }

        TextView disabledTip = findViewById(R.id.deviceAdminTip);
        Button enableBtn = findViewById(R.id.enableDeviceAdminBtn);
        if (policyManager.isAdminActive(deviceAdminComponentName)) {
            lockCheck.setEnabled(true);
            disabledTip.setVisibility(View.GONE);
            enableBtn.setVisibility(View.GONE);
        } else {
            lockCheck.setEnabled(false);
            disabledTip.setVisibility(View.VISIBLE);
            enableBtn.setVisibility(View.VISIBLE);
        }
    }

    private Switch.OnClickListener switchClickListener = new Switch.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.lockDeviceCheck) {
                Switch lockCheck = (Switch)v;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("lockDevice", lockCheck.isChecked());
                editor.apply();
            }
        }
    };

    private SharedPreferences prefs;
    private DevicePolicyManager policyManager;
    private ComponentName deviceAdminComponentName;
    
    private static final int DEVICE_ADMIN_REQUEST_CODE = 42;
}
