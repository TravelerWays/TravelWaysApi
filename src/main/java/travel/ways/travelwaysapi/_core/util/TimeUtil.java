package travel.ways.travelwaysapi._core.util;

import lombok.Getter;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Getter
public class TimeUtil implements Comparable<TimeUtil> {
    private final Date date;

    TimeUtil() {
        var calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        date = calendar.getTime();
    }

    TimeUtil(long time) {
        var calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        date = calendar.getTime();
    }

    public static TimeUtil Now() {
        return new TimeUtil();
    }

    public long getTime() {
        return date.getTime();
    }

    public Timestamp getTimestamp() {
        return new Timestamp(date.getTime());
    }

    @Override
    public String toString() {
        return date.toString();
    }

    public TimeUtil addMinutes(long minutes) {
        date.setTime(date.getTime() + minutes * 60 * 1000);
        return this;
    }

    public TimeUtil addHours(long hours) {
        date.setTime(date.getTime() + hours * 60 * 60 * 1000);
        return this;
    }

    public TimeUtil addDays(long days) {
        date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
        return this;
    }

    @Override
    public int compareTo(TimeUtil o) {
        return (int) (date.getTime() - o.getTime());
    }
}
