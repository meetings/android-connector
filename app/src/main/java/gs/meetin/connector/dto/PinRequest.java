package gs.meetin.connector.dto;


public class PinRequest {
    // Response
    public ApiError error;

    // Request
    private String email;
    private String include_pin;
    private String allow_register;

    public PinRequest(String email) {
        this.email = email;
        this.include_pin = "1";
        this.allow_register = "1";
    }
}
