package gs.meetin.connector.dto;

public class CalendarSuggestion {
    long eventId;
    String title;
    long beginEpoch;
    long endEpoch;
    String description;
    String location;
    String participantList;
    String organizer;

    public CalendarSuggestion(long eventId,
                       String title,
                       long beginEpoch,
                       long endEpoch,
                       String description,
                       String location,
                       String participantList,
                       String organizer) {
        this.eventId = eventId;
        this.title = title;
        this.beginEpoch = beginEpoch;
        this.endEpoch = endEpoch;
        this.description = description;
        this.location = location;
        this.participantList = participantList;
        this.organizer = organizer;
    }

    public long getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public long getBeginEpoch() {
        return beginEpoch;
    }

    public long getEndEpoch() {
        return endEpoch;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getParticipantList() {
        return participantList;
    }

    public String getOrganizer() {
        return organizer;
    }
}
