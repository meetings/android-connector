package gs.meetin.connector.dto;

public class CalendarSuggestion {
    long eventId;
    String title;
    long begin_epocn;
    long end_epoch;
    String description;
    String location;
    String participant_list;
    String organizer;

    public CalendarSuggestion(long eventId,
                       String title,
                       long begin_epocn,
                       long end_epoch,
                       String description,
                       String location,
                       String participant_list,
                       String organizer) {
        this.eventId = eventId;
        this.title = title;
        this.begin_epocn = begin_epocn;
        this.end_epoch = end_epoch;
        this.description = description;
        this.location = location;
        this.participant_list = participant_list;
        this.organizer = organizer;
    }

    public long getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public long getBegin_epocn() {
        return begin_epocn;
    }

    public long getEnd_epoch() {
        return end_epoch;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getParticipant_list() {
        return participant_list;
    }

    public String getOrganizer() {
        return organizer;
    }
}
