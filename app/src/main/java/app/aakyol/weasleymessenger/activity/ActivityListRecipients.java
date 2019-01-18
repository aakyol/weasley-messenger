package app.aakyol.weasleymessenger.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import app.aakyol.weasleymessenger.R;
import app.aakyol.weasleymessenger.helper.DBHelper;
import app.aakyol.weasleymessenger.helper.PermissionHelper;
import app.aakyol.weasleymessenger.helper.SnackbarHelper;
import app.aakyol.weasleymessenger.model.RecipientModel;
import app.aakyol.weasleymessenger.resource.AppResources;
import app.aakyol.weasleymessenger.service.LocationService;

public class ActivityListRecipients extends AppCompatActivity {

    private Intent locationServiceIntent;
    public static View activityViewObject;
    private Activity listRecipientActivity;
    private Context listRecipientActivityContent;

    private ListView listView;
    private SQLiteDatabase recipientDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_recipients);

        listRecipientActivity = this;
        listRecipientActivityContent = this;

        listView = (ListView) findViewById(R.id.recipient_list);
        recipientDatabase = new DBHelper(this).getReadableDatabase();

        activityViewObject = findViewById(android.R.id.content);
        locationServiceIntent = new Intent(this, LocationService.class);

        requestPermissions();

        Button addRecipientButton = (Button) findViewById(R.id.add_recipient_button);
        addRecipientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newRecipientIntent = new Intent(listRecipientActivityContent, ActivityNewRecipient.class);
                listRecipientActivity.startActivity(newRecipientIntent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editRecipientIntent = new Intent(listRecipientActivityContent, ActivityEditRecipient.class);
                editRecipientIntent.putExtra(ActivityEditRecipient.RECIPIENT_ID, ((RecipientModel) parent.getAdapter().getItem(position)).getDbID());
                listRecipientActivity.startActivity(editRecipientIntent);
            }
        });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS
        }, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        checkIfPermissionsGranted();
    }

    @Override
    protected void onResume() {
        super.onResume();

        String[] columns = {
                DBHelper.DBEntry._ID,
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_NAME,
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_PHONE,
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_MESSAGE,
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_LATITUDE,
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_LONGITUDE
        };

        Cursor cursor = recipientDatabase.query(
                DBHelper.DBEntry.TABLE_NAME,
                columns,
                "1",
                null,
                null,
                null,
                null,
                null
        );

        List<RecipientModel> recipients = new ArrayList<>();
        while(cursor.moveToNext()) {
            RecipientModel recipient = new RecipientModel();
            recipient.setDbID(cursor.getInt(0));
            recipient.setAliasName(cursor.getString(1));
            recipient.setPhoneNumber(cursor.getString(2));
            recipient.setMessageToBeSent(cursor.getString(3));
            recipient.setLatitude(cursor.getDouble(4));
            recipient.setLongitude(cursor.getDouble(5));
            recipients.add(recipient);
        }
        cursor.close();

        AppResources.currentRecipientList = recipients;

        ArrayAdapter adapter = new ArrayAdapter<RecipientModel>(this,
                R.layout.activity_list_recipient_layout, recipients);


        listView.setAdapter(adapter);
    }

    private void checkIfPermissionsGranted() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        requestPermissions();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        SnackbarHelper.printLongSnackbarMessage(activityViewObject,
                                "Permission checks failed. The application will no longer" +
                                        "behave as expected. Please restart the application.");
                }
            }
        };

        if(!PermissionHelper.checkPermissions(listRecipientActivity, listRecipientActivityContent)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Permissions are revoked. The application needs these permissions to" +
                    "work properly. Would you like to allow the permissions?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
        else {
            startForegroundService(locationServiceIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_list_recipients, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}