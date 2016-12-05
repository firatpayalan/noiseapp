package com.firat.noiseapp;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by FIRAT on 05.12.2016.
 */
public class SoundCapture {
    public static final String TAG = "SoundCapture";
    private AudioRecord audioRecord =null;
    private int bufferSize = 0;
    private boolean isRecording = false;
    private Thread recordingThread = null;
    public static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    public static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    public static final String AUDIO_RECORDER_FILE_EXT_WAV=".wav";
    public static final int RECORDER_BPP = 16;

    public SoundCapture()
    {
        bufferSize = AudioRecord.getMinBufferSize(8000
                , AudioFormat.CHANNEL_IN_STEREO
                , AudioFormat.ENCODING_PCM_16BIT);
    }

    public void startRecording()
    {
        Log.d(TAG, "start recording");

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC
                ,44100
                , AudioFormat.CHANNEL_IN_STEREO
                ,AudioFormat.ENCODING_PCM_16BIT
                ,bufferSize);
        int recorderState = audioRecord.getState();
        if (recorderState == 1)
            audioRecord.startRecording();

        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile();
            }
        },"Recording Thread");

        recordingThread.start();
    }
    public String stopRecording()
    {
        Log.d(TAG, "stop recording");

        if ( audioRecord!=null)
        {
            isRecording = false;
        }
        if (audioRecord.getState() == 1)
        {
            audioRecord.stop();
            audioRecord.release();
        }
        audioRecord = null;
        recordingThread = null;

        String outputFile = getFilename();
        copyWavFile(getTempFilename(), outputFile);
        deleteTempFile();

        return outputFile;
    }

    private void deleteTempFile()
    {
        File file = new File(getTempFilename());
        file.delete();
    }

    private void copyWavFile(String inFileName, String outFileName)
    {
        try
        {
            FileInputStream fin = null;
            FileOutputStream fos = null;
            long totalAudioLen = 0;
            long totalDataLen = totalAudioLen + 36;
            long longSampleRate = 44100;
            int channels = 2;
            long byteRate = RECORDER_BPP * 44100 * channels / 8;

            byte[] data = new byte[bufferSize];
            fin = new FileInputStream(inFileName);
            fos = new FileOutputStream(outFileName);

            totalAudioLen = fin.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            Log.i(TAG,"File size= "+totalDataLen);
            writeWavFileHeader(fos,totalAudioLen,totalDataLen,longSampleRate,channels,byteRate);
            while (fin.read(data) != -1)
            {
                fos.write(data);
            }

            fin.close();
            fos.close();
        }
        catch (Exception e)
        {
            Log.e(TAG,e.getMessage(),e.fillInStackTrace());
        }

    }

    private void writeWavFileHeader(FileOutputStream fos, long totalAudioLen, long totalDataLen, long longSampleRate, int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        fos.write(header,0,44);
    }




    private void writeAudioDataToFile()
    {
        byte data[] = new byte[bufferSize];
        String fileName = getTempFilename();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
        }
        catch (FileNotFoundException e)
        {
            Log.e(TAG,e.getMessage(),e.fillInStackTrace());
        }

        int read = 0;

        if (null != fos)
        {
            while (isRecording)
            {
                read = audioRecord.read(data,0,bufferSize);
                if (AudioRecord.ERROR_INVALID_OPERATION != read)
                {
                    try {
                        fos.write(data);
                    }
                    catch (IOException e)
                    {
                        Log.e(TAG,e.getMessage(),e.fillInStackTrace());
                    }
                }
            }
        }
    }

    private String getTempFilename()
    {
        String filepath = Environment.getExternalStorageDirectory().getPath();
//        String filepath = context.getFilesDir().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists())
        {
            file.mkdirs();
        }

        File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);
        if (tempFile.exists())
        {
            tempFile.delete();
        }

        return (file.getAbsolutePath()+"/"+AUDIO_RECORDER_TEMP_FILE);
    }

    private String getFilename()
    {
        String filepath = Environment.getExternalStorageDirectory().getPath();
//        String filepath = context.getFilesDir().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);
        Log.d(TAG,"File location = "+filepath+"/"+AUDIO_RECORDER_FOLDER);
        if (!file.exists())
            file.mkdirs();

        return (file.getAbsolutePath()+"/"+System.currentTimeMillis()+AUDIO_RECORDER_FILE_EXT_WAV);
    }
}
