package app.aakyol.weasleymessenger.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import app.aakyol.weasleymessenger.resource.AppResources;

import static androidx.core.content.PermissionChecker.checkSelfPermission;


/**
 * Created by aakyo on 31/03/2018.
 */

public class PermissionHelper {

    /**
     * Fetch the permission status for fine location service
     * @param context
     * @return the result of permission check
     */
    public static boolean checkPermissions(final Activity activity, final Context context) {
        return checkIfLocationPermissionGranted(activity, context)
                && checkIfSMSPermissionGranted(activity, context)
                && checkIfReadContactPermissionGranted(activity, context);
    }

    /**
     * Checks if its allowed to use the fine location service
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean checkIfLocationPermissionGranted(final Activity activity, final Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(AppResources.HelperConstants.PermissionHelperConstant.LOG_TAG_PERMISSION,"Permission for location is granted");
                return true;
            } else {
                Log.d(AppResources.HelperConstants.PermissionHelperConstant.LOG_TAG_PERMISSION,"Permission for location is revoked");
                return false;
            }
        }
        else { // On API < 23, the permissions are informed to the user during installation
            Log.d(AppResources.HelperConstants.PermissionHelperConstant.LOG_TAG_PERMISSION,"Permission for location is granted");
            return true;
        }
    }

    /**
     * Checks if its allowed to use the sms sending functionality
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean checkIfSMSPermissionGranted(final Activity activity, final Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(context, android.Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(AppResources.HelperConstants.PermissionHelperConstant.LOG_TAG_PERMISSION,"Permission for SMS sending is granted");
                return true;
            } else {

                Log.d(AppResources.HelperConstants.PermissionHelperConstant.LOG_TAG_PERMISSION,"Permission for SMS sending is revoked");
                return false;
            }
        }
        else { // On API < 23, the permissions are informed to the user during installation
            Log.d(AppResources.HelperConstants.PermissionHelperConstant.LOG_TAG_PERMISSION,"Permission for SMS sending is granted");
            return true;
        }
    }

    /**
     * Checks if its allowed to use the sms sending functionality
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean checkIfReadContactPermissionGranted(final Activity activity, final Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(AppResources.HelperConstants.PermissionHelperConstant.LOG_TAG_PERMISSION,"Permission for reading contacts sending is granted");
                return true;
            } else {

                Log.d(AppResources.HelperConstants.PermissionHelperConstant.LOG_TAG_PERMISSION,"Permission for reading contacts sending is revoked");
                return false;
            }
        }
        else { // On API < 23, the permissions are informed to the user during installation
            Log.d(AppResources.HelperConstants.PermissionHelperConstant.LOG_TAG_PERMISSION,"Permission for reading contacts sending is granted");
            return true;
        }
    }
}