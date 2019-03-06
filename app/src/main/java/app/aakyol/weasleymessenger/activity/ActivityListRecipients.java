package app.aakyol.weasleymessenger.activity;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import app.aakyol.weasleymessenger.AppComponent;
import app.aakyol.weasleymessenger.AppModule;
import app.aakyol.weasleymessenger.DaggerAppComponent;
import app.aakyol.weasleymessenger.R;
import app.aakyol.weasleymessenger.helper.DBHelper;
import app.aakyol.weasleymessenger.helper.LoadingSpinnerHelper;
import app.aakyol.weasleymessenger.helper.PermissionHelper;
import app.aakyol.weasleymessenger.helper.SnackbarHelper;
import app.aakyol.weasleymessenger.model.RecipientModel;
import app.aakyol.weasleymessenger.resource.AppResources;
import app.aakyol.weasleymessenger.service.LocationService;

import static app.aakyol.weasleymessenger.resource.AppResources.LogConstans.AppLogConstants.LOG_TAG_ACTIVITYLISTRECIPIENTS;

public class ActivityListRecipients extends AppCompatActivity {

    private Activity listRecipientActivity;
    private static Context listRecipientActivityContext;
    private ActionBar actionBar;

    public static View listRecipientActivityViewObject;
    private ListView listView;

    private AppComponent appComponent;
    private DBHelper dbHelper;

    public static Context getContext() {
        return listRecipientActivityContext;
    }

    public static View getView() {
        return listRecipientActivityViewObject;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_recipients);

        listRecipientActivity = this;
        listRecipientActivityContext = this;

        listView = (ListView) findViewById(R.id.recipient_list);

        listRecipientActivityViewObject = findViewById(android.R.id.content);

        actionBar = getActionBar();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule())
                .build();
        dbHelper = appComponent.getDBHelper();

        if(AppResources.isLocationServiceManuallySwitched) {
            SnackbarHelper.printLongSnackbarMessage(
                    listRecipientActivityViewObject,
                    "The location service is manually started/stopped by you. " +
                             "If it is stopped, you should go to the settings and start it manually " +
                             "for the application to function properly.");
        }
        else {
            if (Objects.isNull(AppResources.isLocationServiceRunning) || !AppResources.isLocationServiceRunning) {
                AppResources.locationServiceIntent = new Intent(this, LocationService.class);
                requestPermissions();
            } else {
                Log.d(LOG_TAG_ACTIVITYLISTRECIPIENTS, "Location service is already running.");
            }
        }

        Button addRecipientButton = (Button) findViewById(R.id.add_recipient_button);
        addRecipientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadingSpinnerHelper.setSpinnerVisible();
                Intent newRecipientIntent = new Intent(listRecipientActivityContext, ActivityNewRecipient.class);
                listRecipientActivity.startActivity(newRecipientIntent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LoadingSpinnerHelper.setSpinnerVisible();
                Intent editRecipientIntent = new Intent(listRecipientActivityContext, ActivityEditRecipient.class);
                editRecipientIntent.putExtra(ActivityEditRecipient.RECIPIENT_ID, ((RecipientModel) parent.getAdapter().getItem(position)).getDbID());
                listRecipientActivity.startActivity(editRecipientIntent);
            }
        });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.FOREGROUND_SERVICE
        }, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        checkIfPermissionsGranted();
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<RecipientModel> recipients = dbHelper.getAllRecipients();
        AppResources.currentRecipients.currentRecipientList = recipients;
        ArrayAdapter adapter = new ArrayAdapter<>(this,
                R.layout.activity_list_recipient_layout,
                R.id.listview_alias,
                recipients);

        listView.setAdapter(adapter);

        LoadingSpinnerHelper.setLoadingSpinner(this);

        listView.post(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < listView.getChildCount(); i++) {
                    final int index = i;
                    ((Switch) listView.getChildAt(index).findViewById(R.id.listview_switch)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String enabledRecipientAlias = dbHelper
                                    .getAllRecipientsById(((RecipientModel) listView.getAdapter().getItem(index)).getDbID()).getAlias();
                            if(((Switch) view).isChecked()){
                                AppResources.enabledRecipientList.add(enabledRecipientAlias);
                            }
                            else {
                                AppResources.enabledRecipientList.remove(enabledRecipientAlias);
                            }
                        }
                    });
                }
            }
        });
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
                        SnackbarHelper.printLongSnackbarMessage(listRecipientActivityViewObject,
                                "Permission checks failed. The application will no longer" +
                                        "behave as expected. Please restart the application.");
                }
            }
        };

        if(!PermissionHelper.checkPermissions(listRecipientActivity, listRecipientActivityContext)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Permissions are revoked. The application needs these permissions to" +
                    "work properly. Would you like to allow the permissions?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
        else {
            startForegroundService(AppResources.locationServiceIntent);
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
        if (id == R.id.action_settings_button) {
            LoadingSpinnerHelper.setSpinnerVisible();
            Intent newSettingsIntent = new Intent(listRecipientActivityContext, ActivitySettings.class);
            listRecipientActivity.startActivity(newSettingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}