package app.aakyol.weasleymessenger.helper;

import android.view.View;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class SnackbarHelper {

    /**
     * Prints a snackbar message with a short duration
     * @param activityViewObject
     * @param message
     */
    public static void printShortSnackbarMessage(final View activityViewObject, final String message) {
        Snackbar.make(activityViewObject,
                message,
                BaseTransientBottomBar.LENGTH_SHORT).setAction("Location: ", null).show();
    }

    /**
     * Prints a snackbar message with a long duration
     * @param activityViewObject
     * @param message
     */
    public static void printLongSnackbarMessage(final View activityViewObject, final String message) {
        Snackbar.make(activityViewObject,
                message,
                BaseTransientBottomBar.LENGTH_LONG).setAction("Location: ", null).show();
    }
}
