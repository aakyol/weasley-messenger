package app.aakyol.weasleymessenger.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import app.aakyol.weasleymessenger.R;
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

        listRecipientActivity = this;
        listRecipientActivityContent = this;

        activityViewObject = findViewById(android.R.id.content);
        locationServiceIntent = new Intent(this, LocationService.class);

        requestPermissions();

        Button locationRefreshButton = (Button) findViewById(R.id.location_refresh_button);
        locationRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS
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
                        /*Snackbar.make(activityViewObject,
                                "Permission checks failed. The application will no longer" +
                                        "behave as expected. Please restart the application.",
                                Snackbar.LENGTH_LONG).setAction("Action", null).show();*/
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