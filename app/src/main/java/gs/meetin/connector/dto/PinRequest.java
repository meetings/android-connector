package gs.meetin.connector.dto;


public class PinRequest extends MtnResponse {

    // Request
    private String email;
    private String includePin;
    private String allowRegister;

    public PinRequest(String email) {
        this.email = email;
        this.includePin = "1";
        this.allowRegister = "0";
    }
}
