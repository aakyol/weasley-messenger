package app.aakyol.weasleymessenger.activity;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
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
import app.aakyol.weasleymessenger.helper.LoadingSpinnerHelper;
import app.aakyol.weasleymessenger.helper.SnackbarHelper;
import app.aakyol.weasleymessenger.model.ContactModel;
import app.aakyol.weasleymessenger.resource.AppResources;
import app.aakyol.weasleymessenger.validator.RecipientValidator;

public class ActivityNewRecipient extends AppCompatActivity {

    private LocationResult locationForRecipientMessage = null;
    private AppComponent appComponent;
    private DBHelper dbHelper;
    private RecipientValidator recipientValidator;

    private ContactModel selectedContact = null;

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
        locationText.setText("Please fetch a location for your message:");

        final TextView phoneNumberText = findViewById(R.id.phone_number_current);
        phoneNumberText.setText("Select a contact as a recipient:");

        final Button selectContactButton = findViewById(R.id.phone_number_button);
        selectContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getContactList(v);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

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
                final String name = Objects.nonNull(selectedContact) ? selectedContact.getName() : "";
                final String phoneNo = Objects.nonNull(selectedContact) ? selectedContact.getPhoneNo() : "";
                final String message = ((EditText) findViewById(R.id.message_to_be_sent_input)).getText().toString();
                final String distance = ((EditText) findViewById(R.id.location_distance_input)).getText().toString();
                if(recipientValidator.ifAnyFieldIsEmpty(alias, phoneNo, message, distance)) {
                    SnackbarHelper.printLongSnackbarMessage(findViewById(android.R.id.content),
                            "One of the fields is empty, which is not allowed.");
                }
                else if(AppResources.currentRecipients.isRecipientWithAliasExists(alias)) {
                    SnackbarHelper.printLongSnackbarMessage(findViewById(android.R.id.content),
                            "This alias exists. Alias must be unique.");
                }
                else if(Objects.nonNull(locationForRecipientMessage)) {
                    Location lastLocation = locationForRecipientMessage.getLastLocation();
                    LoadingSpinnerHelper.setSpinnerVisible();
                    dbHelper.addRecipient(alias, name, phoneNo, message, distance, Double.toString(lastLocation.getLatitude()), Double.toString(lastLocation.getLongitude()));
                    SnackbarHelper.printLongSnackbarMessage(ActivityListRecipients.listRecipientActivityViewObject,
                            "Recipient \"" + ((EditText) findViewById(R.id.recipient_alias_input)).getText().toString() + "\" is  saved.");
                    LoadingSpinnerHelper.setSpinnerGone();
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
    protected void onResume() {
        super.onResume();

        LoadingSpinnerHelper.setSpinnerGone();
        LoadingSpinnerHelper.setLoadingSpinner(this);
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
            final TextView phoneNumberText = findViewById(R.id.phone_number_current);
            phoneNumberText.setText("Current selected contact: " + name);
            selectedContact = new ContactModel(name, phoneNo);
        }
    }
}
  