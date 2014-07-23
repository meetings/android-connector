package gs.meetin.connector;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import de.greenrobot.event.EventBus;
import gs.meetin.connector.events.SuggestionEvent;

import static gs.meetin.connector.events.Event.EventType.UPDATE_SOURCES;

public class ConnectorService extends IntentService {

    private boolean running = false;

    private SessionManager sessionManager;
    private SuggestionManager suggestionManager;

    public ConnectorService() {
        super("ConnectorService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Mtn.gs", "Starting service...");

        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        running = true;

        sessionManager = new SessionManager(this);

        suggestionManager = new SuggestionManager(this, sessionManager);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        Log.d("Mtn.gs", "Stopping service...");
        running = false;
        suggestionManager = null;
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (running) {
            synchronized (this) {
                try {
                    Log.d("Mtn.gs", "Syncing suggestions... ");
                    EventBus.getDefault().post(new SuggestionEvent(UPDATE_SOURCES));
                    wait(Constants.updateInterval);
                } catch (Exception e) {
                }
            }
        }
    }

    public void onEvent(SuggestionEvent event) {
        switch (event.getType()) {

            case UPDATE_SOURCES:
                suggestionManager.updateSuggestionSources();
                break;

            case UPDATE_SUGGESTIONS:
                suggestionManager.updateSuggestions();
                break;
        }
    }
}
