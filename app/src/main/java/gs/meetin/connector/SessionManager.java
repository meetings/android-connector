package gs.meetin.connector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import de.greenrobot.event.EventBus;
import gs.meetin.connector.events.SessionEvent;

import static gs.meetin.connector.events.Event.EventType.LOGIN;
import static gs.meetin.connector.events.Event.EventType.LOGOUT;

public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;

    // Context
    Context context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "MeetingsConnectorPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "isLoggedIn";

    public static final String KEY_USER_ID = "userId";

    public static final String KEY_TOKEN = "token";

    public static final String KEY_EMAIL = "email";

    public SessionManager(Context context){
        this.context = context;
        pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    public void signIn(String userId, String token, String email) {
        Log.d("Mtn.gs", "Logging in");
        EventBus.getDefault().post(new SessionEvent(LOGIN));

        saveSessionData(userId, token, email);

        startMainActivity();
    }

    public void signOut() {
        Log.d("Mtn.gs", "Logging out");
        EventBus.getDefault().post(new SessionEvent(LOGOUT));

        clearSessionData();

        startLoginActivity();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    private void saveSessionData(String userId, String token, String email){
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_EMAIL, email);

        editor.commit();
    }

    private void clearSessionData() {
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(IS_LOGIN, false);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_EMAIL);

        editor.commit();
    }

    private void startMainActivity() {
        Intent connectorIntent = new Intent(context, ConnectorActivity.class);
        connectorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(connectorIntent);
    }

    private void startLoginActivity() {
        Intent i = new Intent(context, LoginEmailActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }

    public String getUserId(){
        return pref.getString(KEY_USER_ID, null);
    }
    public String getToken(){
        return pref.getString(KEY_TOKEN, null);
    }
    public String getUserEmail(){
        return pref.getString(KEY_EMAIL, null);
    }
}
