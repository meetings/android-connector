package gs.meetin.connector.events;

public abstract class Event {
    public static enum EventType {
        // Login
        LOGIN,
        LOGOUT,
        PIN_REQUEST_SUCCESSFUL,
        LOGIN_SUCCESSFUL,

        // Suggestion
        GET_SOURCES_SUCCESSFUL,
        UPDATE_SOURCES,
        UPDATE_SUGGESTIONS
    }

    private EventType type;

    public Event(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

}