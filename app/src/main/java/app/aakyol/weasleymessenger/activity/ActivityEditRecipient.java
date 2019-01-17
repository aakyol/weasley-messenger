package app.aakyol.weasleymessenger.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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
import app.aakyol.weasleymessenger.model.RecipientModel;

public class ActivityEditRecipient extends AppCompatActivity {

    public static final String RECIPIENT_ID = "recipientId";

    private final Context activityContext = this;

    private LocationResult locationForRecipientMessage = null;
    private RecipientModel recipient = null;
    private int recipientDBRowId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipient);

        recipientDBRowId = (int) getIntent().getExtras().get(RECIPIENT_ID);
        if(recipientDBRowId != -1) {
            SQLiteDatabase db = new DBHelper(this).getReadableDatabase();

            String[] columns = {
                    DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_NAME,
                    DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_PHONE,
                    DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_MESSAGE,
                    DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_LATITUDE,
                    DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_LONGITUDE
            };

            String selection = DBHelper.DBEntry._ID + " = ?";
            String[] selectionArgs = { String.valueOf(recipientDBRowId) };

            Cursor cursor = db.query(
                    DBHelper.DBEntry.TABLE_NAME,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            recipient = new RecipientModel();
            while(cursor.moveToNext()) {
                recipient.setAliasName(cursor.getString(0));
                recipient.setPhoneNumber(cursor.getString(1));
                recipient.setMessageToBeSent(cursor.getString(2));
                recipient.setLatitude(cursor.getDouble(3));
                recipient.setLongitude(cursor.getDouble(4));
            }
            cursor.close();
        }
        else {
            SnackbarHelper.printLongSnackbarMessage(ActivityListRecipients.activityViewObject,
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
                SQLiteDatabase db =  new DBHelper(activityContext).getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_NAME,((EditText) findViewById(R.id.edit_recipient_name_input)).getText().toString());
                values.put(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_PHONE,((EditText) findViewById(R.id.edit_phone_number_input)).getText().toString());
                values.put(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_MESSAGE,((EditText) findViewById(R.id.edit_message_to_be_sent_input)).getText().toString());
                if(Objects.isNull(locationForRecipientMessage)) {
                    values.put(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_LATITUDE,recipient.getLatitude());
                    values.put(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_LONGITUDE,recipient.getLongitude());
                }
                else {
                    values.put(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_LATITUDE, locationForRecipientMessage.getLastLocation().getLatitude());
                    values.put(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_LONGITUDE, locationForRecipientMessage.getLastLocation().getLongitude());
                }
                db.update(DBHelper.DBEntry.TABLE_NAME, values, DBHelper.DBEntry._ID + " = ?", new String[] {String.valueOf(recipientDBRowId)});
                db.close();
                SnackbarHelper.printLongSnackbarMessage(ActivityListRecipients.activityViewObject,
                        "Recipient \"" + ((EditText) findViewById(R.id.edit_recipient_name_input)).getText().toString() + "\" is saved.");
                finish();
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
                                SQLiteDatabase db =  new DBHelper(activityContext).getWritableDatabase();
                                ContentValues values = new ContentValues();
                                db.delete(DBHelper.DBEntry.TABLE_NAME, DBHelper.DBEntry._ID + " = ?", new String[] {String.valueOf(recipientDBRowId)});
                                db.close();
                                SnackbarHelper.printLongSnackbarMessage(ActivityListRecipients.activityViewObject,
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
}
