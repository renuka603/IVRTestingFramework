package com.example.renukathakur.IvrMissCall;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class SmsSenderService extends Service {

    int DELAY_MILLISECONDS = 1800000;
    RequestQueue requestQueue;

    Handler h;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        h = new Handler();
        final SharedPreferences sharedPreferences = getSharedPreferences("IVR_RENUKA", MODE_PRIVATE);
        final SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        requestQueue = Volley.newRequestQueue(this);
        final StringRequest SucessCallbackRequest = new StringRequest(Request.Method.GET, "http://api.babajob.com/api/sms/0/?toNumber=9740530275&messageBody=Passed", new Response.Listener<String>() {
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
                boolean lastTestPassed = sharedPreferences.getBoolean("lastTestPassed", false);
                if(lastTestPassed) {
                    Log.d("PhoneStatReceiver", "PASSED....");
                    requestQueue.add(SucessCallbackRequest);
                } else {
                    Log.d("PhoneStatReceiver", "FAILED....");
                    requestQueue.add(ErrorCallbackRequest);
                }
                sharedPreferencesEditor.putBoolean("lastTestPassed", false);
                h.postDelayed(this, DELAY_MILLISECONDS);
            }
        };

        h.postDelayed(r, DELAY_MILLISECONDS);

        return START_STICKY;
    }
}
