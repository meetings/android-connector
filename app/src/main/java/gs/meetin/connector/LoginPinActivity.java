package gs.meetin.connector;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import gs.meetin.connector.R;

public class LoginPinActivity extends ActionBarActivity {
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pin);

        Intent intent = getIntent();
        email = intent.getStringExtra(LoginEmailActivity.EXTRA_EMAIL);

        setButtonListeners();
    }

    private void setButtonListeners() {
        Button sendPin = (Button) findViewById(R.id.buttonSignInPin);

        final String userId = "1251";
        final String token = "1251-af24a";

        sendPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn(userId, token, email);
                closeActivity();
            }
        });
    }

    private void logIn(String userId, String token, String email) {

        SessionManager sessionManager = new SessionManager(this);

        sessionManager.createLoginSession(userId, token, email);
    }

    private void closeActivity() {
        Intent connectorIntent = new Intent(this, ConnectorActivity.class);
        startActivity(connectorIntent);
        finish();
    }
}
