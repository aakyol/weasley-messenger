package app.aakyol.weasleymessenger.service;

import android.Manifest;
import android.app.Notification;
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

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import app.aakyol.weasleymessenger.R;
import app.aakyol.weasleymessenger.helper.MessageHelper;
import app.aakyol.weasleymessenger.model.RecipientModel;
import app.aakyol.weasleymessenger.resource.AppResources;

import static app.aakyol.weasleymessenger.resource.AppResources.LogConstans.ServiceLogConstans.LOG_TAG_LOCATIONSERVICE;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Created by aakyo on 18/03/2018.
 */

public class LocationService extends Service {

    private LocationRequest locationRequest;

    private long UPDATE_INTERVAL = 30 * 1000;
    private long FASTEST_INTERVAL = 60 * 1000;

    private Set<String> sentList = new HashSet<>();
    private Handler locationHandler = new Handler();
    private Context locationServiceContext;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new Notification();
        locationServiceContext = this;
        startLocationUpdates();
        startForeground(0, notification);
        return START_STICKY;
    }

    private void startLocationUpdates() {

        Runnable locationRunner = new Runnable() {
            @Override
            public void run() {
                // Create the location request to start receiving updates
                locationRequest = new LocationRequest();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
                                    final List<RecipientModel> recipients = AppResources.currentRecipientList;
                                    final double currentLatitude = locationResult.getLastLocation().getLatitude();
                                    final double currentLongitude = locationResult.getLastLocation().getLongitude();
                                    for (RecipientModel recipient : recipients) {
                                        final double recipientLatitude = recipient.getLatitude();
                                        final double recipientLongitude = recipient.getLongitude();
                                        float[] distance = new float[1];
                                        Location.distanceBetween(recipientLatitude, recipientLongitude, currentLatitude, currentLongitude, distance);
                                        if (!sentList.contains(recipient.getAliasName()) && distance[0] < 20.0) {
                                            Log.d(LOG_TAG_LOCATIONSERVICE, "Matched location. Sending the message to recipient \"" + recipient.getAliasName() + "\". " +
                                                    "Distance to location for accuracy: " + distance[0]);
                                            MessageHelper.sendSMSMessage(recipient.getPhoneNumber(), recipient.getMessageToBeSent());
                                            sentList.add(recipient.getAliasName());
                                        } else if (distance[0] >= 20.0 && sentList.contains(recipient.getAliasName())) {
                                            sentList.remove(recipient.getAliasName());
                                        }
                                    }
                                }
                            },
                            Looper.myLooper());
                }
            }
        };
        locationHandler.postDelayed(locationRunner, 10000);
    }
}