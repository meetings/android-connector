package gs.meetin.connector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import gs.meetin.connector.adapters.LoginAdapter;
import gs.meetin.connector.events.ErrorEvent;
import gs.meetin.connector.events.SessionEvent;
import gs.meetin.connector.services.LoginService;
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
        showAlert(event.getTitle(), event.getMessage());
    }

    private void setButtonListeners() {
        Button sendPin = (Button) findViewById(R.id.buttonSignInPin);

        sendPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                (findViewById(R.id.loginPinProgress)).setVisibility(View.VISIBLE);

                signIn();
            }
        });
    }

    private void signIn() {
        String pin = ((EditText) findViewById(R.id.inputPin)).getText().toString();

        loginService.login(email, pin);
    }

    private void showAlert(String title, String message) {

        (findViewById(R.id.buttonSignInPin)).setEnabled(true);
        (findViewById(R.id.loginEmailProgress)).setVisibility(View.INVISIBLE);

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
