package gs.meetin.connector;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginEmailActivity extends ActionBarActivity {

    public final static String EXTRA_EMAIL = "gs.meetin.connector.LOGIN_EMAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        setButtonListeners();
    }

    protected void showPinRequest() {
        String email = ((EditText) findViewById(R.id.inputEmail)).getText().toString();

        Intent loginIntent = new Intent(this, LoginPinActivity.class);
        loginIntent.putExtra(EXTRA_EMAIL, email);
        startActivity(loginIntent);

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
}
