package gs.meetin.connector;

import android.content.Context;
import android.util.Log;

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
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SuggestionManager {

    private Context context;

    private SuggestionService suggestionService;

    private HashMap<String, ArrayList<CalendarSuggestion>> previousSuggestions;

    private String containerName;
    private String androidId;

    public SuggestionManager(Context context, SessionManager sessionManager) {
        this.context = context;

        String appVersion = Device.appVersion(context);

        RestAdapter sessionAdapter = SessionAdapter.build(sessionManager.getUserId(), sessionManager.getToken(), appVersion);
        suggestionService = new SuggestionService(sessionAdapter, sessionManager.getUserId());

        previousSuggestions = new HashMap<String, ArrayList<CalendarSuggestion>>();

        containerName = Device.getDeviceName();
        androidId = Device.getAndroidId(context.getContentResolver());
    }

    public boolean update(boolean forceUpdate) {
        final short unmanned = (short) (forceUpdate ? 0 : 1);

        ArrayList<SuggestionSource> suggestionSources = new CalendarManager().getCalendars(context);

        final ArrayList<SuggestionBatch> updateList = getSuggestions(suggestionSources, forceUpdate);

        if(forceUpdate || previousSuggestions.size() < suggestionSources.size() || !updateList.isEmpty()) {
            // Found new suggestions
            updateSuggestionSources(unmanned, suggestionSources, new Callback() {

                @Override
                public void success(Object o, Response response) {
                    updateSuggestions(unmanned, updateList);
                    suggestionService.getSources(unmanned);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("Mtn.gs", error.getMessage());
                }
            });

            return true;
        } else {
            return false;
        }
    }

   public ArrayList<SuggestionBatch> getSuggestions(ArrayList<SuggestionSource> suggestionSources, boolean forceUpdate) {

       ArrayList<SuggestionBatch> updateList = new ArrayList<SuggestionBatch>();

       DateTime todayDateTime = DateHelper.today();
       long todayEpoch = todayDateTime.getMillis() / 1000;
       long threeMonthsFromNowEpoch = todayDateTime.plusMonths(3).getMillis() / 1000;
       
       for(Iterator<SuggestionSource> source = suggestionSources.iterator(); source.hasNext();) {

           SuggestionSource calendar = source.next();

           ArrayList<CalendarSuggestion> suggestions = new CalendarManager().getEventsFromCalendar(context, calendar.getName());

           // If new suggestions were found or if user has pressed 'Sync now', update in memory cache and send new results to backend
           if(forceUpdate || hasNewMeetings(calendar.getName(), suggestions)) {
               previousSuggestions.put(calendar.getName(), suggestions);

               updateList.add(new SuggestionBatch(containerName, "phone", androidId, calendar.getName(), calendar.getName(), calendar.getIsPrimary(), todayEpoch, threeMonthsFromNowEpoch, suggestions));
           }
       }

       return updateList;
    }

    public void updateSuggestionSources(short unmanned, ArrayList<SuggestionSource> suggestionSources, Callback cb) {
        SourceContainer sourceContainer = new SourceContainer(containerName, "phone", androidId, suggestionSources);

        suggestionService.updateSources(unmanned, sourceContainer, cb);
    }

    // Send an empty suggestion source list to remove suggestion sources from backend
    public void removeSuggestionSources() {
        short unmanned = 0;
        updateSuggestionSources(unmanned, new ArrayList<SuggestionSource>(), null);
    }

    public void updateSuggestions(short unmanned, ArrayList<SuggestionBatch> updateList) {
        for(Iterator<SuggestionBatch> itBatch = updateList.iterator(); itBatch.hasNext();) {
            SuggestionBatch batch = itBatch.next();

            suggestionService.updateSuggestions(unmanned, batch);
        }
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
