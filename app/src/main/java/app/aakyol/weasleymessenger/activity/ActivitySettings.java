package app.aakyol.weasleymessenger.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import app.aakyol.weasleymessenger.AppComponent;
import app.aakyol.weasleymessenger.AppModule;
import app.aakyol.weasleymessenger.DaggerAppComponent;
import app.aakyol.weasleymessenger.R;
import app.aakyol.weasleymessenger.helper.SnackbarHelper;
import app.aakyol.weasleymessenger.resource.AppResources;
import app.aakyol.weasleymessenger.validator.SettingsValidator;

public class ActivitySettings extends AppCompatActivity {

    private AppComponent appComponent;
    private SettingsValidator settingsValidator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule())
                .build();
        settingsValidator = appComponent.getSettingsValidator();

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
                final String inputInterval = ((EditText) findViewById(R.id.location_refresh_rate_input)).getText().toString();
                final String inputAccuracy = accuracySpinner.getSelectedItem().toString();
                if(!settingsValidator.ifAllFieldsAreEmpty(inputInterval, inputAccuracy)) {
                    if(!settingsValidator.ifIntervalIsEmpty(inputInterval)) {
                        AppResources.WEASLEY_SERVICE_LOCATION_FASTEST_INTERVAL = Long.valueOf(inputInterval) * 60 * 1000
                        ;
                    }
                    if(!settingsValidator.ifAccuracyIsSame(inputAccuracy)) {
                        AppResources.WEASLEY_SERVICE_LOCATION_ACCURACY = inputAccuracy;
                    }
                    stopService(AppResources.locationServiceIntent);
                    startForegroundService(AppResources.locationServiceIntent);
                    finish();
                }
                else {
                    SnackbarHelper.printLongSnackbarMessage(view, "No setting is changed. You can leave the settings page via the back button.");
                }
            }
        });
    }
}
