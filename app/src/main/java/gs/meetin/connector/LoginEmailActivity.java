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
import gs.meetin.connector.events.ErrorEvent;
import gs.meetin.connector.events.SessionEvent;
import gs.meetin.connector.services.LoginHandler;

public class LoginEmailActivity extends ActionBarActivity {

    public final static String EXTRA_EMAIL = "gs.meetin.connector.LOGIN_EMAIL";

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        EventBus.getDefault().register(this);

        setButtonListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(SessionEvent event) {
        switch (event.getType()) {

            case SessionEvent.PIN_REQUEST_SUCCESSFUL:
                Intent loginIntent = new Intent(this, LoginPinActivity.class);
                loginIntent.putExtra(EXTRA_EMAIL, email);
                startActivity(loginIntent);

                break;

            case SessionEvent.LOGIN:
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

            LoginHandler lh = new LoginHandler();
            lh.requestPin(email);

        } else {
            showAlert(getString(R.string.invalid_email_title), getString(R.string.invalid_email_message));
        }
    }

    private boolean isValidEmail(CharSequence email) {
        if (email == null) {
            return false;
        }

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
