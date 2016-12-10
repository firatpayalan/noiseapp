package com.firat.noiseapp.model;

/**
 * Created by FIRAT on 11.12.2016.
 */
public class NoiseRequest {
    private double leq;
    private double latitude;
    private double longtitude;

    public double getLeq() {
        return leq;
    }

    public void setLeq(double leq) {
        this.leq = leq;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }
}
