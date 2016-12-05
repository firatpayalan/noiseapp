package com.firat.noiseapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.firat.noiseapp.service.SoundCaptureTimerTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;

/**
 * Created by FIRAT on 04.12.2016.
 */
@Deprecated
public class ScreenDetectBReciever extends BroadcastReceiver {
    public static final String TAG = "ScreenDetectBtReceiver";
    public static final int RECORD_TIME=1000*5;
    private static Timer timer = new Timer();


    @Override
    public void onReceive(Context context, Intent intent) {
        try
        {
            SoundCapture soundCapture = new SoundCapture();
            if (intent.getAction().equals(Intent.ACTION_USER_PRESENT))
            {
                Log.d(TAG, "screen unlocked");
//                timer.scheduleAtFixedRate(new SoundCaptureTimerTask(), 0, 10000); //+5000 ms

//                soundCapture.startRecording(context);
//                Thread.sleep(RECORD_TIME);
//                soundCapture.stopRecording(context);
            }
            else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
            {
                Log.d(TAG, "screen locked");
//                timer.cancel();
            }

        }
        catch (Exception e)
        {
            Log.e(TAG,e.getMessage(),e.fillInStackTrace());

        }
    }

}
