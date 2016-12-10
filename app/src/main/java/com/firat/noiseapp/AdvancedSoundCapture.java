package com.firat.noiseapp;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.firat.noiseapp.model.AndroidAudioSpecification;
import com.firat.noiseapp.model.AudioDecoder;
import com.firat.noiseapp.model.AudioSpecification;
import com.firat.noiseapp.model.AudioStream;
import com.firat.noiseapp.model.Filter;
import com.firat.noiseapp.model.Noise;
import com.firat.noiseapp.model.PCMAudioDecoder;
import com.firat.noiseapp.model.RawAudioStream;
import com.firat.noiseapp.util.MathNT;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import im.delight.android.location.SimpleLocation;

/**
 * Created by FIRAT on 10.12.2016.
 */
public class AdvancedSoundCapture{
    public static final String TAG = "AdvancedSoundCapture";
    public static final int SAMPLING_FREQUENCY_HZ=48000;
    public static final int CHANNEL_SIZE= AudioFormat.CHANNEL_IN_MONO;
    public static final int ENCODE_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public static final int BITS_PER_SAMPLE=16;
    private byte[] byteBuffer = null;
    private AudioRecord audioRecord;
    private int bufferSize = 0;
    private AudioSpecification audioSpecification =null;
    private AudioDecoder decoder;
    protected Filter theAFilter = null;
    protected Timer timer;
    protected TimerTask task;

    public AdvancedSoundCapture()
    {
        bufferSize = Math.max(getByteRate(),AudioRecord.getMinBufferSize(SAMPLING_FREQUENCY_HZ, CHANNEL_SIZE, ENCODE_FORMAT));
//        setBufferSize(AudioRecord.getMinBufferSize(SAMPLING_FREQUENCY_HZ, CHANNEL_SIZE, ENCODE_FORMAT));
        audioSpecification = new AndroidAudioSpecification(SAMPLING_FREQUENCY_HZ,BITS_PER_SAMPLE,1);

        theAFilter = Filter.getFilter(Filter.FILTER_TYPE_A_WEIGHTING,SAMPLING_FREQUENCY_HZ);
    }


    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
    public TimerTask getTask()
    {
        return new TimerTask() {
            @Override
            public void run() {
                try {

                    record();
                } catch (Exception e) {
                    Log.e(TAG, "run: EXCEPTION",e.fillInStackTrace());
                    e.printStackTrace();
                }
            }
        };
    }

    public void startTask()
    {
        timer = new Timer();
        task = getTask();
        timer.schedule(task,0,1000); //1 sn lik kayıtlar
    }

    //sound recording.
    public void record() throws Exception {

        if (audioRecord == null)
        {
            audioRecord = ((AndroidAudioSpecification)audioSpecification).getAudioRecord(getBufferSize());
            audioRecord.startRecording();
        }
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            throw new Exception("AudioRecord not initialized");
        }
        //recording start...

        byteBuffer = new byte[bufferSize]; //creating buffer
        //Note current time:
        long recordStartTime = System.currentTimeMillis();
        //reading bytes
        int bytesRead = audioRecord.read(byteBuffer,0,bufferSize);
        //decoding byte streams
        RawAudioStream rawAudioStream = (RawAudioStream)AudioStream.packageInSuitableStream(audioSpecification, recordStartTime, byteBuffer);
        processAudioRecord(rawAudioStream);

    }

    public void processAudioRecord(AudioStream audioStream) throws Exception {
        decoder = audioSpecification.getDecoder();
        double[] samples = decoder.decodeSamplesFloating(audioStream,0,(int)audioStream.getSampleRate()-1,0);
        analyseSamples(samples);
    }
    public double analyseSamples(double[] samples) throws Exception {
//        Log.d(TAG, "analyseSamples: preprocessed ="+ Arrays.toString(samples));
        double[] filteredSamples=theAFilter.apply(samples);
//        Log.d(TAG, "analyseSamples: filtered ="+Arrays.toString(filteredSamples));
        return computeLeq(filteredSamples);

    }

    private double computeLeq(double samples[]) throws Exception {
        double sumsquare = 0.0d, leq;
        for (int i = 0; i < samples.length; i++)
            sumsquare += samples[i] * samples[i];
        leq = (10.0d * MathNT.log10(sumsquare / samples.length)) + 93.97940008672037609572522210551d;
        if (Double.isNaN(leq) || leq <= 0)
            throw new Exception("Leq is NaN, negative or zero: " + Double.toString(leq));
        Log.d(TAG, "computeLeq: "+leq);
        Noise.getInstance().getLeqs().add(leq);
        return leq;
    }

    public double release()
    {
        //stop timer
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
            //don't do recorder=null here!!!
        }
        //stop recorder
        if (audioRecord!=null)
        {
            try {
                audioRecord.stop();
            }
            catch (Exception e)
            {
                Log.e(TAG, "release: ERROR",e.fillInStackTrace());;
            }
            audioRecord.release();
            audioRecord=null;
        }
        //calculate leqs.
        double meanLeqs = MathNT.meanOfList(Noise.getInstance().getLeqs());
        Log.d(TAG, "release: meanleqs = "+meanLeqs);
        //clear list
        Noise.getInstance().getLeqs().clear();
        return meanLeqs;
    }

    public AudioRecord getAudioRecord() {
        return audioRecord;
    }

    public void setAudioRecord(AudioRecord audioRecord) {
        this.audioRecord = audioRecord;
    }
    /**
     * Used to calculate how many bytes are needed for a certain amount of time
     *
     * @return the number of bytes needed to represent one second
     */
    public int getByteRate() {
        return SAMPLING_FREQUENCY_HZ *1 * BITS_PER_SAMPLE/ 8;
    }

}
