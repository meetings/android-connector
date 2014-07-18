package gs.meetin.connector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.greenrobot.event.EventBus;
import gs.meetin.connector.adapters.LoginAdapter;
import gs.meetin.connector.events.ErrorEvent;
import gs.meetin.connector.events.SessionEvent;
import gs.meetin.connector.services.LoginService;
import retrofit.RestAdapter;

public class LoginEmailActivity extends ActionBarActivity {

    public final static String EXTRA_EMAIL = "gs.meetin.connector.LOGIN_EMAIL";

    private String email;
    private LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        EventBus.getDefault().register(this);

        RestAdapter loginAdapter = LoginAdapter.build();
        loginService = new LoginService(loginAdapter);

        setButtonListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(SessionEvent event) {
        switch (event.getType()) {

            case PIN_REQUEST_SUCCESSFUL:
                Intent loginIntent = new Intent(this, LoginPinActivity.class);
                loginIntent.putExtra(EXTRA_EMAIL, email);
                startActivity(loginIntent);

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
        Button sendEmail = (Button) findViewById(R.id.buttonSignInEmail);

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPinRequest();
            }
        });
    }

    private void showPinRequest() {
        email = ((EditText) findViewById(R.id.inputEmail)).getText().toString();

        if (isValidEmail(email)) {
            loginService.requestPin(email);

        } else {
            showAlert(getString(R.string.invalid_email_title), getString(R.string.invalid_email_message));
        }
    }

    private boolean isValidEmail(CharSequence email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
