package dev.builditbear.utility;

import java.sql.Timestamp;
import java.time.*;
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
    public static final DateTimeFormatter standardDate = DateTimeFormatter.ofPattern("LLL dd, yyyy");
    public static final DateTimeFormatter standardDateAndTime = DateTimeFormatter.ofPattern("LLL dd, yyyy @ hh:mm a");
    public static final DateTimeFormatter showTimezone= DateTimeFormatter.ofPattern("hh:mm:ss a z");

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

    /**
     * Converts a timestamp pulled from the database into a LocalDateTime set for the same point in time in the user's timezone.
     * @param timestamp The timestamp pulled from the database.
     * @return A new LocalDateTime object representing the same point in time, but in the user's local timezone.
     */
    public static LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
        return convertToTimeZone(timestamp.toLocalDateTime(), ZoneId.of("UTC+00:00"), TimeZone.getDefault().toZoneId());
    }

    /**
     * Converts the provided time and date at the given timezone into the equivalent time and date at another timezone.
     * @param originTime A time and date at some timezone.
     * @param originZoneId The Timezone ID for the date and time of day described by originTime.
     * @param targetZoneId The Timezone ID for which we would like to get an equivalent time and date for.
     * @return A LocalDateTime object representing the same point in time as the LocalDateTime and origin TimeZone passed in, but at the
     * specified target timezone.
     */
    public static LocalDateTime convertToTimeZone(LocalDateTime originTime, ZoneId originZoneId, ZoneId targetZoneId) {
        ZonedDateTime zonedOriginTime = originTime.atZone(originZoneId);
        ZonedDateTime zonedDestinationTime = zonedOriginTime.withZoneSameInstant(targetZoneId);
        return zonedDestinationTime.toLocalDateTime();
    }

    /**
     * Finds the date on which today's month begins.
     * @return The first day of today's month.
     */
    public static LocalDate getMonthStartDate() {
        LocalDate date = LocalDate.now();
        while(date.getDayOfMonth() != 1) {
            date = date.minusDays(1);
        }
        return date;
    }

    /**
     * Finds the date on which a given date's month begins.
     * @param date The date to find the first of the month for.
     * @return The first day of the given date's month.
     */
    public static LocalDate getMonthStartDate(LocalDate date) {
        while(date.getDayOfMonth() != 1) {
            date = date.minusDays(1);
        }
        return date;
    }

    /**
     * Finds the date on which today's week begins.
     * @return The first day of today's week.
     */
    public static LocalDate getWeekStartDate() {
        LocalDate date = LocalDate.now();
        while(date.getDayOfWeek() != DayOfWeek.SUNDAY) {
            date = date.minusDays(1);
        }
        return date;
    }

    /**
     * Finds the date on which a given date's week begins.
     * @param date The date to find the first day of the week for.
     * @return The day on which the given date's week begins.
     */
    public static LocalDate getWeekStartDate(LocalDate date) {
        while(date.getDayOfWeek() != DayOfWeek.SUNDAY) {
            date = date.minusDays(1);
        }
        return date;
    }
}
