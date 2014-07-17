package gs.meetin.connector.dto;

/**
 * Created by tuomaslahti on 17/07/14.
 */
public class SuggestionSource {

    private String name;
    private String idInsideContainer;
    private boolean isPrimary;

    public SuggestionSource(String name, String idInsideContainer, boolean isPrimary) {
        this.name = name;
        this.idInsideContainer = idInsideContainer;
        this.isPrimary = isPrimary;
    }

    public String getName() {
        return name;
    }

    public String getIdInsideContainer() {
        return idInsideContainer;
    }

    public boolean isPrimary() {
        return isPrimary;
    }
}