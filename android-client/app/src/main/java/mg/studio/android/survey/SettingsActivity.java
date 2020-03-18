package mg.studio.android.survey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(getPackageName() + ".pref", MODE_PRIVATE);
        setContentView(R.layout.activity_settings);
        Switch lockCheck = findViewById(R.id.lockDeviceCheck);
        lockCheck.setOnClickListener(switchClickListener);
        if (prefs.getBoolean("lockDevice", false)) {
            lockCheck.setChecked(true);
        } else {
            lockCheck.setChecked(false);
        }
    }

    Switch.OnClickListener switchClickListener = new Switch.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.lockDeviceCheck) {
                Switch lockCheck = (Switch)v;
                if (lockCheck.isChecked()) {
                    // TODO: Request device admin permission.
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("lockDevice", lockCheck.isChecked());
                editor.apply();
            }
        }
    };

    private SharedPreferences prefs;
}
