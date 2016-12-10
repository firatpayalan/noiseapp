package com.firat.noiseapp.util;

/**
 * Created by FIRAT on 10.12.2016.
 */
public class AudioFormat {

    public static final int NOT_SPECIFIED = -1;
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    public static final int BIG_ENDIAN = 1;    //Motorola byte order
    public static final int LITTLE_ENDIAN = 0;    //Intel byte order
    public static final int SIGNED = 1;
    public static final int UNSIGNED = 0;
    //Containers (each corresponds to an AudioStream subclass)
    public static final int CONTAINER_WAVE = 0;
    public static final int CONTAINER_AIFF = 1;
    public static final int CONTAINER_RAW = 2;
    public static final int CONTAINER_MP4 = 3;
    //Audio encodings
    public static final int ENCODING_LINEAR_PCM = 0;
    //3GPP...
    public static final int ENCODING_IEEE_FLOAT_PCM = 1;
    public static final int ENCODING_ULAW = 2;
    public static final int ENCODING_ALAW = 3;
    public static final int ENCODING_MSADPCM = 4;
    public static final int ENCODING_MPEG = 5;
    //Channels
    public static final int CHANNELS_MONO = 1;
    //...
    public static final int CHANNELS_STEREO = 2;
    /**
     * Common sample rates, ordered from high to low
     */
    public static final int[] SAMPLE_RATES = new int[]{ /*384000, 192000, 96000, */ 48000, 44100, 32000, 22050, 16000, 11025, 8000/*, 4000*/}; //Hz
    //...
    /**
     *
     */
    private static final long serialVersionUID = -7998982400931493700L;

    private AudioFormat() {
    } //no-one should instantiate this class

    public static String getContainerName(int container) {
        switch (container) {
            case CONTAINER_WAVE:
                return "WAVE";
            case CONTAINER_AIFF:
                return "AIFF";
            case CONTAINER_RAW:
                return "RAW";
            case CONTAINER_MP4:
                return "MP4";
            //...
            case NOT_SPECIFIED:
                return "unknown/unsupported";
            default:
                return "unknown/unsupported";
        }
    }

    public static String getEncodingName(int encoding) {
        switch (encoding) {
            case ENCODING_LINEAR_PCM:
                return "LINEAR_PCM";
            case ENCODING_IEEE_FLOAT_PCM:
                return "IEEE_FLOAT_PCM";
            case ENCODING_ULAW:
                return "ULAW";
            case ENCODING_ALAW:
                return "alaw";
            case ENCODING_MSADPCM:
                return "msadpcm";
            case ENCODING_MPEG:
                return "mpegaudio";
            //...
            case NOT_SPECIFIED:
                return "unknown/unsupported";
            default:
                return "unknown/unsupported";
        }
    }

}
