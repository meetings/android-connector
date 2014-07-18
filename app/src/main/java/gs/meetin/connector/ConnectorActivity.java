package gs.meetin.connector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Iterator;

import de.greenrobot.event.EventBus;
import gs.meetin.connector.adapters.SessionAdapter;
import gs.meetin.connector.dto.CalendarSuggestion;
import gs.meetin.connector.dto.SourceContainer;
import gs.meetin.connector.dto.SuggestionBatch;
import gs.meetin.connector.dto.SuggestionSource;
import gs.meetin.connector.events.SessionEvent;
import gs.meetin.connector.events.SuggestionEvent;
import gs.meetin.connector.services.SuggestionService;
import gs.meetin.connector.utils.DateHelper;
import gs.meetin.connector.utils.Device;
import retrofit.RestAdapter;

import static gs.meetin.connector.events.Event.EventType.UPDATE_SOURCES;


public class ConnectorActivity extends ActionBarActivity {

    private SessionManager sessionManager;
    private SuggestionService suggestionService;
    private ArrayList<SuggestionSource> suggestionSources;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connector);

        EventBus.getDefault().register(this);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        setButtonListeners();

        TextView userEmail = (TextView) findViewById(R.id.textUserEmail);
        userEmail.setText(sessionManager.getUserEmail());

        RestAdapter sessionAdapter = SessionAdapter.build(sessionManager.getUserId(), sessionManager.getToken());
        suggestionService = new SuggestionService(sessionAdapter, sessionManager.getUserId());

        startCalendarService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(SessionEvent event) {
        switch (event.getType()) {
            case LOGOUT:
                finish();
                break;
        }
    }

    public void onEvent(SuggestionEvent event) {
        switch (event.getType()) {

            case UPDATE_SOURCES:
                updateSuggestionSources();
                break;

            case UPDATE_SUGGESTIONS:
                updateSuggestions();
                break;
        }
    }

    private void setButtonListeners() {
        Button btnLogout = (Button) findViewById(R.id.buttonLogout);
        Button btnSyncNow = (Button) findViewById(R.id.buttonSyncNow);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopCalendarService();
                sessionManager.signOut();
            }
        });

        btnSyncNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SuggestionEvent(UPDATE_SOURCES));
            }
        });
    }

    private void startCalendarService() {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        Intent serviceIntent = new Intent(this, ConnectorService.class);
        startService(serviceIntent);
    }

    private void stopCalendarService() {
        Toast.makeText(this, "service stopping", Toast.LENGTH_SHORT).show();

        Intent serviceIntent = new Intent(this, ConnectorService.class);
        stopService(serviceIntent);
    }

    private void updateSuggestionSources() {
        suggestionSources = new CalendarManager().getCalendars(getApplicationContext());

        String containerName = Device.getDeviceName();
        String androidId = Device.getAndroidId(getContentResolver());

        SourceContainer sourceContainer = new SourceContainer(containerName, "phone", androidId, suggestionSources);

        suggestionService.updateSources(sourceContainer);
    }

    private void updateSuggestions() {

        ArrayList<SuggestionBatch> suggestionBatches = new ArrayList<SuggestionBatch>();

        DateTime todayDateTime = DateHelper.today();
        long todayEpoch = todayDateTime.getMillis() / 1000;
        long threeMonthsFromNowEpoch = todayDateTime.plusMonths(3).getMillis() / 1000;

        String containerName = Device.getDeviceName();
        String androidId = Device.getAndroidId(getContentResolver());

        for(Iterator<SuggestionSource> source = suggestionSources.iterator(); source.hasNext();) {

            SuggestionSource calendar = source.next();

            ArrayList<CalendarSuggestion> suggestions = new CalendarManager().getEventsFromCalendar(getApplicationContext(), calendar.getName());

            SuggestionBatch batch = new SuggestionBatch(containerName, androidId, calendar.getName(), calendar.getName(), calendar.getIsPrimary(), todayEpoch, threeMonthsFromNowEpoch, suggestions);

            suggestionService.updateSuggestions(batch);

        }
    }
}
