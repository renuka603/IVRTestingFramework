package com.example.renukathakur.IvrMissCall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.TimeUtils;
import android.telephony.SmsMessage;
import android.util.Log;

import java.security.Timestamp;
import java.sql.Time;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SmsReceivedBroadcastReceiver extends BroadcastReceiver {

    String LOG_TAG = "DEBUG_REC";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPreferences = context.getSharedPreferences("IVR_RENUKA", Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        Log.d(LOG_TAG, "Sms Received");
        Bundle bundle = intent.getExtras();

        SmsMessage[] msgs = null;

        String str = "";

        if (bundle != null) {
            // Retrieve the SMS Messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            // For every SMS message received
            for (int i = 0; i < msgs.length; i++) {
                // Convert Object array
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                // Sender's phone number
                str += "SMS from " + msgs[i].getOriginatingAddress() + " : ";
                // Fetch the text message
                str += msgs[i].getMessageBody().toString();
                // Newline <img src="http://codetheory.in/wp-includes/images/smilies/simple-smile.png" alt=":-)" class="wp-smiley" style="height: 1em; max-height: 1em;">
                str += "\n";
            }

            // Display the entire SMS Message
            //Log.d(TAG, str);
        }
        if ( str.contains("IVR Test")) {

            sharedPreferencesEditor.putLong("lastOutgoingCall", System.currentTimeMillis());
            sharedPreferencesEditor.apply();

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            callIntent.setData(Uri.parse("tel:08880004444"));
            context.startActivity(callIntent);
        }
    }
}
