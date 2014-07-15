package gs.meetin.connector;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class ConnectorActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connector);


        // user is not logged in redirect him to Login Activity
        Intent loginIntent = new Intent(this, LoginEmailActivity.class);
        // Closing all the Activities
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        startActivity(loginIntent);



        Button startService = (Button) findViewById(R.id.buttonStartService);
        Button stopService = (Button) findViewById(R.id.buttonStopService);

        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context ctx = getApplicationContext();

                Toast.makeText(ctx, "service starting", Toast.LENGTH_SHORT).show();

                Intent serviceIntent = new Intent(ctx, ConnectorService.class);
                ctx.startService(serviceIntent);
            }
        });

        stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context ctx = getApplicationContext();

                Toast.makeText(ctx, "service stopping", Toast.LENGTH_SHORT).show();

                Intent serviceIntent = new Intent(ctx, ConnectorService.class);
                ctx.stopService(serviceIntent);
            }
        });
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.connector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
