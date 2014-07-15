package gs.meetin.connector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

public class SessionManager {

    public final static String ACTION_LOGIN = "gs.meetin.connector.ACTION_LOGIN";
    public final static String ACTION_LOGOUT = "gs.meetin.connector.ACTION_LOGOUT";
    // Shared Preferences
    SharedPreferences pref;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "MeetingsConnectorPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "isLoggedIn";

    public static final String KEY_USER_ID = "userId";

    public static final String KEY_TOKEN = "token";

    public static final String KEY_EMAIL = "email";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }


    public void signIn(String userId, String token, String email) {
        Log.d("Mtn.gs", "Logging in");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SessionManager.ACTION_LOGIN);
        _context.sendBroadcast(broadcastIntent);

        saveSessionData(userId, token, email);

        startMainActivity();
    }

    public void signOut() {
        Log.d("Mtn.gs", "Logging out");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SessionManager.ACTION_LOGOUT);
        _context.sendBroadcast(broadcastIntent);

        clearSessionData();

        startLoginActivity();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            signOut();
        }
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return user;
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
        Intent connectorIntent = new Intent(_context, ConnectorActivity.class);
        connectorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        _context.startActivity(connectorIntent);
    }

    private void startLoginActivity() {
        Intent i = new Intent(_context, LoginEmailActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        _context.startActivity(i);
    }
}
