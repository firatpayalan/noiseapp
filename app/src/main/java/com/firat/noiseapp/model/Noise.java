package com.firat.noiseapp.model;

import java.io.File;

/**
 * Created by FIRAT on 06.12.2016.
 */
public class Noise {
    //http://stackoverflow.com/questions/4126625/how-do-i-send-a-file-in-android-from-a-mobile-device-to-server-using-http
    private File soundFile;
    private String gpsCoordinates;

    public File getSoundFile() {
        return soundFile;
    }

    public void setSoundFile(File soundFile) {
        this.soundFile = soundFile;
    }

    public String getGpsCoordinates() {
        return gpsCoordinates;
    }

    public void setGpsCoordinates(String gpsCoordinates) {
        this.gpsCoordinates = gpsCoordinates;
    }
}
