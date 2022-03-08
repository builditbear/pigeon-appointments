package dev.builditbear.utility;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Provides common utility methods for easily converting between timezones, printing LocalDateTime objects in
 * various formats, and other time related tasks.
 */
public class TimeConversion {
    static DateTimeFormatter standard = DateTimeFormatter.ofPattern("hh:mm:ss a");
    static DateTimeFormatter showTimezone= DateTimeFormatter.ofPattern("hh:mm:ss a z");

    /**
     * Converts a given LocalDateTime object's time into a formatted String in UTC time.
     * @param ldt A LocalDateTime object to convert to a formatted String.
     * @return A String value describing the time represented by currentLdt in the format "hh:mm:ss a".
     */
    public static String printUTCTime(LocalDateTime ldt) {
        return ldt.atZone(ZoneOffset.ofHours(-8)).format(standard.withZone(ZoneOffset.UTC));
    }

    public static String printZonedLocalTime(LocalDateTime ldt) {
        ZonedDateTime zdt = ldt.atZone(TimeZone.getDefault().toZoneId());
        return zdt.format(showTimezone);
    }
}
