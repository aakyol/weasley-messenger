package app.aakyol.weasleymessenger.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import app.aakyol.weasleymessenger.constants.AppConstants;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Created by aakyo on 18/03/2018.
 */

public class LocationService extends Service {

    private LocationRequest locationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;
    private long FASTEST_INTERVAL = 2 * 1000;

    private Handler locationHandler = new Handler();
    private Context locationServiceContext;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationServiceContext = this;
        startLocationUpdates();
        return super.onStartCommand(intent, flags, startId);
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
                SettingsClient settingsClient = LocationServices.getSettingsClient(locationServiceContext);
                settingsClient.checkLocationSettings(locationSettingsRequest);

                // new Google API SDK v11 uses getFusedLocationProviderClient(this)
                while (ActivityCompat.checkSelfPermission(locationServiceContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(locationServiceContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
                getFusedLocationProviderClient(locationServiceContext).requestLocationUpdates(locationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                Log.d(AppConstants.LogConstans.ServiceLogConstans.LOG_TAG_LOCATIONSERVICE,"Location fetched: " + locationResult.getLastLocation());
                            }
                        },
                        Looper.myLooper());
            }
        };
        locationHandler.postDelayed(locationRunner, 10000);
    }
}
