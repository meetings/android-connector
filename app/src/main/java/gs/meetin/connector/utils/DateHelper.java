package gs.meetin.connector.utils;

import org.joda.time.DateTime;

public class DateHelper {

    public static DateTime today() {
        return new DateTime().withTimeAtStartOfDay();
    }

    public static String EpochToDateTimeString(long epoch, String format) {
        return new DateTime(epoch * 1000).toString(format);
    }
}
