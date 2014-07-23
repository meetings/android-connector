package gs.meetin.connector;

import android.content.Context;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import gs.meetin.connector.adapters.SessionAdapter;
import gs.meetin.connector.dto.CalendarSuggestion;
import gs.meetin.connector.dto.SourceContainer;
import gs.meetin.connector.dto.SuggestionBatch;
import gs.meetin.connector.dto.SuggestionSource;
import gs.meetin.connector.services.SuggestionService;
import gs.meetin.connector.utils.DateHelper;
import gs.meetin.connector.utils.Device;
import retrofit.RestAdapter;

public class SuggestionManager {

    private Context context;

    private SuggestionService suggestionService;
    private ArrayList<SuggestionSource> suggestionSources;

    private HashMap<String, ArrayList<CalendarSuggestion>> previousSuggestions;

    public SuggestionManager(Context context, SessionManager sessionManager) {
        this.context = context;

        RestAdapter sessionAdapter = SessionAdapter.build(sessionManager.getUserId(), sessionManager.getToken());
        suggestionService = new SuggestionService(sessionAdapter, sessionManager.getUserId());

        previousSuggestions = new HashMap<String, ArrayList<CalendarSuggestion>>();
    }

    public void updateSuggestionSources() {
        suggestionSources = new CalendarManager().getCalendars(context.getApplicationContext());

        String containerName = Device.getDeviceName();
        String androidId = Device.getAndroidId(context.getContentResolver());

        SourceContainer sourceContainer = new SourceContainer(containerName, "phone", androidId, suggestionSources);

        suggestionService.updateSources(sourceContainer);
    }

    public void updateSuggestions() {

        DateTime todayDateTime = DateHelper.today();
        long todayEpoch = todayDateTime.getMillis() / 1000;
        long threeMonthsFromNowEpoch = todayDateTime.plusMonths(3).getMillis() / 1000;

        String containerName = Device.getDeviceName();
        String androidId = Device.getAndroidId(context.getContentResolver());

        for(Iterator<SuggestionSource> source = suggestionSources.iterator(); source.hasNext();) {

            SuggestionSource calendar = source.next();

            ArrayList<CalendarSuggestion> suggestions = new CalendarManager().getEventsFromCalendar(context.getApplicationContext(), calendar.getName());

            // If new suggestions were found, update in memory cache and send new results to backend
            if(hasNewMeetings(calendar.getName(), suggestions)) {

                previousSuggestions.put(calendar.getName(), suggestions);

                SuggestionBatch batch = new SuggestionBatch(containerName, "phone", androidId, calendar.getName(), calendar.getName(), calendar.getIsPrimary(), todayEpoch, threeMonthsFromNowEpoch, suggestions);

                suggestionService.updateSuggestions(batch);
            }
        }

        suggestionService.getSources();
    }

    // Compare in memory cache of meetings to the ones found in calendar
    private boolean hasNewMeetings(String calendarName, ArrayList<CalendarSuggestion> suggestions) {

        ArrayList<CalendarSuggestion> previous = previousSuggestions.get(calendarName);

        if(previous == null) {
            return true;
        }

        List<CalendarSuggestion> previousList = new ArrayList<CalendarSuggestion>(previous);
        List<CalendarSuggestion> newList = new ArrayList<CalendarSuggestion>(suggestions);

        previousList.removeAll(suggestions);
        newList.removeAll(previous);

        return !newList.isEmpty() || !previousList.isEmpty();
    }
}
