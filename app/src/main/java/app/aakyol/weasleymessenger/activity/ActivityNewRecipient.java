package app.aakyol.weasleymessenger.activity;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.LocationResult;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import app.aakyol.weasleymessenger.AppComponent;
import app.aakyol.weasleymessenger.AppModule;
import app.aakyol.weasleymessenger.DaggerAppComponent;
import app.aakyol.weasleymessenger.R;
import app.aakyol.weasleymessenger.helper.DBHelper;
import app.aakyol.weasleymessenger.helper.SnackbarHelper;
import app.aakyol.weasleymessenger.resource.AppResources;
import app.aakyol.weasleymessenger.validator.RecipientValidator;

public class ActivityNewRecipient extends AppCompatActivity {

    private final Context activityContext = this;
    private LocationResult locationForRecipientMessage = null;

    private AppComponent appComponent;
    private DBHelper dbHelper;
    private RecipientValidator recipientValidator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipient);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule())
                .build();
        dbHelper = appComponent.getDBHelper();
        recipientValidator = appComponent.getRecipientValidator();

        final TextView locationText = findViewById(R.id.location_current);
        locationText.setText("");

        final Button fetchLocationButton = findViewById(R.id.location_button);
        fetchLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationResult location = AppResources.currentLocation;
                if(Objects.nonNull(location)) {
                    locationForRecipientMessage = location;
                    SnackbarHelper.printLongSnackbarMessage(findViewById(android.R.id.content),
                            "Fetched latitude and longitude: " +
                                    location.getLastLocation().getLatitude()
                                    + ", " + location.getLastLocation().getLongitude());
                    locationText.setText("Current location on recipient: " + locationForRecipientMessage.getLastLocation().getLatitude() + ", " + locationForRecipientMessage.getLastLocation().getLongitude());
                }
                else {
                    SnackbarHelper.printLongSnackbarMessage(findViewById(android.R.id.content),
                            "Location is not available at this time.");
                }
            }
        });

        final Button saveRecipientButton = findViewById(R.id.save_button);
        saveRecipientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String alias = ((EditText) findViewById(R.id.recipient_alias_input)).getText().toString();
                final String phoneNo = ((EditText) findViewById(R.id.phone_number_input)).getText().toString();
                final String message = ((EditText) findViewById(R.id.message_to_be_sent_input)).getText().toString();
                if(recipientValidator.ifAnyFieldIsEmpty(alias, phoneNo, message)) {
                    SnackbarHelper.printLongSnackbarMessage(findViewById(android.R.id.content),
                            "One of the fields is empty, which is not allowed.");
                }
                else if(Objects.nonNull(locationForRecipientMessage)) {
                    Location lastLocation = locationForRecipientMessage.getLastLocation();
                    dbHelper.addRecipient(alias, phoneNo, message, Double.toString(lastLocation.getLatitude()), Double.toString(lastLocation.getLongitude()));
                    SnackbarHelper.printLongSnackbarMessage(ActivityListRecipients.listRecipientActivityViewObject,
                            "Recipient \"" + ((EditText) findViewById(R.id.recipient_alias_input)).getText().toString() + "\" is  saved.");
                    finish();
                }
                else {
                    SnackbarHelper.printLongSnackbarMessage(findViewById(android.R.id.content),
                            "Location is not saved. Please try again to fetch your current location.");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
  