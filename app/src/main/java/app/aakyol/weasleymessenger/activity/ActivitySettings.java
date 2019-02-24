package app.aakyol.weasleymessenger.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.location.LocationRequest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import app.aakyol.weasleymessenger.R;
import app.aakyol.weasleymessenger.resource.AppResources;

public class ActivitySettings extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Spinner accuracySpinner = (Spinner) findViewById(R.id.location_accuracy_dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.accuracy_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accuracySpinner.setAdapter(adapter);

        Button serviceButon = (Button) findViewById(R.id.service_button);
        if(AppResources.isLocationServiceRunning) {
            serviceButon.setText(R.string.stop_location_service);
        }
        else {
            serviceButon.setText(R.string.start_location_service);
        }
        serviceButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AppResources.isLocationServiceRunning) {
                    stopService(AppResources.locationServiceIntent);
                }
                else {
                    startForegroundService(AppResources.locationServiceIntent);
                }
            }
        });

        Button saveSettingsButton = (Button) findViewById(R.id.save_settings_button);
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final long inputInterval = Long.valueOf(((EditText) findViewById(R.id.location_refresh_rate_input)).getText().toString());
                final String inputAccuracy = accuracySpinner.getSelectedItem().toString();
                //TODO: Validation on inputs and check if they changed, change and restart in that case, ignore otherwise
                System.out.println();
            }
        });
    }
}
