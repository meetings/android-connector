package gs.meetin.connector;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;
import gs.meetin.connector.dto.SuggestionSource;
import gs.meetin.connector.events.SessionEvent;
import gs.meetin.connector.events.SuggestionEvent;
import gs.meetin.connector.events.UIEvent;
import gs.meetin.connector.utils.DateHelper;
import gs.meetin.connector.utils.Device;
import gs.meetin.connector.utils.Dialogs;

import static gs.meetin.connector.events.Event.EventType.SET_BUTTONS_DISABLED;
import static gs.meetin.connector.events.Event.EventType.UPDATE_SUGGESTIONS;


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

            startCalendarService();

            setButtonListeners();

            TextView userEmail = (TextView) findViewById(R.id.textUserEmail);
            userEmail.setText(sessionManager.getUserEmail());

            long lastSync = sessionManager.getLastSync();
            TextView lastSyncDate = (TextView) findViewById(R.id.lastSyncDate);
            if(lastSync != 0) {
                lastSyncDate.setText(DateHelper.EpochToDateTimeString(sessionManager.getLastSync()));
            } else {
                lastSyncDate.setText(R.string.never);
            }
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

    public void onEventMainThread(UIEvent event) {
        switch (event.getType()) {

            case SET_BUTTONS_ENABLED:
                (findViewById(R.id.buttonSyncNow)).setEnabled(true);
                (findViewById(R.id.connectorProgress)).setVisibility(View.INVISIBLE);

                break;

            case SET_BUTTONS_DISABLED:
                (findViewById(R.id.buttonSyncNow)).setEnabled(false);
                (findViewById(R.id.connectorProgress)).setVisibility(View.VISIBLE);

                break;

            case SET_LAST_SYNC_TIME:
                Log.d("Mtn.gs", "Sync time refresh " + DateHelper.EpochToDateTimeString(event.getLong("lastSync")));
                sessionManager.setLastSync(event.getLong("lastSync"));
                TextView lastSyncDate = (TextView) findViewById(R.id.lastSyncDate);
                lastSyncDate.setText(DateHelper.EpochToDateTimeString(event.getLong("lastSync")));

                break;
        }
    }

    private void setButtonListeners() {
        final Context context = this;
        Button btnLogout = (Button) findViewById(R.id.buttonLogout);
        Button btnSyncNow = (Button) findViewById(R.id.buttonSyncNow);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialogs.twoButtonDialog(
                        context,
                        R.string.disconnect_title,
                        R.string.disconnect_message,
                        R.string.OK,
                        R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new SuggestionManager(context, sessionManager).removeSuggestionSources();

                                stopCalendarService();
                                sessionManager.signOut();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }
                ).show();
            }
        });

        btnSyncNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new UIEvent(SET_BUTTONS_DISABLED));

                SuggestionEvent suggestionEvent = new SuggestionEvent(UPDATE_SUGGESTIONS);
                suggestionEvent.putBoolean("forceUpdate", true);
                EventBus.getDefault().post(suggestionEvent);
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
