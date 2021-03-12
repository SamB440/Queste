package com.convallyria.queste.util;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    private TimeUtils() {}

    public static long convertTicks(long ticks, TimeUnit to) {
        long milliseconds = 50 * ticks;
        return to.convert(milliseconds, TimeUnit.MILLISECONDS);
    }

    public static long convert(long toConvert, TimeUnit from, TimeUnit to) {
        return to.convert(toConvert, from);
    }
}
