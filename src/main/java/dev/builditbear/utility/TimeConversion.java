package dev.builditbear.utility;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Provides common utility methods for easily converting between timezones, printing LocalDateTime objects in
 * various formats, and other time related tasks.
 */
public final class TimeConversion {
    private TimeConversion() {
        throw new RuntimeException("Instantiation of TimeConversion is not allowed.");
    }

    private static final DateTimeFormatter standard = DateTimeFormatter.ofPattern("hh:mm:ss a");
    private static final DateTimeFormatter showTimezone= DateTimeFormatter.ofPattern("hh:mm:ss a z");

    /**
     * Converts a given LocalDateTime object's time into a formatted String in UTC time.
     * @param ldt A LocalDateTime object used to generate the formatted output String.
     * @return A String describing the time represented by ldt in the format "hh:mm:ss a", where "a" is AM or PM.
     */
    public static String printUTCTime(LocalDateTime ldt) {
        return ldt.atZone(ZoneOffset.ofHours(-8)).format(standard.withZone(ZoneOffset.UTC));
    }

    /**
     * Converts a given LocalDateTime object's time into a formatted String depicting the local time and timezone.
     * @param ldt A LocalDateTime object used to generate the formatted output String.
     * @return A String describing the local time represented by ldt in the format "hh:mm:ss a z", where "a" is AM or PM,
     * and "z" is the local timezone ID.
     */
    public static String printZonedLocalTime(LocalDateTime ldt) {
        ZonedDateTime zdt = ldt.atZone(TimeZone.getDefault().toZoneId());
        return zdt.format(showTimezone);
    }

    public static LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
        return timestamp.toLocalDateTime();
    }

    public static Timestamp localDateTimeToTimestamp(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime);
    }
}
