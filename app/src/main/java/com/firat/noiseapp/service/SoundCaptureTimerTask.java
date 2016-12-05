package com.firat.noiseapp.service;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.firat.noiseapp.SoundCapture;

import java.util.TimerTask;

/**
 * Created by FIRAT on 04.12.2016.
 */
@Deprecated
public class SoundCaptureTimerTask extends TimerTask {
    public static final String TAG = "SoundCaptureTimerTask";
    private SoundCapture soundCapture = null;

    public SoundCaptureTimerTask()
    {
        soundCapture = new SoundCapture();
    }
    @Override
    public void run() {

        try {
            Log.d(TAG, "task running");
            soundCapture.startRecording();
            Thread.sleep(5000);
            soundCapture.stopRecording();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }

    }
}
