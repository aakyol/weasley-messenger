package app.aakyol.weasleymessenger.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
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
import app.aakyol.weasleymessenger.model.RecipientModel;

public class ActivityEditRecipient extends AppCompatActivity {

    public static final String RECIPIENT_ID = "recipientId";

    private final Context activityContext = this;

    private LocationResult locationForRecipientMessage = null;
    private RecipientModel recipient = null;
    private int recipientDBRowId = -1;

    private AppComponent appComponent;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipient);

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule())
                .build();
        dbHelper = appComponent.getDBHelper();

        recipientDBRowId = (int) getIntent().getExtras().get(RECIPIENT_ID);
        if(recipientDBRowId != -1) {
            recipient = dbHelper.getAllRecipientsById(recipientDBRowId);;
        }
        else {
            SnackbarHelper.printLongSnackbarMessage(ActivityListRecipients.listRecipientActivityViewObject,
                    "An error occured. Please contact the app author with this error code: E0005");
            finish();
        }

        final EditText nameText = findViewById(R.id.edit_recipient_name_input);
        nameText.setText(recipient.getAliasName());

        final EditText phoneText = findViewById(R.id.edit_phone_number_input);
        phoneText.setText(recipient.getPhoneNumber());

        final EditText messageText = findViewById(R.id.edit_message_to_be_sent_input);
        messageText.setText(recipient.getMessageToBeSent());

        final TextView locationText = findViewById(R.id.edit_location_current);
        locationText.setText("Current location on recipient: " + recipient.getLatitude() + ", " + recipient.getLongitude());

        final Button backButton = findViewById(R.id.edit_back_button_recipient);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Button fetchLocationButton = findViewById(R.id.edit_location_button);
        fetchLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationResult location = app.aakyol.weasleymessenger.resource.AppResources.currentLocation;
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

        final Button saveRecipientButton = findViewById(R.id.edit_save_button);
        saveRecipientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String alias = ((EditText) findViewById(R.id.edit_recipient_name_input)).getText().toString();
                final String phoneNo = ((EditText) findViewById(R.id.edit_phone_number_input)).getText().toString();
                final String message = ((EditText) findViewById(R.id.edit_message_to_be_sent_input)).getText().toString();
                String latitude;
                String longitude;
                if(ifAnyFieldIsEmpty(alias, phoneNo, message)) {
                    SnackbarHelper.printLongSnackbarMessage(findViewById(android.R.id.content),
                            "One of the fields is empty, which is not allowed.");
                }
                else {
                    if (Objects.isNull(locationForRecipientMessage)) {
                        latitude = Double.toString(recipient.getLatitude());
                        longitude = Double.toString(recipient.getLongitude());
                    } else {
                        latitude = Double.toString(locationForRecipientMessage.getLastLocation().getLatitude());
                        longitude =  Double.toString(locationForRecipientMessage.getLastLocation().getLongitude());
                    }
                    dbHelper.updateRecipient(recipientDBRowId, alias, phoneNo, message, latitude, longitude);
                    SnackbarHelper.printLongSnackbarMessage(ActivityListRecipients.listRecipientActivityViewObject,
                            "Recipient \"" + ((EditText) findViewById(R.id.edit_recipient_name_input)).getText().toString() + "\" is saved.");
                    finish();
                }
            }
        });

        final Button deleteRecipientButton = findViewById(R.id.edit_delete_button);
        deleteRecipientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String recipientName = ((EditText) findViewById(R.id.edit_recipient_name_input)).getText().toString();
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                dialog.dismiss();
                                dbHelper.deleteRecipient(recipientDBRowId);
                                SnackbarHelper.printLongSnackbarMessage(ActivityListRecipients.listRecipientActivityViewObject,
                                        "Recipient \"" + recipientName + "\" is deleted.");
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
                builder.setMessage("Are you sure to delete the recipient \"" + recipientName + "\" ?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
    }

    public Boolean ifAnyFieldIsEmpty(final String alias, final String phoneNo, final String message) {
        return (alias.isEmpty() || phoneNo.isEmpty() || message.isEmpty());
    }
}
