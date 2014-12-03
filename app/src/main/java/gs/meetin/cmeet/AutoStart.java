package gs.meetin.cmeet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Listener for android.intent.action.BOOT_COMPLETED.
 * Starts service on boot.
 */
public class AutoStart extends BroadcastReceiver
{
    public void onReceive(Context context, Intent i)
    {
        SessionManager sessionManager = new SessionManager(context);

        if(sessionManager.isLoggedIn()) {
            Intent serviceIntent = new Intent(context, ConnectorService.class);
            context.startService(serviceIntent);
        }
    }
}
