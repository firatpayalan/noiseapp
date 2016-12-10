package com.firat.noiseapp.model;

import com.firat.noiseapp.util.AudioFormat;

/**
 * Created by FIRAT on 10.12.2016.
 */
public abstract class AudioDecoder {

    /**
     *
     */
    private static final long serialVersionUID = 3692715809206878728L;

    //STATICS:
    public static AudioDecoder getAudioDecoderFor(int encoding) throws Exception {
        AudioDecoder decoder = null;
        switch (encoding) {
            case AudioFormat.ENCODING_LINEAR_PCM:
                decoder = new PCMAudioDecoder();
                break;
            //...
            case AudioFormat.NOT_SPECIFIED:    //log.debug("Cannot decide in decoder (no encoding specified)");
                break; //method will return null
            default:
                throw new Exception("Unsupported encoding (no suitable decoder class)");
        }
        return decoder;
    }

    /**
     * @param audiostream to decode
     * @param sampleIndex in interval [0, numSamples[
     * @param channel     numbered 0, 1, ...
     * @return the wave amplitude at/for this sample as a double precision integer in the [-(2^(bitsPerSample-1)); 2^(bitsPerSample-1)-1] interval
     */
    public abstract long decodeSampleInteger(AudioStream audioStream, int sampleIndex, int channel);

    public long[] decodeSamplesInteger(AudioStream audioStream, int start, int end, int channel) {
        if (end < start)
            throw new IllegalArgumentException("Invalid sample range: end before start");
        long[] samples = new long[end - start + 1];
        for (int i = 0; i < samples.length; i++)
            samples[i] = decodeSampleInteger(audioStream, start + i, channel);
        return samples;
    }

    /**
     * @param audiostream to decode
     * @param sampleIndex
     * @param channel     numbered 0, 1, ...
     * @return the wave amplitude at/for this sample as a double precision floating point number in the interval [-1.0; 1.0]
     */
    public abstract double decodeSampleFloating(AudioStream audioStream, int sampleIndex, int channel);

    public double[] decodeSamplesFloating(AudioStream audioStream, int start, int end, int channel) {
        if (end < start)
            throw new IllegalArgumentException("Invalid sample range: end before start");
        double[] samples = new double[end - start + 1];
        for (int i = 0; i < samples.length; i++)
            samples[i] = decodeSampleFloating(audioStream, start + i, channel);
        return samples;
    }
}
