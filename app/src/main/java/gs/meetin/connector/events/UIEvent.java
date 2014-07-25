package gs.meetin.connector.events;

public class UIEvent extends Event {

    private long lastSync;

    public UIEvent(EventType type) {
        super(type);
    }

    public UIEvent(EventType type, long lastSync) {
        super(type);
        this.lastSync = lastSync;
    }

    public long getLastSync() {
        return lastSync;
    }
}
