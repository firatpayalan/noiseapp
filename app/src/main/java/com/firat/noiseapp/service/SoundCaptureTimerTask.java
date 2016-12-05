package com.firat.noiseapp.service;

import android.util.Log;

import java.util.TimerTask;

/**
 * Created by FIRAT on 04.12.2016.
 */
public class SoundCaptureTimerTask extends TimerTask {
    public static final String TAG = "SoundCaptureTimerTask";
    @Override
    public void run() {
        Log.d(TAG,"task running");
    }
}
