package gs.meetin.connector;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import gs.meetin.connector.dto.Attendee;
import gs.meetin.connector.dto.CalendarSuggestion;
import gs.meetin.connector.dto.SuggestionSource;
import gs.meetin.connector.utils.DateHelper;

public class CalendarManager {

    // Projection arrays. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] CALENDAR_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 1
            CalendarContract.Calendars.IS_PRIMARY                     // 2
    };

    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Events._ID,                              // 0
            CalendarContract.Events.TITLE,                            // 1
            CalendarContract.Events.HAS_ATTENDEE_DATA,                // 2
            CalendarContract.Events.ORGANIZER,                        // 3
            CalendarContract.Events.DTSTART,                          // 4
            CalendarContract.Events.DTEND,                            // 5
            CalendarContract.Events.DESCRIPTION,                      // 6
            CalendarContract.Events.EVENT_LOCATION                    // 7
    };

    public static final String[] ATTENDEE_PROJECTION = new String[] {
            CalendarContract.Attendees._ID,                           // 0
            CalendarContract.Attendees.ATTENDEE_NAME,                 // 1
            CalendarContract.Attendees.ATTENDEE_EMAIL                 // 2
    };

    // The indices for the projection arrays above.
    private static final int PROJECTION_ID_INDEX = 0;

    // Calendar indices
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 1;
    private static final int PROJECTION_IS_PRIMARY_INDEX   = 2;

    // Event indices
    private static final int PROJECTION_TITLE_INDEX         = 1;
    private static final int PROJECTION_ATTENDEE_DATA_INDEX = 2;
    private static final int PROJECTION_ORGANIZER_INDEX     = 3;
    private static final int PROJECTION_DTSTART_INDEX       = 4;
    private static final int PROJECTION_DTEND_INDEX         = 5;
    private static final int PROJECTION_DESCRIPTION_INDEX   = 6;
    private static final int PROJECTION_LOCATION_INDEX      = 7;

    // Attendee indices
    private static final int PROJECTION_ATTENDEE_NAME_INDEX  = 1;
    private static final int PROJECTION_ATTENDEE_EMAIL_INDEX = 2;

    public ArrayList<SuggestionSource> getCalendars(Context context) {
        Log.d("Mtn.gs", "Reading calendars...");

        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "(" + CalendarContract.Calendars.VISIBLE + " = 1)";

        Cursor cur = cr.query(uri, CALENDAR_PROJECTION, selection, null, null);

        Log.d("Mtn.gs", "Found "+ cur.getCount() + " calendars");

        ArrayList<SuggestionSource> sources = new ArrayList<SuggestionSource>();

        while (cur.moveToNext()) {
            long calId = cur.getLong(PROJECTION_ID_INDEX);
            String displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            boolean isPrimary = cur.getString(PROJECTION_IS_PRIMARY_INDEX).equals("1");

            Log.d("Mtn.gs", "CalId:" + calId);
            Log.d("Mtn.gs", "displayname:" + displayName);
            Log.d("Mtn.gs", "is primary:" + isPrimary);

            SuggestionSource source = new SuggestionSource(displayName, displayName, isPrimary);

            sources.add(source);
        }

        cur.close();

        return sources;
    }

    public ArrayList<CalendarSuggestion> getEventsFromCalendar(Context context, String calendarName) {
        Log.d("Mtn.gs", "Reading events from " + calendarName);

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

         ArrayList<CalendarSuggestion> suggestions = new ArrayList<CalendarSuggestion>();

        while (cur.moveToNext()) {
            long evtId = cur.getLong(PROJECTION_ID_INDEX);
            String title = cur.getString(PROJECTION_TITLE_INDEX);

            long startDate = cur.getLong(PROJECTION_DTSTART_INDEX);
            long endDate = cur.getLong(PROJECTION_DTEND_INDEX);

            String description = cur.getString(PROJECTION_DESCRIPTION_INDEX);
            String location = cur.getString(PROJECTION_LOCATION_INDEX);

            String organizer = cur.getString(PROJECTION_ORGANIZER_INDEX);

            boolean hasAttendeeData = cur.getString(PROJECTION_ATTENDEE_DATA_INDEX).equals("1");
            String attendees = "";

            if(hasAttendeeData) {
                ArrayList<Attendee> attendeeList = getAttendeesForEvent(context, evtId);
                attendees = attendeeListToString(attendeeList);
            }

            Log.d("Mtn.gs", "------- Found event");
            Log.d("Mtn.gs", "evtId:" + evtId);
            Log.d("Mtn.gs", "title:" + title);
            Log.d("Mtn.gs", "start:" + startDate);
            Log.d("Mtn.gs", "end:" + endDate);
            Log.d("Mtn.gs", "organizer:" + organizer);
            Log.d("Mtn.gs", "attendees:" + attendees);


            CalendarSuggestion suggestion = new CalendarSuggestion(evtId,
                                                                   title,
                                                                   startDate / 1000,
                                                                   endDate / 1000,
                                                                   description,
                                                                   location,
                                                                   attendees,
                                                                   organizer);

            suggestions.add(suggestion);
        }

        cur.close();

        return suggestions;
    }

    private ArrayList<Attendee> getAttendeesForEvent(Context context, long eventId) {
        Log.d("Mtn.gs", "Reading attendees for " + eventId);

        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Attendees.CONTENT_URI;
        String selection = "(" + CalendarContract.Attendees.EVENT_ID + " = ?)";

        String[] selectionArgs = new String[] { Long.toString(eventId) };

        Cursor cur = cr.query(uri, ATTENDEE_PROJECTION, selection, selectionArgs, null);

        Log.d("Mtn.gs", "Found "+ cur.getCount() + " attendees");

        ArrayList<Attendee> attendees = new ArrayList<Attendee>();

        while (cur.moveToNext()) {
            long attId = cur.getLong(PROJECTION_ID_INDEX);
            String attendeeName = cur.getString(PROJECTION_ATTENDEE_NAME_INDEX);
            String attendeeEmail = cur.getString(PROJECTION_ATTENDEE_EMAIL_INDEX);

            Log.d("Mtn.gs", "attId:" + attId);
            Log.d("Mtn.gs", "attendee:" + attendeeName);
            Log.d("Mtn.gs", "email:" + attendeeEmail);

            Attendee attendee = new Attendee(attendeeName, attendeeEmail);

            attendees.add(attendee);
        }

        cur.close();

        return attendees;
    }

    private String attendeeListToString(ArrayList<Attendee> attendeeList) {
        StringBuilder attendees = new StringBuilder();

        for(Iterator<Attendee> i = attendeeList.iterator(); i.hasNext();) {
            attendees.append(i.next().toString());

            if(i.hasNext()) {
                attendees.append(", ");
            }
        }

        return attendees.toString();
    }
}
