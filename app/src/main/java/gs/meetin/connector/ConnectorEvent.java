package gs.meetin.connector;

public class ConnectorEvent {

    public static final int LOGIN = 1;
    public static final int LOGOUT = 2;

    private int type;

    public ConnectorEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
