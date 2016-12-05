package com.firat.noiseapp.activity;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.PersistableBundle;

/**
 * Created by FIRAT on 04.12.2016.
 */
public class SoundRecordActivity extends Activity {
    private MediaRecorder mediaRecorder;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
    }
}
