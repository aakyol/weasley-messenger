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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.location.LocationResult;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import app.aakyol.weasleymessenger.R;
import app.aakyol.weasleymessenger.constants.AppResources;
import app.aakyol.weasleymessenger.helper.DBHelper;
import app.aakyol.weasleymessenger.helper.PermissionHelper;
import app.aakyol.weasleymessenger.helper.SnackbarHelper;
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

        Button locationRefreshButton = (Button) findViewById(R.id.location_refresh_button);
        locationRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationResult currentLocation = AppResources.currentLocation;
                if(Objects.nonNull(currentLocation)) {
                    SnackbarHelper.printLongSnackbarMessage(activityViewObject,"Current latitude and longitude: " +
                            AppResources.currentLocation.getLastLocation().getLatitude()
                            + ", " + AppResources.currentLocation.getLastLocation().getLongitude());
                }
                else {
                    SnackbarHelper.printLongSnackbarMessage(activityViewObject,
                            "Location is not ready...");
                }
            }
        });

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

            }
        });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
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
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_NAME
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

        List<String> itemNames = new ArrayList<>();
        while(cursor.moveToNext()) {
            String itemName = cursor.getString(
                    cursor.getColumnIndexOrThrow(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_NAME));
            itemNames.add(itemName);
        }
        cursor.close();

       ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_list_recipient_layout, itemNames);


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
            startService(locationServiceIntent);
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