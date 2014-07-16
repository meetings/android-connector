package gs.meetin.connector.events;

import org.apache.http.message.BasicNameValuePair;

public class SessionEvent {

    public static final int LOGIN = 1;
    public static final int LOGOUT = 2;
    public static final int PIN_REQUEST_SUCCESSFUL = 3;
    public static final int LOGIN_SUCCESSFUL = 4;

    private int type;

    private BasicNameValuePair user;

    public SessionEvent(int type) {
        this.type = type;
    }

    public SessionEvent(int type, BasicNameValuePair user) {
        this.type = type;
        this.user = user;
    }

    public int getType() {
        return type;
    }

    public BasicNameValuePair getUser() {
        return user;
    }
}
