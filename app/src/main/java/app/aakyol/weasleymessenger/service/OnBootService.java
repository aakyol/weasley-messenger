package app.aakyol.weasleymessenger.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Objects;

import app.aakyol.weasleymessenger.AppComponent;
import app.aakyol.weasleymessenger.AppModule;
import app.aakyol.weasleymessenger.DaggerAppComponent;
import app.aakyol.weasleymessenger.helper.DBHelper;
import app.aakyol.weasleymessenger.resource.AppResources;

public class OnBootService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        AppComponent appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(context))
                .build();
        DBHelper dbHelper = appComponent.getDBHelper();

        if (!dbHelper.getServiceSettings()) {
            dbHelper.addServiceSettings(
                    AppResources.serviceSettings.WEASLEY_SERVICE_LOCATION_FASTEST_INTERVAL,
                    AppResources.serviceSettings.WEASLEY_SERVICE_LOCATION_ACCURACY,
                    AppResources.serviceSettings.WEASLEY_SERVICE_IF_MANUALLY_STOPPED,
                    AppResources.serviceSettings.WEASLEY_SERVICE_ON_BOOT_STARTUP
            );
        }

        if(!AppResources.serviceSettings.WEASLEY_SERVICE_IF_MANUALLY_STOPPED && AppResources.serviceSettings.WEASLEY_SERVICE_ON_BOOT_STARTUP) {
            if (Objects.isNull(AppResources.WEASLEY_SERVICE_INTENT)) {
                AppResources.WEASLEY_SERVICE_INTENT = new Intent(context, LocationService.class);
            }
            context.startForegroundService(AppResources.WEASLEY_SERVICE_INTENT);
        }
    }
}
