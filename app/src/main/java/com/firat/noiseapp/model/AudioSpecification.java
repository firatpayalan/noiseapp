package com.firat.noiseapp.model;

import android.util.Log;

import com.firat.noiseapp.util.AudioFormat;
import com.firat.noiseapp.util.StringUtils;

/**
 * Created by FIRAT on 10.12.2016.
 */
public class AudioSpecification {

    /**
     *
     */
    private static final long serialVersionUID = -7618472296840770722L;

    //STATIC FIELDS:
    protected static char serialisationSeparator = '|';

    //DYNAMIC FIELDS:
    //Specifications for capture string:
    protected int encoding = AudioFormat.NOT_SPECIFIED;
    protected int sampleRate = AudioFormat.NOT_SPECIFIED; //in Hz
    protected int bitsPerSample = AudioFormat.NOT_SPECIFIED;
    protected int endianness = AudioFormat.NOT_SPECIFIED;
    protected int signed = AudioFormat.NOT_SPECIFIED;
    protected int numChannels = AudioFormat.NOT_SPECIFIED; //1 = mono; 2 = stereo, ...
    //Result properties of recorded audio:
    protected int resultContainerType = AudioFormat.NOT_SPECIFIED; //e.g. WAVE, AIFF, ...
    protected Class<?> resultContainerClass = null; //e.g. net.noisetube.audio.format.WAVEAudioStream, ...
    protected int resultEncoding = AudioFormat.NOT_SPECIFIED; //e.g. "LINEAR_PCM", ...
    protected int resultSampleRate = AudioFormat.NOT_SPECIFIED;
    protected int resultBitsPerSample = AudioFormat.NOT_SPECIFIED;
    protected int resultEndianness = AudioFormat.NOT_SPECIFIED;
    protected int resultSigned = AudioFormat.NOT_SPECIFIED;
    protected int resultNumChannels = AudioFormat.NOT_SPECIFIED;
    protected boolean resultKnown = false;
    //Decoder for audiostreams that adhere to this audioSpecs:
//    protected AudioDecoder decoder = null; //can be chosen based on encoding or resultEncoding (see getDecoder())
//    private Logger log = Logger.getInstance();

    public AudioSpecification() {
        //everything unspecified
    }

    /**
     * @param encoding
     * @param sampleRate
     * @param bitsPerSample
     */
    public AudioSpecification(int encoding, int sampleRate, int bitsPerSample, int numChannels) {
        initialize(encoding, sampleRate, bitsPerSample, numChannels);
    }

    public static AudioSpecification deserialise(String serialisedAudioSpec) {
        return deserialise(serialisedAudioSpec, new AudioSpecification()); //platform independant AudioSpec
    }

    protected static AudioSpecification deserialise(String serialisedAudioSpec, AudioSpecification newAudioSpec) //TODO endianess, signed
    {    //Format: 		encoding|sampleRate|bitsPerSample|channels[resultContainerType|resultEncoding|resultSampleRate|resultBitsPerSample|resultNumChannels]
        //parts[]-idx:		0		1			2			3				4					5				6						7						8
        String[] parts = StringUtils.split(serialisedAudioSpec, serialisationSeparator);
        if (parts == null || parts.length < 4)
            throw new IllegalArgumentException("Not a valid serialised AudioSpecification: " + serialisedAudioSpec);
        if (parts[0] != null && parts[0].length() > 0)
            newAudioSpec.encoding = Integer.parseInt(parts[0]);
        if (parts[1] != null && parts[1].length() > 0)
            newAudioSpec.sampleRate = Integer.parseInt(parts[1]);
        if (parts[2] != null && parts[2].length() > 0)
            newAudioSpec.bitsPerSample = Integer.parseInt(parts[2]);
        if (parts[3] != null && parts[3].length() > 0)
            newAudioSpec.numChannels = Integer.parseInt(parts[3]);
        if (parts.length >= 9) {
            if (parts[4] != null && parts[4].length() > 0)
                newAudioSpec.resultContainerType = Integer.parseInt(parts[4]);
            if (parts[5] != null && parts[5].length() > 0)
                newAudioSpec.resultEncoding = Integer.parseInt(parts[5]);
            if (parts[6] != null && parts[6].length() > 0)
                newAudioSpec.resultSampleRate = Integer.parseInt(parts[6]);
            if (parts[7] != null && parts[7].length() > 0)
                newAudioSpec.resultBitsPerSample = Integer.parseInt(parts[7]);
            if (parts[8] != null && parts[8].length() > 0)
                newAudioSpec.resultNumChannels = Integer.parseInt(parts[8]);
            newAudioSpec.resultKnown = true;
        }
        if (parts.length > 9) {
            String[] additionalParams = new String[parts.length - 9];
            for (int p = 9; p < parts.length; p++)
                additionalParams[p - 9] = parts[p];
            newAudioSpec.deserialiseAdditionalParameters(additionalParams);
        }
        return newAudioSpec;
    }

    protected void initialize(int encoding, int sampleRate, int bitsPerSample, int numChannels) {
        if (sampleRate != AudioFormat.NOT_SPECIFIED && sampleRate <= 0)
            throw new IllegalArgumentException("sampleRate cannot be <= 0");
        if (bitsPerSample != AudioFormat.NOT_SPECIFIED && (bitsPerSample <= 0 || bitsPerSample % 8 != 0))
            throw new IllegalArgumentException("bitsPerSample needs to be > 0 and a multiple of 8");
        if (numChannels != AudioFormat.NOT_SPECIFIED && numChannels < 1)
            throw new IllegalArgumentException("numChannels needs to be > 0");
        this.encoding = encoding;
        this.sampleRate = sampleRate;
        this.bitsPerSample = bitsPerSample;
        this.numChannels = numChannels;
    }

    public String serialise() //TODO endianess, signed
    {    //Format: 		encoding|sampleRate|bitsPerSample|channels[resultContainerType|resultEncoding|resultSampleRate|resultBitsPerSample|resultNumChannels]
        //parts[]-idx:		0		1			2			3				4					5				6						7						8
        String[] parts = new String[resultKnown ? 9 : 4];
        parts[0] = (encoding == AudioFormat.NOT_SPECIFIED ? "" : Integer.toString(encoding));
        parts[1] = (sampleRate == AudioFormat.NOT_SPECIFIED ? "" : Integer.toString(sampleRate));
        parts[2] = (bitsPerSample == AudioFormat.NOT_SPECIFIED ? "" : Integer.toString(bitsPerSample));
        parts[3] = (numChannels == AudioFormat.NOT_SPECIFIED ? "" : Integer.toString(numChannels));
        if (resultKnown) {
            parts[4] = (resultContainerType == AudioFormat.NOT_SPECIFIED ? "" : Integer.toString(resultContainerType));
            parts[5] = (resultEncoding == AudioFormat.NOT_SPECIFIED ? "" : Integer.toString(resultEncoding));
            parts[6] = (resultSampleRate == AudioFormat.NOT_SPECIFIED ? "" : Integer.toString(resultSampleRate));
            parts[7] = (resultBitsPerSample == AudioFormat.NOT_SPECIFIED ? "" : Integer.toString(resultBitsPerSample));
            parts[8] = (resultNumChannels == AudioFormat.NOT_SPECIFIED ? "" : Integer.toString(resultNumChannels));
        }
        return StringUtils.concat(parts, serialisationSeparator);
    }

    protected void deserialiseAdditionalParameters(String[] params) {
        //do nothing (but can be overridden by subclasses
    }

    public String getPrescriptionString() {
        return StringUtils.concat(new String[]{(encoding == AudioFormat.NOT_SPECIFIED ? "?" : AudioFormat.getEncodingName(encoding)),
                        (sampleRate == AudioFormat.NOT_SPECIFIED ? "?" : Integer.toString(sampleRate)),
                        (bitsPerSample == AudioFormat.NOT_SPECIFIED ? "?" : Integer.toString(bitsPerSample)),
                        (numChannels == AudioFormat.NOT_SPECIFIED ? "?" : Integer.toString(numChannels))},
                ',');
    }

    public String getDescriptionString() {
        if (resultKnown)
            return StringUtils.concat(new String[]{(resultContainerType == AudioFormat.NOT_SPECIFIED ? "?" : AudioFormat.getContainerName(resultContainerType)),
                            (resultEncoding == AudioFormat.NOT_SPECIFIED ? "?" : AudioFormat.getEncodingName(resultEncoding)),
                            (resultSampleRate == AudioFormat.NOT_SPECIFIED ? "?" : Integer.toString(resultSampleRate)),
                            (resultBitsPerSample == AudioFormat.NOT_SPECIFIED ? "?" : Integer.toString(resultBitsPerSample)),
                            (resultNumChannels == AudioFormat.NOT_SPECIFIED ? "?" : Integer.toString(resultNumChannels))},
                    ',');
        else
            return "untested";
    }

    public String toVerboseString() {
        return getPrescriptionString() + "~" + getDescriptionString();
    }

    public String toString() {
        return (resultKnown ? getDescriptionString() : getPrescriptionString());
    }

    /**
     * @return the encoding
     */
    public int getEncoding() {
        return encoding;
    }

    public boolean isEncodingSet() {
        return encoding != AudioFormat.NOT_SPECIFIED;
    }

    /**
     * @return the sampleRate
     */
    public int getSampleRate() {
        return sampleRate;
    }

    public boolean isSampleRateSet() {
        return sampleRate != AudioFormat.NOT_SPECIFIED;
    }

    /**
     * @return the bitsPerSample
     */
    public int getBitsPerSample() {
        return bitsPerSample;
    }

    public boolean isBitsPerSampleSet() {
        return bitsPerSample != AudioFormat.NOT_SPECIFIED;
    }

    /**
     * @return the numChannels
     */
    public int getNumChannels() {
        return numChannels;
    }

    public boolean isNumChannelsSet() {
        return numChannels != AudioFormat.NOT_SPECIFIED;
    }

    /**
     * @return the endianness
     */
    public int getEndianness() {
        return endianness;
    }

    public boolean isEndiannessSet() {
        return endianness != AudioFormat.NOT_SPECIFIED;
    }

    /**
     * @return the signed
     */
    public int getSigned() {
        return signed;
    }

    public boolean isSignedSet() {
        return signed != AudioFormat.NOT_SPECIFIED;
    }

    /**
     * Used to calculate how many bytes are needed for a certain amount of time
     *
     * @return the number of bytes needed to represent one second
     */
    public int getByteRate() {
        return sampleRate * numChannels * bitsPerSample / 8;
    }

    /**
     * @return the resultKnown
     */
    public boolean isResultKnown() {
        return resultKnown;
    }

    /**
     * @return the resultContainerType
     */
    public int getResultContainerType() {
        return resultContainerType;
    }

    /**
     * @return the resultContainerClass
     */
    public Class<?> getResultContainerClass() {
        return resultContainerClass;
    }

    /**
     * @return the resultEncoding
     */
    public int getResultEncoding() {
        return resultEncoding;
    }

    public boolean isResultEncodingSet() {
        return resultEncoding != AudioFormat.NOT_SPECIFIED;
    }


    /**
     * @return the decoder
     */
    public AudioDecoder getDecoder() {
        return new PCMAudioDecoder();
    }

    /**
     * @return the resultSampleRate
     */
    public int getResultSampleRate() {
        return resultSampleRate;
    }

    public boolean isResultSampleRateSet() {
        return resultSampleRate != AudioFormat.NOT_SPECIFIED;
    }

    /**
     * @return the resultBitsPerSample
     */
    public int getResultBitsPerSample() {
        return resultBitsPerSample;
    }

    public boolean isResultBitsPerSampleSet() {
        return resultBitsPerSample != AudioFormat.NOT_SPECIFIED;
    }

    /**
     * @return the resultNumChannels
     */
    public int getResultNumChannels() {
        return resultNumChannels;
    }

    public boolean isResultNumChannelsSet() {
        return resultNumChannels != AudioFormat.NOT_SPECIFIED;
    }

    /**
     * @return the resultEndianness
     */
    public int getResultEndianness() {
        return resultEndianness;
    }

    public boolean isResultEndiannessSet() {
        return resultEndianness != AudioFormat.NOT_SPECIFIED;
    }

    /**
     * @return the resultSigned
     */
    public int getResultSigned() {
        return resultSigned;
    }

    public boolean isResultSignedSet() {
        return resultSigned != AudioFormat.NOT_SPECIFIED;
    }

    /**
     * Used to calculate how many bytes are needed for a certain amount of time
     *
     * @return the number of bytes needed to represent one second
     */
    public int getResultByteRate() {
        if (isResultSampleRateSet() && isResultNumChannelsSet() && isResultBitsPerSampleSet())
            return resultSampleRate * resultNumChannels * resultBitsPerSample / 8;
        else
            return AudioFormat.NOT_SPECIFIED;
    }

    public void inferResultsFrom(AudioStream audioStream) {
//        this.resultContainerType = audioStream.getContainerType();
//        this.resultContainerClass = audioStream.getClass();
//        if (audioStream.getEncoding() != AudioFormat.NOT_SPECIFIED) {
//            this.resultEncoding = audioStream.getEncoding(); //
//            if (resultEncoding != encoding)
//                this.decoder = null; //!!! in case it was set based on the excepted encoding, a proper decoder (based on resultEncoding) will be set when getResultDecorder() is called
//        }
//        if (audioStream.getSampleRate() != AudioFormat.NOT_SPECIFIED)
//            this.resultSampleRate = (int) audioStream.getSampleRate();
//        if (audioStream.getBitsPerSample() != AudioFormat.NOT_SPECIFIED)
//            this.resultBitsPerSample = audioStream.getBitsPerSample();
//        if (audioStream.getNumChannels() != AudioFormat.NOT_SPECIFIED)
//            this.resultNumChannels = audioStream.getNumChannels();
//        resultKnown = true; //!!!
    }

    public boolean equals(Object o) {
        if (!(o instanceof AudioSpecification))
            return false;
        AudioSpecification aSpec = (AudioSpecification) o;
        if (resultKnown) {
            if (aSpec.resultKnown) {
                if (resultContainerType != AudioFormat.NOT_SPECIFIED) {
                    if (aSpec.resultContainerType == AudioFormat.NOT_SPECIFIED)
                        return false;
                    else if (resultContainerType != aSpec.resultContainerType)
                        return false;
                } else {
                    if (aSpec.resultContainerType != AudioFormat.NOT_SPECIFIED)
                        return false;
                }
                if (resultEncoding != AudioFormat.NOT_SPECIFIED) {
                    if (aSpec.resultEncoding == AudioFormat.NOT_SPECIFIED)
                        return false;
                    else if (resultEncoding != aSpec.resultEncoding)
                        return false;
                } else {
                    if (aSpec.resultEncoding != AudioFormat.NOT_SPECIFIED)
                        return false;
                }
                if (resultSampleRate != AudioFormat.NOT_SPECIFIED) {
                    if (aSpec.resultSampleRate == AudioFormat.NOT_SPECIFIED)
                        return false;
                    else if (resultSampleRate != aSpec.resultSampleRate)
                        return false;
                } else {
                    if (aSpec.resultSampleRate != AudioFormat.NOT_SPECIFIED)
                        return false;
                }
                if (resultBitsPerSample != AudioFormat.NOT_SPECIFIED) {
                    if (aSpec.resultBitsPerSample == AudioFormat.NOT_SPECIFIED)
                        return false;
                    else if (resultBitsPerSample != aSpec.resultBitsPerSample)
                        return false;
                } else {
                    if (aSpec.resultBitsPerSample != AudioFormat.NOT_SPECIFIED)
                        return false;
                }
                if (resultNumChannels != AudioFormat.NOT_SPECIFIED) {
                    if (aSpec.resultNumChannels == AudioFormat.NOT_SPECIFIED)
                        return false;
                    else if (resultNumChannels != aSpec.resultNumChannels)
                        return false;
                } else {
                    if (aSpec.resultNumChannels != AudioFormat.NOT_SPECIFIED)
                        return false;
                }
            } else
                return false;
        } else {
            if (aSpec.resultKnown)
                return false;
            else {
                if (encoding != AudioFormat.NOT_SPECIFIED) {
                    if (aSpec.encoding == AudioFormat.NOT_SPECIFIED)
                        return false;
                    else if (encoding != aSpec.encoding)
                        return false;
                } else {
                    if (aSpec.encoding != AudioFormat.NOT_SPECIFIED)
                        return false;
                }
                if (sampleRate != AudioFormat.NOT_SPECIFIED) {
                    if (aSpec.sampleRate == AudioFormat.NOT_SPECIFIED)
                        return false;
                    else if (sampleRate != aSpec.sampleRate)
                        return false;
                } else {
                    if (aSpec.sampleRate != AudioFormat.NOT_SPECIFIED)
                        return false;
                }
                if (bitsPerSample != AudioFormat.NOT_SPECIFIED) {
                    if (aSpec.bitsPerSample == AudioFormat.NOT_SPECIFIED)
                        return false;
                    else if (bitsPerSample != aSpec.bitsPerSample)
                        return false;
                } else {
                    if (aSpec.bitsPerSample != AudioFormat.NOT_SPECIFIED)
                        return false;
                }
                if (numChannels != AudioFormat.NOT_SPECIFIED) {
                    if (aSpec.numChannels == AudioFormat.NOT_SPECIFIED)
                        return false;
                    else if (numChannels != aSpec.numChannels)
                        return false;
                } else {
                    if (aSpec.numChannels != AudioFormat.NOT_SPECIFIED)
                        return false;
                }
            }
        }
        return true;
    }

    public int compareTo(AudioSpecification o) {
        if (this.equals(o))
            return 0;
        if (isResultKnown() && o.isResultKnown()) {
            if (resultContainerType != AudioFormat.NOT_SPECIFIED) //container: favour WAVE over others
            {
                if (o.resultContainerType != AudioFormat.NOT_SPECIFIED) {
                    if (resultContainerType == AudioFormat.CONTAINER_WAVE && o.resultContainerType != AudioFormat.CONTAINER_WAVE)
                        return 1; //this is WAVE, the other is not
                    if (resultContainerType != AudioFormat.CONTAINER_WAVE && o.resultContainerType == AudioFormat.CONTAINER_WAVE)
                        return -1; //this is not WAVE, the other is
                }
            }
            if (resultEncoding != AudioFormat.NOT_SPECIFIED) //encoding: favour PCM over others
            {
                if (o.resultEncoding != AudioFormat.NOT_SPECIFIED) {
                    if (resultEncoding == AudioFormat.ENCODING_LINEAR_PCM && o.resultEncoding != AudioFormat.ENCODING_LINEAR_PCM)
                        return 1; //this is PCM, the other is not
                    if (resultEncoding != AudioFormat.ENCODING_LINEAR_PCM && o.resultEncoding == AudioFormat.ENCODING_LINEAR_PCM)
                        return -1; //this is not PCM, the other is
                }
            }
            if (resultSampleRate != AudioFormat.NOT_SPECIFIED) //sampleRate: higher = better
            {
                if (o.resultSampleRate != AudioFormat.NOT_SPECIFIED) {
                    if (resultSampleRate > o.resultSampleRate)
                        return 1;
                    if (resultSampleRate < o.resultSampleRate)
                        return -1;
                }
            }
            if (resultBitsPerSample != AudioFormat.NOT_SPECIFIED) //bitsPerSample: higher = better
            {
                if (o.resultBitsPerSample != AudioFormat.NOT_SPECIFIED) {
                    if (resultBitsPerSample > o.resultBitsPerSample)
                        return 1;
                    if (resultBitsPerSample < o.resultBitsPerSample)
                        return -1;
                }
            }
            if (resultNumChannels != AudioFormat.NOT_SPECIFIED)//channels: lower = better (we want Mono)
            {
                if (o.resultNumChannels != AudioFormat.NOT_SPECIFIED) {
                    if (resultNumChannels < o.resultNumChannels)
                        return 1;
                    if (resultNumChannels > o.resultNumChannels)
                        return -1;
                }
            }
        } else {
            if (encoding != AudioFormat.NOT_SPECIFIED) //encoding: favour wav/pcm over others
            {
                if (o.encoding != AudioFormat.NOT_SPECIFIED) {
                    if (encoding == AudioFormat.ENCODING_LINEAR_PCM && o.encoding != AudioFormat.ENCODING_LINEAR_PCM)
                        return 1; //this is WAV/PCM, the other is not
                    if (encoding != AudioFormat.ENCODING_LINEAR_PCM && o.encoding == AudioFormat.ENCODING_LINEAR_PCM)
                        return -1; //this is not WAV/PCM, the other is
                }
            }
            if (sampleRate != AudioFormat.NOT_SPECIFIED) //sampleRate: higher = better
            {
                if (o.sampleRate != AudioFormat.NOT_SPECIFIED) {
                    if (sampleRate > o.sampleRate)
                        return 1;
                    if (sampleRate < o.sampleRate)
                        return -1;
                }
            }
            if (bitsPerSample != AudioFormat.NOT_SPECIFIED) //bitsPerSample: higher = better
            {
                if (o.bitsPerSample != AudioFormat.NOT_SPECIFIED) {
                    if (bitsPerSample > o.bitsPerSample)
                        return 1;
                    if (bitsPerSample < o.bitsPerSample)
                        return -1;
                }
            }
            if (numChannels != AudioFormat.NOT_SPECIFIED)//channels: lower = better (we want Mono)
            {
                if (o.numChannels != AudioFormat.NOT_SPECIFIED) {
                    if (numChannels < o.numChannels)
                        return 1;
                    if (numChannels > o.numChannels)
                        return -1;
                }
            }
        }
        return 0;
    }

}
