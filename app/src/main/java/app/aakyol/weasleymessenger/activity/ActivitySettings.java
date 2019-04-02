package app.aakyol.weasleymessenger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import app.aakyol.weasleymessenger.AppComponent;
import app.aakyol.weasleymessenger.AppModule;
import app.aakyol.weasleymessenger.DaggerAppComponent;
import app.aakyol.weasleymessenger.R;
import app.aakyol.weasleymessenger.helper.DBHelper;
import app.aakyol.weasleymessenger.helper.LoadingSpinnerHelper;
import app.aakyol.weasleymessenger.helper.SnackbarHelper;
import app.aakyol.weasleymessenger.resource.AppResources;
import app.aakyol.weasleymessenger.service.LocationService;
import app.aakyol.weasleymessenger.validator.SettingsValidator;

public class ActivitySettings extends AppCompatActivity {

    private AppComponent appComponent;
    private SettingsValidator settingsValidator;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule())
                .build();
        settingsValidator = appComponent.getSettingsValidator();
        dbHelper = appComponent.getDBHelper();

        LoadingSpinnerHelper.setSpinnerGone();

        final Spinner accuracySpinner = (Spinner) findViewById(R.id.location_accuracy_dropdown);
        final EditText intervalText = (EditText) findViewById(R.id.location_refresh_rate_input);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.accuracy_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accuracySpinner.setAdapter(adapter);

        accuracySpinner.setSelection(adapter.getPosition(AppResources.serviceSettings.WEASLEY_SERVICE_LOCATION_ACCURACY));
        intervalText.setText(String.valueOf(AppResources.serviceSettings.WEASLEY_SERVICE_LOCATION_FASTEST_INTERVAL / (60 * 1000)));

        final Button serviceButon = (Button) findViewById(R.id.service_button);
        if(Objects.nonNull(AppResources.isLocationServiceRunning) && AppResources.isLocationServiceRunning) {
            serviceButon.setText(R.string.stop_location_service);
        }
        else {
            serviceButon.setText(R.string.start_location_service);
        }
        serviceButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!AppResources.serviceSettings.WEASLEY_SERVICE_IF_MANUALLY_STOPPED) {
                    AppResources.serviceSettings.WEASLEY_SERVICE_IF_MANUALLY_STOPPED = true;
                    dbHelper.updateServiceIsManuallyStoppedSettings(true);

                }
                if(Objects.nonNull(AppResources.isLocationServiceRunning) && AppResources.isLocationServiceRunning) {
                    stopService(AppResources.WEASLEY_SERVICE_INTENT);
                    serviceButon.setText(R.string.start_location_service);
                }
                else {
                    if(Objects.isNull(AppResources.WEASLEY_SERVICE_INTENT)) {
                        AppResources.WEASLEY_SERVICE_INTENT = new Intent(ActivityListRecipients.getContext(), LocationService.class);
                    }
                    startForegroundService(AppResources.WEASLEY_SERVICE_INTENT);
                    serviceButon.setText(R.string.stop_location_service);
                }
            }
        });

        Button saveSettingsButton = (Button) findViewById(R.id.save_settings_button);
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String inputInterval = intervalText.getText().toString();
                final String inputAccuracy = accuracySpinner.getSelectedItem().toString();
                boolean settingsChanged = false;
                if(!settingsValidator.ifAllFieldsAreEmpty(inputInterval, inputAccuracy)) {
                    if(!settingsValidator.ifIntervalIsSame(inputInterval)) {
                        long newInterval = Long.valueOf(inputInterval) * 60 * 1000;
                        AppResources.serviceSettings.WEASLEY_SERVICE_LOCATION_FASTEST_INTERVAL = newInterval;
                        dbHelper.updateServiceIntervalSettings(newInterval);
                        settingsChanged = true;
                    }
                    if(!settingsValidator.ifAccuracyIsSame(inputAccuracy)) {
                        AppResources.serviceSettings.WEASLEY_SERVICE_LOCATION_ACCURACY = inputAccuracy;
                        dbHelper.updateServiceAccuracySettings(inputAccuracy);
                        settingsChanged = true;
                    }
                    if(settingsChanged) {
                        stopService(AppResources.WEASLEY_SERVICE_INTENT);
                        startForegroundService(AppResources.WEASLEY_SERVICE_INTENT);
                        SnackbarHelper.printLongSnackbarMessage(ActivityListRecipients.getView(), "Weasley Helper has been restarted to have the changes in effect.");
                    }
                    finish();
                }
                else {
                    SnackbarHelper.printLongSnackbarMessage(view, "No setting is changed. You can leave the settings page via the back button.");
                }
            }
        });

        Button deleteAllRecipientsButton = (Button) findViewById(R.id.delete_all_contacts_button);
        deleteAllRecipientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.deleteAllRecipients();
                SnackbarHelper.printLongSnackbarMessage(view, "All contacts deleted. You may leave the settings page via the back button.");
            }
        });
    }


}
