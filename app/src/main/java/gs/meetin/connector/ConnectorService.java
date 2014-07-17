package gs.meetin.connector;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ConnectorService extends IntentService {

    private boolean running = false;

    public ConnectorService() {
        super("ConnectorService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Mtn.gs", "Starting service...");
        running = true;
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d("Mtn.gs", "Stopping service...");
        running = false;
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (running) {
            synchronized (this) {
                try {
                    Log.d("Mtn.gs", "Syncing suggestions... ");
                    wait((1000 * 60) * 15);
                } catch (Exception e) {
                }
            }
        }
    }
}
