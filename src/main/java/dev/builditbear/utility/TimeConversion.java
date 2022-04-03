package dev.builditbear.utility;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public static Timestamp localDateTimeToTimestamp(LocalDateTime ldt) {
        return Timestamp.valueOf(ldt);
    }

    /**
     * Converts the provided time and date at the given timezone into the equivalent time and date at the user's local timezone.
     * @param foreignTime A time and date at some timezone other than the user's local timezone.
     * @param zoneId The Timezone ID for the date and time of day described by foreignTime.
     * @return A LocalDateTime object representing the same point in time as the LocalDateTime passed in, but at the user's
     * local timezone.
     */
    public static LocalDateTime toLocalTimeZone(LocalDateTime foreignTime, String zoneId) {
        ZonedDateTime zonedForeignTime = foreignTime.atZone(ZoneId.of(zoneId));
        ZonedDateTime zonedLocalTime = zonedForeignTime.withZoneSameInstant(TimeZone.getDefault().toZoneId());
        return zonedLocalTime.toLocalDateTime();
    }
}
