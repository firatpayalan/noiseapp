package com.firat.noiseapp.util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by FIRAT on 10.12.2016.
 */
public class StringUtils {

    //TODO check usage
    static public String addTabsFront(String string, int tabs) {
        StringBuilder bff = new StringBuilder();
        for (int t = 0; t < tabs; t++)
            bff.append("\t");
        bff.append(string);
        return bff.toString();
    }

    static public String replace(String toSearch, char toFind,
                                 String replaceWith) {
        return toSearch.replace(new String(new char[]{toFind}), replaceWith);
    }

    static public String replace(String toSearch, String toFind,
                                 char replaceWith) {
        return toSearch.replace(toFind, new String(new char[]{replaceWith}));
    }

    static public String left(String string, int substringLength) {
        if (substringLength < 0)
            throw new IllegalArgumentException(
                    "Substring length cannot be negative!");
        return string.substring(0, substringLength);
    }

    static public String right(String string, int substringLength) {
        if (substringLength < 0)
            throw new IllegalArgumentException(
                    "Substring length cannot be negative!");
        return string.substring(string.length() - substringLength,
                string.length());
    }

    static public String padWithLeadingZeros(String string, int desiredLength) {
        StringBuilder bff = new StringBuilder(string);
        while (bff.length() < desiredLength)
            bff.insert(0, '0');
        return bff.toString();
    }

    static public String cutOrExtendAtTail(String string, int desiredLength,
                                           char filler) {
        if (string.length() > desiredLength)
            return left(string, desiredLength);
        else if (string.length() < desiredLength) {
            StringBuilder bff = new StringBuilder(string);
            for (int i = 0; i < desiredLength - string.length(); i++)
                bff.append(filler);
            return bff.toString();
        } else
            return string;
    }

    static public String removeWhiteSpace(String string) {
        string = replace(string, '\n', "");
        string = replace(string, '\r', "");
        string = replace(string, '\t', "");
        string = replace(string, ' ', "");
        return string;
    }

    static public String contractToCamelCase(String string) {
        StringBuilder bff = new StringBuilder(string.length());
        boolean previousWasSpace = false;
        for (int i = 0; i < string.length(); i++) {
            char currentChar = string.charAt(i);
            if (currentChar == ' ') {
                previousWasSpace = true;
            } else {
                if (previousWasSpace)
                    bff.append(Character.toUpperCase(currentChar));
                else
                    bff.append(currentChar);
                previousWasSpace = false;
            }
        }
        return bff.toString();
    }

    static public String removeWhiteSpaceAndContractToCamelCase(String string) {
        string = replace(string, '\n', "");
        string = replace(string, '\r', "");
        string = replace(string, '\t', "");
        return contractToCamelCase(string);
    }

    public static String concat(String[] parts, char separator) {
        if (parts == null)
            throw new NullPointerException("Parts array cannot be null");
        StringBuilder bff = new StringBuilder();
        String sep = new Character(separator).toString();
        for (int p = 0; p < parts.length; p++)
            bff.append(((p > 0) ? sep : "")).append(parts[p]);
        return bff.toString();
    }

    public static String[] split(String string, char separatorChar) {
        return split(string, new char[]{separatorChar});
    }

    public static String[] split(String string, char[] separatorChars) {
        if (string == null || string.equals("")) {
            return new String[]{string};
        }

        StringBuilder regex = new StringBuilder(separatorChars.length);
        final char OR = '|';
        final int up = separatorChars.length - 1;
        for (int s = 0; s < up; s++) {
            regex.append(separatorChars[s]).append(OR);
        }
        regex.append(separatorChars[up]); // last delimiter

        return string.split(regex.toString());
    }

    /**
     * Converts a long timestamp to a string in the following format:</br>
     * YYYY[dateSeparator
     * ]MM[dateSeparator]DD[dateTimeSeparator]hh[timeSeparator]
     * mm[timeSeparator]ss
     *
     * @param timeStamp
     * @param dateSeparator
     * @param timeSeparator
     * @param dateTimeSeparator
     * @return A String containing the formatted timestamp
     */
    static public String formatDateTime(long timeStamp, String dateSeparator,
                                        String timeSeparator, String dateTimeSeparator) {
        if (dateTimeSeparator == null)
            dateTimeSeparator = "";
        // Date (YYYY[dateSeparator]MM[dateSeparator]DD)
        StringBuilder bff = new StringBuilder(formatDate(timeStamp,
                dateSeparator));
        // [dateTimeSeparator]
        bff.append(dateTimeSeparator);
        // Time (hh[timeSeparator]mm[timeSeparator]ss)
        bff.append(formatTime(timeStamp, timeSeparator));
        // return the result
        return bff.toString();
    }

    /**
     * Converts a long timestamp to a string in the following format:</br>
     * YYYY[dateSeparator]MM[dateSeparator]DD[dateTimeSeparator]
     *
     * @param timeStamp
     * @param dateSeparator
     * @return A String containing the formatted timestamp
     */
    static public String formatDate(long timeStamp, String dateSeparator) {
        if (dateSeparator == null)
            dateSeparator = "";
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault()); // use system time zone
        cal.setTime(new Date(timeStamp)); // set the time
        // Format the string:
        StringBuilder bff = new StringBuilder();
        // Date (YYYY[dateSeparator]MM[dateSeparator]DD)
        bff.append(StringUtils.padWithLeadingZeros(
                String.valueOf(cal.get(Calendar.YEAR)), 4)
                + dateSeparator);
        bff.append(StringUtils.padWithLeadingZeros(
                String.valueOf(cal.get(Calendar.MONTH) + 1), 2)
                + dateSeparator);
        bff.append(StringUtils.padWithLeadingZeros(
                String.valueOf(cal.get(Calendar.DAY_OF_MONTH)), 2));
        // return the result
        return bff.toString();
    }

    /**
     * Converts a long timestamp to a string in the following format:</br>
     * hh[timeSeparator]mm[timeSeparator]ss
     *
     * @param timeStamp
     * @param timeSeparator
     * @return A String containing the formatted timestamp
     */
    static public String formatTime(long timeStamp, String timeSeparator) {
        if (timeSeparator == null)
            timeSeparator = "";
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault()); // use system time zone
        cal.setTime(new Date(timeStamp)); // set the time
        // Format the string:
        StringBuilder bff = new StringBuilder();
        // Time (hh[timeSeparator]mm[timeSeparator]ss)
        bff.append(StringUtils.padWithLeadingZeros(
                String.valueOf(cal.get(Calendar.HOUR_OF_DAY)), 2)
                + timeSeparator);
        bff.append(StringUtils.padWithLeadingZeros(
                String.valueOf(cal.get(Calendar.MINUTE)), 2)
                + timeSeparator);
        bff.append(StringUtils.padWithLeadingZeros(
                String.valueOf(cal.get(Calendar.SECOND)), 2));
        // return the result
        return bff.toString();
    }

    public static String formatTimeSpanDHMMS(long milliseconds) {
        // Days
        long days = milliseconds / 86400000;
        // Hours
        long hours = (milliseconds % 86400000) / 3600000;
        // Minutes
        long minutes = ((milliseconds % 86400000) % 3600000) / 60000;
        // Seconds.millis
        float seconds = (((milliseconds % 86400000) % 3600000) % 60000) / 1000;
        return "" + ((days > 0) ? (days + "d ") : "")
                + ((hours > 0) ? (hours + "h ") : "")
                + ((minutes > 0) ? (minutes + "m ") : "") + seconds + "s";
    }

    public static String formatTimeSpanColons(long milliseconds) {
        final long hours = milliseconds / 3600000;
        final long minutes = (milliseconds % 3600000) / 60000;
        final long seconds = ((milliseconds % 3600000) % 60000) / 1000;
        StringBuilder sb = new StringBuilder();
        sb.append(hours < 10 ? '0' : "").append(hours).append(':');
        sb.append(minutes < 10 ? '0' : "").append(minutes).append(':');
        sb.append(seconds < 10 ? '0' : "").append(seconds);
        return sb.toString();
    }

    public static String formatDistance(float distanceInMeter, int powerOfTen) {
        double roundTo = MathNT.powerOfTen(powerOfTen);
        double distance;
        String unit;
        if (distanceInMeter > 1000d) { // kilometer
            distance = distanceInMeter / 1000d;
            unit = " km";
        } else { // meter
            distance = distanceInMeter;
            unit = " m";
        }
        double rounded = MathNT.round(distance / roundTo) * roundTo;
        String number = Double.toString(rounded);
        if (powerOfTen < 0)
            number = StringUtils.cutOrExtendAtTail(
                    number,
                    Integer.toString((int) rounded).length()
                            + Math.abs(powerOfTen) + 1, '0');
        return number + unit;
    }

    public static String litToString(List<String> list,
                                     String separator) {
        if (list != null && !list.isEmpty()) {
            StringBuilder bff = new StringBuilder();
            final int up = list.size() - 1;

            for (int i = 0; i < up; i++) {
                bff.append(list.get(i)).append(separator);
            }

            bff.append(list.get(up));

            return bff.toString();
        }
        return "";
    }

    public static String arrayToString(String[] array, String separator) {
        if (array != null && array.length > 0) {
            final int up = array.length - 1;
            StringBuilder bff = new StringBuilder();
            for (int i = 0; i < up; i++) {
                bff.append(array[i]).append(separator);
            }

            bff.append(array[up]);

            return bff.toString();
        }

        return "";
    }
}
