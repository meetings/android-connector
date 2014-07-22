package gs.meetin.connector.events;

import java.util.ArrayList;
import java.util.List;

import gs.meetin.connector.dto.SuggestionSource;

public class SuggestionEvent extends Event {

    private List<SuggestionSource> suggestionSources;

    public SuggestionEvent(EventType type) {
        super(type);
    }

    public SuggestionEvent(EventType type, List<SuggestionSource> suggestionSources) {
        super(type);

        this.suggestionSources = suggestionSources;
    }

    public List<SuggestionSource> getSuggestionSources() {
        return suggestionSources;
    }
}
