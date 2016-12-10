package com.firat.noiseapp.model;

import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.firat.noiseapp.util.AudioFormat;

import java.nio.ByteOrder;

/**
 * Created by FIRAT on 10.12.2016.
 */
public class AndroidAudioSpecification extends AudioSpecification {

    public AndroidAudioSpecification(int sampleRate, int bitsPerSample, int numChannels) {
        super(AudioFormat.ENCODING_LINEAR_PCM, sampleRate, bitsPerSample, numChannels);
        switch (bitsPerSample) {
            case 8:
                signed = AudioFormat.UNSIGNED;
                break;
            case 16:
                signed = AudioFormat.SIGNED;
                break;
            //TODO case 24 : signed = ??
            //TODO case 32
            //TODO case 64
        }
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN)
            endianness = AudioFormat.BIG_ENDIAN;
        else
            endianness = AudioFormat.LITTLE_ENDIAN;
    }

    public int getChannelConfig() {
        if (numChannels == AudioFormat.NOT_SPECIFIED)
            return android.media.AudioFormat.CHANNEL_IN_MONO;
        switch (numChannels) {
            case 1:
                return android.media.AudioFormat.CHANNEL_IN_MONO;
            case 2:
                return android.media.AudioFormat.CHANNEL_IN_STEREO;
            default:
                return android.media.AudioFormat.CHANNEL_IN_MONO;
        }
    }

    public int getAudioFormat() {
        switch (bitsPerSample) {
            case 8:
                return android.media.AudioFormat.ENCODING_PCM_8BIT;
            case 16:
                return android.media.AudioFormat.ENCODING_PCM_16BIT;
            //TODO case 24
            //TODO case 32
            //TODO case 64
            default:
                return android.media.AudioFormat.ENCODING_PCM_16BIT;
        }
    }

    public AudioRecord getAudioRecord(int bufferSize) {
        //TODO use other Audiosource to avoid AGC, noise cancellation, etc.?
        // See: http://groups.google.com/group/android-platform/browse_thread/thread/d9924506324e655d
        return new AudioRecord(MediaRecorder.AudioSource.MIC, getSampleRate(), getChannelConfig(), getAudioFormat(), bufferSize);
    }
}
