package dev.builditbear.utility;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Provides common utility methods for easily converting between timezones, printing LocalDateTime objects in
 * various formats, and other time related tasks.
 */
public class TimeConversion {
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");

    /**
     * Converts a given LocalDateTime object's time into a formatted String for easy reading by users.
     * @param ldt A LocalDateTime object to convert to a formatted String.
     * @return A String value describing the time represented by currentLdt in the format "hh:mm:ss a".
     */
    public static String printUTCTime(LocalDateTime ldt) {
        return ldt.atZone(ZoneOffset.ofHours(-8)).format(formatter.withZone(ZoneOffset.UTC));
    }
}
