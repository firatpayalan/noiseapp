package com.firat.noiseapp.model;

import android.util.Log;

import com.firat.noiseapp.util.AudioFormat;

/**
 * Created by FIRAT on 10.12.2016.
 */
public class RawAudioStream extends AudioStream {

    private static final int headerSize = 0;
    private int blockAlign;

    protected void parseHeader() throws Exception {
        //There is no header so we trust the audiospec:
        encoding = audioSpecUsedForRecording.getEncoding();
        sampleRate = audioSpecUsedForRecording.getSampleRate();
        bitsPerSample = audioSpecUsedForRecording.getBitsPerSample();
        numChannels = audioSpecUsedForRecording.getNumChannels();
        endianness = audioSpecUsedForRecording.getEndianness();
        signed = audioSpecUsedForRecording.getSigned();
        if (numChannels != AudioFormat.NOT_SPECIFIED && bitsPerSample != AudioFormat.NOT_SPECIFIED) {
            blockAlign = numChannels * bitsPerSample / 8;
            numSamples = data.length / blockAlign;
            if (sampleRate != AudioFormat.NOT_SPECIFIED)
                byteRate = sampleRate * blockAlign;
        }
    }

    public void fixSampleRate(int correctSampleRate) {
        sampleRate = correctSampleRate;
        //update depending field:
        if (blockAlign != AudioFormat.NOT_SPECIFIED)
            byteRate = sampleRate * blockAlign; //ByteRate = SampleRate * NumChannels * BitsPerSample/8 = SampleRate * BlockAlign
    }

    public boolean isValidWrtAudioSpec(AudioSpecification audioSpec) //override
    {
        return true; //we trust the AudioSpec so there's no point in validating
    }

    public int getAudioDataSize() {
        return data.length;
    }

    public int getContainerType() {
        return AudioFormat.CONTAINER_RAW;
    }

    public String getFileExtension() {
        return "raw"; //for lack of something better
    }

    public byte[] getDataBytes(boolean forWaveFile) {
        byte[] result = null;
        if (!forWaveFile)
            result = data;
        else {
            byte[] keep = data; //swap
            //Write RIFF/WAVE header:
            data = new byte[44 + keep.length];
            writeString(0, "RIFF");
            writeUnsignedInt(4, 36 + keep.length);
            writeString(8, "WAVE");
            writeString(12, "fmt ");
            writeUnsignedInt(16, 16);
            writeUnsignedShort(20, 1);
            writeUnsignedShort(22, numChannels);
            writeUnsignedInt(24, sampleRate);
            writeUnsignedInt(28, byteRate);
            writeUnsignedShort(32, blockAlign);
            writeUnsignedShort(34, bitsPerSample);
            writeString(36, "data");
            writeUnsignedInt(40, keep.length);
            for (int b = 0; b < keep.length; b++)
                data[44 + b] = keep[b];
            result = data;
            data = keep; //swap back
        }
        return result;
    }

    /**
     * @param sampleIndex in interval [0, numSamples[
     * @return offset (within the dataBytes byte array) of the first byte of the sample with number <sampleIndex>
     */
    public int getSampleOffset(int sampleIndex) {
        if (sampleIndex < 0 || sampleIndex >= numSamples)
        {
            Log.d(TAG, "getSampleOffset: "+sampleIndex);

            throw new IllegalArgumentException("Invalid sample index, should be 0 <= sampleIndex < " + numSamples);
        }
        return headerSize /*skip header*/ +
                (sampleIndex * blockAlign); /*skip previous samples*/
    }
}
