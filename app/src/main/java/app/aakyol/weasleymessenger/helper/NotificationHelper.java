package app.aakyol.weasleymessenger.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import app.aakyol.weasleymessenger.R;
import app.aakyol.weasleymessenger.resource.AppResources;

public class NotificationHelper {

    /**
     * Creates the notification channel to put message sent notifications through
     *
     * @param context
     */
    public static void createNotificationChannel(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(AppResources.WEASLEY_SERVICE_NOTIFICATION_ID, AppResources.WEASLEY_SERVICE_NAME, importance);
            channel.setDescription(AppResources.WEASLEY_SERVICE_DESCRIPTION);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static NotificationCompat.Builder buildNotification(final String notificationText, final Context context, final PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(context, AppResources.WEASLEY_SERVICE_NOTIFICATION_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(AppResources.WEASLEY_SERVICE_NAME)
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

    /**
     * Notification sender
     *
     * @param notificationText
     * @param context
     * @param pendingIntent
     */
    public static void sendNotificationToDevice(final String notificationText, final Context context, final PendingIntent pendingIntent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, buildNotification(notificationText, context, pendingIntent).build());
    }
}
