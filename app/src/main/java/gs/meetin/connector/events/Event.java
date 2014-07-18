package gs.meetin.connector.events;

public class Event {
    public static enum EventType {
        LOGIN,
        LOGOUT,
        PIN_REQUEST_SUCCESSFUL,
        LOGIN_SUCCESSFUL
    }

    private EventType type;

    public Event(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

}