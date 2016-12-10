package com.firat.noiseapp.model;

import android.util.Log;

import com.firat.noiseapp.util.AudioFormat;
import com.firat.noiseapp.util.MathNT;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by FIRAT on 10.12.2016.
 */
public abstract class AudioStream {

    public static final String TAG = "AudioStream";
    //STATICS--------------------------------------------------------
    static private Vector<Class<?>> audioStreamClasses = new Vector<Class<?>>();


    static //by lack of reflection on Java ME/CLDC
    {
        try {
            audioStreamClasses.addElement(RawAudioStream.class);
        } catch (Exception ignore) {
            Log.e(TAG,ignore.getMessage(),ignore.fillInStackTrace());
        }
    }

    //DYNAMICS-------------------------------------------------------
    protected AudioSpecification audioSpecUsedForRecording;
    protected long recordStartTime;
    protected byte[] data;
    //Package visibility:
    int encoding = AudioFormat.NOT_SPECIFIED;
    int numChannels = AudioFormat.NOT_SPECIFIED;
    long sampleRate = AudioFormat.NOT_SPECIFIED;
    long byteRate = AudioFormat.NOT_SPECIFIED;
    int bitsPerSample = AudioFormat.NOT_SPECIFIED;
    int endianness = AudioFormat.NOT_SPECIFIED;
    int signed = AudioFormat.NOT_SPECIFIED;
    long numSamples = AudioFormat.NOT_SPECIFIED; //number of samples (per channel!)


    protected AudioStream() //needed for factory method above!
    {
    }

    /**
     * @param dataBytes
     * @throws IOException
     */
    public AudioStream(AudioSpecification audioSpecUsedForRecording, long recordStartTime, byte[] dataBytes) throws Exception {
        initialise(audioSpecUsedForRecording, recordStartTime, dataBytes);
    }

    /**
     * Factory method to package rawData (byte array) in a suitable SoundStream instance, depending on this AudioSpecification
     *
     * @param rawData
     * @return a SoundStream object
     */
    static public AudioStream packageInSuitableStream(AudioSpecification audioSpecUsedForRecording, long recordStartTime, byte[] rawData) {
        if (audioSpecUsedForRecording.isResultKnown())
            return packageInInstance(audioSpecUsedForRecording, recordStartTime, rawData, audioSpecUsedForRecording.getResultContainerClass());
        else {    //look for a suitable stream type:
            Enumeration<Class<?>> aStreamClassEnum = audioStreamClasses.elements();
            while (aStreamClassEnum.hasMoreElements()) {
                AudioStream stream = packageInInstance(audioSpecUsedForRecording, recordStartTime, rawData, aStreamClassEnum.nextElement());
                if (stream != null)
                    return stream;
            }
            return null; //no suitable class found
        }
    }

    static private AudioStream packageInInstance(AudioSpecification audioSpecUsedForRecording, long recordStartTime, byte[] rawData, Class<?> audioStreamClass) {
        try {
            AudioStream aStream = (AudioStream) audioStreamClass.newInstance();
            aStream.initialise(audioSpecUsedForRecording, recordStartTime, rawData); //will throw Exception if something goes wrong
            //log.debug("Byte array packaged as " + aStreamClass.getName());
            return aStream; //all went fine, return SoundStream object
        } catch (Exception e) {
            return null;
        }
    }

    private void initialise(AudioSpecification audioSpecUsedForRecording, long recordStartTime, byte[] dataBytes) throws Exception {
        if (dataBytes == null || dataBytes.length == 0)
            throw new IllegalArgumentException("rawData cannot be null or empty");
        this.audioSpecUsedForRecording = audioSpecUsedForRecording;
        this.recordStartTime = recordStartTime;
        this.data = dataBytes;
        parseHeader(); //!!!
        //MOVED TO AudioRecorder.testRecord() (no need to do this every time): audioSpecUsedForRecording.inferResultsFrom(this);
    }

    /**
     * This will parse the header(s) of the stream and will
     * check if it conforms, if not an exception is thrown,
     * if yes the audioSpecUsedForRecording object will be
     * updated with the "resulting" fields.
     *
     * @throws Exception
     */
    protected abstract void parseHeader() throws Exception;

    public boolean isValidWrtAudioSpec(AudioSpecification audioSpec) {
        try {
            if (audioSpec.isEncodingSet() && encoding != audioSpec.getEncoding())
                throw new Exception("audioSpec does not describe a WAVE/PCM encoding, while this appears to be a WAVE/PCM steam");
            if (audioSpec.isSampleRateSet() && sampleRate != audioSpec.getSampleRate())
                throw new Exception("Incorrect sample rate (" + sampleRate + " instead of " + audioSpec.getSampleRate() + ")");
            if (audioSpec.isNumChannelsSet() && numChannels != audioSpec.getNumChannels())
                throw new Exception("Incorrect channel count (" + numChannels + " instead of " + audioSpec.getNumChannels() + ")");
            if (audioSpec.isBitsPerSampleSet() && bitsPerSample != audioSpec.getBitsPerSample())
                throw new Exception("Incorrect bitsPerSample (" + bitsPerSample + " instead of " + audioSpec.getBitsPerSample() + ")");
        } catch (Exception e) {
            return false;
        }
        return true; //valid!
    }

    public boolean isValidWrtRecordTime(int recordTimeMS) {
        float recordTimeS = (float) recordTimeMS / 1000;
        long expectedMinimalDataSize = (long) (recordTimeS * byteRate); //expected minimal bytes of sample data

        if (getAudioDataSize() < expectedMinimalDataSize) {
            long missingBytes = expectedMinimalDataSize - getAudioDataSize();
            float secondsMissing = (float) missingBytes / byteRate;
            float prcTimeMissing = secondsMissing / (recordTimeS * 100f);
            long missingSamples = (long) (secondsMissing * sampleRate * numChannels);
            return false;
        }
        return true; //valid!
    }

    /**
     * Validates the stream wrt to the AudioSpecification that was used to record it
     *
     * @return
     */
    public boolean isValidWrtAudioSpec() {
        return isValidWrtAudioSpec(audioSpecUsedForRecording);
    }

    public boolean isValid(int recordTimeMS) {
        return (isValidWrtAudioSpec() && isValidWrtRecordTime(recordTimeMS));
    }

    public boolean isValid(AudioSpecification audioSpec, int recordTimeMS) {
        return (isValidWrtAudioSpec(audioSpec) && isValidWrtRecordTime(recordTimeMS));
    }

    /**
     * @return the size (in bytes) of the total stream (incl. header)
     */
    public int getSteamSize() {
        return data.length;
    }

    /**
     * @return the size (in bytes) of the audio data (only the actual samples)
     */
    public abstract int getAudioDataSize();

    public float getLengthSeconds() {
        int audioBytes = getAudioDataSize();
        if (audioBytes != AudioFormat.NOT_SPECIFIED && byteRate != 0)
            return (float) audioBytes / byteRate;
        else {
            return 0;
        }
    }

    public int getSampleIndexForSeekPosition(int elapsedTimeMS) {
        return (int) MathNT.round(((double) elapsedTimeMS / 1000d) * sampleRate);
    }

    /**
     * @param sampleIndex in interval [0, numSamples[
     * @return offset (within the dataBytes byte array) of the first byte of the sample with number <sampleIndex>
     */
    public abstract int getSampleOffset(int sampleIndex);

    public abstract int getContainerType();

    public abstract String getFileExtension();

    /**
     * @return the audioSpecUsedForRecording
     */
    public AudioSpecification getAudioSpecUsedForRecording() {
        return audioSpecUsedForRecording;
    }

    /**
     * @return the recordStartTime
     */
    public long getRecordStartTime() {
        return recordStartTime;
    }

    public byte[] getDataBytes() {
        return data;
    }

    public int getEncoding() {
        return encoding;
    }

//    public boolean isDecodeable() {
//        return audioSpecUsedForRecording.getDecoder() != null;
//    }

    public long getSampleRate() {
        return sampleRate;
    }

    public long getByteRate() {
        return byteRate;
    }

    public int getBitsPerSample() {
        return bitsPerSample;
    }

    public int getNumChannels() {
        return numChannels;
    }

    public int getRawDataSize() {
        return data.length;
    }

    public long getNumSamples() {
        return numSamples;
    }

    /**
     * @return the "endianness"
     */
    public int getEndianness() {
        return endianness;
    }

    /**
     * @return the signed
     */
    public int getSigned() {
        return signed;
    }

    protected byte readByte(int i) {
        return data[i];
    }

    protected short readShort(int offset) {
        return readSignedShort(offset); //signed is default (since this is Java)
    }

    protected short readSignedShort(int offset) {
        return (short) readValue(offset, 2);
    }

    protected int readUnsignedShort(int offset) {
        return (int) readValue(offset, 2);
    }

    protected void writeShort(int offset, short newValue) {
        writeSignedShort(offset, newValue); //signed is default (since this is Java)
    }

    protected void writeSignedShort(int offset, short newValue) {
        writeValue(offset, 2, newValue);
    }

    protected void writeUnsignedShort(int offset, int newValue) {
        if (newValue >= 0 && newValue <= 65535) //65535 = 2^16 - 1
            writeValue(offset, 2, newValue);
        else
            throw new IllegalArgumentException("newValue out of range (should be: 0 <= newValue <= 65535)");
    }

    protected int readInt(int offset) {
        return readSignedInt(offset); //signed is default (since this is Java)
    }

    protected int readSignedInt(int offset) {
        return (int) readValue(offset, 4);
    }

    protected long readUnsignedInt(int offset) {
        return /*(long)*/ readValue(offset, 4);
    }

    protected void writeInt(int offset, int newValue) {
        writeSignedInt(offset, newValue); //signed is default (since this is Java)
    }

    protected void writeSignedInt(int offset, int newValue) {
        writeValue(offset, 4, newValue);
    }

    protected void writeUnsignedInt(int offset, long newValue) {
        if (newValue >= 0 && newValue <= 4294967295L) //4294967295 = 2^32 - 1
            writeValue(offset, 4, newValue);
        else
            throw new IllegalArgumentException("newValue out of range (should be: 0 <= newValue <= 4294967295)");
    }

    protected long readLong(int offset) {
        return readSignedLong(offset);
    }

    protected long readSignedLong(int offset) {
        return readValue(offset, 8);
    }

    protected void writeLong(int offset, long newValue) {
        writeSignedLong(offset, newValue);
    }

    protected void writeSignedLong(int offset, long newValue) {
        writeValue(offset, 8, newValue);
    }

    protected long readValue(int offset, int numOfBytes) {
        if (numOfBytes > 8)
            throw new IllegalArgumentException("numOfBytes is max 8 (=long)");
        long result = 0;
        if (endianness == AudioFormat.BIG_ENDIAN) //Big Endian
            for (int i = 0; i < numOfBytes; i++)
                result |= (long) (data[offset + i] & 0xFF) << (((numOfBytes - 1) - i) * 8);
        else //Little Endian (default, so also when endianness = AudioFormat.NOT_SPECIFIED)
            for (int i = 0; i < numOfBytes; i++)
                result |= (long) (data[offset + i] & 0xFF) << (i * 8);
        return result;
    }

    protected void writeValue(int offset, int numOfBytes, long newValue) {
        if (numOfBytes > 8)
            throw new IllegalArgumentException("numOfBytes is max 8 (=long)");
        if (endianness == AudioFormat.BIG_ENDIAN) //Big Endian
            for (int i = 0; i < numOfBytes; i++)
                data[offset + i] = (byte) ((newValue >> (((numOfBytes - 1) - i) * 8)) & 0xFF);
        else //Little Endian (default, so also when endianness = AudioFormat.NOT_SPECIFIED)
            for (int i = 0; i < numOfBytes; i++)
                data[offset + i] = (byte) ((newValue >> (i * 8)) & 0xFF);
    }

    protected byte[] readBytes(int offset, int numBytes) {
        if ((offset < 0) || (offset > data.length) || (numBytes < 0) || ((offset + numBytes) > data.length))
            throw new IndexOutOfBoundsException();
        byte[] bytes = new byte[numBytes];
        for (int i = 0; i < numBytes; i++)
            bytes[i] = data[offset + i];
        return bytes;
    }

    protected void writeBytes(int offset, byte[] bytes) {
        if (bytes == null)
            throw new NullPointerException();
        if ((offset < 0) || (offset > data.length) || (bytes.length < 0) || ((offset + bytes.length) > data.length))
            throw new IndexOutOfBoundsException();
        for (int i = 0; i < bytes.length; i++)
            data[offset + i] = bytes[i];
    }

    protected String readString(int offset, int length) {
        return new String(readBytes(offset, length));
    }

    protected void writeString(int offset, String newValue) {
        writeBytes(offset, newValue.getBytes());
    }
}
