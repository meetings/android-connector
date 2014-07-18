package gs.meetin.connector.dto;

import java.util.ArrayList;

public class SuggestionBatch {
    public ApiError error;

    private String sourceContainerId;
    private String sourceContainerType;
    private String sourceContainerName;
    private String sourceIdInsideContainer;
    private String sourceName;
    private boolean sourceIsPrimary;
    private long timespanBeginEpoch;
    private long timespanEndEpoch;

    private ArrayList<CalendarSuggestion> suggestions;

    public SuggestionBatch(String sourceContainerName,
                           String sourceContainerType,
                           String sourceContainerId,
                           String sourceIdInsideContainer,
                           String sourceName,
                           boolean sourceIsPrimary,
                           long timespanBeginEpoch,
                           long timespanEndEpoch,
                           ArrayList<CalendarSuggestion> suggestions) {
        this.sourceContainerName = sourceContainerName;
        this.sourceContainerType = sourceContainerType;
        this.sourceContainerId = sourceContainerId;
        this.sourceIdInsideContainer = sourceIdInsideContainer;
        this.sourceName = sourceName;
        this.sourceIsPrimary = sourceIsPrimary;
        this.timespanBeginEpoch = timespanBeginEpoch;
        this.timespanEndEpoch = timespanEndEpoch;
        this.suggestions = suggestions;
    }
}
