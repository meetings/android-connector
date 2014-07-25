package gs.meetin.connector.events;

public abstract class Event {
    public static enum EventType {
        // Login
        LOGIN,
        LOGOUT,
        PIN_REQUEST_SUCCESSFUL,
        LOGIN_SUCCESSFUL,

        // Suggestion
        SET_LAST_SYNC_TIME,
        UPDATE_SUGGESTIONS,

        // UI
        SET_BUTTONS_ENABLED,
        SET_BUTTONS_DISABLED
    }

    private EventType type;

    public Event(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

}