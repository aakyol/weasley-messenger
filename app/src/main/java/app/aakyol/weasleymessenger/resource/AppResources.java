package app.aakyol.weasleymessenger.resource;

import android.content.Intent;

import com.google.android.gms.location.LocationResult;

import java.util.List;
import java.util.Set;

import app.aakyol.weasleymessenger.model.RecipientModel;

/**
 * Created by aakyo on 31/03/2018.
 */

public class AppResources {

    public static class LogConstans {

        public static class ServiceLogConstans {

            public static final String LOG_TAG_LOCATIONSERVICE = "LocationService";

        }

        public static class AppLogConstants {

            public static final String LOG_TAG_ACTIVITYLISTRECIPIENTS = "ActivityListRecipients";

        }
    }

    public static class HelperConstants {

        public static class PermissionHelperConstant {

            public static final String LOG_TAG_PERMISSION = "Permission";

        }

    }

    public static final int RESULT_PICK_CONTACT = 100;

    public static final String WEASLEY_SERVICE_NAME = "Weasley Helper";
    public static final String WEASLEY_SERVICE_DESCRIPTION = "Weasley Helper message sending notification.";
    public static final String WEASLEY_SERVICE_NOTIFICATION_ID = "weasley_helper";
    public static final long WEASLEY_SERVICE_LOCATION_FASTEST_INTERVAL = 10 * 60 * 1000;
    public static final long WEASLEY_SERVICE_LOCATION_UPDATE_INTERVAL = WEASLEY_SERVICE_LOCATION_FASTEST_INTERVAL * 2;
    public static Intent locationServiceIntent;

    public static Boolean isLocationServiceRunning;
    public static LocationResult currentLocation;
    public static Set<String> enabledRecipientList;

    public static class currentRecipients {

        public static List<RecipientModel> currentRecipientList;

        public static boolean isRecipientWithAliasExists(final String alias) {
            for(RecipientModel recipient : currentRecipientList) {
                if(recipient.getAlias().equals(alias)) {
                    return true;
                }
            }
            return false;
        }
    }
}
