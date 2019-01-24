package app.aakyol.weasleymessenger.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import app.aakyol.weasleymessenger.model.ContactModel;
import app.aakyol.weasleymessenger.model.RecipientModel;
import app.aakyol.weasleymessenger.resource.AppResources;
import app.aakyol.weasleymessenger.validator.RecipientValidator;

public class ActivityEditRecipient extends AppCompatActivity {

    public static final String RECIPIENT_ID = "recipientId";

    private final Context activityContext = this;

    private LocationResult locationForRecipientMessage = null;
    private RecipientModel recipient = null;
    private int recipientDBRowId = -1;

    private AppComponent appComponent;
    private DBHelper dbHelper;
    private RecipientValidator recipientValidator;

    private ContactModel selectedContact = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipient);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule())
                .build();
        dbHelper = appComponent.getDBHelper();
        recipientValidator = appComponent.getRecipientValidator();

        recipientDBRowId = (int) getIntent().getExtras().get(RECIPIENT_ID);
        if(recipientDBRowId != -1) {
            recipient = dbHelper.getAllRecipientsById(recipientDBRowId);;
        }
        else {
            SnackbarHelper.printLongSnackbarMessage(ActivityListRecipients.listRecipientActivityViewObject,
                    "An error occured. Please contact the app author with this error code: E0005");
            finish();
        }

        final EditText aliasText = findViewById(R.id.edit_recipient_alias_input);
        aliasText.setText(recipient.getAlias());

        final TextView phoneText = findViewById(R.id.edit_phone_number_current);
        phoneText.setText("Current selected contact: " + recipient.getName());

        final EditText messageText = findViewById(R.id.edit_message_to_be_sent_input);
        messageText.setText(recipient.getMessageToBeSent());

        final EditText distanceText = findViewById(R.id.edit_location_distance_input);
        distanceText.setText(String.format(recipient.getDistance().toString()));

        final TextView locationText = findViewById(R.id.edit_location_current);
        locationText.setText("Current location on recipient: " + recipient.getLatitude() + ", " + recipient.getLongitude());

        final Button selectContactButton = findViewById(R.id.edit_phone_number_button);
        selectContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContactList(v);
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
                final String alias = ((EditText) findViewById(R.id.edit_recipient_alias_input)).getText().toString();
                final String name = selectedContact.getName();
                final String phoneNo = selectedContact.getPhoneNo();
                final String message = ((EditText) findViewById(R.id.edit_message_to_be_sent_input)).getText().toString();
                final String distance = ((EditText) findViewById(R.id.edit_location_distance_input)).getText().toString();
                String latitude;
                String longitude;
                if(recipientValidator.ifAnyFieldIsEmpty(alias, phoneNo, message, distance)) {
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
                    dbHelper.updateRecipient(recipientDBRowId, alias, name, phoneNo, message, distance, latitude, longitude);
                    SnackbarHelper.printLongSnackbarMessage(ActivityListRecipients.listRecipientActivityViewObject,
                            "Recipient \"" + ((EditText) findViewById(R.id.edit_recipient_alias_input)).getText().toString() + "\" is saved.");
                    finish();
                }
            }
        });

        final Button deleteRecipientButton = findViewById(R.id.edit_delete_button);
        deleteRecipientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String recipientAlias = ((EditText) findViewById(R.id.edit_recipient_alias_input)).getText().toString();
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                dialog.dismiss();
                                dbHelper.deleteRecipient(recipientDBRowId);
                                AppResources.sentList.remove(recipientAlias);
                                SnackbarHelper.printLongSnackbarMessage(ActivityListRecipients.listRecipientActivityViewObject,
                                        "Recipient \"" + recipientAlias + "\" is deleted.");
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
                builder.setMessage("Are you sure to delete the recipient \"" + recipientAlias + "\" ?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
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

    public void getContactList(View v)
    {
        Intent contactsIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactsIntent, AppResources.RESULT_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        getSelectedContact(data);
    }

    private void getSelectedContact(final Intent data) {
        if(Objects.nonNull(data)) {
            Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
            cursor.moveToFirst();
            int phoneNoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String phoneNo = cursor.getString(phoneNoIndex);
            String name = cursor.getString(nameIndex);
            final TextView phoneNumberText = findViewById(R.id.edit_phone_number_current);
            phoneNumberText.setText("Current selected contact: " + name);
            selectedContact = new ContactModel(name, phoneNo);
        }
    }
}
