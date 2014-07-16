package gs.meetin.connector.events;

public class ErrorEvent {

    private String title;
    private String message;

    public ErrorEvent(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
