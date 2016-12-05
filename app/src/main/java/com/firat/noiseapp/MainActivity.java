package com.firat.noiseapp;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.firat.noiseapp.service.SoundCaptureService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(new ScreenDetectBReciever(), new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(new ScreenDetectBReciever(), new IntentFilter(Intent.ACTION_USER_PRESENT));

        setContentView(R.layout.activity_main);
//        Intent intent = new Intent(Intent.ACTION_SYNC,null,this, SoundCaptureService.class);
//        startService(intent);
    }

}
