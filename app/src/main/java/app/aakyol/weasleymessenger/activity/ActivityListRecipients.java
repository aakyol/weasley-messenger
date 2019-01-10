package app.aakyol.weasleymessenger.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.gms.location.LocationResult;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import app.aakyol.weasleymessenger.R;
import app.aakyol.weasleymessenger.constants.AppResources;
import app.aakyol.weasleymessenger.helper.DBHelper;
import app.aakyol.weasleymessenger.helper.PermissionHelper;
import app.aakyol.weasleymessenger.service.LocationService;

public class ActivityListRecipients extends AppCompatActivity {

    private Intent locationServiceIntent;
    private View activityViewObject;
    private Activity listRecipientActivity;
    private Context listRecipientActivityContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_recipients);

        DBHelper mDbHelper = new DBHelper(this);
        String[] colmuns = {
                BaseColumns._ID,
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_NAME,
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_PHONE,
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_MESSAGE,
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_LOCATION
        };
        Cursor query = mDbHelper.getReadableDatabase().query(DBHelper.DBEntry.TABLE_NAME,colmuns,null,null,null,null,null);

        listRecipientActivity = this;
        listRecipientActivityContent = this;

        activityViewObject = findViewById(android.R.id.content);
        locationServiceIntent = new Intent(this, LocationService.class);

        requestPermissions();

        Button locationRefreshButton = (Button) findViewById(R.id.location_refresh_button);
        locationRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationResult currentLocation = AppResources.currentLocation;
                if(Objects.nonNull(currentLocation)) {
                    Snackbar.make(activityViewObject,
                            "Current latitude and longitude: " +
                                    AppResources.currentLocation.getLastLocation().getLatitude()
                                    + ", " + AppResources.currentLocation.getLastLocation().getLongitude(),
                            BaseTransientBottomBar.LENGTH_LONG).setAction("Location: ", null).show();
                }
                else {
                    Snackbar.make(activityViewObject,
                            "Location is not ready...",
                            BaseTransientBottomBar.LENGTH_LONG).setAction("Location: ", null).show();
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

    private void checkIfPermissionsGranted() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        requestPermissions();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        Snackbar.make(activityViewObject,
                                "Permission checks failed. The application will no longer" +
                                        "behave as expected. Please restart the application.",
                                BaseTransientBottomBar.LENGTH_LONG).setAction("Action", null).show();
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