package app.aakyol.weasleymessenger.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.util.Log;

import static app.aakyol.weasleymessenger.resource.AppResources.LogConstans.ServiceLogConstans.LOG_TAG_LOCATIONSERVICE;

public class MessageHelper {

    /**
     * SMS Message sender
     * @param phoneNo
     * @param message
     */
    public static void sendSMSMessage(final String phoneNo, final String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
    }

    /**
     * WhatsApp message sender
     * @param context
     */
    public static void sendWhatsAppMessage(final String phoneNo, final String message, final Context context) {

        PackageManager pm = context.getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");

            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, message);
            context.startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Log.d( LOG_TAG_LOCATIONSERVICE,"WhatsApp not Installed");
        }

    }
}
