package com.firat.noiseapp.model;

import java.util.Hashtable;

/**
 * Created by FIRAT on 10.12.2016.
 */
public class Filter {

    //Predefined filters
    public static final String FILTER_TYPE_A_WEIGHTING = "A";

    //STATICS--------------------------------------------------------
    public static final String FILTER_TYPE_C_WEIGHTING = "C";
    static final Hashtable<Integer, Filter> AweightingFilters = new Hashtable<Integer, Filter>();

    static {
        /*
         * A-Weighting coefficients were computed with MatLab using the "adsgn" function from
		 * the "octave" toolbox by Christophe Couvreur (Faculte Polytechnique de Mons):
		 * 		http://www.mathworks.com/matlabcentral/fileexchange/69
		 * Alternatively they can also be computed with Python (with numpy and scipy libraries)
		 * using this script (which is a port of the MatLab function mentioned above):
		 * 		https://gist.github.com/148112
		 */
        //8000 Hz
        AweightingFilters.put(new Integer(8000),
                new Filter(FILTER_TYPE_A_WEIGHTING,
                        8000,
                        new double[]{1.0d, -2.1284671930091217D,
                                0.29486689801012067D, 1.8241838307350515D,
                                -0.80566289431197835D, -0.39474979828429368D,
                                0.20985485460803321D},
                        new double[]{0.63062094682387282D, -1.2612418936477434D,
                                -0.63062094682387637D, 2.5224837872954899D,
                                -0.6306209468238686D, -1.2612418936477467D,
                                0.63062094682387237D}));
        //16000 Hz
        AweightingFilters.put(new Integer(16000),
                new Filter(FILTER_TYPE_A_WEIGHTING,
                        16000,
                        new double[]{1.0d, -2.867832572992166100D,
                                2.221144410202319500D, 0.455268334788656860D,
                                -0.983386863616282910D, 0.055929941424134225D,
                                0.118878103828561270D},
                        new double[]{0.53148982982355708D, -1.0629796596471122D,
                                -0.53148982982356319D, 2.1259593192942332D,
                                -0.53148982982355686D, -1.0629796596471166D,
                                0.53148982982355797D}));
        //22050 Hz
        AweightingFilters.put(new Integer(22050),
                new Filter(FILTER_TYPE_A_WEIGHTING,
                        22050,
                        new double[]{1.0d, -3.2290788052250736D,
                                3.3544948812360302D, -0.73178436806573255D,
                                -0.6271627581807262D, 0.17721420050208803D,
                                0.056317166973834924D},
                        new double[]{0.44929985042991927D, -0.89859970085984164D,
                                -0.4492998504299115D, 1.7971994017196726D,
                                -0.44929985042992043D, -0.89859970085983754D,
                                0.44929985042991943D}));
        //24000 Hz
        AweightingFilters.put(new Integer(24000),
                new Filter(FILTER_TYPE_A_WEIGHTING,
                        24000,
                        new double[]{1.0d, -3.3259960042,
                                3.6771610793, -1.1064760768,
                                -0.4726706735, 0.1861941760,
                                0.0417877134},
                        new double[]{0.4256263893, -0.8512527786,
                                -0.4256263893, 1.7025055572,
                                -0.4256263893, -0.8512527786,
                                0.4256263893}));
        //32000 Hz
        AweightingFilters.put(new Integer(32000),
                new Filter(FILTER_TYPE_A_WEIGHTING,
                        32000,
                        new double[]{1.0d, -3.6564460432,
                                4.8314684507, -2.5575974966,
                                0.2533680394, 0.1224430322,
                                0.0067640722},
                        new double[]{0.3434583387, -0.6869166774,
                                -0.3434583387, 1.3738333547,
                                -0.3434583387, -0.6869166774,
                                0.3434583387}));
        //44100 Hz
        AweightingFilters.put(new Integer(44100),
                new Filter(FILTER_TYPE_A_WEIGHTING,
                        44100,
                        new double[]{1.0d, -4.0195761811158315D,
                                6.1894064429206921D, -4.4531989035441155D,
                                1.4208429496218764D, -0.14182547383030436D,
                                0.0043511772334950787D},
                        new double[]{0.2557411252042574D, -0.51148225040851436D,
                                -0.25574112520425807D, 1.0229645008170318D,
                                -0.25574112520425918D, -0.51148225040851414D,
                                0.25574112520425729D}));
        //48000 Hz
        AweightingFilters.put(new Integer(48000),
                new Filter(FILTER_TYPE_A_WEIGHTING,
                        48000,
                        new double[]{1.0d, -4.113043408775872D,
                                6.5531217526550503D, -4.9908492941633842D,
                                1.7857373029375754D, -0.24619059531948789D,
                                0.011224250033231334D},
                        new double[]{0.2343017922995132D, -0.4686035845990264D,
                                -0.23430179229951431D, 0.9372071691980528D,
                                -0.23430179229951364D, -0.46860358459902524D,
                                0.23430179229951273D}));

		/*
         * TODO predefine C-weighting filters
		 * C-Weighting coefficients can be computed with MatLab using the "cdsgn" script from
		 * the "octave" library by Christophe Couvreur (Faculte Polytechnique de Mons):
		 * 		http://www.mathworks.com/matlabcentral/fileexchange/69
		 */
    }

//    protected static Logger log = Logger.getInstance();
    //DYNAMICS-------------------------------------------------------
    private String type;
    private int sampleRate;
    private int order; //always = aCoef.length - 1 = bCoef.length (but we keep it in a variable for convenience)
    private double[] aCoef;
    private double[] bCoef;
    private boolean keepConditions;
    private double[] conditions;

    private Filter(String type, int sampleRate, double[] Acoef, double[] Bcoef) {
        this(type, sampleRate, Acoef, Bcoef, false); //not keeping conditions is default
    }

    private Filter(String type, int sampleRate, double[] Acoef, double[] Bcoef, boolean keepConditions) {
        if (Acoef.length != Bcoef.length)
            throw new IllegalArgumentException("Mismatch in number of A and B coefficients");
        if (Acoef[0] != 1.0d)
            throw new IllegalArgumentException("Coefficients must be normalized (a_0 must be = 1");
        this.sampleRate = sampleRate;
        this.order = Acoef.length - 1; //= Bcoef.length - 1
        this.aCoef = Acoef;
        this.bCoef = Bcoef;
        this.keepConditions = keepConditions;
        conditions = new double[order]; //initialize conditions (all 0.0d)
    }

    static public Filter getFilter(String type, int sampleRate) {
        Filter f = null;
        if (type == FILTER_TYPE_A_WEIGHTING)
            f = (Filter) AweightingFilters.get(new Integer(sampleRate));
            //more later?
        else
            throw new IllegalArgumentException("Unknown filter type");
        if (f == null)
            throw new IllegalArgumentException("No " + type + (type.length() == 1 ? "-weighting" : "") + " filter defined for this sample rate (" + sampleRate + ")");
        return f;
    }

    /**
     * Describes the application of a filter of a certain order on an input signal of certain length
     * by writing out (on System.out) the difference equations which relate the output signal (y) to
     * the input signal (x).
     *
     * @param inputlength
     * @param order
     */
    public static void documentApply(int inputlength, int order) {
        String[] output = new String[inputlength];
        String[] conditions = new String[order];
        for (int i = 0; i < inputlength; i++) {
            String x_i = "x_" + i;
            String y_i = "(b_0*" + x_i + ") + " + (conditions[0] == null ? "0" : conditions[0]);
            //Store filtered sample:
            output[i] = y_i;
            //Adjust new conditions:
            for (int j = 0; j < order - 1; j++)
                conditions[j] = "(b_" + (j + 1) + "*" + x_i + ") " +
                        "- (a_" + (j + 1) + "*y_" + i + ") + "
                        + (conditions[j + 1] == null ? "0" : conditions[j + 1]);
            //Last condition:
            conditions[order - 1] = "(b_" + order + "*" + x_i + ") " +
                    "- (a_" + order + "*y_" + i + ")";
        }

        //Show formulas:
        System.out.println("Output:");
        for (int i = 0; i < output.length; i++) {
        }
        for (int j = 0; j < order; j++) {
        }
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * @return the keepConditions
     */
    public boolean isKeepConditions() {
        return keepConditions;
    }

    /**
     * @param keepConditions the keepConditions to set
     */
    public void setKeepConditions(boolean keepConditions) {
        this.keepConditions = keepConditions;
    }

    public double[] apply(double input[]) {
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            double x_i = input[i];
            //Filter sample:
            double y_i = x_i * bCoef[0] + conditions[0];
            //Store filtered sample:
            output[i] = y_i;
            //Adjust conditions:
            // all but the last condition:
            for (int j = 0; j < order - 1; j++)
                conditions[j] = x_i * bCoef[j + 1]
                        - y_i * aCoef[j + 1]
                        + conditions[j + 1];
            // last condition:
            conditions[order - 1] = x_i * bCoef[order]
                    - y_i * aCoef[order];
        }
        if (!keepConditions)
            conditions = new double[order]; //reset conditions
        return output;
    }

    public double[] apply(double[] input, double[] conditions) {
        if (conditions.length != order)
            throw new IllegalArgumentException("Initial conditions array length must match filter order");
        this.conditions = conditions;
        return apply(input);
    }

    public String toString() {
        return type + (type.length() == 1 ? "-weighting" : "") + " filter for samplerate of " + sampleRate + " Hz";
    }
}
