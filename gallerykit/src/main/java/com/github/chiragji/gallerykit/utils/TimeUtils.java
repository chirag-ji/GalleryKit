package com.github.chiragji.gallerykit.utils;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

/**
 * Utility class containing some common methods to interact with time
 *
 * @author Chirag
 * @since 1.0.0
 */
public abstract class TimeUtils {
    private static DecimalFormat format = new DecimalFormat("00");

    /**
     * Converts mills to string representation showing minutes and seconds in well format
     *
     * @param mills Mills
     * @return Well formatted time string
     */
    public static String getMillsToTime(long mills) {
        long secs = TimeUnit.SECONDS.convert(mills, TimeUnit.MILLISECONDS);
        long min = 0;
        if (mills > 0 && mills < 1000)
            secs = 1;
        else if (secs > 60) {
            min = secs / 60;
            secs %= 60;
        }
        return format.format(min) + ":" + format.format(secs);
    }
}
