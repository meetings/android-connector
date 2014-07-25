package gs.meetin.connector.events;

import java.util.ArrayList;
import java.util.List;

import gs.meetin.connector.dto.SuggestionSource;

public class SuggestionEvent extends Event {

    public SuggestionEvent(EventType type) {
        super(type);
    }
}
