package app.aakyol.weasleymessenger.service;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import app.aakyol.weasleymessenger.helper.MessageHelper;
import app.aakyol.weasleymessenger.helper.NotificationHelper;
import app.aakyol.weasleymessenger.model.RecipientModel;
import app.aakyol.weasleymessenger.resource.AppResources;

import static app.aakyol.weasleymessenger.resource.AppResources.LogConstans.ServiceLogConstans.LOG_TAG_LOCATIONSERVICE;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Created by aakyo on 18/03/2018.
 */

public class LocationService extends Service {

    private LocationRequest locationRequest;

    private long UPDATE_INTERVAL;
    private long FASTEST_INTERVAL;

    private Handler locationHandler = new Handler();
    private Context locationServiceContext;
    private OutputStreamWriter outputStreamWriter;
    private PendingIntent pendingIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationServiceContext = this;
        NotificationHelper.createNotificationChannel(locationServiceContext);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        UPDATE_INTERVAL = AppResources.serviceSettings.WEASLEY_SERVICE_LOCATION_FASTEST_INTERVAL * 2;
        FASTEST_INTERVAL = AppResources.serviceSettings.WEASLEY_SERVICE_LOCATION_FASTEST_INTERVAL;

        startForeground(
                101,
                NotificationHelper.buildNotification("Weasley Helper is working, busy as a lacewing fly.",
                        locationServiceContext, pendingIntent
                ).build()
        );

        if (Objects.isNull(AppResources.enabledRecipientList)) {
            AppResources.enabledRecipientList = new HashSet<>();
        }

        startLocationUpdates();
        AppResources.isLocationServiceRunning = true;

        return START_STICKY;
    }

    private void startLocationUpdates() {

        Runnable locationRunner = new Runnable() {
            @Override
            public void run() {
                // Create the location request to start receiving updates
                locationRequest = new LocationRequest();
                if (AppResources.serviceSettings.WEASLEY_SERVICE_LOCATION_ACCURACY.equals("HIGH")) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                } else if (AppResources.serviceSettings.WEASLEY_SERVICE_LOCATION_ACCURACY.equals("MEDIUM")) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                }
                locationRequest.setInterval(UPDATE_INTERVAL);
                locationRequest.setFastestInterval(FASTEST_INTERVAL);

                // Create LocationSettingsRequest object using location request
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                builder.addLocationRequest(locationRequest);
                LocationSettingsRequest locationSettingsRequest = builder.build();

                // Check whether location settings are satisfied
                final SettingsClient settingsClient = LocationServices.getSettingsClient(locationServiceContext);
                settingsClient.checkLocationSettings(locationSettingsRequest);

                // new Google API SDK v11 uses getFusedLocationProviderClient(this)
                if (ActivityCompat.checkSelfPermission(locationServiceContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(locationServiceContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getFusedLocationProviderClient(locationServiceContext).requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    Log.d(LOG_TAG_LOCATIONSERVICE, "Location fetched: " + locationResult.getLastLocation());
                                    AppResources.currentLocation = locationResult;
                                    final List<RecipientModel> recipients = AppResources.currentRecipients.currentRecipientList;
                                    final double currentLatitude = locationResult.getLastLocation().getLatitude();
                                    final double currentLongitude = locationResult.getLastLocation().getLongitude();
                                    for (RecipientModel recipient : recipients) {
                                        final double recipientLatitude = recipient.getLatitude();
                                        final double recipientLongitude = recipient.getLongitude();
                                        float[] distance = new float[1];
                                        Location.distanceBetween(recipientLatitude, recipientLongitude, currentLatitude, currentLongitude, distance);
                                        if (AppResources.enabledRecipientList.contains(recipient.getAlias()) && distance[0] < recipient.getDistance()) {
                                            Log.d(LOG_TAG_LOCATIONSERVICE, "Matched location. Sending the message to recipient \"" + recipient.getName() + "\". " +
                                                    "Distance to location for accuracy: " + distance[0]);
                                            try {
                                                outputStreamWriter = new OutputStreamWriter(locationServiceContext.openFileOutput("weasley_service_logs.txt", Context.MODE_APPEND));
                                                outputStreamWriter.write("Matched location. Sending the message to recipient \"" + recipient.getAlias() + "\". " +
                                                        "Distance to location for accuracy: " + distance[0] + "m. \n");
                                                outputStreamWriter.close();
                                            } catch (IOException e) {
                                                Log.d(AppResources.LogConstans.ServiceLogConstans.LOG_TAG_LOCATIONSERVICE, "File writer failed to write.");
                                            }
                                            MessageHelper.sendSMSMessage(recipient.getPhoneNumber(), recipient.getMessageToBeSent());
                                            NotificationHelper.sendNotificationToDevice("The message for alias " + recipient.getAlias() + " is sent.", locationServiceContext, pendingIntent);
                                            AppResources.enabledRecipientList.remove(recipient.getAlias());
                                        } else if (distance[0] >= recipient.getDistance() && !AppResources.enabledRecipientList.contains(recipient.getAlias())) {
                                            AppResources.enabledRecipientList.add(recipient.getAlias());
                                        }
                                    }
                                }
                            },
                            Looper.myLooper());
                }
            }
        };
        locationHandler.postDelayed(locationRunner, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppResources.isLocationServiceRunning = false;
    }
}