package gs.meetin.connector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import gs.meetin.connector.adapters.LoginAdapter;
import gs.meetin.connector.events.ErrorEvent;
import gs.meetin.connector.events.SessionEvent;
import gs.meetin.connector.services.LoginService;
import gs.meetin.connector.utils.Dialogs;
import retrofit.RestAdapter;

public class LoginPinActivity extends Activity {

    private String email;
    private LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pin);

        EventBus.getDefault().register(this);

        RestAdapter loginAdapter = LoginAdapter.build();
        loginService = new LoginService(loginAdapter);

        Intent intent = getIntent();
        email = intent.getStringExtra(LoginEmailActivity.EXTRA_EMAIL);

        TextView userEmail = (TextView) findViewById(R.id.textUserEmail);

        userEmail.setText(email);

        setActionListeners();
        setButtonListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(SessionEvent event) {
        switch (event.getType()) {

            case LOGIN_SUCCESSFUL:
                SessionManager sessionManager = new SessionManager(this);
                sessionManager.signIn(event.getUser().getName(), event.getUser().getValue(), email);

                break;

            case LOGIN:
                finish();
                break;
        }
    }

    public void onEvent(ErrorEvent event) {

        (findViewById(R.id.buttonSignInPin)).setEnabled(true);
        (findViewById(R.id.loginPinProgress)).setVisibility(View.INVISIBLE);

        Dialogs.simpleAlert(this, event.getTitle(), event.getMessage()).show();
    }

    private void setActionListeners() {
        EditText inputPin = (EditText) findViewById(R.id.inputPin);

        inputPin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    signIn();
                }
                return true;
            }
        });
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
        (findViewById(R.id.buttonSignInPin)).setEnabled(false);
        (findViewById(R.id.loginPinProgress)).setVisibility(View.VISIBLE);

        String pin = ((EditText) findViewById(R.id.inputPin)).getText().toString();

        loginService.login(email, pin);
    }
}
