package com.example.renukathakur.IvrMissCall;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by RenukaThakur on 9/17/15.
 */

/**
 * Listener to detect incoming calls.
 */
public class CallStateListener extends PhoneStateListener {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    public CallStateListener(Context context) {
        this.context = context;

        sharedPreferences = context.getSharedPreferences("IVR_RENUKA", Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                // called when someone is ringing to this phone

                Toast.makeText(context,
                        "Incoming: " + incomingNumber,
                        LENGTH_LONG).show();
                sharedPreferencesEditor.putLong("lastIncomingCall", System.currentTimeMillis());
                sharedPreferencesEditor.putBoolean("shouldCompare", true);
                sharedPreferencesEditor.putBoolean("lastTestPassed", true);
                sharedPreferencesEditor.apply();
                break;


        }
    }
}