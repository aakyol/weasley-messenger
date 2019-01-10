package app.aakyol.weasleymessenger.constants;

import com.google.android.gms.location.LocationResult;

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

            public static final String LOG_TAG_LOCATIONPERMISSION = "Permission: Location:";

        }

    }

    public static LocationResult currentLocation;
}
