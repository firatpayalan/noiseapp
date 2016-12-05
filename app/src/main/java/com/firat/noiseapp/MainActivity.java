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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firat.noiseapp.service.SoundCaptureService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private Button stopButton;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        final Intent intent = new Intent(Intent.ACTION_SYNC,null,this, SoundCaptureService.class);
        startService(intent);

        stopButton = (Button)findViewById(R.id.stopButton);
        startButton = (Button)findViewById(R.id.startButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSuccess = stopService(intent);
                if (isSuccess)
                    Toast.makeText(MainActivity.this,"Service Stopped!",Toast.LENGTH_LONG).show();
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startService(intent);
               Toast.makeText(MainActivity.this,"Service Started!",Toast.LENGTH_LONG).show();
            }
        });

    }
}
