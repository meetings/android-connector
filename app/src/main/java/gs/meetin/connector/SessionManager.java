package gs.meetin.connector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
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

    /**
     * Create login session
     * */
    public void createLoginSession(String userId, String token, String email){
        SharedPreferences.Editor editor = pref.edit();
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_USER_ID, userId);

        // Storing email in pref
        editor.putString(KEY_TOKEN, token);

        editor.putString(KEY_EMAIL, email);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            startLoginActivity();
        }
    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clear all login data from Shared Preferences
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(IS_LOGIN, false);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_EMAIL);

        editor.commit();

        startLoginActivity();
    }

    /**
     * Quick check for login
     * **/
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    private void startLoginActivity() {
        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginEmailActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }
}
