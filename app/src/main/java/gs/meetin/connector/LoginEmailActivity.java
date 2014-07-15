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
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginEmailActivity extends ActionBarActivity {

    public final static String EXTRA_EMAIL = "gs.meetin.connector.LOGIN_EMAIL";

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SessionManager.ACTION_LOGIN);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Mtn.gs", "[Email activity]: Received login action");
                finish();
            }
        };

        registerReceiver(broadcastReceiver, intentFilter);

        setButtonListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
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
        String email = ((EditText) findViewById(R.id.inputEmail)).getText().toString();

        if (isValidEmail(email)) {
            Intent loginIntent = new Intent(this, LoginPinActivity.class);
            loginIntent.putExtra(EXTRA_EMAIL, email);
            startActivity(loginIntent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(R.string.invalid_email_message)
                   .setTitle(R.string.invalid_email_title)
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

    private boolean isValidEmail(CharSequence email) {
        if (email == null) {
            return false;
        }

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
