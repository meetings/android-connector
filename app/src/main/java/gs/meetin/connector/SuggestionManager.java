package gs.meetin.connector;

import android.content.Context;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Iterator;

import gs.meetin.connector.adapters.SessionAdapter;
import gs.meetin.connector.dto.CalendarSuggestion;
import gs.meetin.connector.dto.SourceContainer;
import gs.meetin.connector.dto.SuggestionBatch;
import gs.meetin.connector.dto.SuggestionSource;
import gs.meetin.connector.events.SuggestionEvent;
import gs.meetin.connector.services.SuggestionService;
import gs.meetin.connector.utils.DateHelper;
import gs.meetin.connector.utils.Device;
import retrofit.RestAdapter;

public class SuggestionManager {

    private Context context;

    private SuggestionService suggestionService;
    private ArrayList<SuggestionSource> suggestionSources;

    public SuggestionManager(Context context, SessionManager sessionManager) {
        this.context = context;

        RestAdapter sessionAdapter = SessionAdapter.build(sessionManager.getUserId(), sessionManager.getToken());
        suggestionService = new SuggestionService(sessionAdapter, sessionManager.getUserId());
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

            SuggestionBatch batch = new SuggestionBatch(containerName, "phone", androidId, calendar.getName(), calendar.getName(), calendar.getIsPrimary(), todayEpoch, threeMonthsFromNowEpoch, suggestions);

            suggestionService.updateSuggestions(batch);

        }
    }
}
