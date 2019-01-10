package app.aakyol.weasleymessenger.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.LocationResult;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import app.aakyol.weasleymessenger.R;
import app.aakyol.weasleymessenger.constants.AppResources;
import app.aakyol.weasleymessenger.helper.DBHelper;
import app.aakyol.weasleymessenger.helper.SnackbarHelper;

public class ActivityNewRecipient extends AppCompatActivity {

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

        final Button fetchLocationButton = findViewById(R.id.location_button);
        fetchLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationResult location = AppResources.currentLocation;
                if(Objects.nonNull(location)) {
                    locationForRecipientMessage = location;
                    fetchLocationButton.setEnabled(false);
                    SnackbarHelper.printLongSnackbarMessage(findViewById(android.R.id.content),
                            "Fetched latitude and longitude: " +
                                    location.getLastLocation().getLatitude()
                                    + ", " + location.getLastLocation().getLongitude());
                }
                else {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Location is not available at this time.",
                            BaseTransientBottomBar.LENGTH_LONG).setAction("Location: ", null).show();
                }
            }
        });

        final Button saveRecipientButton = findViewById(R.id.save_button);
        saveRecipientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.nonNull(locationForRecipientMessage)) {
                    ContentValues values = new ContentValues();
                    values.put(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_NAME,findViewById(R.id.full));
                }
                else {
                    SnackbarHelper.printLongSnackbarMessage(findViewById(android.R.id.content),
                            "Location is not saved. Please try again to fetch your current location.");
                }
            }
        });
    }

}
  