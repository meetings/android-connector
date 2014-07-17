package gs.meetin.connector;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.Date;

import gs.meetin.connector.utils.DateHelper;

public class CalendarManager {

    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] CALENDAR_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME          // 2
    };

    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Events._ID,                           // 0
            CalendarContract.Events.TITLE,                         // 1
            CalendarContract.Events.HAS_ATTENDEE_DATA,             // 2
            CalendarContract.Events.ORGANIZER,                     // 3
            CalendarContract.Events.DTSTART,                       // 4
            CalendarContract.Events.DTEND                          // 5
    };

    // The indices for the projection arrays above.
    private static final int PROJECTION_ID_INDEX = 0;

    // Calendar indices
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;

    // Event indices
    private static final int PROJECTION_TITLE_INDEX = 1;
    private static final int PROJECTION_ATTENDEE_DATA_INDEX = 2;
    private static final int PROJECTION_ORGANIZER_INDEX = 3;
    private static final int PROJECTION_DTSTART_INDEX = 4;
    private static final int PROJECTION_DTEND_INDEX = 5;

    public void listCalendars(Context context) {
        Log.d("Mtn.gs", "------- Reading calendars...");

        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "(" + CalendarContract.Calendars.VISIBLE + " = 1)";

        Cursor cur = cr.query(uri, CALENDAR_PROJECTION, selection, null, null);

        Log.d("Mtn.gs", "Found "+ cur.getCount() + " calendars");

        while (cur.moveToNext()) {
            long calID = cur.getLong(PROJECTION_ID_INDEX);
            String displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            String accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);

            Log.d("Mtn.gs", "------- Found calendar");
            Log.d("Mtn.gs", "CalId:" + calID);
            Log.d("Mtn.gs", "displayname:" + displayName);
            Log.d("Mtn.gs", "accountname:" + accountName);
        }
    }

    public void listEventsFromCalendar(Context context, String calendarName) {
        Log.d("Mtn.gs", "------- Reading events from " + calendarName);

        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String selection = "((" + CalendarContract.Events.VISIBLE + " = 1) AND ("
                                + CalendarContract.Events.CALENDAR_DISPLAY_NAME + " = ?) AND ("
                                + CalendarContract.Events.DTSTART + " > ?) AND ("
                                + CalendarContract.Events.DTEND + " < ?))";

        DateTime todayDateTime = DateHelper.today();
        String today = Long.toString(todayDateTime.getMillis());
        String threeMonthsFromNow = Long.toString(todayDateTime.plusMonths(3).getMillis());

        String[] selectionArgs = new String[] { calendarName, today, threeMonthsFromNow };

        Cursor cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        Log.d("Mtn.gs", "Found "+ cur.getCount() + " events");


        while (cur.moveToNext()) {
            long evtID = cur.getLong(PROJECTION_ID_INDEX);
            String title = cur.getString(PROJECTION_TITLE_INDEX);
            String organizer = cur.getString(PROJECTION_ORGANIZER_INDEX);
            String hasAttendeeData = cur.getString(PROJECTION_ATTENDEE_DATA_INDEX);
            long startDate = cur.getLong(PROJECTION_DTSTART_INDEX);
            long endDate = cur.getLong(PROJECTION_DTEND_INDEX);

            Log.d("Mtn.gs", "------- Found event");
            Log.d("Mtn.gs", "CalId:" + evtID);
            Log.d("Mtn.gs", "title:" + title);
            Log.d("Mtn.gs", "organizer:" + organizer);
            Log.d("Mtn.gs", "has attendee data:" + hasAttendeeData);
            Log.d("Mtn.gs", "start:" + startDate);
            Log.d("Mtn.gs", "startdate:" + new Date(startDate));
            Log.d("Mtn.gs", "end:" + endDate);
        }
    }
}
