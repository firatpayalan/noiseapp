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

import com.firat.noiseapp.model.Noise;
import com.firat.noiseapp.model.NoiseRequest;
import com.firat.noiseapp.service.SoundCaptureService;
import com.firat.noiseapp.util.MathNT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    AdvancedSoundCapture soundCapture = new AdvancedSoundCapture();
    private Button stopButton;
    private Button startButton;
    private Timer locationTimer;
    private SimpleLocation location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        final Intent intent = new Intent(Intent.ACTION_SYNC,null,this, SoundCaptureService.class);
//        startService(intent);

        location = new SimpleLocation(this);
        stopButton = (Button)findViewById(R.id.stopButton);
        startButton = (Button)findViewById(R.id.startButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoiseRequest request = new NoiseRequest();

                double meanLeq = soundCapture.release();

                List<Double> coordinates = endLocationTask();
                request.setLeq(meanLeq);
                request.setLatitude(coordinates.get(0));
                request.setLongtitude(coordinates.get(1));
                coordinates.clear();
                //request object is ready to send web server.
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    Log.d(TAG, "onClick: lati= "+location.getLatitude());
//                    Log.d(TAG, "onClick: long= "+simpleLocation.getLongitude());
                    soundCapture.startTask();
                    startLocationTask();
                } catch (Exception e) {
                    Log.e(TAG,e.getMessage(),e.fillInStackTrace());
                    e.printStackTrace();
                }
//               startService(intent);
//               Toast.makeText(MainActivity.this,"Service Started!",Toast.LENGTH_LONG).show();
            }
        });

    }


    //stop timer and clear lists.
    public List<Double> endLocationTask()
    {
        List<Double> coordinates = new ArrayList<>();
        locationTimer.cancel();
        locationTimer=null;
        double meanLat = MathNT.meanOfList(Noise.getInstance().getLatitudes());
        double meanLong = MathNT.meanOfList(Noise.getInstance().getLongtitudes());
        Log.d(TAG, "endLocationTask: mean latitude=" + meanLat);
        Log.d(TAG, "endLocationTask: mean longtitude=" + meanLong);
        Noise.getInstance().getLongtitudes().clear();
        Noise.getInstance().getLatitudes().clear();
        coordinates.add(meanLat);
        coordinates.add(meanLong);
        return coordinates;
    }
    public void startLocationTask()
    {
        locationTimer = new Timer();
        locationTimer.schedule(getLocations(),0,1000);
    }
    public TimerTask getLocations(){
        return new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "run: latitude="+location.getLatitude());
                Noise.getInstance().getLatitudes().add(location.getLatitude());
                Log.d(TAG, "run: longtitude=" + location.getLongitude());
                Noise.getInstance().getLongtitudes().add(location.getLongitude());
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        location.beginUpdates();
    }

    @Override
    protected void onPause() {
        location.endUpdates();
        super.onPause();
    }
}
