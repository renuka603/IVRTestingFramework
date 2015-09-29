package com.example.renukathakur.IvrMissCall;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by RenukaThakur on 9/24/15.

public class CallStateBroadcastReceiver extends BroadcastReceiver {
    TelephonyManager telephony;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("CallBack_Diff", "BroadcastReceiver_OnCall");

        CallStateListener phoneListener = new CallStateListener(context);
        telephony = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

}**/

public class CallStateBroadcastReceiver extends BroadcastReceiver{

    private static final String TAG = "PhoneStatReceiver";
    private static boolean incomingFlag = false;
    private static String incoming_number = null;
    TelephonyManager telephony;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            incomingFlag = false;
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.i(TAG, "call OUT:"+phoneNumber);
        }else{
            TelephonyManager tm =
                    (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    incomingFlag = true;
                    incoming_number = intent.getStringExtra("incoming_number");
                    Log.i(TAG, "RINGING :"+ incoming_number);
                    CallStateListener phoneListener = new CallStateListener(context);
                    telephony = (TelephonyManager) context
                            .getSystemService(Context.TELEPHONY_SERVICE);
                    telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if(incomingFlag){
//                        Intent callIntent = new Intent(context, RecordService.class);
//                        context.startService(callIntent);
                        Log.i(TAG, "incoming ACCEPT :"+ incoming_number);
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if(incomingFlag){
//                        context.stopService(new Intent(context, RecordService.class));
                        Log.i(TAG, "incoming IDLE");
                    }
                    break;
            }
        }
    }
}

