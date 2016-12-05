package com.firat.noiseapp.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;

/**
 * Created by FIRAT on 04.12.2016.
 */
public class SoundCaptureService extends Service {

    public static final String TAG = "SoundCaptureService";
    private static Timer timer = new Timer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service started");
        //timer.scheduleAtFixedRate(new SoundCaptureTimerTask(),0,1000);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"Service destroyed");
    }
}
