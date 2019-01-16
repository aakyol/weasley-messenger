package app.aakyol.weasleymessenger.resource;

import com.google.android.gms.location.LocationResult;

import java.util.List;

import app.aakyol.weasleymessenger.model.RecipientModel;

/**
 * Created by aakyo on 31/03/2018.
 */

public class AppResources {

    public class LogConstans {

        public class ServiceLogConstans {

            public static final String LOG_TAG_LOCATIONSERVICE = "LocationService";

        }
    }

    public class HelperConstants {

        public class PermissionHelperConstant {

            public static final String LOG_TAG_PERMISSION = "Permission";

        }

    }

    public static LocationResult currentLocation;
    public static List<RecipientModel> currentRecipientList;
}
