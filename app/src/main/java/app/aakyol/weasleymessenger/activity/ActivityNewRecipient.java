package app.aakyol.weasleymessenger.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.LocationResult;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import app.aakyol.weasleymessenger.R;
import app.aakyol.weasleymessenger.helper.DBHelper;
import app.aakyol.weasleymessenger.helper.SnackbarHelper;
import app.aakyol.weasleymessenger.resource.AppResources;

public class ActivityNewRecipient extends AppCompatActivity {

    private final Context activityContext = this;

    private LocationResult locationForRecipientMessage = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipient);

        final Button backButton = findViewById(R.id.back_button_recipient);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
                if(Objects.nonNull(locationForRecipientMessage)) {
                    SQLiteDatabase db =  new DBHelper(activityContext).getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_NAME,((EditText) findViewById(R.id.recipient_name_input)).getText().toString());
                    values.put(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_PHONE,((EditText) findViewById(R.id.phone_number_input)).getText().toString());
                    values.put(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_MESSAGE,((EditText) findViewById(R.id.message_to_be_sent_input)).getText().toString());
                    values.put(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_LATITUDE,locationForRecipientMessage.getLastLocation().getLatitude());
                    values.put(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_LONGITUDE,locationForRecipientMessage.getLastLocation().getLongitude());
                    db.insert(DBHelper.DBEntry.TABLE_NAME, null, values);
                    db.close();
                    SnackbarHelper.printLongSnackbarMessage(ActivityListRecipients.activityViewObject,
                            "Recipient \"" + ((EditText) findViewById(R.id.recipient_name_input)).getText().toString() + "\" is  saved.");
                    finish();
                }
                else {
                    SnackbarHelper.printLongSnackbarMessage(findViewById(android.R.id.content),
                            "Location is not saved. Please try again to fetch your current location.");
                }
            }
        });
    }

}
  