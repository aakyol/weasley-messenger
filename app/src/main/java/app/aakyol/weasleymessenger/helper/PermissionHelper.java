package app.aakyol.weasleymessenger.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import app.aakyol.weasleymessenger.activity.ActivityListRecipients;
import app.aakyol.weasleymessenger.constants.AppConstants;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * Created by aakyo on 31/03/2018.
 */

public class PermissionHelper {

    /**
     * Fetch the permission status for fine location service and sms sending
     * @param context
     * @return the result of permission check
     */
    public static boolean checkPermissions(final Activity activity, final Context context) {
        return checkIfLocationPermissionGranted(activity, context)
            && checkIfSMSPermissionGranted(activity, context);
    }

    /**
     * Checks if its alloved to use the fine location service
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean checkIfLocationPermissionGranted(final Activity activity, final Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityListRecipients.requestLocationPermission();
            if (checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(AppConstants.HelperConstants.PermissionHelperConstant.LOG_TAG_LOCATIONPERMISSION,"Permission for location is granted");
                return true;
            } else {

                Log.d(AppConstants.HelperConstants.PermissionHelperConstant.LOG_TAG_LOCATIONPERMISSION,"Permission for location is revoked");
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.d(AppConstants.HelperConstants.PermissionHelperConstant.LOG_TAG_LOCATIONPERMISSION,"Permission for location is granted");
            return true;
        }
    }

    /**
     * Checks if its alloved to use the sms sending functionality
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean checkIfSMSPermissionGranted(final Activity activity, final Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, 1);
            if (checkSelfPermission(context, Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(AppConstants.HelperConstants.PermissionHelperConstant.LOG_TAG_SMSPERMISSION,"Permission for SMS sending is granted");
                return true;
            } else {

                Log.d(AppConstants.HelperConstants.PermissionHelperConstant.LOG_TAG_SMSPERMISSION,"Permission for SMS sending is revoked");
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.d(AppConstants.HelperConstants.PermissionHelperConstant.LOG_TAG_SMSPERMISSION,"Permission for SMS sending is granted");
            return true;
        }
    }
}