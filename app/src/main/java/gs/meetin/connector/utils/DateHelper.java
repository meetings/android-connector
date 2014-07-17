package gs.meetin.connector.utils;

import org.joda.time.DateTime;

public class DateHelper {

    public static DateTime today() {
        return new DateTime().withTimeAtStartOfDay();
    }
}
