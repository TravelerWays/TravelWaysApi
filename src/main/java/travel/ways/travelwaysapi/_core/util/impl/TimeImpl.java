package travel.ways.travelwaysapi._core.util.impl;

import lombok.Getter;
import org.springframework.stereotype.Component;
import travel.ways.travelwaysapi._core.util.Time;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Getter
@Component
public class TimeImpl implements Time, Comparable<TimeImpl> {
    private final Date date;

    public TimeImpl() {
        var calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        date = calendar.getTime();
    }

    TimeImpl(long time) {
        var calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        date = calendar.getTime();
    }

    @Override
    public TimeImpl now() {
        return new TimeImpl();
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

    public TimeImpl addMinutes(long minutes) {
        date.setTime(date.getTime() + minutes * 60 * 1000);
        return this;
    }

    public TimeImpl addHours(long hours) {
        date.setTime(date.getTime() + hours * 60 * 60 * 1000);
        return this;
    }

    public TimeImpl addDays(long days) {
        date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
        return this;
    }

    @Override
    public int compareTo(TimeImpl o) {
        return (int) (date.getTime() - o.getTime());
    }

}
