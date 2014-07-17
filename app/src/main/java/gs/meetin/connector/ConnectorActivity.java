package gs.meetin.connector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import gs.meetin.connector.events.SessionEvent;


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

        userEmail.setText(sessionManager.getUserDetails().get(SessionManager.KEY_EMAIL));
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
        Button btnStartService = (Button) findViewById(R.id.buttonStartService);
        Button btnStopService = (Button) findViewById(R.id.buttonStopService);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopCalendarService();
                sessionManager.signOut();

            }
        });

        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startCalendarService();

                new CalendarManager().getEventsFromCalendar(getApplicationContext(), "Tuomas Lahti");
            }
        });

        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopCalendarService();
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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.connector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
