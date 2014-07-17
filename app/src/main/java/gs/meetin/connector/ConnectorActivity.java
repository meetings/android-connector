package gs.meetin.connector;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import gs.meetin.connector.dto.SourceContainer;
import gs.meetin.connector.dto.SuggestionSource;
import gs.meetin.connector.events.SessionEvent;
import gs.meetin.connector.services.Suggestion;


public class ConnectorActivity extends ActionBarActivity {

    private SessionManager sessionManager;

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

        startCalendarService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(SessionEvent event) {
        switch (event.getType()) {
            case SessionEvent.LOGOUT:
                finish();
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
                ArrayList<SuggestionSource> suggestionSources = new ArrayList<SuggestionSource>();//new CalendarManager().getCalendars(getApplicationContext());
                String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                SourceContainer sourceContainer = new SourceContainer("Nexus 5", "phone", androidId, suggestionSources);

                Suggestion s = new Suggestion(sessionManager.getUserId(), sessionManager.getToken());

                s.updateSources(sourceContainer);

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
}
