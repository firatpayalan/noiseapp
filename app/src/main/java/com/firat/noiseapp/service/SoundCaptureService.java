package com.firat.noiseapp.service;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.firat.noiseapp.SoundCapture;

import java.util.Timer;
import java.util.TimerTask;

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
        timer.scheduleAtFixedRate(new SoundCaptureTimerTask(), 0, 10000);

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

    class SoundCaptureTimerTask extends TimerTask {
        public static final String TAG = "SoundCaptureTimerTask";
        private SoundCapture soundCapture = null;

        public SoundCaptureTimerTask()
        {
            soundCapture = new SoundCapture();
        }

        @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
        @Override
        public void run() {

            try {
                Log.d(TAG, "task running");
                PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
                if (pm.isInteractive())
                {
                    soundCapture.startRecording();
                    Thread.sleep(5000);
                    String currentFile = soundCapture.stopRecording(); //constructed file name
                    //gps tagging
                    //send to server.
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, e.getMessage(), e.fillInStackTrace());
            }

        }
    }
}
