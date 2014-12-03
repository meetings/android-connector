package gs.meetin.cmeet;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import gs.meetin.cmeet.adapters.LoginAdapter;
import gs.meetin.cmeet.events.ErrorEvent;
import gs.meetin.cmeet.events.SessionEvent;
import gs.meetin.cmeet.services.LoginService;
import gs.meetin.cmeet.utils.Dialogs;
import retrofit.RestAdapter;

public class LoginEmailActivity extends Activity {

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

            case PIN_REQUEST_SUCCESSFUL:

                (findViewById(R.id.buttonSignInEmail)).setEnabled(true);
                (findViewById(R.id.loginEmailProgress)).setVisibility(View.INVISIBLE);

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

        (findViewById(R.id.buttonSignInEmail)).setEnabled(true);
        (findViewById(R.id.loginEmailProgress)).setVisibility(View.INVISIBLE);

        if(event.getErrorCode() == 2) {
            Dialogs.twoButtonDialog(
                    this,
                    R.string.unknown_email_title,
                    R.string.unknown_email_message,
                    R.string.yes,
                    R.string.no,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://mobile.meetin.gs"));
                            startActivity(browserIntent);
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }
            ).show();
        } else {
            Dialogs.simpleAlert(this, event.getTitle(), event.getMessage()).show();
        }
    }

    private void setActionListeners() {
        EditText inputEmail = (EditText) findViewById(R.id.inputEmail);

        inputEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    sendPinRequest();
                }
                return true;
            }
        });
    }

    private void setButtonListeners() {
        Button sendEmail = (Button) findViewById(R.id.buttonSignInEmail);

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPinRequest();
            }
        });

        Button sendEmail1 = (Button) findViewById(R.id.buttonSignInEmailQuick1);

        sendEmail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText) findViewById(R.id.inputEmail)).setText("tuomas.lahti+001+demo@meetin.gs");
            }
        });
    }

    private void sendPinRequest() {
        (findViewById(R.id.buttonSignInEmail)).setEnabled(false);
        (findViewById(R.id.loginEmailProgress)).setVisibility(View.VISIBLE);

        email = ((EditText) findViewById(R.id.inputEmail)).getText().toString();

        if (isValidEmail(email)) {
            loginService.requestPin(email);

        } else {
            EventBus.getDefault().post(new ErrorEvent(getString(R.string.invalid_email_title), getString(R.string.invalid_email_message)));
        }
    }

    private boolean isValidEmail(CharSequence email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
