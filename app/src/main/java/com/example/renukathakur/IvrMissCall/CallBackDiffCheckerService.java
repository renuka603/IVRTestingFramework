package com.example.renukathakur.IvrMissCall;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by RenukaThakur on 9/24/15.
 */
public class CallBackDiffCheckerService extends Service {

    Handler handler;
    int DELAY_MILLISECONDS = 300;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferenceEditor;

    long lastOutgoingCall, lastIncomingCall;
    boolean shouldCompare;

    RequestQueue requestQueue;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("IVR_RENUKA", MODE_PRIVATE);
        sharedPreferenceEditor = sharedPreferences.edit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        requestQueue = Volley.newRequestQueue(this);
        final StringRequest SucessCallbackRequest = new StringRequest(Request.Method.GET, "http://api.babajob.com/api/sms/0/?toNumber=9740530275&messageBody=Passed", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("PhonStatReceiver", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("PhoneStatListener", "Error thrown");
            }
        });

        final StringRequest ErrorCallbackRequest = new StringRequest(Request.Method.GET, "http://api.babajob.com/api/sms/0/?toNumber=9740530275&messageBody=Failed", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("PhoneStatReceiver", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("PhoneStatListener", "Error thrown");
            }
        });

        Runnable r = new Runnable() {
            @Override
            public void run() {
                // CONTINUOUS CODE OVER HERE
                lastOutgoingCall = sharedPreferences.getLong("lastOutgoingCall", System.currentTimeMillis());
                lastIncomingCall = sharedPreferences.getLong("lastIncomingCall", System.currentTimeMillis());
                shouldCompare = sharedPreferences.getBoolean("shouldCompare", false);

                if(shouldCompare) {
                    if(lastIncomingCall - lastOutgoingCall > 900000) {
                        // fire the "FAILED" api call
                        Log.d("PhoneStatReceiver", "FAILED...." + lastIncomingCall + ":" + lastOutgoingCall);

                    requestQueue.add(ErrorCallbackRequest);

                    } else {
                        // fire the "SUCCESS" api call
                        Log.d("PhoneStatReceiver", "SUCCESS....");
                        requestQueue.add(SucessCallbackRequest);

                    }
                    sharedPreferenceEditor.putBoolean("shouldCompare", false);
                    sharedPreferenceEditor.apply();
                }

                handler.postDelayed(this, DELAY_MILLISECONDS);
            }
        };
        handler.postDelayed(r, DELAY_MILLISECONDS);
        return START_STICKY;
    }
}
