package com.firat.noiseapp.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FIRAT on 06.12.2016.
 */
public class Noise {
    private List<Double> leqs;
    private List<Double> latitudes;
    private List<Double> longtitudes;

    private static Noise noise = null;
    public Noise()
    {
        leqs = new ArrayList<Double>();
        latitudes = new ArrayList<Double>();
        longtitudes = new ArrayList<Double>();
    }

    public static Noise getInstance()
    {
        if (noise == null)
            noise = new Noise();
        return noise;
    }
    public List<Double> getLeqs() {
        return leqs;
    }

    public void setLeqs(List<Double> leqs) {
        this.leqs = leqs;
    }

    public List<Double> getLatitudes() {
        return latitudes;
    }

    public void setLatitudes(List<Double> latitudes) {
        this.latitudes = latitudes;
    }

    public List<Double> getLongtitudes() {
        return longtitudes;
    }

    public void setLongtitudes(List<Double> longtitudes) {
        this.longtitudes = longtitudes;
    }
}
