package gs.meetin.connector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginPinActivity extends ActionBarActivity {
    private String email;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pin);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SessionManager.ACTION_LOGIN);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Mtn.gs", "[Pin activity]: Received login action");
                finish();
            }
        };

        registerReceiver(broadcastReceiver, intentFilter);

        Intent intent = getIntent();
        email = intent.getStringExtra(LoginEmailActivity.EXTRA_EMAIL);

        TextView userEmail = (TextView) findViewById(R.id.textUserEmail);

        userEmail.setText(email);

        setButtonListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void setButtonListeners() {
        Button sendPin = (Button) findViewById(R.id.buttonSignInPin);

        final String userId = "1251";
        final String token = "1251-af24a";

        sendPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(userId, token, email);
            }
        });
    }

    private void signIn(String userId, String token, String email) {
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.signIn(userId, token, email);
    }

}
