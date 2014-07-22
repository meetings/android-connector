package gs.meetin.connector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import gs.meetin.connector.events.SessionEvent;
import gs.meetin.connector.events.SuggestionEvent;

import static gs.meetin.connector.events.Event.EventType.UPDATE_SOURCES;


public class ConnectorActivity extends Activity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connector);

        EventBus.getDefault().register(this);

        sessionManager = new SessionManager(this);
        if(!sessionManager.isLoggedIn()) {

            sessionManager.signOut();

        } else {

            setButtonListeners();

            TextView userEmail = (TextView) findViewById(R.id.textUserEmail);
            userEmail.setText(sessionManager.getUserEmail());

            startCalendarService();
        }
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
}
