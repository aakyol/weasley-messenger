package app.aakyol.weasleymessenger.helper;

import android.telephony.SmsManager;

public class SMSHelper {

    /**
     * SMS sender
     * @param phoneNo
     * @param message
     */
    public static void sendSMS(final String phoneNo, final String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
    }
}
