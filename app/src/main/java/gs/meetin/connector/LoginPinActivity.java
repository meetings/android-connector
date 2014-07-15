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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pin);

        setButtonListeners();
    }

    private void setButtonListeners() {
        Button sendPin = (Button) findViewById(R.id.buttonSignInPin);

        sendPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeActivity();
            }
        });
    }

    private void closeActivity() {
        Intent connectorIntent = new Intent(this, ConnectorActivity.class);
        startActivity(connectorIntent);
        finish();
    }
}
