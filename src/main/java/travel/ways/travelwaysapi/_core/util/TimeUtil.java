package travel.ways.travelwaysapi._core.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class TimeUtil {
    public static Date Now() {
        var calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return calendar.getTime();
    }

    public static Timestamp NowTimestamp(){
        var calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp ParseToTimestamp(Date date){
        return new Timestamp(date.getTime());
    }

    public static Date ParseToDate(Timestamp timestamp){
        return new Date(timestamp.getTime());
    }

    public static Date AddMinutes(Date time, int minutes) {
        return new Date(time.getTime() + (long) minutes * 60 * 1000);
    }

}
