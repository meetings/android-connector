package gs.meetin.connector;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;

import de.greenrobot.event.EventBus;

public class LoginPinActivity extends ActionBarActivity {
    private String email;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pin);

        EventBus.getDefault().register(this);

        Intent intent = getIntent();
        email = intent.getStringExtra(LoginEmailActivity.EXTRA_EMAIL);

        TextView userEmail = (TextView) findViewById(R.id.textUserEmail);

        userEmail.setText(email);

        setButtonListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(ConnectorEvent event) {
        switch (event.getType()) {
            case ConnectorEvent.LOGIN:
                finish();
                break;
        }
    }

    private void setButtonListeners() {
        Button sendPin = (Button) findViewById(R.id.buttonSignInPin);

        sendPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn();
            }
        });
    }

    private void signIn() {

        BasicNameValuePair user;
        String pin = ((EditText) findViewById(R.id.inputPin)).getText().toString();

        LoginHandler lh = new LoginHandler();

        try {
            user = lh.login(email, pin);
        } catch (LoginException ex) {
            showAlert(getString(R.string.pin_request_error), ex.getMessage());
            return;
        }

        SessionManager sessionManager = new SessionManager(this);
        sessionManager.signIn(user.getName(), user.getValue(), email);
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
